package server.connection;


/**
 * ClassNotFoundExceptionException0.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.4  Built on : Apr 26, 2008 (06:24:30 EDT)
 */



public class ClassNotFoundExceptionException0 extends java.lang.Exception{
    
    private VerySimpleDBServiceStub.ClassNotFoundException faultMessage;
    
    public ClassNotFoundExceptionException0() {
        super("ClassNotFoundExceptionException0");
    }
           
    public ClassNotFoundExceptionException0(java.lang.String s) {
       super(s);
    }
    
    public ClassNotFoundExceptionException0(java.lang.String s, java.lang.Throwable ex) {
      super(s, ex);
    }
    
    public void setFaultMessage(VerySimpleDBServiceStub.ClassNotFoundException msg){
       faultMessage = msg;
    }
    
    public VerySimpleDBServiceStub.ClassNotFoundException getFaultMessage(){
       return faultMessage;
    }
}
    