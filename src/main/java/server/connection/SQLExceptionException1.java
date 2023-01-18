package server.connection;


/**
 * SQLExceptionException1.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.4  Built on : Apr 26, 2008 (06:24:30 EDT)
 */



public class SQLExceptionException1 extends java.lang.Exception{
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private  VerySimpleDBServiceStub.SQLExceptionE faultMessage;
    
    public SQLExceptionException1() {
        super("SQLExceptionException1");
    }
           
    public SQLExceptionException1(java.lang.String s) {
       super(s);
    }
    
    public SQLExceptionException1(java.lang.String s, java.lang.Throwable ex) {
      super(s, ex);
    }
    
    public void setFaultMessage( VerySimpleDBServiceStub.SQLExceptionE msg){
       faultMessage = msg;
    }
    
    public  VerySimpleDBServiceStub.SQLExceptionE getFaultMessage(){
       return faultMessage;
    }
}
    