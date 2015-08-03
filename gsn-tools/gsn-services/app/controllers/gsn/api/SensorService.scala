package controllers.gsn.api

import scala.collection.JavaConversions.asScalaBuffer
import scala.collection.mutable.ArrayBuffer
import scala.concurrent.Future
import scala.concurrent.Promise
import scala.concurrent.duration.DurationInt
import scala.util.Try
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatterBuilder
import com.typesafe.config.ConfigFactory
import akka.actor._
import controllers.gsn.Global
import gsn.data._
import gsn.xpr.XprConditions
import play.api.Play.current
import play.api.libs.concurrent.Akka
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc._
import play.Logger
import scala.util.Success
import gsn.data.format.XmlSerializer
import play.api.http.ContentTypes
import gsn.data.format.CsvSerializer
import play.api.http.Writeable
import play.api.libs.json.JsValue
import play.api.libs.iteratee.Enumerator
import java.io.OutputStream
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.core.JsonFactory
import java.io.ByteArrayOutputStream
import java.io.ObjectOutputStream
import java.io.ByteArrayInputStream
import play.api.libs.json.JacksonJson
import gsn.data.format.JsonSerializer

object SensorService extends Controller{   
  lazy val conf=ConfigFactory.load
  val defaultMetaProps=conf.getStringList("gsn.api.defaultMetadataProps")
  val dateFormatter={
    val parsers=conf.getStringList("gsn.api.dateTimeFormats").map{d=>
      DateTimeFormat.forPattern(d).getParser
    }.toArray
    new DateTimeFormatterBuilder().append(null,parsers).toFormatter
  }

  val validFormats=Seq(Csv,Json)
  val defaultFormat=Json
 
  def queryparam(name:String)(implicit req:Request[AnyContent])={
    req.queryString.get(name).map(_.head)
  }
  def param[T](name:String,fun: String=>T,default:T)(implicit req:Request[AnyContent])=
    queryparam(name).map(fun(_)).getOrElse(default)
      
  def sensors = Action.async {implicit request=>
    Try{    
      val format=param("format",OutputFormat,defaultFormat)    
      val latestVals=param("latestValues",_.toBoolean,false)
      val timeFormat=queryparam("timeFormat")
      val p=Promise[Seq[SensorData]]
           
      val st=Akka.system.actorSelection("/user/gsnSensorStore")
      val q=Akka.system.actorOf(Props(new QueryActor(p)))
      q ! GetAllSensors(latestVals,timeFormat)
      
      p.future.map{data =>
        val out=format match{          
          case Json=>JsonSerializer.ser(data,Seq(),latestVals)
          case Csv=>CsvSerializer.ser(data, defaultMetaProps, latestVals)
          case Xml=>XmlSerializer.ser(data, defaultMetaProps, latestVals)
        }
        result(out,format)
      } 
    }.recover{
      case t=>
        t.printStackTrace  
        Future(BadRequest(t.getMessage))    
    }.get    
  }
  
  @deprecated("user-password authentication phased out","")
  private def authorizeUserPass(vsname:String)(implicit request:Request[AnyContent])={
    val optUser=queryparam("username")
	val optPass=queryparam("password")
	if (optUser.isDefined && optPass.isDefined){
	  if (!Global.acDs .authorizeVs(vsname, optUser.get, optPass.get))
	    throw new IllegalArgumentException(s"Not authorized user ${optUser.get} for resource $vsname")
	}    
  }
  
  private def globalKeyOk(key:String)=
    key.equals(Global.globalKey )
  
  private def authorizeVs(vsname:String)(implicit request:Request[AnyContent])={     
    if (Global.gsnConf.accessControl.enabled && Global.acDs.hasAccessControl(vsname)){
	  val optApikey=queryparam("apikey")
	  // Deprecated user and pass on query params 
	  //authorizeUserPass(vsname) 
	   
	  if (optApikey.isDefined){	    
	    if (!globalKeyOk(optApikey.get) && !Global.acDs.authorizeVs(vsname, optApikey.get))	      
	      throw new IllegalArgumentException(s"Apikey ${optApikey.get} not authorized for resource $vsname")      
	  }
	  else
	    throw new IllegalArgumentException("Required apikey or user credentials were not provided")
    }
  }
  
