package server.connection;


/**
 * VerySimpleDBServiceCallbackHandler.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.4  Built on : Apr 26, 2008 (06:24:30 EDT)
 */



    /**
     *  VerySimpleDBServiceCallbackHandler Callback class, Users can extend this class and implement
     *  their own receiveResult and receiveError methods.
     */
    public abstract class VerySimpleDBServiceCallbackHandler{



    protected Object clientData;

    /**
    * User can pass in any object that needs to be accessed once the NonBlocking
    * Web service call is finished and appropriate method of this CallBack is called.
    * @param clientData Object mechanism by which the user can pass in user data
    * that will be avilable at the time this callback is called.
    */
    public VerySimpleDBServiceCallbackHandler(Object clientData){
        this.clientData = clientData;
    }

    /**
    * Please use this constructor if you don't want to set any clientData
    */
    public VerySimpleDBServiceCallbackHandler(){
        this.clientData = null;
    }

    /**
     * Get the client data
     */

     public Object getClientData() {
        return clientData;
     }

        
           /**
            * auto generated Axis2 call back method for queryDB method
            * override this method for handling normal response from queryDB operation
            */
           public void receiveResultqueryDB(
                     VerySimpleDBServiceStub.QueryDBResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from queryDB operation
           */
            public void receiveErrorqueryDB(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for preparedQueryDB method
            * override this method for handling normal response from preparedQueryDB operation
            */
           public void receiveResultpreparedQueryDB(
                     VerySimpleDBServiceStub.PreparedQueryDBResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from preparedQueryDB operation
           */
            public void receiveErrorpreparedQueryDB(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getColumnNames method
            * override this method for handling normal response from getColumnNames operation
            */
           public void receiveResultgetColumnNames(
                     VerySimpleDBServiceStub.GetColumnNamesResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getColumnNames operation
           */
            public void receiveErrorgetColumnNames(java.lang.Exception e) {
            }
                


    }
    