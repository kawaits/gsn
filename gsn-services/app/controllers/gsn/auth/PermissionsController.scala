/**
* Global Sensor Networks (GSN) Source Code
* Copyright (c) 2006-2016, Ecole Polytechnique Federale de Lausanne (EPFL)
* 
* This file is part of GSN.
* 
* GSN is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* GSN is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
* 
* You should have received a copy of the GNU General Public License
* along with GSN.  If not, see <http://www.gnu.org/licenses/>.
* 
* File: app/controllers/gsn/auth/PermissionsController.scala
*
* @author Julien Eberle
*
*/
package controllers.gsn.auth

import scala.concurrent.{Future, Promise}
import akka.actor._
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.mvc._
import models.gsn.auth.{Group, User, DataSource, SecurityRole}
import views.html._
import be.objectify.deadbolt.scala.DeadboltActions
import security.gsn.GSNScalaDeadboltHandler
import controllers.gsn.Global
import javax.inject.Inject
import scala.collection.JavaConverters._
import play.core.j.JavaHelpers
import play.mvc.Http.Context
import play.api.libs.concurrent.Akka
import play.api.Play.current
import ch.epfl.gsn.data._
      
object PermissionsController extends Controller with DeadboltActions {
  
    def vs(page:Int) = Restrict(Array(LocalAuthController.ADMIN_ROLE),new GSNScalaDeadboltHandler) { Action.async { implicit request =>
        val count = DataSource.find.findRowCount()
        val p=Promise[Seq[SensorData]]     
        val st=Akka.system.actorSelection("/user/gsnSensorStore")
        val q=Akka.system.actorOf(Props(new QueryActor(p)))
        q ! GetAllSensors(false,None)
        p.future.map{data =>  
            Context.current.set(JavaHelpers.createJavaContext(request))
            data.map(s => Option(DataSource.findByValue(s.sensor.name)).getOrElse({
              val d = new DataSource()
              d.value = s.sensor.name
              d.is_public = false
              d.save()
              d
            })
            )
  		      Ok(access.vslist.render(DataSource.find.setFirstRow((page - 1) * Global.pageLength).setMaxRows(Global.pageLength).findList().asScala, Group.find.findList().asScala, User.find.findList().asScala, count, page, Global.pageLength))
		   }
    }}
  
  def addgroup(page:Int) = Restrict(Array(LocalAuthController.ADMIN_ROLE),new GSNScalaDeadboltHandler) { Action.async { implicit request => Future {
      //hack to work with java-style templates
     Context.current.set(JavaHelpers.createJavaContext(request))
     val count = Group.find.findRowCount()
     var ret:Result = null
     Forms.groupForm.bindFromRequest.fold(
      formWithErrors => {
        BadRequest(access.grouplist.render(Group.find.setFirstRow((page - 1) * Global.pageLength).setMaxRows(Global.pageLength).findList().asScala, User.find.findList().asScala,formWithErrors, count, page, Global.pageLength))
      },
      data => {
        data.action match {
              case "add" => {
                                val newGroup = new Group()
                                newGroup.name = data.name
                                newGroup.description = data.description
                                newGroup.save
                            }
              case "edit" => {
                                val g = Group.find.byId(data.id)
                                if (g == null){ 
                                  ret = NotFound
                                } else {
                                  g.name = data.name
                                  g.description = data.description
                                  g.update
                                }
                             }
              case "del" => {
                                val g = Group.find.byId(data.id)
                                if (g == null) ret = NotFound
                                else {
                                  g.users.clear               
                                  g.saveManyToManyAssociations("users")
                                  g.delete
                                }
                            }
               }
        
        if (ret != null)  ret else Ok(access.grouplist.render(Group.find.setFirstRow((page - 1) * Global.pageLength).setMaxRows(Global.pageLength).findList().asScala,User.find.findList().asScala, Forms.groupForm, count, page, Global.pageLength))
      }
    )
      
		  
        }
      }
  }
  
  def addtogroup(page:Int) = Restrict(Array(LocalAuthController.ADMIN_ROLE),new GSNScalaDeadboltHandler) { Action.async { implicit request => Future {
      //hack to work with java-style templates
        Context.current.set(JavaHelpers.createJavaContext(request))
        val count = Group.find.findRowCount()
        val g = request.queryString.get("group_id").map { x => Group.find.byId(x.head.toLong) }
        val u = request.queryString.get("user_id").map { x => User.find.byId(x.head.toLong) }

        u.fold(BadRequest(access.grouplist.render(Group.find.setFirstRow((page - 1) * Global.pageLength).setMaxRows(Global.pageLength).findList().asScala,User.find.findList().asScala, Forms.groupForm, count, page, Global.pageLength)))(user => {
            g.fold(BadRequest(access.grouplist.render(Group.find.setFirstRow((page - 1) * Global.pageLength).setMaxRows(Global.pageLength).findList().asScala,User.find.findList().asScala, Forms.groupForm, count, page, Global.pageLength)))(group => {
                group.users.add(user)
                group.saveManyToManyAssociations("users")
                Ok(access.grouplist.render(Group.find.setFirstRow((page - 1) * Global.pageLength).setMaxRows(Global.pageLength).findList().asScala,User.find.findList().asScala, Forms.groupForm, count, page, Global.pageLength))
            })
        })   
        }
      }
  }
  