  def sensorData(sensorid:String) = Action.async {implicit request=>
    Try{
      val vsname=sensorid.toLowerCase
      //to enable
      authorizeVs(sensorid)
    	
      val size:Option[Int]=queryparam("size").map(_.toInt)
      val fieldStr:Option[String]=queryparam("fields")
      val filterStr:Option[String]=queryparam("filter")
      val fromStr:Option[String]=queryparam("from")
      val toStr:Option[String]=queryparam("to")
      val timeFormat:Option[String]=queryparam("timeFormat")

      val format=param("format",OutputFormat,defaultFormat)           
      val filters=new ArrayBuffer[String]
      val fields:Array[String]=
        if (!fieldStr.isDefined) Array() 
        else fieldStr.get.split(",")
      if (fromStr.isDefined)          
        filters+= "timed>"+dateFormatter.parseDateTime(fromStr.get).getMillis
      if (toStr.isDefined)          
        filters+= "timed<"+dateFormatter.parseDateTime(toStr.get).getMillis
      val conds=XprConditions.parseConditions(filterStr.toArray).recover{                 
        case e=>throw new IllegalArgumentException("illegal conditions in filter: "+e.getMessage())
      }.get.map(_.toString)
 
      
      val p=Promise[Seq[SensorData]]               
      val q=Akka.system.actorOf(Props(new QueryActor(p)))
      Logger.debug("request the query actor")
      q ! GetSensorData(vsname,fields,conds++filters,size,timeFormat)
      //val to=play.api.libs.concurrent.Promise.timeout(throw new Exception("bad things"), 15.second)
      p.future.map{data=>        
        Logger.debug("before formatting")
                 
        format match{
            case controllers.gsn.api.Json=>
              val pp=JsonSerializer.ser(data.head,Seq(),false)
              Logger.debug("serialized json")
              
              Logger.debug("strings")
              //val en=Enumerator.enumerate(top.get)
              Ok(pp)
            case Csv=>Ok(CsvSerializer.ser(data.head,Seq(),false))
          }
          
      }.recover{
        case t=> BadRequest(t.getMessage)
      }
      /*
      val copo =Future.firstCompletedOf(Seq(p.future,to)).map{          
        data=> 
          format match{
            case Json=>Ok(JsonSerializer.ser(data.head,Seq(),false))
            case Csv=>Ok(CsvSerializer.ser(data.head,Seq(),false))
          }
      }.recoverWith{case e=>
        
        Logger.error(e.getMessage+e.getStackTrace().mkString("\n"))
        Future(BadRequest(e.getMessage))
      }
      copo*/
    }.recover{
      case t=>Future(BadRequest("Error: "+t.getMessage))
    }.get
  }

  
  def sensorField(sensorid:String,fieldid:String) = 
    Action.async {implicit request=>
    Try{
      val vsname=sensorid.toLowerCase
      //to enable
      //authorizeVs(sensorid)
    	
      val format=param("format",OutputFormat,defaultFormat)           
      val filters=new ArrayBuffer[String]
 
      val p=Promise[Seq[SensorData]]               
      val q=Akka.system.actorOf(Props(new QueryActor(p)))
      
      q ! GetSensorData(sensorid,Seq(fieldid),Seq(),Some(1),None)
      p.future.map{data=>        
          
            val series=data.head.ts.find(ts=>ts.output.fieldName.equalsIgnoreCase(fieldid))
            val fieldData=series.flatMap{s=>
              s.series.headOption              
            }.map {
              case l:Long=>Ok(l.toString)
              case d:Double=>Ok(d.toString)
              case bin:Array[Byte]=>Ok(bin).as("image")
            }         
          fieldData.get
      }.recover{
        case t=> BadRequest(t.getMessage)
      }
    }.recover{
      case t=>Future(BadRequest("Error: "+t.getMessage))
    }.get
  }

