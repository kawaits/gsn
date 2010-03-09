
/**
 * SensorManagerMessageReceiverInOut.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.4.1  Built on : Aug 13, 2008 (05:03:35 LKT)
 */
        package gsn.hydrosys.sensormanager;

        /**
        *  SensorManagerMessageReceiverInOut message receiver
        */

        public class SensorManagerMessageReceiverInOut extends org.apache.axis2.receivers.AbstractInOutMessageReceiver{


        public void invokeBusinessLogic(org.apache.axis2.context.MessageContext msgContext, org.apache.axis2.context.MessageContext newMsgContext)
        throws org.apache.axis2.AxisFault{

        try {

        // get the implementation class for the Web Service
        Object obj = getTheImplementationObject(msgContext);

        SensorManagerSkeleton skel = (SensorManagerSkeleton)obj;
        //Out Envelop
        org.apache.axiom.soap.SOAPEnvelope envelope = null;
        //Find the axisOperation that has been set by the Dispatch phase.
        org.apache.axis2.description.AxisOperation op = msgContext.getOperationContext().getAxisOperation();
        if (op == null) {
        throw new org.apache.axis2.AxisFault("Operation is not located, if this is doclit style the SOAP-ACTION should specified via the SOAP Action to use the RawXMLProvider");
        }

        java.lang.String methodName;
        if((op.getName() != null) && ((methodName = org.apache.axis2.util.JavaUtils.xmlNameToJava(op.getName().getLocalPart())) != null)){

        

            if("getOutputStructure".equals(methodName)){
                
                gsn.hydrosys.sensormanager.GetOutputStructureResponse getOutputStructureResponse4 = null;
	                        gsn.hydrosys.sensormanager.GetOutputStructure wrappedParam =
                                                             (gsn.hydrosys.sensormanager.GetOutputStructure)fromOM(
                                    msgContext.getEnvelope().getBody().getFirstElement(),
                                    gsn.hydrosys.sensormanager.GetOutputStructure.class,
                                    getEnvelopeNamespaces(msgContext.getEnvelope()));
                                                
                                               getOutputStructureResponse4 =
                                                   
                                                   
                                                         skel.getOutputStructure(wrappedParam)
                                                    ;
                                            
                                        envelope = toEnvelope(getSOAPFactory(msgContext), getOutputStructureResponse4, false);
                                    } else 

            if("listEnabledWrappers".equals(methodName)){
                
                gsn.hydrosys.sensormanager.ListEnabledWrappersResponse listEnabledWrappersResponse6 = null;
	                        listEnabledWrappersResponse6 =
                                                     
                                                 skel.listEnabledWrappers()
                                                ;
                                            
                                        envelope = toEnvelope(getSOAPFactory(msgContext), listEnabledWrappersResponse6, false);
                                    } else 

            if("deleteVirtualSensor".equals(methodName)){
                
                gsn.hydrosys.sensormanager.DeleteVirtualSensorResponse deleteVirtualSensorResponse8 = null;
	                        gsn.hydrosys.sensormanager.DeleteVirtualSensor wrappedParam =
                                                             (gsn.hydrosys.sensormanager.DeleteVirtualSensor)fromOM(
                                    msgContext.getEnvelope().getBody().getFirstElement(),
                                    gsn.hydrosys.sensormanager.DeleteVirtualSensor.class,
                                    getEnvelopeNamespaces(msgContext.getEnvelope()));
                                                
                                               deleteVirtualSensorResponse8 =
                                                   
                                                   
                                                         skel.deleteVirtualSensor(wrappedParam)
                                                    ;
                                            
                                        envelope = toEnvelope(getSOAPFactory(msgContext), deleteVirtualSensorResponse8, false);
                                    } else 

            if("registerQuery".equals(methodName)){
                
                gsn.hydrosys.sensormanager.RegisterQueryResponse registerQueryResponse10 = null;
	                        gsn.hydrosys.sensormanager.RegisterQuery wrappedParam =
                                                             (gsn.hydrosys.sensormanager.RegisterQuery)fromOM(
                                    msgContext.getEnvelope().getBody().getFirstElement(),
                                    gsn.hydrosys.sensormanager.RegisterQuery.class,
                                    getEnvelopeNamespaces(msgContext.getEnvelope()));
                                                
                                               registerQueryResponse10 =
                                                   
                                                   
                                                         skel.registerQuery(wrappedParam)
                                                    ;
                                            
                                        envelope = toEnvelope(getSOAPFactory(msgContext), registerQueryResponse10, false);
                                    } else 

            if("listVirtualSensors".equals(methodName)){
                
                gsn.hydrosys.sensormanager.ListVirtualSensorsResponse listVirtualSensorsResponse12 = null;
	                        listVirtualSensorsResponse12 =
                                                     
                                                 skel.listVirtualSensors()
                                                ;
                                            
                                        envelope = toEnvelope(getSOAPFactory(msgContext), listVirtualSensorsResponse12, false);
                                    } else 

            if("listAvailableWrappers".equals(methodName)){
                
                gsn.hydrosys.sensormanager.ListAvailableWrappersResponse listAvailableWrappersResponse14 = null;
	                        listAvailableWrappersResponse14 =
                                                     
                                                 skel.listAvailableWrappers()
                                                ;
                                            
                                        envelope = toEnvelope(getSOAPFactory(msgContext), listAvailableWrappersResponse14, false);
                                    } else 

            if("unregisterQuery".equals(methodName)){
                
                gsn.hydrosys.sensormanager.UnregisterQueryResponse unregisterQueryResponse16 = null;
	                        gsn.hydrosys.sensormanager.UnregisterQuery wrappedParam =
                                                             (gsn.hydrosys.sensormanager.UnregisterQuery)fromOM(
                                    msgContext.getEnvelope().getBody().getFirstElement(),
                                    gsn.hydrosys.sensormanager.UnregisterQuery.class,
                                    getEnvelopeNamespaces(msgContext.getEnvelope()));
                                                
                                               unregisterQueryResponse16 =
                                                   
                                                   
                                                         skel.unregisterQuery(wrappedParam)
                                                    ;
                                            
                                        envelope = toEnvelope(getSOAPFactory(msgContext), unregisterQueryResponse16, false);
                                    } else 

            if("createVirtualSensor".equals(methodName)){
                
                gsn.hydrosys.sensormanager.CreateVirtualSensorResponse createVirtualSensorResponse18 = null;
	                        gsn.hydrosys.sensormanager.CreateVirtualSensor wrappedParam =
                                                             (gsn.hydrosys.sensormanager.CreateVirtualSensor)fromOM(
                                    msgContext.getEnvelope().getBody().getFirstElement(),
                                    gsn.hydrosys.sensormanager.CreateVirtualSensor.class,
                                    getEnvelopeNamespaces(msgContext.getEnvelope()));
                                                
                                               createVirtualSensorResponse18 =
                                                   
                                                   
                                                         skel.createVirtualSensor(wrappedParam)
                                                    ;
                                            
                                        envelope = toEnvelope(getSOAPFactory(msgContext), createVirtualSensorResponse18, false);
                                    
            } else {
              throw new java.lang.RuntimeException("method not found");
            }
        

        newMsgContext.setEnvelope(envelope);
        }
        }
        catch (java.lang.Exception e) {
        throw org.apache.axis2.AxisFault.makeFault(e);
        }
        }
        
        //
            private  org.apache.axiom.om.OMElement  toOM(gsn.hydrosys.sensormanager.GetOutputStructure param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(gsn.hydrosys.sensormanager.GetOutputStructure.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(gsn.hydrosys.sensormanager.GetOutputStructureResponse param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(gsn.hydrosys.sensormanager.GetOutputStructureResponse.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(gsn.hydrosys.sensormanager.ListEnabledWrappersResponse param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(gsn.hydrosys.sensormanager.ListEnabledWrappersResponse.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(gsn.hydrosys.sensormanager.DeleteVirtualSensor param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(gsn.hydrosys.sensormanager.DeleteVirtualSensor.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(gsn.hydrosys.sensormanager.DeleteVirtualSensorResponse param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(gsn.hydrosys.sensormanager.DeleteVirtualSensorResponse.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(gsn.hydrosys.sensormanager.RegisterQuery param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(gsn.hydrosys.sensormanager.RegisterQuery.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(gsn.hydrosys.sensormanager.RegisterQueryResponse param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(gsn.hydrosys.sensormanager.RegisterQueryResponse.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(gsn.hydrosys.sensormanager.ListVirtualSensorsResponse param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(gsn.hydrosys.sensormanager.ListVirtualSensorsResponse.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(gsn.hydrosys.sensormanager.ListAvailableWrappersResponse param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(gsn.hydrosys.sensormanager.ListAvailableWrappersResponse.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(gsn.hydrosys.sensormanager.UnregisterQuery param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(gsn.hydrosys.sensormanager.UnregisterQuery.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(gsn.hydrosys.sensormanager.UnregisterQueryResponse param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(gsn.hydrosys.sensormanager.UnregisterQueryResponse.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(gsn.hydrosys.sensormanager.CreateVirtualSensor param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(gsn.hydrosys.sensormanager.CreateVirtualSensor.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(gsn.hydrosys.sensormanager.CreateVirtualSensorResponse param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(gsn.hydrosys.sensormanager.CreateVirtualSensorResponse.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
                    private  org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, gsn.hydrosys.sensormanager.GetOutputStructureResponse param, boolean optimizeContent)
                        throws org.apache.axis2.AxisFault{
                      try{
                          org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
                           
                                    emptyEnvelope.getBody().addChild(param.getOMElement(gsn.hydrosys.sensormanager.GetOutputStructureResponse.MY_QNAME,factory));
                                

                         return emptyEnvelope;
                    } catch(org.apache.axis2.databinding.ADBException e){
                        throw org.apache.axis2.AxisFault.makeFault(e);
                    }
                    }
                    
                         private gsn.hydrosys.sensormanager.GetOutputStructureResponse wrapgetOutputStructure(){
                                gsn.hydrosys.sensormanager.GetOutputStructureResponse wrappedElement = new gsn.hydrosys.sensormanager.GetOutputStructureResponse();
                                return wrappedElement;
                         }
                    
                    private  org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, gsn.hydrosys.sensormanager.ListEnabledWrappersResponse param, boolean optimizeContent)
                        throws org.apache.axis2.AxisFault{
                      try{
                          org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
                           
                                    emptyEnvelope.getBody().addChild(param.getOMElement(gsn.hydrosys.sensormanager.ListEnabledWrappersResponse.MY_QNAME,factory));
                                

                         return emptyEnvelope;
                    } catch(org.apache.axis2.databinding.ADBException e){
                        throw org.apache.axis2.AxisFault.makeFault(e);
                    }
                    }
                    
                         private gsn.hydrosys.sensormanager.ListEnabledWrappersResponse wraplistEnabledWrappers(){
                                gsn.hydrosys.sensormanager.ListEnabledWrappersResponse wrappedElement = new gsn.hydrosys.sensormanager.ListEnabledWrappersResponse();
                                return wrappedElement;
                         }
                    
                    private  org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, gsn.hydrosys.sensormanager.DeleteVirtualSensorResponse param, boolean optimizeContent)
                        throws org.apache.axis2.AxisFault{
                      try{
                          org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
                           
                                    emptyEnvelope.getBody().addChild(param.getOMElement(gsn.hydrosys.sensormanager.DeleteVirtualSensorResponse.MY_QNAME,factory));
                                

                         return emptyEnvelope;
                    } catch(org.apache.axis2.databinding.ADBException e){
                        throw org.apache.axis2.AxisFault.makeFault(e);
                    }
                    }
                    
                         private gsn.hydrosys.sensormanager.DeleteVirtualSensorResponse wrapdeleteVirtualSensor(){
                                gsn.hydrosys.sensormanager.DeleteVirtualSensorResponse wrappedElement = new gsn.hydrosys.sensormanager.DeleteVirtualSensorResponse();
                                return wrappedElement;
                         }
                    
                    private  org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, gsn.hydrosys.sensormanager.RegisterQueryResponse param, boolean optimizeContent)
                        throws org.apache.axis2.AxisFault{
                      try{
                          org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
                           
                                    emptyEnvelope.getBody().addChild(param.getOMElement(gsn.hydrosys.sensormanager.RegisterQueryResponse.MY_QNAME,factory));
                                

                         return emptyEnvelope;
                    } catch(org.apache.axis2.databinding.ADBException e){
                        throw org.apache.axis2.AxisFault.makeFault(e);
                    }
                    }
                    
                         private gsn.hydrosys.sensormanager.RegisterQueryResponse wrapregisterQuery(){
                                gsn.hydrosys.sensormanager.RegisterQueryResponse wrappedElement = new gsn.hydrosys.sensormanager.RegisterQueryResponse();
                                return wrappedElement;
                         }
                    
                    private  org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, gsn.hydrosys.sensormanager.ListVirtualSensorsResponse param, boolean optimizeContent)
                        throws org.apache.axis2.AxisFault{
                      try{
                          org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
                           
                                    emptyEnvelope.getBody().addChild(param.getOMElement(gsn.hydrosys.sensormanager.ListVirtualSensorsResponse.MY_QNAME,factory));
                                

                         return emptyEnvelope;
                    } catch(org.apache.axis2.databinding.ADBException e){
                        throw org.apache.axis2.AxisFault.makeFault(e);
                    }
                    }
                    
                         private gsn.hydrosys.sensormanager.ListVirtualSensorsResponse wraplistVirtualSensors(){
                                gsn.hydrosys.sensormanager.ListVirtualSensorsResponse wrappedElement = new gsn.hydrosys.sensormanager.ListVirtualSensorsResponse();
                                return wrappedElement;
                         }
                    
                    private  org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, gsn.hydrosys.sensormanager.ListAvailableWrappersResponse param, boolean optimizeContent)
                        throws org.apache.axis2.AxisFault{
                      try{
                          org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
                           
                                    emptyEnvelope.getBody().addChild(param.getOMElement(gsn.hydrosys.sensormanager.ListAvailableWrappersResponse.MY_QNAME,factory));
                                

                         return emptyEnvelope;
                    } catch(org.apache.axis2.databinding.ADBException e){
                        throw org.apache.axis2.AxisFault.makeFault(e);
                    }
                    }
                    
                         private gsn.hydrosys.sensormanager.ListAvailableWrappersResponse wraplistAvailableWrappers(){
                                gsn.hydrosys.sensormanager.ListAvailableWrappersResponse wrappedElement = new gsn.hydrosys.sensormanager.ListAvailableWrappersResponse();
                                return wrappedElement;
                         }
                    
                    private  org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, gsn.hydrosys.sensormanager.UnregisterQueryResponse param, boolean optimizeContent)
                        throws org.apache.axis2.AxisFault{
                      try{
                          org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
                           
                                    emptyEnvelope.getBody().addChild(param.getOMElement(gsn.hydrosys.sensormanager.UnregisterQueryResponse.MY_QNAME,factory));
                                

                         return emptyEnvelope;
                    } catch(org.apache.axis2.databinding.ADBException e){
                        throw org.apache.axis2.AxisFault.makeFault(e);
                    }
                    }
                    
                         private gsn.hydrosys.sensormanager.UnregisterQueryResponse wrapunregisterQuery(){
                                gsn.hydrosys.sensormanager.UnregisterQueryResponse wrappedElement = new gsn.hydrosys.sensormanager.UnregisterQueryResponse();
                                return wrappedElement;
                         }
                    
                    private  org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, gsn.hydrosys.sensormanager.CreateVirtualSensorResponse param, boolean optimizeContent)
                        throws org.apache.axis2.AxisFault{
                      try{
                          org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
                           
                                    emptyEnvelope.getBody().addChild(param.getOMElement(gsn.hydrosys.sensormanager.CreateVirtualSensorResponse.MY_QNAME,factory));
                                

                         return emptyEnvelope;
                    } catch(org.apache.axis2.databinding.ADBException e){
                        throw org.apache.axis2.AxisFault.makeFault(e);
                    }
                    }
                    
                         private gsn.hydrosys.sensormanager.CreateVirtualSensorResponse wrapcreateVirtualSensor(){
                                gsn.hydrosys.sensormanager.CreateVirtualSensorResponse wrappedElement = new gsn.hydrosys.sensormanager.CreateVirtualSensorResponse();
                                return wrappedElement;
                         }
                    


        /**
        *  get the default envelope
        */
        private org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory){
        return factory.getDefaultEnvelope();
        }


        private  java.lang.Object fromOM(
        org.apache.axiom.om.OMElement param,
        java.lang.Class type,
        java.util.Map extraNamespaces) throws org.apache.axis2.AxisFault{

        try {
        
                if (gsn.hydrosys.sensormanager.GetOutputStructure.class.equals(type)){
                
                           return gsn.hydrosys.sensormanager.GetOutputStructure.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (gsn.hydrosys.sensormanager.GetOutputStructureResponse.class.equals(type)){
                
                           return gsn.hydrosys.sensormanager.GetOutputStructureResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (gsn.hydrosys.sensormanager.ListEnabledWrappersResponse.class.equals(type)){
                
                           return gsn.hydrosys.sensormanager.ListEnabledWrappersResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (gsn.hydrosys.sensormanager.DeleteVirtualSensor.class.equals(type)){
                
                           return gsn.hydrosys.sensormanager.DeleteVirtualSensor.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (gsn.hydrosys.sensormanager.DeleteVirtualSensorResponse.class.equals(type)){
                
                           return gsn.hydrosys.sensormanager.DeleteVirtualSensorResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (gsn.hydrosys.sensormanager.RegisterQuery.class.equals(type)){
                
                           return gsn.hydrosys.sensormanager.RegisterQuery.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (gsn.hydrosys.sensormanager.RegisterQueryResponse.class.equals(type)){
                
                           return gsn.hydrosys.sensormanager.RegisterQueryResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (gsn.hydrosys.sensormanager.ListVirtualSensorsResponse.class.equals(type)){
                
                           return gsn.hydrosys.sensormanager.ListVirtualSensorsResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (gsn.hydrosys.sensormanager.ListAvailableWrappersResponse.class.equals(type)){
                
                           return gsn.hydrosys.sensormanager.ListAvailableWrappersResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (gsn.hydrosys.sensormanager.UnregisterQuery.class.equals(type)){
                
                           return gsn.hydrosys.sensormanager.UnregisterQuery.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (gsn.hydrosys.sensormanager.UnregisterQueryResponse.class.equals(type)){
                
                           return gsn.hydrosys.sensormanager.UnregisterQueryResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (gsn.hydrosys.sensormanager.CreateVirtualSensor.class.equals(type)){
                
                           return gsn.hydrosys.sensormanager.CreateVirtualSensor.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (gsn.hydrosys.sensormanager.CreateVirtualSensorResponse.class.equals(type)){
                
                           return gsn.hydrosys.sensormanager.CreateVirtualSensorResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
        } catch (java.lang.Exception e) {
        throw org.apache.axis2.AxisFault.makeFault(e);
        }
           return null;
        }



    

        /**
        *  A utility method that copies the namepaces from the SOAPEnvelope
        */
        private java.util.Map getEnvelopeNamespaces(org.apache.axiom.soap.SOAPEnvelope env){
        java.util.Map returnMap = new java.util.HashMap();
        java.util.Iterator namespaceIterator = env.getAllDeclaredNamespaces();
        while (namespaceIterator.hasNext()) {
        org.apache.axiom.om.OMNamespace ns = (org.apache.axiom.om.OMNamespace) namespaceIterator.next();
        returnMap.put(ns.getPrefix(),ns.getNamespaceURI());
        }
        return returnMap;
        }

        private org.apache.axis2.AxisFault createAxisFault(java.lang.Exception e) {
        org.apache.axis2.AxisFault f;
        Throwable cause = e.getCause();
        if (cause != null) {
            f = new org.apache.axis2.AxisFault(e.getMessage(), cause);
        } else {
            f = new org.apache.axis2.AxisFault(e.getMessage());
        }

        return f;
    }

        }//end of class
    