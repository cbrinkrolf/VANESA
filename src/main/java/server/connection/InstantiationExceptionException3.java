package server.connection;


/**
 * InstantiationExceptionException3.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.4  Built on : Apr 26, 2008 (06:24:30 EDT)
 */



public class InstantiationExceptionException3 extends java.lang.Exception{
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private  VerySimpleDBServiceStub.InstantiationException faultMessage;
    
    public InstantiationExceptionException3() {
        super("InstantiationExceptionException3");
    }
           
    public InstantiationExceptionException3(java.lang.String s) {
       super(s);
    }
    
    public InstantiationExceptionException3(java.lang.String s, java.lang.Throwable ex) {
      super(s, ex);
    }
    
    public void setFaultMessage( VerySimpleDBServiceStub.InstantiationException msg){
       faultMessage = msg;
    }
    
    public  VerySimpleDBServiceStub.InstantiationException getFaultMessage(){
       return faultMessage;
    }
}
    