  def removefromgroup(page:Int) = Restrict(Array(LocalAuthController.ADMIN_ROLE),new GSNScalaDeadboltHandler) { Action.async { implicit request => Future {
      //hack to work with java-style templates
     Context.current.set(JavaHelpers.createJavaContext(request))
     val count = Group.find.findRowCount()
        val g = request.queryString.get("group_id").map { x => Group.find.byId(x.head.toLong) }
        val u = request.queryString.get("user_id").map { x => User.find.byId(x.head.toLong) }

        u.fold(BadRequest(access.grouplist.render(Group.find.setFirstRow((page - 1) * Global.pageLength).setMaxRows(Global.pageLength).findList().asScala,User.find.findList().asScala, Forms.groupForm, count, page, Global.pageLength)))(user => {
            g.fold(BadRequest(access.grouplist.render(Group.find.setFirstRow((page - 1) * Global.pageLength).setMaxRows(Global.pageLength).findList().asScala,User.find.findList().asScala, Forms.groupForm, count, page, Global.pageLength)))(group => {
                group.users.remove(user)
                group.saveManyToManyAssociations("users")
                Ok(access.grouplist.render(Group.find.setFirstRow((page - 1) * Global.pageLength).setMaxRows(Global.pageLength).findList().asScala,User.find.findList().asScala, Forms.groupForm, count, page, Global.pageLength))
            })
        })   
        }
      }
  }
  
  def groups(page:Int) = Restrict(Array(LocalAuthController.ADMIN_ROLE),new GSNScalaDeadboltHandler) { Action.async { implicit request => Future {
        //hack to work with java-style templates
        Context.current.set(JavaHelpers.createJavaContext(request))
        val count = Group.find.findRowCount()
  		  Ok(access.grouplist.render(Group.find.setFirstRow((page - 1) * Global.pageLength).setMaxRows(Global.pageLength).findList().asScala,User.find.findList().asScala, Forms.groupForm, count, page, Global.pageLength))
		  }
      }
  }
  
  def users(page:Int) = Restrict(Array(LocalAuthController.ADMIN_ROLE),new GSNScalaDeadboltHandler) { Action.async { implicit request => Future {
        //hack to work with java-style templates
        Context.current.set(JavaHelpers.createJavaContext(request))
        val count = User.find.findRowCount()
  		  Ok(access.userlist.render(User.find.setFirstRow((page - 1) * Global.pageLength).setMaxRows(Global.pageLength).findList().asScala, SecurityRole.find.findList().asScala, count, page, Global.pageLength))
		  }
      }
  }
  
  def addrole(page:Int) = Restrict(Array(LocalAuthController.ADMIN_ROLE),new GSNScalaDeadboltHandler) { Action.async { implicit request => Future {
      //hack to work with java-style templates
     Context.current.set(JavaHelpers.createJavaContext(request))
        val count = User.find.findRowCount()
        val r = request.queryString.get("role_id").map { x => SecurityRole.find.byId(x.head.toLong) }
        val u = request.queryString.get("user_id").map { x => User.find.byId(x.head.toLong) }

        u.fold(BadRequest(access.userlist.render(User.find.setFirstRow((page - 1) * Global.pageLength).setMaxRows(Global.pageLength).findList().asScala, SecurityRole.find.findList().asScala, count, page, Global.pageLength)))(user => {
            r.fold(BadRequest(access.userlist.render(User.find.setFirstRow((page - 1) * Global.pageLength).setMaxRows(Global.pageLength).findList().asScala, SecurityRole.find.findList().asScala, count, page, Global.pageLength)))(role => {
                user.roles.add(role)
                user.saveManyToManyAssociations("roles")
                Ok(access.userlist.render(User.find.setFirstRow((page - 1) * Global.pageLength).setMaxRows(Global.pageLength).findList().asScala, SecurityRole.find.findList().asScala, count, page, Global.pageLength))
            })
        })   
        }
      }
  }
  