  def sensorMetadata(sensorid:String) = Action.async {implicit request=>
    Try{
      //to enable
      //authorizeVs(sensorid)    	
      val latestVals=param("latestValues",_.toBoolean,false)
      val timeFormat:Option[String]=queryparam("timeFormat")
      val format=param("format",OutputFormat,defaultFormat)            
      val p=Promise[Seq[SensorData]]               
      val q=Akka.system.actorOf(Props(new QueryActor(p)))      
      q ! GetSensor(sensorid,latestVals,timeFormat)
      //val to=play.api.libs.concurrent.Promise.timeout(throw new Exception("bad things"), 15.second)
      
      p.future.map{data=>        
        format match {
            case Json=>Ok(JsonSerializer.ser(data.head,Seq(),false))
            case Csv=>Ok(CsvSerializer.ser(data.head,Seq(),false))
            case Xml=>Ok(XmlSerializer.ser(data.head, Seq(), latestVals))
        }          
      }.recover{
        case t=> BadRequest(t.getMessage)        
      }
    }.recover{
      case t=> Future(BadRequest(t.getMessage))
    }.get
  }

  def sensorSearch = Action.async {implicit request=>
    Try{
      //to enable
      //authorizeVs(sensorid)
      val sensorsStr:Option[String]=queryparam("vsnames")    	
      val size:Option[Int]=queryparam("size").map(_.toInt)
      val fieldStr:Option[String]=queryparam("fields")
      val filterStr:Option[String]=queryparam("filter")
      val fromStr:Option[String]=queryparam("from")
      val toStr:Option[String]=queryparam("to")
      val timeFormat:Option[String]=queryparam("timeFormat")

      val format=param("format",OutputFormat,defaultFormat)           
      val filters=new ArrayBuffer[String]
      val fields:Array[String]=
        if (!fieldStr.isDefined) Array() 
        else fieldStr.get.split(",")
      val vsnames:Array[String]=
        if (!sensorsStr.isDefined) Array() 
        else sensorsStr.get.split(",")
      if (fromStr.isDefined)          
        filters+= "timed>"+dateFormatter.parseDateTime(fromStr.get).getMillis
      if (toStr.isDefined)          
        filters+= "timed<"+dateFormatter.parseDateTime(toStr.get).getMillis
      val conds=XprConditions.parseConditions(filterStr.toArray).recover{                 
        case e=>throw new IllegalArgumentException("illegal conditions in filter: "+e.getMessage())
      }.get.map(_.toString)
 
      
      val dataset=vsnames.map{sensorid=>
        val p=Promise[Seq[SensorData]]               
        val q=Akka.system.actorOf(Props(new QueryActor(p)))
      
        q ! GetSensorData(sensorid,fields,conds++filters,size,timeFormat)
        p.future.map{data=>           
          data.head
        }      
      
        
      }.toSeq
      val pp=Future.sequence(dataset)
      pp.map{dats=>
        format match {
            case Json=>Ok(JsonSerializer.ser(dats,Seq(),false))
            case Csv=>Ok(CsvSerializer.serZip(dats,Seq(),false)).as("application/zip")
            case Xml=>Ok(XmlSerializer.ser(dats, Seq(), false))
        }          
      }.recover{
        case t=> BadRequest(t.getMessage)              
      }      
    }.recover{
      case t=>Future(BadRequest("Error: "+t.getMessage))
    }.get
  }

  
  def download= Action.async {implicit request=>
    //request.body.
    Future(Ok(""))
    
  }
  
  def result(s:Object,out:OutputFormat)={
    val contentType = out match {
      case Xml=>ContentTypes.XML
      case Json=>ContentTypes.JSON
      case _ =>ContentTypes.TEXT
    }
    s match {
      case s:String=>Ok(s).as(contentType)
      case j:JsValue=>
        Ok(j).as(contentType)
    }
  }
  
  
}