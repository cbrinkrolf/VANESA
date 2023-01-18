package server.connection;


/**
 * IllegalAccessExceptionException2.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.4  Built on : Apr 26, 2008 (06:24:30 EDT)
 */



public class IllegalAccessExceptionException2 extends java.lang.Exception{
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private VerySimpleDBServiceStub.IllegalAccessException faultMessage;
    
    public IllegalAccessExceptionException2() {
        super("IllegalAccessExceptionException2");
    }
           
    public IllegalAccessExceptionException2(java.lang.String s) {
       super(s);
    }
    
    public IllegalAccessExceptionException2(java.lang.String s, java.lang.Throwable ex) {
      super(s, ex);
    }
    
    public void setFaultMessage(VerySimpleDBServiceStub.IllegalAccessException msg){
       faultMessage = msg;
    }
    
    public VerySimpleDBServiceStub.IllegalAccessException getFaultMessage(){
       return faultMessage;
    }
}
    