  def removerole(page:Int) = Restrict(Array(LocalAuthController.ADMIN_ROLE),new GSNScalaDeadboltHandler) { Action.async { implicit request => Future {
      //hack to work with java-style templates
     Context.current.set(JavaHelpers.createJavaContext(request))
     val count = User.find.findRowCount()
        val r = request.queryString.get("role_id").map { x => SecurityRole.find.byId(x.head.toLong) }
        val u = request.queryString.get("user_id").map { x => User.find.byId(x.head.toLong) }

        u.fold(BadRequest(access.userlist.render(User.find.setFirstRow((page - 1) * Global.pageLength).setMaxRows(Global.pageLength).findList().asScala, SecurityRole.find.findList().asScala, count, page, Global.pageLength)))(user => {
            r.fold(BadRequest(access.userlist.render(User.find.setFirstRow((page - 1) * Global.pageLength).setMaxRows(Global.pageLength).findList().asScala, SecurityRole.find.findList().asScala, count, page, Global.pageLength)))(role => {
                user.roles.remove(role)
                user.saveManyToManyAssociations("roles")
                Ok(access.userlist.render(User.find.setFirstRow((page - 1) * Global.pageLength).setMaxRows(Global.pageLength).findList().asScala, SecurityRole.find.findList().asScala, count, page, Global.pageLength))
            })
        })   
        }
      }
  }
  
  def addtovs(page:Int) = Restrict(Array(LocalAuthController.ADMIN_ROLE),new GSNScalaDeadboltHandler) { Action.async { implicit request => Future {
      //hack to work with java-style templates
      Context.current.set(JavaHelpers.createJavaContext(request))
      val count = DataSource.find.findRowCount()
      val v = request.queryString.get("vs_id").map { x => DataSource.find.byId(x.head.toLong) }
      v.fold(BadRequest(access.vslist.render(DataSource.find.setFirstRow((page - 1) * Global.pageLength).setMaxRows(Global.pageLength).findList().asScala, Group.find.findList().asScala, User.find.findList().asScala, count, page, Global.pageLength)))(vs => {
          request.queryString.get("id").map {x => x.head match {
              case s if s.startsWith("ur") => {
                  vs.r_users.add(User.find.byId(s.stripPrefix("ur").toLong))
                  vs.saveManyToManyAssociations("r_users")
                  }
              case s if s.startsWith("gr") => {
                  vs.r_groups.add(Group.find.byId(s.stripPrefix("gr").toLong))
                  vs.saveManyToManyAssociations("r_groups")
                  }
              case s if s.startsWith("uw") => {
                  vs.w_users.add(User.find.byId(s.stripPrefix("uw").toLong))
                  vs.saveManyToManyAssociations("w_users")
                  }
              case s if s.startsWith("gw") => {
                  vs.w_groups.add(Group.find.byId(s.stripPrefix("gw").toLong))
                  vs.saveManyToManyAssociations("w_groups")
                  }
              case s if s.startsWith("a") => {
                  vs.setIs_public(true)
                  vs.save()
              }
          }}
          Ok(access.vslist.render(DataSource.find.setFirstRow((page - 1) * Global.pageLength).setMaxRows(Global.pageLength).findList().asScala, Group.find.findList().asScala, User.find.findList().asScala, count, page, Global.pageLength))
        })   
        }
      }
  }
  
  def removefromvs(page:Int) = Restrict(Array(LocalAuthController.ADMIN_ROLE),new GSNScalaDeadboltHandler) { Action.async { implicit request => Future {
      //hack to work with java-style templates
      Context.current.set(JavaHelpers.createJavaContext(request))
      val count = DataSource.find.findRowCount()
      val v = request.queryString.get("vs_id").map { x => DataSource.find.byId(x.head.toLong) }
      v.fold(BadRequest(access.vslist.render(DataSource.find.setFirstRow((page - 1) * Global.pageLength).setMaxRows(Global.pageLength).findList().asScala, Group.find.findList().asScala, User.find.findList().asScala, count, page, Global.pageLength)))(vs => {
          request.queryString.get("id").map {x => x.head match {
              case s if s.startsWith("ur") => {
                  vs.r_users.remove(User.find.byId(s.stripPrefix("ur").toLong))
                  vs.saveManyToManyAssociations("r_users")
                  }
              case s if s.startsWith("gr") => {
                  vs.r_groups.remove(Group.find.byId(s.stripPrefix("gr").toLong))
                  vs.saveManyToManyAssociations("r_groups")
                  }
              case s if s.startsWith("uw") => {
                  vs.w_users.remove(User.find.byId(s.stripPrefix("uw").toLong))
                  vs.saveManyToManyAssociations("w_users")
                  }
              case s if s.startsWith("gw") => {
                  vs.w_groups.remove(Group.find.byId(s.stripPrefix("gw").toLong))
                  vs.saveManyToManyAssociations("w_groups")
                  }
              case s if s.startsWith("a") => {
                  vs.setIs_public(false)
                  vs.save()
              }
          }}
          Ok(access.vslist.render(DataSource.find.setFirstRow((page - 1) * Global.pageLength).setMaxRows(Global.pageLength).findList().asScala, Group.find.findList().asScala, User.find.findList().asScala, count, page, Global.pageLength))
        })   
        }
      }
  }

}

