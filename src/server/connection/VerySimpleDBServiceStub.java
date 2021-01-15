package server.connection;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMNamespace;

/**
 * VerySimpleDBServiceStub.java
 *
 * This file was auto-generated from WSDL by the Apache Axis2 version: 1.4 Built
 * on : Apr 26, 2008 (06:24:30 EDT)
 */

/*
 * VerySimpleDBServiceStub java implementation
 */

public class VerySimpleDBServiceStub extends org.apache.axis2.client.Stub {
	protected org.apache.axis2.description.AxisOperation[] _operations;

	// hashmaps to keep the fault mapping
	private java.util.HashMap<javax.xml.namespace.QName, String> faultExceptionNameMap = new java.util.HashMap<javax.xml.namespace.QName, String>();
	private java.util.HashMap<javax.xml.namespace.QName, String> faultExceptionClassNameMap = new java.util.HashMap<javax.xml.namespace.QName, String>();
	private java.util.HashMap<javax.xml.namespace.QName, String> faultMessageMap = new java.util.HashMap<javax.xml.namespace.QName, String>();

	private static int counter = 0;

	private static synchronized String getUniqueSuffix() {
		// reset the counter if it is greater than 99999
		if (counter > 99999) {
			counter = 0;
		}
		counter = counter + 1;
		return Long.toString(System.currentTimeMillis()) + "_" + counter;
	}

	private void populateAxisService() throws org.apache.axis2.AxisFault {

		// creating the Service with a unique name
		_service = new org.apache.axis2.description.AxisService("VerySimpleDBService" + getUniqueSuffix());
		addAnonymousOperations();

		// creating the operations
		org.apache.axis2.description.AxisOperation __operation;

		_operations = new org.apache.axis2.description.AxisOperation[3];

		__operation = new org.apache.axis2.description.OutInAxisOperation();

		__operation.setName(new javax.xml.namespace.QName("http://axis.de", "queryDB"));
		_service.addOperation(__operation);

		_operations[0] = __operation;

		__operation = new org.apache.axis2.description.OutInAxisOperation();

		__operation.setName(new javax.xml.namespace.QName("http://axis.de", "preparedQueryDB"));
		_service.addOperation(__operation);

		_operations[1] = __operation;

		__operation = new org.apache.axis2.description.OutInAxisOperation();

		__operation.setName(new javax.xml.namespace.QName("http://axis.de", "getColumnNames"));
		_service.addOperation(__operation);

		_operations[2] = __operation;

	}

	// populates the faults
	private void populateFaults() {

		faultExceptionNameMap.put(new javax.xml.namespace.QName("http://axis.de", "ClassNotFoundException"),
				" ClassNotFoundExceptionException0");
		faultExceptionClassNameMap.put(new javax.xml.namespace.QName("http://axis.de", "ClassNotFoundException"),
				" ClassNotFoundExceptionException0");
		faultMessageMap.put(new javax.xml.namespace.QName("http://axis.de", "ClassNotFoundException"),
				" VerySimpleDBServiceStub$ClassNotFoundException");

		faultExceptionNameMap.put(new javax.xml.namespace.QName("http://axis.de", "SQLException"),
				" SQLExceptionException1");
		faultExceptionClassNameMap.put(new javax.xml.namespace.QName("http://axis.de", "SQLException"),
				" SQLExceptionException1");
		faultMessageMap.put(new javax.xml.namespace.QName("http://axis.de", "SQLException"),
				" VerySimpleDBServiceStub$SQLExceptionE");

		faultExceptionNameMap.put(new javax.xml.namespace.QName("http://axis.de", "IllegalAccessException"),
				" IllegalAccessExceptionException2");
		faultExceptionClassNameMap.put(new javax.xml.namespace.QName("http://axis.de", "IllegalAccessException"),
				" IllegalAccessExceptionException2");
		faultMessageMap.put(new javax.xml.namespace.QName("http://axis.de", "IllegalAccessException"),
				" VerySimpleDBServiceStub$IllegalAccessException");

		faultExceptionNameMap.put(new javax.xml.namespace.QName("http://axis.de", "InstantiationException"),
				" InstantiationExceptionException3");
		faultExceptionClassNameMap.put(new javax.xml.namespace.QName("http://axis.de", "InstantiationException"),
				" InstantiationExceptionException3");
		faultMessageMap.put(new javax.xml.namespace.QName("http://axis.de", "InstantiationException"),
				" VerySimpleDBServiceStub$InstantiationException");

		faultExceptionNameMap.put(new javax.xml.namespace.QName("http://axis.de", "ClassNotFoundException"),
				" ClassNotFoundExceptionException0");
		faultExceptionClassNameMap.put(new javax.xml.namespace.QName("http://axis.de", "ClassNotFoundException"),
				" ClassNotFoundExceptionException0");
		faultMessageMap.put(new javax.xml.namespace.QName("http://axis.de", "ClassNotFoundException"),
				" VerySimpleDBServiceStub$ClassNotFoundException");

		faultExceptionNameMap.put(new javax.xml.namespace.QName("http://axis.de", "SQLException"),
				" SQLExceptionException1");
		faultExceptionClassNameMap.put(new javax.xml.namespace.QName("http://axis.de", "SQLException"),
				" SQLExceptionException1");
		faultMessageMap.put(new javax.xml.namespace.QName("http://axis.de", "SQLException"),
				" VerySimpleDBServiceStub$SQLExceptionE");

		faultExceptionNameMap.put(new javax.xml.namespace.QName("http://axis.de", "IllegalAccessException"),
				" IllegalAccessExceptionException2");
		faultExceptionClassNameMap.put(new javax.xml.namespace.QName("http://axis.de", "IllegalAccessException"),
				" IllegalAccessExceptionException2");
		faultMessageMap.put(new javax.xml.namespace.QName("http://axis.de", "IllegalAccessException"),
				" VerySimpleDBServiceStub$IllegalAccessException");

		faultExceptionNameMap.put(new javax.xml.namespace.QName("http://axis.de", "InstantiationException"),
				" InstantiationExceptionException3");
		faultExceptionClassNameMap.put(new javax.xml.namespace.QName("http://axis.de", "InstantiationException"),
				" InstantiationExceptionException3");
		faultMessageMap.put(new javax.xml.namespace.QName("http://axis.de", "InstantiationException"),
				" VerySimpleDBServiceStub$InstantiationException");

	}

	/**
	 * Constructor that takes in a configContext
	 */

	public VerySimpleDBServiceStub(org.apache.axis2.context.ConfigurationContext configurationContext,
			java.lang.String targetEndpoint) throws org.apache.axis2.AxisFault {
		this(configurationContext, targetEndpoint, false);
	}

	/**
	 * Constructor that takes in a configContext and useseperate listner
	 */
	public VerySimpleDBServiceStub(org.apache.axis2.context.ConfigurationContext configurationContext,
			java.lang.String targetEndpoint, boolean useSeparateListener) throws org.apache.axis2.AxisFault {
		// To populate AxisService
		populateAxisService();
		populateFaults();

		_serviceClient = new org.apache.axis2.client.ServiceClient(configurationContext, _service);

		configurationContext = _serviceClient.getServiceContext().getConfigurationContext();

		_serviceClient.getOptions().setTo(new org.apache.axis2.addressing.EndpointReference(targetEndpoint));
		_serviceClient.getOptions().setUseSeparateListener(useSeparateListener);

		// Set the soap version
		_serviceClient.getOptions()
				.setSoapVersionURI(org.apache.axiom.soap.SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI);

	}

	/**
	 * Default Constructor
	 */
	public VerySimpleDBServiceStub(org.apache.axis2.context.ConfigurationContext configurationContext)
			throws org.apache.axis2.AxisFault {

		this(configurationContext,
				"http://129.70.142.193:8080/axis2/services/VerySimpleDBService.VerySimpleDBServiceHttpSoap12Endpoint");

	}

	/**
	 * Default Constructor
	 */
	public VerySimpleDBServiceStub() throws org.apache.axis2.AxisFault {

		this("http://129.70.142.193:8080/axis2/services/VerySimpleDBService.VerySimpleDBServiceHttpSoap12Endpoint");

	}

	/**
	 * Constructor taking the target endpoint
	 */
	public VerySimpleDBServiceStub(java.lang.String targetEndpoint) throws org.apache.axis2.AxisFault {
		this(null, targetEndpoint);
	}

	/**
	 * Auto generated method signature
	 * 
	 * @see VerySimpleDBService#queryDB
	 * @param queryDB1
	 * 
	 * @throws ClassNotFoundExceptionException0 :
	 * @throws SQLExceptionException1           :
	 * @throws IllegalAccessExceptionException2 :
	 * @throws InstantiationExceptionException3 :
	 */

	public VerySimpleDBServiceStub.QueryDBResponse queryDB(

			VerySimpleDBServiceStub.QueryDB queryDB1)

			throws java.rmi.RemoteException

			, ClassNotFoundExceptionException0, SQLExceptionException1, IllegalAccessExceptionException2,
			InstantiationExceptionException3 {
		org.apache.axis2.context.MessageContext _messageContext = null;
		try {
			org.apache.axis2.client.OperationClient _operationClient = _serviceClient
					.createClient(_operations[0].getName());
			_operationClient.getOptions().setAction("urn:queryDB");
			_operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);

			addPropertyToOperationClient(_operationClient,
					org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR, "&");

			// create a message context
			_messageContext = new org.apache.axis2.context.MessageContext();

			// create SOAP envelope with that payload
			org.apache.axiom.soap.SOAPEnvelope env = null;

			env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()), queryDB1,
					optimizeContent(new javax.xml.namespace.QName("http://axis.de", "queryDB")));

			// adding SOAP soap_headers
			_serviceClient.addHeadersToEnvelope(env);
			// set the message context with that soap envelope
			_messageContext.setEnvelope(env);

			// add the message contxt to the operation client
			_operationClient.addMessageContext(_messageContext);

			// execute the operation client
			_operationClient.execute(true);

			org.apache.axis2.context.MessageContext _returnMessageContext = _operationClient
					.getMessageContext(org.apache.axis2.wsdl.WSDLConstants.MESSAGE_LABEL_IN_VALUE);
			org.apache.axiom.soap.SOAPEnvelope _returnEnv = _returnMessageContext.getEnvelope();

			java.lang.Object object = fromOM(_returnEnv.getBody().getFirstElement(),
					VerySimpleDBServiceStub.QueryDBResponse.class, getEnvelopeNamespaces(_returnEnv));

			return (VerySimpleDBServiceStub.QueryDBResponse) object;

		} catch (org.apache.axis2.AxisFault f) {

			org.apache.axiom.om.OMElement faultElt = f.getDetail();
			if (faultElt != null) {
				if (faultExceptionNameMap.containsKey(faultElt.getQName())) {
					// make the fault by reflection
					try {
						java.lang.String exceptionClassName = (java.lang.String) faultExceptionClassNameMap
								.get(faultElt.getQName());
						java.lang.Class<?> exceptionClass = java.lang.Class.forName(exceptionClassName);
						java.lang.Exception ex = (java.lang.Exception) exceptionClass.newInstance();
						// message class
						java.lang.String messageClassName = (java.lang.String) faultMessageMap.get(faultElt.getQName());
						java.lang.Class<?> messageClass = java.lang.Class.forName(messageClassName);
						java.lang.Object messageObject = fromOM(faultElt, messageClass, null);
						java.lang.reflect.Method m = exceptionClass.getMethod("setFaultMessage",
								new java.lang.Class[] { messageClass });
						m.invoke(ex, new java.lang.Object[] { messageObject });

						if (ex instanceof ClassNotFoundExceptionException0) {
							throw (ClassNotFoundExceptionException0) ex;
						}

						if (ex instanceof SQLExceptionException1) {
							throw (SQLExceptionException1) ex;
						}

						if (ex instanceof IllegalAccessExceptionException2) {
							throw (IllegalAccessExceptionException2) ex;
						}

						if (ex instanceof InstantiationExceptionException3) {
							throw (InstantiationExceptionException3) ex;
						}

						throw new java.rmi.RemoteException(ex.getMessage(), ex);
					} catch (java.lang.ClassCastException e) {
						// we cannot intantiate the class - throw the original Axis fault
						throw f;
					} catch (java.lang.ClassNotFoundException e) {
						// we cannot intantiate the class - throw the original Axis fault
						throw f;
					} catch (java.lang.NoSuchMethodException e) {
						// we cannot intantiate the class - throw the original Axis fault
						throw f;
					} catch (java.lang.reflect.InvocationTargetException e) {
						// we cannot intantiate the class - throw the original Axis fault
						throw f;
					} catch (java.lang.IllegalAccessException e) {
						// we cannot intantiate the class - throw the original Axis fault
						throw f;
					} catch (java.lang.InstantiationException e) {
						// we cannot intantiate the class - throw the original Axis fault
						throw f;
					}
				} else {
					throw f;
				}
			} else {
				throw f;
			}
		} finally {
			_messageContext.getTransportOut().getSender().cleanup(_messageContext);
		}
	}

	/**
	 * Auto generated method signature for Asynchronous Invocations
	 * 
	 * @see VerySimpleDBService#startqueryDB
	 * @param queryDB1
	 * 
	 */
	public void startqueryDB(

			VerySimpleDBServiceStub.QueryDB queryDB1,

			final VerySimpleDBServiceCallbackHandler callback)

			throws java.rmi.RemoteException {

		org.apache.axis2.client.OperationClient _operationClient = _serviceClient
				.createClient(_operations[0].getName());
		_operationClient.getOptions().setAction("urn:queryDB");
		_operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);

		addPropertyToOperationClient(_operationClient,
				org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR, "&");

		// create SOAP envelope with that payload
		org.apache.axiom.soap.SOAPEnvelope env = null;
		final org.apache.axis2.context.MessageContext _messageContext = new org.apache.axis2.context.MessageContext();

		// Style is Doc.

		env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()), queryDB1,
				optimizeContent(new javax.xml.namespace.QName("http://axis.de", "queryDB")));

		// adding SOAP soap_headers
		_serviceClient.addHeadersToEnvelope(env);
		// create message context with that soap envelope
		_messageContext.setEnvelope(env);

		// add the message context to the operation client
		_operationClient.addMessageContext(_messageContext);

		_operationClient.setCallback(new org.apache.axis2.client.async.AxisCallback() {
			public void onMessage(org.apache.axis2.context.MessageContext resultContext) {
				try {
					org.apache.axiom.soap.SOAPEnvelope resultEnv = resultContext.getEnvelope();

					java.lang.Object object = fromOM(resultEnv.getBody().getFirstElement(),
							VerySimpleDBServiceStub.QueryDBResponse.class, getEnvelopeNamespaces(resultEnv));
					callback.receiveResultqueryDB((VerySimpleDBServiceStub.QueryDBResponse) object);

				} catch (org.apache.axis2.AxisFault e) {
					callback.receiveErrorqueryDB(e);
				}
			}

			public void onError(java.lang.Exception error) {
				if (error instanceof org.apache.axis2.AxisFault) {
					org.apache.axis2.AxisFault f = (org.apache.axis2.AxisFault) error;
					org.apache.axiom.om.OMElement faultElt = f.getDetail();
					if (faultElt != null) {
						if (faultExceptionNameMap.containsKey(faultElt.getQName())) {
							// make the fault by reflection
							try {
								java.lang.String exceptionClassName = (java.lang.String) faultExceptionClassNameMap
										.get(faultElt.getQName());
								java.lang.Class<?> exceptionClass = java.lang.Class.forName(exceptionClassName);
								java.lang.Exception ex = (java.lang.Exception) exceptionClass.newInstance();
								// message class
								java.lang.String messageClassName = (java.lang.String) faultMessageMap
										.get(faultElt.getQName());
								java.lang.Class<?> messageClass = java.lang.Class.forName(messageClassName);
								java.lang.Object messageObject = fromOM(faultElt, messageClass, null);
								java.lang.reflect.Method m = exceptionClass.getMethod("setFaultMessage",
										new java.lang.Class[] { messageClass });
								m.invoke(ex, new java.lang.Object[] { messageObject });

								if (ex instanceof ClassNotFoundExceptionException0) {
									callback.receiveErrorqueryDB((ClassNotFoundExceptionException0) ex);
									return;
								}

								if (ex instanceof SQLExceptionException1) {
									callback.receiveErrorqueryDB((SQLExceptionException1) ex);
									return;
								}

								if (ex instanceof IllegalAccessExceptionException2) {
									callback.receiveErrorqueryDB((IllegalAccessExceptionException2) ex);
									return;
								}

								if (ex instanceof InstantiationExceptionException3) {
									callback.receiveErrorqueryDB((InstantiationExceptionException3) ex);
									return;
								}

								callback.receiveErrorqueryDB(new java.rmi.RemoteException(ex.getMessage(), ex));
							} catch (java.lang.ClassCastException e) {
								// we cannot intantiate the class - throw the original Axis fault
								callback.receiveErrorqueryDB(f);
							} catch (java.lang.ClassNotFoundException e) {
								// we cannot intantiate the class - throw the original Axis fault
								callback.receiveErrorqueryDB(f);
							} catch (java.lang.NoSuchMethodException e) {
								// we cannot intantiate the class - throw the original Axis fault
								callback.receiveErrorqueryDB(f);
							} catch (java.lang.reflect.InvocationTargetException e) {
								// we cannot intantiate the class - throw the original Axis fault
								callback.receiveErrorqueryDB(f);
							} catch (java.lang.IllegalAccessException e) {
								// we cannot intantiate the class - throw the original Axis fault
								callback.receiveErrorqueryDB(f);
							} catch (java.lang.InstantiationException e) {
								// we cannot intantiate the class - throw the original Axis fault
								callback.receiveErrorqueryDB(f);
							} catch (org.apache.axis2.AxisFault e) {
								// we cannot intantiate the class - throw the original Axis fault
								callback.receiveErrorqueryDB(f);
							}
						} else {
							callback.receiveErrorqueryDB(f);
						}
					} else {
						callback.receiveErrorqueryDB(f);
					}
				} else {
					callback.receiveErrorqueryDB(error);
				}
			}

			public void onFault(org.apache.axis2.context.MessageContext faultContext) {
				org.apache.axis2.AxisFault fault = org.apache.axis2.util.Utils
						.getInboundFaultFromMessageContext(faultContext);
				onError(fault);
			}

			public void onComplete() {
				try {
					_messageContext.getTransportOut().getSender().cleanup(_messageContext);
				} catch (org.apache.axis2.AxisFault axisFault) {
					callback.receiveErrorqueryDB(axisFault);
				}
			}
		});

		org.apache.axis2.util.CallbackReceiver _callbackReceiver = null;
		if (_operations[0].getMessageReceiver() == null && _operationClient.getOptions().isUseSeparateListener()) {
			_callbackReceiver = new org.apache.axis2.util.CallbackReceiver();
			_operations[0].setMessageReceiver(_callbackReceiver);
		}

		// execute the operation client
		_operationClient.execute(false);

	}

	/**
	 * Auto generated method signature
	 * 
	 * @see VerySimpleDBService#preparedQueryDB
	 * @param preparedQueryDB3
	 * 
	 * @throws ClassNotFoundExceptionException0 :
	 * @throws SQLExceptionException1           :
	 * @throws IllegalAccessExceptionException2 :
	 * @throws InstantiationExceptionException3 :
	 */

	public VerySimpleDBServiceStub.PreparedQueryDBResponse preparedQueryDB(

			VerySimpleDBServiceStub.PreparedQueryDB preparedQueryDB3)

			throws java.rmi.RemoteException

			, ClassNotFoundExceptionException0, SQLExceptionException1, IllegalAccessExceptionException2,
			InstantiationExceptionException3 {
		org.apache.axis2.context.MessageContext _messageContext = null;
		try {
			org.apache.axis2.client.OperationClient _operationClient = _serviceClient
					.createClient(_operations[1].getName());
			_operationClient.getOptions().setAction("urn:preparedQueryDB");
			_operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);

			addPropertyToOperationClient(_operationClient,
					org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR, "&");

			// create a message context
			_messageContext = new org.apache.axis2.context.MessageContext();

			// create SOAP envelope with that payload
			org.apache.axiom.soap.SOAPEnvelope env = null;

			env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()), preparedQueryDB3,
					optimizeContent(new javax.xml.namespace.QName("http://axis.de", "preparedQueryDB")));

			// adding SOAP soap_headers
			_serviceClient.addHeadersToEnvelope(env);
			// set the message context with that soap envelope
			_messageContext.setEnvelope(env);

			// add the message contxt to the operation client
			_operationClient.addMessageContext(_messageContext);

			// execute the operation client
			_operationClient.execute(true);

			org.apache.axis2.context.MessageContext _returnMessageContext = _operationClient
					.getMessageContext(org.apache.axis2.wsdl.WSDLConstants.MESSAGE_LABEL_IN_VALUE);
			org.apache.axiom.soap.SOAPEnvelope _returnEnv = _returnMessageContext.getEnvelope();

			java.lang.Object object = fromOM(_returnEnv.getBody().getFirstElement(),
					VerySimpleDBServiceStub.PreparedQueryDBResponse.class, getEnvelopeNamespaces(_returnEnv));

			return (VerySimpleDBServiceStub.PreparedQueryDBResponse) object;

		} catch (org.apache.axis2.AxisFault f) {

			org.apache.axiom.om.OMElement faultElt = f.getDetail();
			if (faultElt != null) {
				if (faultExceptionNameMap.containsKey(faultElt.getQName())) {
					// make the fault by reflection
					try {
						java.lang.String exceptionClassName = (java.lang.String) faultExceptionClassNameMap
								.get(faultElt.getQName());
						java.lang.Class<?> exceptionClass = java.lang.Class.forName(exceptionClassName);
						java.lang.Exception ex = (java.lang.Exception) exceptionClass.newInstance();
						// message class
						java.lang.String messageClassName = (java.lang.String) faultMessageMap.get(faultElt.getQName());
						java.lang.Class<?> messageClass = java.lang.Class.forName(messageClassName);
						java.lang.Object messageObject = fromOM(faultElt, messageClass, null);
						java.lang.reflect.Method m = exceptionClass.getMethod("setFaultMessage",
								new java.lang.Class[] { messageClass });
						m.invoke(ex, new java.lang.Object[] { messageObject });

						if (ex instanceof ClassNotFoundExceptionException0) {
							throw (ClassNotFoundExceptionException0) ex;
						}

						if (ex instanceof SQLExceptionException1) {
							throw (SQLExceptionException1) ex;
						}

						if (ex instanceof IllegalAccessExceptionException2) {
							throw (IllegalAccessExceptionException2) ex;
						}

						if (ex instanceof InstantiationExceptionException3) {
							throw (InstantiationExceptionException3) ex;
						}

						throw new java.rmi.RemoteException(ex.getMessage(), ex);
					} catch (java.lang.ClassCastException e) {
						// we cannot intantiate the class - throw the original Axis fault
						throw f;
					} catch (java.lang.ClassNotFoundException e) {
						// we cannot intantiate the class - throw the original Axis fault
						throw f;
					} catch (java.lang.NoSuchMethodException e) {
						// we cannot intantiate the class - throw the original Axis fault
						throw f;
					} catch (java.lang.reflect.InvocationTargetException e) {
						// we cannot intantiate the class - throw the original Axis fault
						throw f;
					} catch (java.lang.IllegalAccessException e) {
						// we cannot intantiate the class - throw the original Axis fault
						throw f;
					} catch (java.lang.InstantiationException e) {
						// we cannot intantiate the class - throw the original Axis fault
						throw f;
					}
				} else {
					throw f;
				}
			} else {
				throw f;
			}
		} finally {
			_messageContext.getTransportOut().getSender().cleanup(_messageContext);
		}
	}

	/**
	 * Auto generated method signature for Asynchronous Invocations
	 * 
	 * @see VerySimpleDBService#startpreparedQueryDB
	 * @param preparedQueryDB3
	 * 
	 */
	public void startpreparedQueryDB(

			VerySimpleDBServiceStub.PreparedQueryDB preparedQueryDB3,

			final VerySimpleDBServiceCallbackHandler callback)

			throws java.rmi.RemoteException {

		org.apache.axis2.client.OperationClient _operationClient = _serviceClient
				.createClient(_operations[1].getName());
		_operationClient.getOptions().setAction("urn:preparedQueryDB");
		_operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);

		addPropertyToOperationClient(_operationClient,
				org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR, "&");

		// create SOAP envelope with that payload
		org.apache.axiom.soap.SOAPEnvelope env = null;
		final org.apache.axis2.context.MessageContext _messageContext = new org.apache.axis2.context.MessageContext();

		// Style is Doc.

		env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()), preparedQueryDB3,
				optimizeContent(new javax.xml.namespace.QName("http://axis.de", "preparedQueryDB")));

		// adding SOAP soap_headers
		_serviceClient.addHeadersToEnvelope(env);
		// create message context with that soap envelope
		_messageContext.setEnvelope(env);

		// add the message context to the operation client
		_operationClient.addMessageContext(_messageContext);

		_operationClient.setCallback(new org.apache.axis2.client.async.AxisCallback() {
			public void onMessage(org.apache.axis2.context.MessageContext resultContext) {
				try {
					org.apache.axiom.soap.SOAPEnvelope resultEnv = resultContext.getEnvelope();

					java.lang.Object object = fromOM(resultEnv.getBody().getFirstElement(),
							VerySimpleDBServiceStub.PreparedQueryDBResponse.class, getEnvelopeNamespaces(resultEnv));
					callback.receiveResultpreparedQueryDB((VerySimpleDBServiceStub.PreparedQueryDBResponse) object);

				} catch (org.apache.axis2.AxisFault e) {
					callback.receiveErrorpreparedQueryDB(e);
				}
			}

			public void onError(java.lang.Exception error) {
				if (error instanceof org.apache.axis2.AxisFault) {
					org.apache.axis2.AxisFault f = (org.apache.axis2.AxisFault) error;
					org.apache.axiom.om.OMElement faultElt = f.getDetail();
					if (faultElt != null) {
						if (faultExceptionNameMap.containsKey(faultElt.getQName())) {
							// make the fault by reflection
							try {
								java.lang.String exceptionClassName = (java.lang.String) faultExceptionClassNameMap
										.get(faultElt.getQName());
								java.lang.Class<?> exceptionClass = java.lang.Class.forName(exceptionClassName);
								java.lang.Exception ex = (java.lang.Exception) exceptionClass.newInstance();
								// message class
								java.lang.String messageClassName = (java.lang.String) faultMessageMap
										.get(faultElt.getQName());
								java.lang.Class<?> messageClass = java.lang.Class.forName(messageClassName);
								java.lang.Object messageObject = fromOM(faultElt, messageClass, null);
								java.lang.reflect.Method m = exceptionClass.getMethod("setFaultMessage",
										new java.lang.Class[] { messageClass });
								m.invoke(ex, new java.lang.Object[] { messageObject });

								if (ex instanceof ClassNotFoundExceptionException0) {
									callback.receiveErrorpreparedQueryDB((ClassNotFoundExceptionException0) ex);
									return;
								}

								if (ex instanceof SQLExceptionException1) {
									callback.receiveErrorpreparedQueryDB((SQLExceptionException1) ex);
									return;
								}

								if (ex instanceof IllegalAccessExceptionException2) {
									callback.receiveErrorpreparedQueryDB((IllegalAccessExceptionException2) ex);
									return;
								}

								if (ex instanceof InstantiationExceptionException3) {
									callback.receiveErrorpreparedQueryDB((InstantiationExceptionException3) ex);
									return;
								}

								callback.receiveErrorpreparedQueryDB(new java.rmi.RemoteException(ex.getMessage(), ex));
							} catch (java.lang.ClassCastException e) {
								// we cannot intantiate the class - throw the original Axis fault
								callback.receiveErrorpreparedQueryDB(f);
							} catch (java.lang.ClassNotFoundException e) {
								// we cannot intantiate the class - throw the original Axis fault
								callback.receiveErrorpreparedQueryDB(f);
							} catch (java.lang.NoSuchMethodException e) {
								// we cannot intantiate the class - throw the original Axis fault
								callback.receiveErrorpreparedQueryDB(f);
							} catch (java.lang.reflect.InvocationTargetException e) {
								// we cannot intantiate the class - throw the original Axis fault
								callback.receiveErrorpreparedQueryDB(f);
							} catch (java.lang.IllegalAccessException e) {
								// we cannot intantiate the class - throw the original Axis fault
								callback.receiveErrorpreparedQueryDB(f);
							} catch (java.lang.InstantiationException e) {
								// we cannot intantiate the class - throw the original Axis fault
								callback.receiveErrorpreparedQueryDB(f);
							} catch (org.apache.axis2.AxisFault e) {
								// we cannot intantiate the class - throw the original Axis fault
								callback.receiveErrorpreparedQueryDB(f);
							}
						} else {
							callback.receiveErrorpreparedQueryDB(f);
						}
					} else {
						callback.receiveErrorpreparedQueryDB(f);
					}
				} else {
					callback.receiveErrorpreparedQueryDB(error);
				}
			}

			public void onFault(org.apache.axis2.context.MessageContext faultContext) {
				org.apache.axis2.AxisFault fault = org.apache.axis2.util.Utils
						.getInboundFaultFromMessageContext(faultContext);
				onError(fault);
			}

			public void onComplete() {
				try {
					_messageContext.getTransportOut().getSender().cleanup(_messageContext);
				} catch (org.apache.axis2.AxisFault axisFault) {
					callback.receiveErrorpreparedQueryDB(axisFault);
				}
			}
		});

		org.apache.axis2.util.CallbackReceiver _callbackReceiver = null;
		if (_operations[1].getMessageReceiver() == null && _operationClient.getOptions().isUseSeparateListener()) {
			_callbackReceiver = new org.apache.axis2.util.CallbackReceiver();
			_operations[1].setMessageReceiver(_callbackReceiver);
		}

		// execute the operation client
		_operationClient.execute(false);

	}

	/**
	 * Auto generated method signature
	 * 
	 * @see VerySimpleDBService#getColumnNames
	 */

	public VerySimpleDBServiceStub.GetColumnNamesResponse getColumnNames(

	)

			throws java.rmi.RemoteException

	{
		org.apache.axis2.context.MessageContext _messageContext = null;
		try {
			org.apache.axis2.client.OperationClient _operationClient = _serviceClient
					.createClient(_operations[2].getName());
			_operationClient.getOptions().setAction("urn:getColumnNames");
			_operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);

			addPropertyToOperationClient(_operationClient,
					org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR, "&");

			// create a message context
			_messageContext = new org.apache.axis2.context.MessageContext();

			// create SOAP envelope with that payload
			org.apache.axiom.soap.SOAPEnvelope env = null;

			// Style is taken to be "document". No input parameters
			// according to the WS-Basic profile in this case we have to send an empty soap
			// message
			org.apache.axiom.soap.SOAPFactory factory = getFactory(_operationClient.getOptions().getSoapVersionURI());
			env = factory.getDefaultEnvelope();

			// adding SOAP soap_headers
			_serviceClient.addHeadersToEnvelope(env);
			// set the message context with that soap envelope
			_messageContext.setEnvelope(env);

			// add the message contxt to the operation client
			_operationClient.addMessageContext(_messageContext);

			// execute the operation client
			_operationClient.execute(true);

			org.apache.axis2.context.MessageContext _returnMessageContext = _operationClient
					.getMessageContext(org.apache.axis2.wsdl.WSDLConstants.MESSAGE_LABEL_IN_VALUE);
			org.apache.axiom.soap.SOAPEnvelope _returnEnv = _returnMessageContext.getEnvelope();

			java.lang.Object object = fromOM(_returnEnv.getBody().getFirstElement(),
					VerySimpleDBServiceStub.GetColumnNamesResponse.class, getEnvelopeNamespaces(_returnEnv));

			return (VerySimpleDBServiceStub.GetColumnNamesResponse) object;

		} catch (org.apache.axis2.AxisFault f) {

			org.apache.axiom.om.OMElement faultElt = f.getDetail();
			if (faultElt != null) {
				if (faultExceptionNameMap.containsKey(faultElt.getQName())) {
					// make the fault by reflection
					try {
						java.lang.String exceptionClassName = (java.lang.String) faultExceptionClassNameMap
								.get(faultElt.getQName());
						java.lang.Class<?> exceptionClass = java.lang.Class.forName(exceptionClassName);
						java.lang.Exception ex = (java.lang.Exception) exceptionClass.newInstance();
						// message class
						java.lang.String messageClassName = (java.lang.String) faultMessageMap.get(faultElt.getQName());
						java.lang.Class<?> messageClass = java.lang.Class.forName(messageClassName);
						java.lang.Object messageObject = fromOM(faultElt, messageClass, null);
						java.lang.reflect.Method m = exceptionClass.getMethod("setFaultMessage",
								new java.lang.Class[] { messageClass });
						m.invoke(ex, new java.lang.Object[] { messageObject });

						throw new java.rmi.RemoteException(ex.getMessage(), ex);
					} catch (java.lang.ClassCastException e) {
						// we cannot intantiate the class - throw the original Axis fault
						throw f;
					} catch (java.lang.ClassNotFoundException e) {
						// we cannot intantiate the class - throw the original Axis fault
						throw f;
					} catch (java.lang.NoSuchMethodException e) {
						// we cannot intantiate the class - throw the original Axis fault
						throw f;
					} catch (java.lang.reflect.InvocationTargetException e) {
						// we cannot intantiate the class - throw the original Axis fault
						throw f;
					} catch (java.lang.IllegalAccessException e) {
						// we cannot intantiate the class - throw the original Axis fault
						throw f;
					} catch (java.lang.InstantiationException e) {
						// we cannot intantiate the class - throw the original Axis fault
						throw f;
					}
				} else {
					throw f;
				}
			} else {
				throw f;
			}
		} finally {
			_messageContext.getTransportOut().getSender().cleanup(_messageContext);
		}
	}

	/**
	 * Auto generated method signature for Asynchronous Invocations
	 * 
	 * @see VerySimpleDBService#startgetColumnNames
	 */
	public void startgetColumnNames(

			final VerySimpleDBServiceCallbackHandler callback)

			throws java.rmi.RemoteException {

		org.apache.axis2.client.OperationClient _operationClient = _serviceClient
				.createClient(_operations[2].getName());
		_operationClient.getOptions().setAction("urn:getColumnNames");
		_operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);

		addPropertyToOperationClient(_operationClient,
				org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR, "&");

		// create SOAP envelope with that payload
		org.apache.axiom.soap.SOAPEnvelope env = null;
		final org.apache.axis2.context.MessageContext _messageContext = new org.apache.axis2.context.MessageContext();

		// Style is taken to be "document". No input parameters
		// according to the WS-Basic profile in this case we have to send an empty soap
		// message
		org.apache.axiom.soap.SOAPFactory factory = getFactory(_operationClient.getOptions().getSoapVersionURI());
		env = factory.getDefaultEnvelope();

		// adding SOAP soap_headers
		_serviceClient.addHeadersToEnvelope(env);
		// create message context with that soap envelope
		_messageContext.setEnvelope(env);

		// add the message context to the operation client
		_operationClient.addMessageContext(_messageContext);

		_operationClient.setCallback(new org.apache.axis2.client.async.AxisCallback() {
			public void onMessage(org.apache.axis2.context.MessageContext resultContext) {
				try {
					org.apache.axiom.soap.SOAPEnvelope resultEnv = resultContext.getEnvelope();

					java.lang.Object object = fromOM(resultEnv.getBody().getFirstElement(),
							VerySimpleDBServiceStub.GetColumnNamesResponse.class, getEnvelopeNamespaces(resultEnv));
					callback.receiveResultgetColumnNames((VerySimpleDBServiceStub.GetColumnNamesResponse) object);

				} catch (org.apache.axis2.AxisFault e) {
					callback.receiveErrorgetColumnNames(e);
				}
			}

			public void onError(java.lang.Exception error) {
				if (error instanceof org.apache.axis2.AxisFault) {
					org.apache.axis2.AxisFault f = (org.apache.axis2.AxisFault) error;
					org.apache.axiom.om.OMElement faultElt = f.getDetail();
					if (faultElt != null) {
						if (faultExceptionNameMap.containsKey(faultElt.getQName())) {
							// make the fault by reflection
							try {
								java.lang.String exceptionClassName = (java.lang.String) faultExceptionClassNameMap
										.get(faultElt.getQName());
								java.lang.Class<?> exceptionClass = java.lang.Class.forName(exceptionClassName);
								java.lang.Exception ex = (java.lang.Exception) exceptionClass.newInstance();
								// message class
								java.lang.String messageClassName = (java.lang.String) faultMessageMap
										.get(faultElt.getQName());
								java.lang.Class<?> messageClass = java.lang.Class.forName(messageClassName);
								java.lang.Object messageObject = fromOM(faultElt, messageClass, null);
								java.lang.reflect.Method m = exceptionClass.getMethod("setFaultMessage",
										new java.lang.Class[] { messageClass });
								m.invoke(ex, new java.lang.Object[] { messageObject });

								callback.receiveErrorgetColumnNames(new java.rmi.RemoteException(ex.getMessage(), ex));
							} catch (java.lang.ClassCastException e) {
								// we cannot intantiate the class - throw the original Axis fault
								callback.receiveErrorgetColumnNames(f);
							} catch (java.lang.ClassNotFoundException e) {
								// we cannot intantiate the class - throw the original Axis fault
								callback.receiveErrorgetColumnNames(f);
							} catch (java.lang.NoSuchMethodException e) {
								// we cannot intantiate the class - throw the original Axis fault
								callback.receiveErrorgetColumnNames(f);
							} catch (java.lang.reflect.InvocationTargetException e) {
								// we cannot intantiate the class - throw the original Axis fault
								callback.receiveErrorgetColumnNames(f);
							} catch (java.lang.IllegalAccessException e) {
								// we cannot intantiate the class - throw the original Axis fault
								callback.receiveErrorgetColumnNames(f);
							} catch (java.lang.InstantiationException e) {
								// we cannot intantiate the class - throw the original Axis fault
								callback.receiveErrorgetColumnNames(f);
							} catch (org.apache.axis2.AxisFault e) {
								// we cannot intantiate the class - throw the original Axis fault
								callback.receiveErrorgetColumnNames(f);
							}
						} else {
							callback.receiveErrorgetColumnNames(f);
						}
					} else {
						callback.receiveErrorgetColumnNames(f);
					}
				} else {
					callback.receiveErrorgetColumnNames(error);
				}
			}

			public void onFault(org.apache.axis2.context.MessageContext faultContext) {
				org.apache.axis2.AxisFault fault = org.apache.axis2.util.Utils
						.getInboundFaultFromMessageContext(faultContext);
				onError(fault);
			}

			public void onComplete() {
				try {
					_messageContext.getTransportOut().getSender().cleanup(_messageContext);
				} catch (org.apache.axis2.AxisFault axisFault) {
					callback.receiveErrorgetColumnNames(axisFault);
				}
			}
		});

		org.apache.axis2.util.CallbackReceiver _callbackReceiver = null;
		if (_operations[2].getMessageReceiver() == null && _operationClient.getOptions().isUseSeparateListener()) {
			_callbackReceiver = new org.apache.axis2.util.CallbackReceiver();
			_operations[2].setMessageReceiver(_callbackReceiver);
		}

		// execute the operation client
		_operationClient.execute(false);

	}

	/**
	 * A utility method that copies the namepaces from the SOAPEnvelope
	 */
	private java.util.Map<String, String> getEnvelopeNamespaces(org.apache.axiom.soap.SOAPEnvelope env) {
		java.util.Map<String, String> returnMap = new java.util.HashMap<String, String>();
		java.util.Iterator<OMNamespace> namespaceIterator = env.getAllDeclaredNamespaces();
		while (namespaceIterator.hasNext()) {
			org.apache.axiom.om.OMNamespace ns = namespaceIterator.next();
			returnMap.put(ns.getPrefix(), ns.getNamespaceURI());
		}
		return returnMap;
	}

	private javax.xml.namespace.QName[] opNameArray = null;

	private boolean optimizeContent(javax.xml.namespace.QName opName) {

		if (opNameArray == null) {
			return false;
		}
		for (int i = 0; i < opNameArray.length; i++) {
			if (opName.equals(opNameArray[i])) {
				return true;
			}
		}
		return false;
	}

	// http://129.70.142.193:8080/axis2/services/VerySimpleDBService.VerySimpleDBServiceHttpSoap12Endpoint
	public static class SQLException extends Exception implements org.apache.axis2.databinding.ADBBean {
		/*
		 * This type was generated from the piece of schema that had name = SQLException
		 * Namespace URI = http://sql.java/xsd Namespace Prefix = ns2
		 */

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		private static java.lang.String generatePrefix(java.lang.String namespace) {
			if (namespace.equals("http://sql.java/xsd")) {
				return "ns2";
			}
			return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
		}

		/**
		 * field for ErrorCode
		 */

		protected int localErrorCode;

		/*
		 * This tracker boolean wil be used to detect whether the user called the set
		 * method for this attribute. It will be used to determine whether to include
		 * this field in the serialized XML
		 */
		protected boolean localErrorCodeTracker = false;

		/**
		 * Auto generated getter method
		 * 
		 * @return int
		 */
		public int getErrorCode() {
			return localErrorCode;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param ErrorCode
		 */
		public void setErrorCode(int param) {

			// setting primitive attribute tracker to true

			if (param == java.lang.Integer.MIN_VALUE) {
				localErrorCodeTracker = false;

			} else {
				localErrorCodeTracker = true;
			}

			this.localErrorCode = param;

		}

		/**
		 * field for NextException
		 */

		protected SQLException localNextException;

		/*
		 * This tracker boolean wil be used to detect whether the user called the set
		 * method for this attribute. It will be used to determine whether to include
		 * this field in the serialized XML
		 */
		protected boolean localNextExceptionTracker = false;

		/**
		 * Auto generated getter method
		 * 
		 * @return SQLException
		 */
		public SQLException getNextException() {
			return localNextException;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param NextException
		 */
		public void setNextException(SQLException param) {

			if (param != null) {
				// update the setting tracker
				localNextExceptionTracker = true;
			} else {
				localNextExceptionTracker = true;

			}

			this.localNextException = param;

		}

		/**
		 * field for SQLState
		 */

		protected java.lang.String localSQLState;

		/*
		 * This tracker boolean wil be used to detect whether the user called the set
		 * method for this attribute. It will be used to determine whether to include
		 * this field in the serialized XML
		 */
		protected boolean localSQLStateTracker = false;

		/**
		 * Auto generated getter method
		 * 
		 * @return java.lang.String
		 */
		public java.lang.String getSQLState() {
			return localSQLState;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param SQLState
		 */
		public void setSQLState(java.lang.String param) {

			if (param != null) {
				// update the setting tracker
				localSQLStateTracker = true;
			} else {
				localSQLStateTracker = true;

			}

			this.localSQLState = param;

		}

		/**
		 * isReaderMTOMAware
		 * 
		 * @return true if the reader supports MTOM
		 */
		public static boolean isReaderMTOMAware(javax.xml.stream.XMLStreamReader reader) {
			boolean isReaderMTOMAware = false;

			try {
				isReaderMTOMAware = java.lang.Boolean.TRUE
						.equals(reader.getProperty(org.apache.axiom.om.OMConstants.IS_DATA_HANDLERS_AWARE));
			} catch (java.lang.IllegalArgumentException e) {
				isReaderMTOMAware = false;
			}
			return isReaderMTOMAware;
		}

		/**
		 *
		 * @param parentQName
		 * @param factory
		 * @return org.apache.axiom.om.OMElement
		 */
		public org.apache.axiom.om.OMElement getOMElement(final javax.xml.namespace.QName parentQName,
				final org.apache.axiom.om.OMFactory factory) throws org.apache.axis2.databinding.ADBException {

			org.apache.axiom.om.OMDataSource dataSource = new org.apache.axis2.databinding.ADBDataSource(this,
					parentQName) {

				public void serialize(org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter)
						throws javax.xml.stream.XMLStreamException {
					SQLException.this.serialize(parentQName, factory, xmlWriter);
				}
			};
			return new org.apache.axiom.om.impl.llom.OMSourcedElementImpl(parentQName, factory, dataSource);

		}

		public void serialize(final javax.xml.namespace.QName parentQName, final org.apache.axiom.om.OMFactory factory,
				org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException {
			serialize(parentQName, factory, xmlWriter, false);
		}

		public void serialize(final javax.xml.namespace.QName parentQName, final org.apache.axiom.om.OMFactory factory,
				org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter, boolean serializeType)
				throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException {

			java.lang.String prefix = null;
			java.lang.String namespace = null;

			prefix = parentQName.getPrefix();
			namespace = parentQName.getNamespaceURI();

			if ((namespace != null) && (namespace.trim().length() > 0)) {
				java.lang.String writerPrefix = xmlWriter.getPrefix(namespace);
				if (writerPrefix != null) {
					xmlWriter.writeStartElement(namespace, parentQName.getLocalPart());
				} else {
					if (prefix == null) {
						prefix = generatePrefix(namespace);
					}

					xmlWriter.writeStartElement(prefix, parentQName.getLocalPart(), namespace);
					xmlWriter.writeNamespace(prefix, namespace);
					xmlWriter.setPrefix(prefix, namespace);
				}
			} else {
				xmlWriter.writeStartElement(parentQName.getLocalPart());
			}

			java.lang.String namespacePrefix = registerPrefix(xmlWriter, "http://sql.java/xsd");
			if ((namespacePrefix != null) && (namespacePrefix.trim().length() > 0)) {
				writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type",
						namespacePrefix + ":SQLException", xmlWriter);
			} else {
				writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type", "SQLException", xmlWriter);
			}

			if (localExceptionTracker) {

				if (localException != null) {
					if (localException instanceof org.apache.axis2.databinding.ADBBean) {
						((org.apache.axis2.databinding.ADBBean) localException).serialize(
								new javax.xml.namespace.QName("http://axis.de", "Exception"), factory, xmlWriter, true);
					} else {
						java.lang.String namespace2 = "http://axis.de";
						if (!namespace2.equals("")) {
							java.lang.String prefix2 = xmlWriter.getPrefix(namespace2);

							if (prefix2 == null) {
								prefix2 = generatePrefix(namespace2);

								xmlWriter.writeStartElement(prefix2, "Exception", namespace2);
								xmlWriter.writeNamespace(prefix2, namespace2);
								xmlWriter.setPrefix(prefix2, namespace2);

							} else {
								xmlWriter.writeStartElement(namespace2, "Exception");
							}

						} else {
							xmlWriter.writeStartElement("Exception");
						}
						org.apache.axis2.databinding.utils.ConverterUtil.serializeAnyType(localException, xmlWriter);
						xmlWriter.writeEndElement();
					}
				} else {

					// write null attribute
					java.lang.String namespace2 = "http://axis.de";
					if (!namespace2.equals("")) {
						java.lang.String prefix2 = xmlWriter.getPrefix(namespace2);

						if (prefix2 == null) {
							prefix2 = generatePrefix(namespace2);

							xmlWriter.writeStartElement(prefix2, "Exception", namespace2);
							xmlWriter.writeNamespace(prefix2, namespace2);
							xmlWriter.setPrefix(prefix2, namespace2);

						} else {
							xmlWriter.writeStartElement(namespace2, "Exception");
						}

					} else {
						xmlWriter.writeStartElement("Exception");
					}

					// write the nil attribute
					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
					xmlWriter.writeEndElement();

				}

			}
			if (localErrorCodeTracker) {
				namespace = "http://sql.java/xsd";
				if (!namespace.equals("")) {
					prefix = xmlWriter.getPrefix(namespace);

					if (prefix == null) {
						prefix = generatePrefix(namespace);

						xmlWriter.writeStartElement(prefix, "errorCode", namespace);
						xmlWriter.writeNamespace(prefix, namespace);
						xmlWriter.setPrefix(prefix, namespace);

					} else {
						xmlWriter.writeStartElement(namespace, "errorCode");
					}

				} else {
					xmlWriter.writeStartElement("errorCode");
				}

				if (localErrorCode == java.lang.Integer.MIN_VALUE) {

					throw new org.apache.axis2.databinding.ADBException("errorCode cannot be null!!");

				} else {
					xmlWriter.writeCharacters(
							org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localErrorCode));
				}

				xmlWriter.writeEndElement();
			}
			if (localNextExceptionTracker) {
				if (localNextException == null) {

					java.lang.String namespace2 = "http://sql.java/xsd";

					if (!namespace2.equals("")) {
						java.lang.String prefix2 = xmlWriter.getPrefix(namespace2);

						if (prefix2 == null) {
							prefix2 = generatePrefix(namespace2);

							xmlWriter.writeStartElement(prefix2, "nextException", namespace2);
							xmlWriter.writeNamespace(prefix2, namespace2);
							xmlWriter.setPrefix(prefix2, namespace2);

						} else {
							xmlWriter.writeStartElement(namespace2, "nextException");
						}

					} else {
						xmlWriter.writeStartElement("nextException");
					}

					// write the nil attribute
					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
					xmlWriter.writeEndElement();
				} else {
					localNextException.serialize(new javax.xml.namespace.QName("http://sql.java/xsd", "nextException"),
							factory, xmlWriter);
				}
			}
			if (localSQLStateTracker) {
				namespace = "http://sql.java/xsd";
				if (!namespace.equals("")) {
					prefix = xmlWriter.getPrefix(namespace);

					if (prefix == null) {
						prefix = generatePrefix(namespace);

						xmlWriter.writeStartElement(prefix, "sQLState", namespace);
						xmlWriter.writeNamespace(prefix, namespace);
						xmlWriter.setPrefix(prefix, namespace);

					} else {
						xmlWriter.writeStartElement(namespace, "sQLState");
					}

				} else {
					xmlWriter.writeStartElement("sQLState");
				}

				if (localSQLState == null) {
					// write the nil attribute

					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);

				} else {

					xmlWriter.writeCharacters(localSQLState);

				}

				xmlWriter.writeEndElement();
			}
			xmlWriter.writeEndElement();

		}

		/**
		 * Util method to write an attribute with the ns prefix
		 */
		private void writeAttribute(java.lang.String prefix, java.lang.String namespace, java.lang.String attName,
				java.lang.String attValue, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException {
			if (xmlWriter.getPrefix(namespace) == null) {
				xmlWriter.writeNamespace(prefix, namespace);
				xmlWriter.setPrefix(prefix, namespace);

			}

			xmlWriter.writeAttribute(namespace, attName, attValue);

		}

		/**
		 * Register a namespace prefix
		 */
		private java.lang.String registerPrefix(javax.xml.stream.XMLStreamWriter xmlWriter, java.lang.String namespace)
				throws javax.xml.stream.XMLStreamException {
			java.lang.String prefix = xmlWriter.getPrefix(namespace);

			if (prefix == null) {
				prefix = generatePrefix(namespace);

				while (xmlWriter.getNamespaceContext().getNamespaceURI(prefix) != null) {
					prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
				}

				xmlWriter.writeNamespace(prefix, namespace);
				xmlWriter.setPrefix(prefix, namespace);
			}

			return prefix;
		}

		/**
		 * databinding method to get an XML representation of this object
		 *
		 */
		public javax.xml.stream.XMLStreamReader getPullParser(javax.xml.namespace.QName qName)
				throws org.apache.axis2.databinding.ADBException {

			java.util.ArrayList<Object> elementList = new java.util.ArrayList<Object>();
			java.util.ArrayList<QName> attribList = new java.util.ArrayList<QName>();

			attribList.add(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema-instance", "type"));
			attribList.add(new javax.xml.namespace.QName("http://sql.java/xsd", "SQLException"));
			if (localExceptionTracker) {
				elementList.add(new javax.xml.namespace.QName("http://axis.de", "Exception"));

				elementList.add(localException == null ? null : localException);
			}
			if (localErrorCodeTracker) {
				elementList.add(new javax.xml.namespace.QName("http://sql.java/xsd", "errorCode"));

				elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localErrorCode));
			}
			if (localNextExceptionTracker) {
				elementList.add(new javax.xml.namespace.QName("http://sql.java/xsd", "nextException"));

				elementList.add(localNextException == null ? null : localNextException);
			}
			if (localSQLStateTracker) {
				elementList.add(new javax.xml.namespace.QName("http://sql.java/xsd", "sQLState"));

				elementList.add(localSQLState == null ? null
						: org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localSQLState));
			}

			return new org.apache.axis2.databinding.utils.reader.ADBXMLStreamReaderImpl(qName, elementList.toArray(),
					attribList.toArray());

		}

		/**
		 * Factory class that keeps the parse method
		 */
		public static class Factory {

			/**
			 * static method to create the object Precondition: If this object is an
			 * element, the current or next start element starts this object and any
			 * intervening reader events are ignorable If this object is not an element, it
			 * is a complex type and the reader is at the event just after the outer start
			 * element Postcondition: If this object is an element, the reader is positioned
			 * at its end element If this object is a complex type, the reader is positioned
			 * at the end element of its outer element
			 */
			public static SQLException parse(javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception {
				SQLException object = new SQLException();

				java.lang.String nillableValue = null;
				try {

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type") != null) {
						java.lang.String fullTypeName = reader
								.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type");
						if (fullTypeName != null) {
							java.lang.String nsPrefix = null;
							if (fullTypeName.indexOf(":") > -1) {
								nsPrefix = fullTypeName.substring(0, fullTypeName.indexOf(":"));
							}
							nsPrefix = nsPrefix == null ? "" : nsPrefix;

							java.lang.String type = fullTypeName.substring(fullTypeName.indexOf(":") + 1);

							if (!"SQLException".equals(type)) {
								// find namespace for the prefix
								java.lang.String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
								return (SQLException) ExtensionMapper.getTypeObject(nsUri, type, reader);
							}

						}

					}

					// Note all attributes that were handled. Used to differ normal attributes
					// from anyAttributes.

					reader.next();

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()
							&& new javax.xml.namespace.QName("http://axis.de", "Exception").equals(reader.getName())) {

						object.setException(org.apache.axis2.databinding.utils.ConverterUtil.getAnyTypeObject(reader,
								ExtensionMapper.class));

						reader.next();

					} // End of if for expected property start element

					else {

					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement() && new javax.xml.namespace.QName("http://sql.java/xsd", "errorCode")
							.equals(reader.getName())) {

						java.lang.String content = reader.getElementText();

						object.setErrorCode(org.apache.axis2.databinding.utils.ConverterUtil.convertToInt(content));

						reader.next();

					} // End of if for expected property start element

					else {

						object.setErrorCode(java.lang.Integer.MIN_VALUE);

					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement() && new javax.xml.namespace.QName("http://sql.java/xsd", "nextException")
							.equals(reader.getName())) {

						nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
						if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
							object.setNextException(null);
							reader.next();

							reader.next();

						} else {

							object.setNextException(SQLException.Factory.parse(reader));

							reader.next();
						}
					} // End of if for expected property start element

					else {

					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement() && new javax.xml.namespace.QName("http://sql.java/xsd", "sQLState")
							.equals(reader.getName())) {

						nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
						if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {

							java.lang.String content = reader.getElementText();

							object.setSQLState(
									org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

						} else {

							reader.getElementText(); // throw away text nodes if any.
						}

						reader.next();

					} // End of if for expected property start element

					else {

					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement())
						// A start element we are not expecting indicates a trailing invalid property
						throw new org.apache.axis2.databinding.ADBException(
								"Unexpected subelement " + reader.getLocalName());

				} catch (javax.xml.stream.XMLStreamException e) {
					throw new java.lang.Exception(e);
				}

				return object;
			}

		}// end of factory class

	}

	public static class IllegalAccessException implements org.apache.axis2.databinding.ADBBean {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public static final javax.xml.namespace.QName MY_QNAME = new javax.xml.namespace.QName("http://axis.de",
				"IllegalAccessException", "ns1");

		private static java.lang.String generatePrefix(java.lang.String namespace) {
			if (namespace.equals("http://axis.de")) {
				return "ns1";
			}
			return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
		}

		/**
		 * field for IllegalAccessException
		 */

		protected java.lang.Object localIllegalAccessException;

		/*
		 * This tracker boolean wil be used to detect whether the user called the set
		 * method for this attribute. It will be used to determine whether to include
		 * this field in the serialized XML
		 */
		protected boolean localIllegalAccessExceptionTracker = false;

		/**
		 * Auto generated getter method
		 * 
		 * @return java.lang.Object
		 */
		public java.lang.Object getIllegalAccessException() {
			return localIllegalAccessException;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param IllegalAccessException
		 */
		public void setIllegalAccessException(java.lang.Object param) {

			if (param != null) {
				// update the setting tracker
				localIllegalAccessExceptionTracker = true;
			} else {
				localIllegalAccessExceptionTracker = true;

			}

			this.localIllegalAccessException = param;

		}

		/**
		 * isReaderMTOMAware
		 * 
		 * @return true if the reader supports MTOM
		 */
		public static boolean isReaderMTOMAware(javax.xml.stream.XMLStreamReader reader) {
			boolean isReaderMTOMAware = false;

			try {
				isReaderMTOMAware = java.lang.Boolean.TRUE
						.equals(reader.getProperty(org.apache.axiom.om.OMConstants.IS_DATA_HANDLERS_AWARE));
			} catch (java.lang.IllegalArgumentException e) {
				isReaderMTOMAware = false;
			}
			return isReaderMTOMAware;
		}

		/**
		 *
		 * @param parentQName
		 * @param factory
		 * @return org.apache.axiom.om.OMElement
		 */
		public org.apache.axiom.om.OMElement getOMElement(final javax.xml.namespace.QName parentQName,
				final org.apache.axiom.om.OMFactory factory) throws org.apache.axis2.databinding.ADBException {

			org.apache.axiom.om.OMDataSource dataSource = new org.apache.axis2.databinding.ADBDataSource(this,
					MY_QNAME) {

				public void serialize(org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter)
						throws javax.xml.stream.XMLStreamException {
					IllegalAccessException.this.serialize(MY_QNAME, factory, xmlWriter);
				}
			};
			return new org.apache.axiom.om.impl.llom.OMSourcedElementImpl(MY_QNAME, factory, dataSource);

		}

		public void serialize(final javax.xml.namespace.QName parentQName, final org.apache.axiom.om.OMFactory factory,
				org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException {
			serialize(parentQName, factory, xmlWriter, false);
		}

		public void serialize(final javax.xml.namespace.QName parentQName, final org.apache.axiom.om.OMFactory factory,
				org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter, boolean serializeType)
				throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException {

			java.lang.String prefix = null;
			java.lang.String namespace = null;

			prefix = parentQName.getPrefix();
			namespace = parentQName.getNamespaceURI();

			if ((namespace != null) && (namespace.trim().length() > 0)) {
				java.lang.String writerPrefix = xmlWriter.getPrefix(namespace);
				if (writerPrefix != null) {
					xmlWriter.writeStartElement(namespace, parentQName.getLocalPart());
				} else {
					if (prefix == null) {
						prefix = generatePrefix(namespace);
					}

					xmlWriter.writeStartElement(prefix, parentQName.getLocalPart(), namespace);
					xmlWriter.writeNamespace(prefix, namespace);
					xmlWriter.setPrefix(prefix, namespace);
				}
			} else {
				xmlWriter.writeStartElement(parentQName.getLocalPart());
			}

			if (serializeType) {

				java.lang.String namespacePrefix = registerPrefix(xmlWriter, "http://axis.de");
				if ((namespacePrefix != null) && (namespacePrefix.trim().length() > 0)) {
					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type",
							namespacePrefix + ":IllegalAccessException", xmlWriter);
				} else {
					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type", "IllegalAccessException",
							xmlWriter);
				}

			}
			if (localIllegalAccessExceptionTracker) {

				if (localIllegalAccessException != null) {
					if (localIllegalAccessException instanceof org.apache.axis2.databinding.ADBBean) {
						((org.apache.axis2.databinding.ADBBean) localIllegalAccessException).serialize(
								new javax.xml.namespace.QName("http://axis.de", "IllegalAccessException"), factory,
								xmlWriter, true);
					} else {
						java.lang.String namespace2 = "http://axis.de";
						if (!namespace2.equals("")) {
							java.lang.String prefix2 = xmlWriter.getPrefix(namespace2);

							if (prefix2 == null) {
								prefix2 = generatePrefix(namespace2);

								xmlWriter.writeStartElement(prefix2, "IllegalAccessException", namespace2);
								xmlWriter.writeNamespace(prefix2, namespace2);
								xmlWriter.setPrefix(prefix2, namespace2);

							} else {
								xmlWriter.writeStartElement(namespace2, "IllegalAccessException");
							}

						} else {
							xmlWriter.writeStartElement("IllegalAccessException");
						}
						org.apache.axis2.databinding.utils.ConverterUtil.serializeAnyType(localIllegalAccessException,
								xmlWriter);
						xmlWriter.writeEndElement();
					}
				} else {

					// write null attribute
					java.lang.String namespace2 = "http://axis.de";
					if (!namespace2.equals("")) {
						java.lang.String prefix2 = xmlWriter.getPrefix(namespace2);

						if (prefix2 == null) {
							prefix2 = generatePrefix(namespace2);

							xmlWriter.writeStartElement(prefix2, "IllegalAccessException", namespace2);
							xmlWriter.writeNamespace(prefix2, namespace2);
							xmlWriter.setPrefix(prefix2, namespace2);

						} else {
							xmlWriter.writeStartElement(namespace2, "IllegalAccessException");
						}

					} else {
						xmlWriter.writeStartElement("IllegalAccessException");
					}

					// write the nil attribute
					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
					xmlWriter.writeEndElement();

				}

			}
			xmlWriter.writeEndElement();

		}

		/**
		 * Util method to write an attribute with the ns prefix
		 */
		private void writeAttribute(java.lang.String prefix, java.lang.String namespace, java.lang.String attName,
				java.lang.String attValue, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException {
			if (xmlWriter.getPrefix(namespace) == null) {
				xmlWriter.writeNamespace(prefix, namespace);
				xmlWriter.setPrefix(prefix, namespace);

			}

			xmlWriter.writeAttribute(namespace, attName, attValue);

		}

		/**
		 * Register a namespace prefix
		 */
		private java.lang.String registerPrefix(javax.xml.stream.XMLStreamWriter xmlWriter, java.lang.String namespace)
				throws javax.xml.stream.XMLStreamException {
			java.lang.String prefix = xmlWriter.getPrefix(namespace);

			if (prefix == null) {
				prefix = generatePrefix(namespace);

				while (xmlWriter.getNamespaceContext().getNamespaceURI(prefix) != null) {
					prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
				}

				xmlWriter.writeNamespace(prefix, namespace);
				xmlWriter.setPrefix(prefix, namespace);
			}

			return prefix;
		}

		/**
		 * databinding method to get an XML representation of this object
		 *
		 */
		public javax.xml.stream.XMLStreamReader getPullParser(javax.xml.namespace.QName qName)
				throws org.apache.axis2.databinding.ADBException {

			java.util.ArrayList<Object> elementList = new java.util.ArrayList<Object>();
			java.util.ArrayList<Object> attribList = new java.util.ArrayList<Object>();

			if (localIllegalAccessExceptionTracker) {
				elementList.add(new javax.xml.namespace.QName("http://axis.de", "IllegalAccessException"));

				elementList.add(localIllegalAccessException == null ? null : localIllegalAccessException);
			}

			return new org.apache.axis2.databinding.utils.reader.ADBXMLStreamReaderImpl(qName, elementList.toArray(),
					attribList.toArray());

		}

		/**
		 * Factory class that keeps the parse method
		 */
		public static class Factory {

			/**
			 * static method to create the object Precondition: If this object is an
			 * element, the current or next start element starts this object and any
			 * intervening reader events are ignorable If this object is not an element, it
			 * is a complex type and the reader is at the event just after the outer start
			 * element Postcondition: If this object is an element, the reader is positioned
			 * at its end element If this object is a complex type, the reader is positioned
			 * at the end element of its outer element
			 */
			public static IllegalAccessException parse(javax.xml.stream.XMLStreamReader reader)
					throws java.lang.Exception {
				IllegalAccessException object = new IllegalAccessException();

				java.lang.String nillableValue = null;
				try {

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
					if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
						// Skip the element and report the null value. It cannot have subelements.
						while (!reader.isEndElement())
							reader.next();

						return null;

					}

					if (reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type") != null) {
						java.lang.String fullTypeName = reader
								.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type");
						if (fullTypeName != null) {
							java.lang.String nsPrefix = null;
							if (fullTypeName.indexOf(":") > -1) {
								nsPrefix = fullTypeName.substring(0, fullTypeName.indexOf(":"));
							}
							nsPrefix = nsPrefix == null ? "" : nsPrefix;

							java.lang.String type = fullTypeName.substring(fullTypeName.indexOf(":") + 1);

							if (!"IllegalAccessException".equals(type)) {
								// find namespace for the prefix
								java.lang.String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
								return (IllegalAccessException) ExtensionMapper.getTypeObject(nsUri, type, reader);
							}

						}

					}

					// Note all attributes that were handled. Used to differ normal attributes
					// from anyAttributes.

					reader.next();

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()
							&& new javax.xml.namespace.QName("http://axis.de", "IllegalAccessException")
									.equals(reader.getName())) {

						object.setIllegalAccessException(org.apache.axis2.databinding.utils.ConverterUtil
								.getAnyTypeObject(reader, ExtensionMapper.class));

						reader.next();

					} // End of if for expected property start element

					else {

					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement())
						// A start element we are not expecting indicates a trailing invalid property
						throw new org.apache.axis2.databinding.ADBException(
								"Unexpected subelement " + reader.getLocalName());

				} catch (javax.xml.stream.XMLStreamException e) {
					throw new java.lang.Exception(e);
				}

				return object;
			}

		}// end of factory class

	}

	public static class InstantiationException implements org.apache.axis2.databinding.ADBBean {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public static final javax.xml.namespace.QName MY_QNAME = new javax.xml.namespace.QName("http://axis.de",
				"InstantiationException", "ns1");

		private static java.lang.String generatePrefix(java.lang.String namespace) {
			if (namespace.equals("http://axis.de")) {
				return "ns1";
			}
			return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
		}

		/**
		 * field for InstantiationException
		 */

		protected java.lang.Object localInstantiationException;

		/*
		 * This tracker boolean wil be used to detect whether the user called the set
		 * method for this attribute. It will be used to determine whether to include
		 * this field in the serialized XML
		 */
		protected boolean localInstantiationExceptionTracker = false;

		/**
		 * Auto generated getter method
		 * 
		 * @return java.lang.Object
		 */
		public java.lang.Object getInstantiationException() {
			return localInstantiationException;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param InstantiationException
		 */
		public void setInstantiationException(java.lang.Object param) {

			if (param != null) {
				// update the setting tracker
				localInstantiationExceptionTracker = true;
			} else {
				localInstantiationExceptionTracker = true;

			}

			this.localInstantiationException = param;

		}

		/**
		 * isReaderMTOMAware
		 * 
		 * @return true if the reader supports MTOM
		 */
		public static boolean isReaderMTOMAware(javax.xml.stream.XMLStreamReader reader) {
			boolean isReaderMTOMAware = false;

			try {
				isReaderMTOMAware = java.lang.Boolean.TRUE
						.equals(reader.getProperty(org.apache.axiom.om.OMConstants.IS_DATA_HANDLERS_AWARE));
			} catch (java.lang.IllegalArgumentException e) {
				isReaderMTOMAware = false;
			}
			return isReaderMTOMAware;
		}

		/**
		 *
		 * @param parentQName
		 * @param factory
		 * @return org.apache.axiom.om.OMElement
		 */
		public org.apache.axiom.om.OMElement getOMElement(final javax.xml.namespace.QName parentQName,
				final org.apache.axiom.om.OMFactory factory) throws org.apache.axis2.databinding.ADBException {

			org.apache.axiom.om.OMDataSource dataSource = new org.apache.axis2.databinding.ADBDataSource(this,
					MY_QNAME) {

				public void serialize(org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter)
						throws javax.xml.stream.XMLStreamException {
					InstantiationException.this.serialize(MY_QNAME, factory, xmlWriter);
				}
			};
			return new org.apache.axiom.om.impl.llom.OMSourcedElementImpl(MY_QNAME, factory, dataSource);

		}

		public void serialize(final javax.xml.namespace.QName parentQName, final org.apache.axiom.om.OMFactory factory,
				org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException {
			serialize(parentQName, factory, xmlWriter, false);
		}

		public void serialize(final javax.xml.namespace.QName parentQName, final org.apache.axiom.om.OMFactory factory,
				org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter, boolean serializeType)
				throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException {

			java.lang.String prefix = null;
			java.lang.String namespace = null;

			prefix = parentQName.getPrefix();
			namespace = parentQName.getNamespaceURI();

			if ((namespace != null) && (namespace.trim().length() > 0)) {
				java.lang.String writerPrefix = xmlWriter.getPrefix(namespace);
				if (writerPrefix != null) {
					xmlWriter.writeStartElement(namespace, parentQName.getLocalPart());
				} else {
					if (prefix == null) {
						prefix = generatePrefix(namespace);
					}

					xmlWriter.writeStartElement(prefix, parentQName.getLocalPart(), namespace);
					xmlWriter.writeNamespace(prefix, namespace);
					xmlWriter.setPrefix(prefix, namespace);
				}
			} else {
				xmlWriter.writeStartElement(parentQName.getLocalPart());
			}

			if (serializeType) {

				java.lang.String namespacePrefix = registerPrefix(xmlWriter, "http://axis.de");
				if ((namespacePrefix != null) && (namespacePrefix.trim().length() > 0)) {
					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type",
							namespacePrefix + ":InstantiationException", xmlWriter);
				} else {
					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type", "InstantiationException",
							xmlWriter);
				}

			}
			if (localInstantiationExceptionTracker) {

				if (localInstantiationException != null) {
					if (localInstantiationException instanceof org.apache.axis2.databinding.ADBBean) {
						((org.apache.axis2.databinding.ADBBean) localInstantiationException).serialize(
								new javax.xml.namespace.QName("http://axis.de", "InstantiationException"), factory,
								xmlWriter, true);
					} else {
						java.lang.String namespace2 = "http://axis.de";
						if (!namespace2.equals("")) {
							java.lang.String prefix2 = xmlWriter.getPrefix(namespace2);

							if (prefix2 == null) {
								prefix2 = generatePrefix(namespace2);

								xmlWriter.writeStartElement(prefix2, "InstantiationException", namespace2);
								xmlWriter.writeNamespace(prefix2, namespace2);
								xmlWriter.setPrefix(prefix2, namespace2);

							} else {
								xmlWriter.writeStartElement(namespace2, "InstantiationException");
							}

						} else {
							xmlWriter.writeStartElement("InstantiationException");
						}
						org.apache.axis2.databinding.utils.ConverterUtil.serializeAnyType(localInstantiationException,
								xmlWriter);
						xmlWriter.writeEndElement();
					}
				} else {

					// write null attribute
					java.lang.String namespace2 = "http://axis.de";
					if (!namespace2.equals("")) {
						java.lang.String prefix2 = xmlWriter.getPrefix(namespace2);

						if (prefix2 == null) {
							prefix2 = generatePrefix(namespace2);

							xmlWriter.writeStartElement(prefix2, "InstantiationException", namespace2);
							xmlWriter.writeNamespace(prefix2, namespace2);
							xmlWriter.setPrefix(prefix2, namespace2);

						} else {
							xmlWriter.writeStartElement(namespace2, "InstantiationException");
						}

					} else {
						xmlWriter.writeStartElement("InstantiationException");
					}

					// write the nil attribute
					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
					xmlWriter.writeEndElement();

				}

			}
			xmlWriter.writeEndElement();

		}

		/**
		 * Util method to write an attribute with the ns prefix
		 */
		private void writeAttribute(java.lang.String prefix, java.lang.String namespace, java.lang.String attName,
				java.lang.String attValue, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException {
			if (xmlWriter.getPrefix(namespace) == null) {
				xmlWriter.writeNamespace(prefix, namespace);
				xmlWriter.setPrefix(prefix, namespace);

			}

			xmlWriter.writeAttribute(namespace, attName, attValue);

		}

		/**
		 * Register a namespace prefix
		 */
		private java.lang.String registerPrefix(javax.xml.stream.XMLStreamWriter xmlWriter, java.lang.String namespace)
				throws javax.xml.stream.XMLStreamException {
			java.lang.String prefix = xmlWriter.getPrefix(namespace);

			if (prefix == null) {
				prefix = generatePrefix(namespace);

				while (xmlWriter.getNamespaceContext().getNamespaceURI(prefix) != null) {
					prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
				}

				xmlWriter.writeNamespace(prefix, namespace);
				xmlWriter.setPrefix(prefix, namespace);
			}

			return prefix;
		}

		/**
		 * databinding method to get an XML representation of this object
		 *
		 */
		public javax.xml.stream.XMLStreamReader getPullParser(javax.xml.namespace.QName qName)
				throws org.apache.axis2.databinding.ADBException {

			java.util.ArrayList<Object> elementList = new java.util.ArrayList<Object>();
			java.util.ArrayList<Object> attribList = new java.util.ArrayList<Object>();

			if (localInstantiationExceptionTracker) {
				elementList.add(new javax.xml.namespace.QName("http://axis.de", "InstantiationException"));

				elementList.add(localInstantiationException == null ? null : localInstantiationException);
			}

			return new org.apache.axis2.databinding.utils.reader.ADBXMLStreamReaderImpl(qName, elementList.toArray(),
					attribList.toArray());

		}

		/**
		 * Factory class that keeps the parse method
		 */
		public static class Factory {

			/**
			 * static method to create the object Precondition: If this object is an
			 * element, the current or next start element starts this object and any
			 * intervening reader events are ignorable If this object is not an element, it
			 * is a complex type and the reader is at the event just after the outer start
			 * element Postcondition: If this object is an element, the reader is positioned
			 * at its end element If this object is a complex type, the reader is positioned
			 * at the end element of its outer element
			 */
			public static InstantiationException parse(javax.xml.stream.XMLStreamReader reader)
					throws java.lang.Exception {
				InstantiationException object = new InstantiationException();

				java.lang.String nillableValue = null;
				try {

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
					if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
						// Skip the element and report the null value. It cannot have subelements.
						while (!reader.isEndElement())
							reader.next();

						return null;

					}

					if (reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type") != null) {
						java.lang.String fullTypeName = reader
								.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type");
						if (fullTypeName != null) {
							java.lang.String nsPrefix = null;
							if (fullTypeName.indexOf(":") > -1) {
								nsPrefix = fullTypeName.substring(0, fullTypeName.indexOf(":"));
							}
							nsPrefix = nsPrefix == null ? "" : nsPrefix;

							java.lang.String type = fullTypeName.substring(fullTypeName.indexOf(":") + 1);

							if (!"InstantiationException".equals(type)) {
								// find namespace for the prefix
								java.lang.String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
								return (InstantiationException) ExtensionMapper.getTypeObject(nsUri, type, reader);
							}

						}

					}

					// Note all attributes that were handled. Used to differ normal attributes
					// from anyAttributes.

					reader.next();

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()
							&& new javax.xml.namespace.QName("http://axis.de", "InstantiationException")
									.equals(reader.getName())) {

						object.setInstantiationException(org.apache.axis2.databinding.utils.ConverterUtil
								.getAnyTypeObject(reader, ExtensionMapper.class));

						reader.next();

					} // End of if for expected property start element

					else {

					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement())
						// A start element we are not expecting indicates a trailing invalid property
						throw new org.apache.axis2.databinding.ADBException(
								"Unexpected subelement " + reader.getLocalName());

				} catch (javax.xml.stream.XMLStreamException e) {
					throw new java.lang.Exception(e);
				}

				return object;
			}

		}// end of factory class

	}

	public static class Exception implements org.apache.axis2.databinding.ADBBean {
		/*
		 * This type was generated from the piece of schema that had name = Exception
		 * Namespace URI = http://axis.de Namespace Prefix = ns1
		 */

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		private static java.lang.String generatePrefix(java.lang.String namespace) {
			if (namespace.equals("http://axis.de")) {
				return "ns1";
			}
			return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
		}

		/**
		 * field for Exception
		 */

		protected java.lang.Object localException;

		/*
		 * This tracker boolean wil be used to detect whether the user called the set
		 * method for this attribute. It will be used to determine whether to include
		 * this field in the serialized XML
		 */
		protected boolean localExceptionTracker = false;

		/**
		 * Auto generated getter method
		 * 
		 * @return java.lang.Object
		 */
		public java.lang.Object getException() {
			return localException;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param Exception
		 */
		public void setException(java.lang.Object param) {

			if (param != null) {
				// update the setting tracker
				localExceptionTracker = true;
			} else {
				localExceptionTracker = true;

			}

			this.localException = param;

		}

		/**
		 * isReaderMTOMAware
		 * 
		 * @return true if the reader supports MTOM
		 */
		public static boolean isReaderMTOMAware(javax.xml.stream.XMLStreamReader reader) {
			boolean isReaderMTOMAware = false;

			try {
				isReaderMTOMAware = java.lang.Boolean.TRUE
						.equals(reader.getProperty(org.apache.axiom.om.OMConstants.IS_DATA_HANDLERS_AWARE));
			} catch (java.lang.IllegalArgumentException e) {
				isReaderMTOMAware = false;
			}
			return isReaderMTOMAware;
		}

		/**
		 *
		 * @param parentQName
		 * @param factory
		 * @return org.apache.axiom.om.OMElement
		 */
		public org.apache.axiom.om.OMElement getOMElement(final javax.xml.namespace.QName parentQName,
				final org.apache.axiom.om.OMFactory factory) throws org.apache.axis2.databinding.ADBException {

			org.apache.axiom.om.OMDataSource dataSource = new org.apache.axis2.databinding.ADBDataSource(this,
					parentQName) {

				public void serialize(org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter)
						throws javax.xml.stream.XMLStreamException {
					Exception.this.serialize(parentQName, factory, xmlWriter);
				}
			};
			return new org.apache.axiom.om.impl.llom.OMSourcedElementImpl(parentQName, factory, dataSource);

		}

		public void serialize(final javax.xml.namespace.QName parentQName, final org.apache.axiom.om.OMFactory factory,
				org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException {
			serialize(parentQName, factory, xmlWriter, false);
		}

		public void serialize(final javax.xml.namespace.QName parentQName, final org.apache.axiom.om.OMFactory factory,
				org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter, boolean serializeType)
				throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException {

			java.lang.String prefix = null;
			java.lang.String namespace = null;

			prefix = parentQName.getPrefix();
			namespace = parentQName.getNamespaceURI();

			if ((namespace != null) && (namespace.trim().length() > 0)) {
				java.lang.String writerPrefix = xmlWriter.getPrefix(namespace);
				if (writerPrefix != null) {
					xmlWriter.writeStartElement(namespace, parentQName.getLocalPart());
				} else {
					if (prefix == null) {
						prefix = generatePrefix(namespace);
					}

					xmlWriter.writeStartElement(prefix, parentQName.getLocalPart(), namespace);
					xmlWriter.writeNamespace(prefix, namespace);
					xmlWriter.setPrefix(prefix, namespace);
				}
			} else {
				xmlWriter.writeStartElement(parentQName.getLocalPart());
			}

			if (serializeType) {

				java.lang.String namespacePrefix = registerPrefix(xmlWriter, "http://axis.de");
				if ((namespacePrefix != null) && (namespacePrefix.trim().length() > 0)) {
					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type",
							namespacePrefix + ":Exception", xmlWriter);
				} else {
					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type", "Exception", xmlWriter);
				}

			}
			if (localExceptionTracker) {

				if (localException != null) {
					if (localException instanceof org.apache.axis2.databinding.ADBBean) {
						((org.apache.axis2.databinding.ADBBean) localException).serialize(
								new javax.xml.namespace.QName("http://axis.de", "Exception"), factory, xmlWriter, true);
					} else {
						java.lang.String namespace2 = "http://axis.de";
						if (!namespace2.equals("")) {
							java.lang.String prefix2 = xmlWriter.getPrefix(namespace2);

							if (prefix2 == null) {
								prefix2 = generatePrefix(namespace2);

								xmlWriter.writeStartElement(prefix2, "Exception", namespace2);
								xmlWriter.writeNamespace(prefix2, namespace2);
								xmlWriter.setPrefix(prefix2, namespace2);

							} else {
								xmlWriter.writeStartElement(namespace2, "Exception");
							}

						} else {
							xmlWriter.writeStartElement("Exception");
						}
						org.apache.axis2.databinding.utils.ConverterUtil.serializeAnyType(localException, xmlWriter);
						xmlWriter.writeEndElement();
					}
				} else {

					// write null attribute
					java.lang.String namespace2 = "http://axis.de";
					if (!namespace2.equals("")) {
						java.lang.String prefix2 = xmlWriter.getPrefix(namespace2);

						if (prefix2 == null) {
							prefix2 = generatePrefix(namespace2);

							xmlWriter.writeStartElement(prefix2, "Exception", namespace2);
							xmlWriter.writeNamespace(prefix2, namespace2);
							xmlWriter.setPrefix(prefix2, namespace2);

						} else {
							xmlWriter.writeStartElement(namespace2, "Exception");
						}

					} else {
						xmlWriter.writeStartElement("Exception");
					}

					// write the nil attribute
					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
					xmlWriter.writeEndElement();

				}

			}
			xmlWriter.writeEndElement();

		}

		/**
		 * Util method to write an attribute with the ns prefix
		 */
		private void writeAttribute(java.lang.String prefix, java.lang.String namespace, java.lang.String attName,
				java.lang.String attValue, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException {
			if (xmlWriter.getPrefix(namespace) == null) {
				xmlWriter.writeNamespace(prefix, namespace);
				xmlWriter.setPrefix(prefix, namespace);

			}

			xmlWriter.writeAttribute(namespace, attName, attValue);

		}


		/**
		 * Register a namespace prefix
		 */
		private java.lang.String registerPrefix(javax.xml.stream.XMLStreamWriter xmlWriter, java.lang.String namespace)
				throws javax.xml.stream.XMLStreamException {
			java.lang.String prefix = xmlWriter.getPrefix(namespace);

			if (prefix == null) {
				prefix = generatePrefix(namespace);

				while (xmlWriter.getNamespaceContext().getNamespaceURI(prefix) != null) {
					prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
				}

				xmlWriter.writeNamespace(prefix, namespace);
				xmlWriter.setPrefix(prefix, namespace);
			}

			return prefix;
		}

		/**
		 * databinding method to get an XML representation of this object
		 *
		 */
		public javax.xml.stream.XMLStreamReader getPullParser(javax.xml.namespace.QName qName)
				throws org.apache.axis2.databinding.ADBException {

			java.util.ArrayList<Object> elementList = new java.util.ArrayList<Object>();
			java.util.ArrayList<Object> attribList = new java.util.ArrayList<Object>();

			if (localExceptionTracker) {
				elementList.add(new javax.xml.namespace.QName("http://axis.de", "Exception"));

				elementList.add(localException == null ? null : localException);
			}

			return new org.apache.axis2.databinding.utils.reader.ADBXMLStreamReaderImpl(qName, elementList.toArray(),
					attribList.toArray());

		}

		/**
		 * Factory class that keeps the parse method
		 */
		public static class Factory {

			/**
			 * static method to create the object Precondition: If this object is an
			 * element, the current or next start element starts this object and any
			 * intervening reader events are ignorable If this object is not an element, it
			 * is a complex type and the reader is at the event just after the outer start
			 * element Postcondition: If this object is an element, the reader is positioned
			 * at its end element If this object is a complex type, the reader is positioned
			 * at the end element of its outer element
			 */
			public static Exception parse(javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception {
				Exception object = new Exception();

				try {

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type") != null) {
						java.lang.String fullTypeName = reader
								.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type");
						if (fullTypeName != null) {
							java.lang.String nsPrefix = null;
							if (fullTypeName.indexOf(":") > -1) {
								nsPrefix = fullTypeName.substring(0, fullTypeName.indexOf(":"));
							}
							nsPrefix = nsPrefix == null ? "" : nsPrefix;

							java.lang.String type = fullTypeName.substring(fullTypeName.indexOf(":") + 1);

							if (!"Exception".equals(type)) {
								// find namespace for the prefix
								java.lang.String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
								return (Exception) ExtensionMapper.getTypeObject(nsUri, type, reader);
							}

						}

					}

					// Note all attributes that were handled. Used to differ normal attributes
					// from anyAttributes.
					reader.next();

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()
							&& new javax.xml.namespace.QName("http://axis.de", "Exception").equals(reader.getName())) {

						object.setException(org.apache.axis2.databinding.utils.ConverterUtil.getAnyTypeObject(reader,
								ExtensionMapper.class));

						reader.next();

					} // End of if for expected property start element

					else {

					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement())
						// A start element we are not expecting indicates a trailing invalid property
						throw new org.apache.axis2.databinding.ADBException(
								"Unexpected subelement " + reader.getLocalName());

				} catch (javax.xml.stream.XMLStreamException e) {
					throw new java.lang.Exception(e);
				}

				return object;
			}

		}// end of factory class

	}

	public static class GetColumnNamesResponse implements org.apache.axis2.databinding.ADBBean {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public static final javax.xml.namespace.QName MY_QNAME = new javax.xml.namespace.QName("http://axis.de",
				"getColumnNamesResponse", "ns1");

		private static java.lang.String generatePrefix(java.lang.String namespace) {
			if (namespace.equals("http://axis.de")) {
				return "ns1";
			}
			return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
		}

		/**
		 * field for _return This was an Array!
		 */

		protected java.lang.String[] local_return;

		/*
		 * This tracker boolean wil be used to detect whether the user called the set
		 * method for this attribute. It will be used to determine whether to include
		 * this field in the serialized XML
		 */
		protected boolean local_returnTracker = false;

		/**
		 * Auto generated getter method
		 * 
		 * @return java.lang.String[]
		 */
		public java.lang.String[] get_return() {
			return local_return;
		}

		/**
		 * validate the array for _return
		 */
		protected void validate_return(java.lang.String[] param) {

		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param _return
		 */
		public void set_return(java.lang.String[] param) {

			validate_return(param);

			if (param != null) {
				// update the setting tracker
				local_returnTracker = true;
			} else {
				local_returnTracker = true;

			}

			this.local_return = param;
		}

		/**
		 * Auto generated add method for the array for convenience
		 * 
		 * @param param java.lang.String
		 */
		public void add_return(java.lang.String param) {
			if (local_return == null) {
				local_return = new java.lang.String[] {};
			}

			// update the setting tracker
			local_returnTracker = true;

			java.util.List<String> list = org.apache.axis2.databinding.utils.ConverterUtil.toList(local_return);
			list.add(param);
			this.local_return = (java.lang.String[]) list.toArray(new java.lang.String[list.size()]);

		}

		/**
		 * isReaderMTOMAware
		 * 
		 * @return true if the reader supports MTOM
		 */
		public static boolean isReaderMTOMAware(javax.xml.stream.XMLStreamReader reader) {
			boolean isReaderMTOMAware = false;

			try {
				isReaderMTOMAware = java.lang.Boolean.TRUE
						.equals(reader.getProperty(org.apache.axiom.om.OMConstants.IS_DATA_HANDLERS_AWARE));
			} catch (java.lang.IllegalArgumentException e) {
				isReaderMTOMAware = false;
			}
			return isReaderMTOMAware;
		}

		/**
		 *
		 * @param parentQName
		 * @param factory
		 * @return org.apache.axiom.om.OMElement
		 */
		public org.apache.axiom.om.OMElement getOMElement(final javax.xml.namespace.QName parentQName,
				final org.apache.axiom.om.OMFactory factory) throws org.apache.axis2.databinding.ADBException {

			org.apache.axiom.om.OMDataSource dataSource = new org.apache.axis2.databinding.ADBDataSource(this,
					MY_QNAME) {

				public void serialize(org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter)
						throws javax.xml.stream.XMLStreamException {
					GetColumnNamesResponse.this.serialize(MY_QNAME, factory, xmlWriter);
				}
			};
			return new org.apache.axiom.om.impl.llom.OMSourcedElementImpl(MY_QNAME, factory, dataSource);

		}

		public void serialize(final javax.xml.namespace.QName parentQName, final org.apache.axiom.om.OMFactory factory,
				org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException {
			serialize(parentQName, factory, xmlWriter, false);
		}

		public void serialize(final javax.xml.namespace.QName parentQName, final org.apache.axiom.om.OMFactory factory,
				org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter, boolean serializeType)
				throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException {

			java.lang.String prefix = null;
			java.lang.String namespace = null;

			prefix = parentQName.getPrefix();
			namespace = parentQName.getNamespaceURI();

			if ((namespace != null) && (namespace.trim().length() > 0)) {
				java.lang.String writerPrefix = xmlWriter.getPrefix(namespace);
				if (writerPrefix != null) {
					xmlWriter.writeStartElement(namespace, parentQName.getLocalPart());
				} else {
					if (prefix == null) {
						prefix = generatePrefix(namespace);
					}

					xmlWriter.writeStartElement(prefix, parentQName.getLocalPart(), namespace);
					xmlWriter.writeNamespace(prefix, namespace);
					xmlWriter.setPrefix(prefix, namespace);
				}
			} else {
				xmlWriter.writeStartElement(parentQName.getLocalPart());
			}

			if (serializeType) {

				java.lang.String namespacePrefix = registerPrefix(xmlWriter, "http://axis.de");
				if ((namespacePrefix != null) && (namespacePrefix.trim().length() > 0)) {
					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type",
							namespacePrefix + ":getColumnNamesResponse", xmlWriter);
				} else {
					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type", "getColumnNamesResponse",
							xmlWriter);
				}

			}
			if (local_returnTracker) {
				if (local_return != null) {
					namespace = "http://axis.de";
					boolean emptyNamespace = namespace == null || namespace.length() == 0;
					prefix = emptyNamespace ? null : xmlWriter.getPrefix(namespace);
					for (int i = 0; i < local_return.length; i++) {

						if (local_return[i] != null) {

							if (!emptyNamespace) {
								if (prefix == null) {
									java.lang.String prefix2 = generatePrefix(namespace);

									xmlWriter.writeStartElement(prefix2, "return", namespace);
									xmlWriter.writeNamespace(prefix2, namespace);
									xmlWriter.setPrefix(prefix2, namespace);

								} else {
									xmlWriter.writeStartElement(namespace, "return");
								}

							} else {
								xmlWriter.writeStartElement("return");
							}

							xmlWriter.writeCharacters(
									org.apache.axis2.databinding.utils.ConverterUtil.convertToString(local_return[i]));

							xmlWriter.writeEndElement();

						} else {

							// write null attribute
							namespace = "http://axis.de";
							if (!namespace.equals("")) {
								prefix = xmlWriter.getPrefix(namespace);

								if (prefix == null) {
									prefix = generatePrefix(namespace);

									xmlWriter.writeStartElement(prefix, "return", namespace);
									xmlWriter.writeNamespace(prefix, namespace);
									xmlWriter.setPrefix(prefix, namespace);

								} else {
									xmlWriter.writeStartElement(namespace, "return");
								}

							} else {
								xmlWriter.writeStartElement("return");
							}
							writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
							xmlWriter.writeEndElement();

						}

					}
				} else {

					// write the null attribute
					// write null attribute
					java.lang.String namespace2 = "http://axis.de";
					if (!namespace2.equals("")) {
						java.lang.String prefix2 = xmlWriter.getPrefix(namespace2);

						if (prefix2 == null) {
							prefix2 = generatePrefix(namespace2);

							xmlWriter.writeStartElement(prefix2, "return", namespace2);
							xmlWriter.writeNamespace(prefix2, namespace2);
							xmlWriter.setPrefix(prefix2, namespace2);

						} else {
							xmlWriter.writeStartElement(namespace2, "return");
						}

					} else {
						xmlWriter.writeStartElement("return");
					}

					// write the nil attribute
					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
					xmlWriter.writeEndElement();

				}

			}
			xmlWriter.writeEndElement();

		}

		/**
		 * Util method to write an attribute with the ns prefix
		 */
		private void writeAttribute(java.lang.String prefix, java.lang.String namespace, java.lang.String attName,
				java.lang.String attValue, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException {
			if (xmlWriter.getPrefix(namespace) == null) {
				xmlWriter.writeNamespace(prefix, namespace);
				xmlWriter.setPrefix(prefix, namespace);

			}

			xmlWriter.writeAttribute(namespace, attName, attValue);

		}

		/**
		 * Register a namespace prefix
		 */
		private java.lang.String registerPrefix(javax.xml.stream.XMLStreamWriter xmlWriter, java.lang.String namespace)
				throws javax.xml.stream.XMLStreamException {
			java.lang.String prefix = xmlWriter.getPrefix(namespace);

			if (prefix == null) {
				prefix = generatePrefix(namespace);

				while (xmlWriter.getNamespaceContext().getNamespaceURI(prefix) != null) {
					prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
				}

				xmlWriter.writeNamespace(prefix, namespace);
				xmlWriter.setPrefix(prefix, namespace);
			}

			return prefix;
		}

		/**
		 * databinding method to get an XML representation of this object
		 *
		 */
		public javax.xml.stream.XMLStreamReader getPullParser(javax.xml.namespace.QName qName)
				throws org.apache.axis2.databinding.ADBException {

			java.util.ArrayList<Object> elementList = new java.util.ArrayList<Object>();
			java.util.ArrayList<Object> attribList = new java.util.ArrayList<Object>();

			if (local_returnTracker) {
				if (local_return != null) {
					for (int i = 0; i < local_return.length; i++) {

						if (local_return[i] != null) {
							elementList.add(new javax.xml.namespace.QName("http://axis.de", "return"));
							elementList.add(
									org.apache.axis2.databinding.utils.ConverterUtil.convertToString(local_return[i]));
						} else {

							elementList.add(new javax.xml.namespace.QName("http://axis.de", "return"));
							elementList.add(null);

						}

					}
				} else {

					elementList.add(new javax.xml.namespace.QName("http://axis.de", "return"));
					elementList.add(null);

				}

			}

			return new org.apache.axis2.databinding.utils.reader.ADBXMLStreamReaderImpl(qName, elementList.toArray(),
					attribList.toArray());

		}

		/**
		 * Factory class that keeps the parse method
		 */
		public static class Factory {

			/**
			 * static method to create the object Precondition: If this object is an
			 * element, the current or next start element starts this object and any
			 * intervening reader events are ignorable If this object is not an element, it
			 * is a complex type and the reader is at the event just after the outer start
			 * element Postcondition: If this object is an element, the reader is positioned
			 * at its end element If this object is a complex type, the reader is positioned
			 * at the end element of its outer element
			 */
			public static GetColumnNamesResponse parse(javax.xml.stream.XMLStreamReader reader)
					throws java.lang.Exception {
				GetColumnNamesResponse object = new GetColumnNamesResponse();

				java.lang.String nillableValue = null;
				try {

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type") != null) {
						java.lang.String fullTypeName = reader
								.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type");
						if (fullTypeName != null) {
							java.lang.String nsPrefix = null;
							if (fullTypeName.indexOf(":") > -1) {
								nsPrefix = fullTypeName.substring(0, fullTypeName.indexOf(":"));
							}
							nsPrefix = nsPrefix == null ? "" : nsPrefix;

							java.lang.String type = fullTypeName.substring(fullTypeName.indexOf(":") + 1);

							if (!"getColumnNamesResponse".equals(type)) {
								// find namespace for the prefix
								java.lang.String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
								return (GetColumnNamesResponse) ExtensionMapper.getTypeObject(nsUri, type, reader);
							}

						}

					}

					// Note all attributes that were handled. Used to differ normal attributes
					// from anyAttributes.
					reader.next();

					java.util.ArrayList<String> list1 = new java.util.ArrayList<String>();

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()
							&& new javax.xml.namespace.QName("http://axis.de", "return").equals(reader.getName())) {

						// Process the array and step past its final element's end.

						nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
						if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
							list1.add(null);

							reader.next();
						} else {
							list1.add(reader.getElementText());
						}
						// loop until we find a start element that is not part of this array
						boolean loopDone1 = false;
						while (!loopDone1) {
							// Ensure we are at the EndElement
							while (!reader.isEndElement()) {
								reader.next();
							}
							// Step out of this element
							reader.next();
							// Step to next element event.
							while (!reader.isStartElement() && !reader.isEndElement())
								reader.next();
							if (reader.isEndElement()) {
								// two continuous end elements means we are exiting the xml structure
								loopDone1 = true;
							} else {
								if (new javax.xml.namespace.QName("http://axis.de", "return")
										.equals(reader.getName())) {

									nillableValue = reader
											.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
									if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
										list1.add(null);

										reader.next();
									} else {
										list1.add(reader.getElementText());
									}
								} else {
									loopDone1 = true;
								}
							}
						}
						// call the converter utility to convert and set the array

						object.set_return((java.lang.String[]) list1.toArray(new java.lang.String[list1.size()]));

					} // End of if for expected property start element

					else {

					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement())
						// A start element we are not expecting indicates a trailing invalid property
						throw new org.apache.axis2.databinding.ADBException(
								"Unexpected subelement " + reader.getLocalName());

				} catch (javax.xml.stream.XMLStreamException e) {
					throw new java.lang.Exception(e);
				}

				return object;
			}

		}// end of factory class

	}

	public static class QueryDB implements org.apache.axis2.databinding.ADBBean {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public static final javax.xml.namespace.QName MY_QNAME = new javax.xml.namespace.QName("http://axis.de",
				"queryDB", "ns1");

		private static java.lang.String generatePrefix(java.lang.String namespace) {
			if (namespace.equals("http://axis.de")) {
				return "ns1";
			}
			return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
		}

		/**
		 * field for Query
		 */

		protected java.lang.String localQuery;

		/*
		 * This tracker boolean wil be used to detect whether the user called the set
		 * method for this attribute. It will be used to determine whether to include
		 * this field in the serialized XML
		 */
		protected boolean localQueryTracker = false;

		/**
		 * Auto generated getter method
		 * 
		 * @return java.lang.String
		 */
		public java.lang.String getQuery() {
			return localQuery;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param Query
		 */
		public void setQuery(java.lang.String param) {

			if (param != null) {
				// update the setting tracker
				localQueryTracker = true;
			} else {
				localQueryTracker = true;

			}

			this.localQuery = param;

		}

		/**
		 * field for Database
		 */

		protected java.lang.String localDatabase;

		/*
		 * This tracker boolean wil be used to detect whether the user called the set
		 * method for this attribute. It will be used to determine whether to include
		 * this field in the serialized XML
		 */
		protected boolean localDatabaseTracker = false;

		/**
		 * Auto generated getter method
		 * 
		 * @return java.lang.String
		 */
		public java.lang.String getDatabase() {
			return localDatabase;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param Database
		 */
		public void setDatabase(java.lang.String param) {

			if (param != null) {
				// update the setting tracker
				localDatabaseTracker = true;
			} else {
				localDatabaseTracker = true;

			}

			this.localDatabase = param;

		}

		/**
		 * isReaderMTOMAware
		 * 
		 * @return true if the reader supports MTOM
		 */
		public static boolean isReaderMTOMAware(javax.xml.stream.XMLStreamReader reader) {
			boolean isReaderMTOMAware = false;

			try {
				isReaderMTOMAware = java.lang.Boolean.TRUE
						.equals(reader.getProperty(org.apache.axiom.om.OMConstants.IS_DATA_HANDLERS_AWARE));
			} catch (java.lang.IllegalArgumentException e) {
				isReaderMTOMAware = false;
			}
			return isReaderMTOMAware;
		}

		/**
		 *
		 * @param parentQName
		 * @param factory
		 * @return org.apache.axiom.om.OMElement
		 */
		public org.apache.axiom.om.OMElement getOMElement(final javax.xml.namespace.QName parentQName,
				final org.apache.axiom.om.OMFactory factory) throws org.apache.axis2.databinding.ADBException {

			org.apache.axiom.om.OMDataSource dataSource = new org.apache.axis2.databinding.ADBDataSource(this,
					MY_QNAME) {

				public void serialize(org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter)
						throws javax.xml.stream.XMLStreamException {
					QueryDB.this.serialize(MY_QNAME, factory, xmlWriter);
				}
			};
			return new org.apache.axiom.om.impl.llom.OMSourcedElementImpl(MY_QNAME, factory, dataSource);

		}

		public void serialize(final javax.xml.namespace.QName parentQName, final org.apache.axiom.om.OMFactory factory,
				org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException {
			serialize(parentQName, factory, xmlWriter, false);
		}

		public void serialize(final javax.xml.namespace.QName parentQName, final org.apache.axiom.om.OMFactory factory,
				org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter, boolean serializeType)
				throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException {

			java.lang.String prefix = null;
			java.lang.String namespace = null;

			prefix = parentQName.getPrefix();
			namespace = parentQName.getNamespaceURI();

			if ((namespace != null) && (namespace.trim().length() > 0)) {
				java.lang.String writerPrefix = xmlWriter.getPrefix(namespace);
				if (writerPrefix != null) {
					xmlWriter.writeStartElement(namespace, parentQName.getLocalPart());
				} else {
					if (prefix == null) {
						prefix = generatePrefix(namespace);
					}

					xmlWriter.writeStartElement(prefix, parentQName.getLocalPart(), namespace);
					xmlWriter.writeNamespace(prefix, namespace);
					xmlWriter.setPrefix(prefix, namespace);
				}
			} else {
				xmlWriter.writeStartElement(parentQName.getLocalPart());
			}

			if (serializeType) {

				java.lang.String namespacePrefix = registerPrefix(xmlWriter, "http://axis.de");
				if ((namespacePrefix != null) && (namespacePrefix.trim().length() > 0)) {
					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type",
							namespacePrefix + ":queryDB", xmlWriter);
				} else {
					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type", "queryDB", xmlWriter);
				}

			}
			if (localQueryTracker) {
				namespace = "http://axis.de";
				if (!namespace.equals("")) {
					prefix = xmlWriter.getPrefix(namespace);

					if (prefix == null) {
						prefix = generatePrefix(namespace);

						xmlWriter.writeStartElement(prefix, "query", namespace);
						xmlWriter.writeNamespace(prefix, namespace);
						xmlWriter.setPrefix(prefix, namespace);

					} else {
						xmlWriter.writeStartElement(namespace, "query");
					}

				} else {
					xmlWriter.writeStartElement("query");
				}

				if (localQuery == null) {
					// write the nil attribute

					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);

				} else {

					xmlWriter.writeCharacters(localQuery);

				}

				xmlWriter.writeEndElement();
			}
			if (localDatabaseTracker) {
				namespace = "http://axis.de";
				if (!namespace.equals("")) {
					prefix = xmlWriter.getPrefix(namespace);

					if (prefix == null) {
						prefix = generatePrefix(namespace);

						xmlWriter.writeStartElement(prefix, "database", namespace);
						xmlWriter.writeNamespace(prefix, namespace);
						xmlWriter.setPrefix(prefix, namespace);

					} else {
						xmlWriter.writeStartElement(namespace, "database");
					}

				} else {
					xmlWriter.writeStartElement("database");
				}

				if (localDatabase == null) {
					// write the nil attribute

					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);

				} else {

					xmlWriter.writeCharacters(localDatabase);

				}

				xmlWriter.writeEndElement();
			}
			xmlWriter.writeEndElement();

		}

		/**
		 * Util method to write an attribute with the ns prefix
		 */
		private void writeAttribute(java.lang.String prefix, java.lang.String namespace, java.lang.String attName,
				java.lang.String attValue, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException {
			if (xmlWriter.getPrefix(namespace) == null) {
				xmlWriter.writeNamespace(prefix, namespace);
				xmlWriter.setPrefix(prefix, namespace);

			}

			xmlWriter.writeAttribute(namespace, attName, attValue);

		}

		/**
		 * Register a namespace prefix
		 */
		private java.lang.String registerPrefix(javax.xml.stream.XMLStreamWriter xmlWriter, java.lang.String namespace)
				throws javax.xml.stream.XMLStreamException {
			java.lang.String prefix = xmlWriter.getPrefix(namespace);

			if (prefix == null) {
				prefix = generatePrefix(namespace);

				while (xmlWriter.getNamespaceContext().getNamespaceURI(prefix) != null) {
					prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
				}

				xmlWriter.writeNamespace(prefix, namespace);
				xmlWriter.setPrefix(prefix, namespace);
			}

			return prefix;
		}

		/**
		 * databinding method to get an XML representation of this object
		 *
		 */
		public javax.xml.stream.XMLStreamReader getPullParser(javax.xml.namespace.QName qName)
				throws org.apache.axis2.databinding.ADBException {

			java.util.ArrayList<Object> elementList = new java.util.ArrayList<Object>();
			java.util.ArrayList<Object> attribList = new java.util.ArrayList<Object>();

			if (localQueryTracker) {
				elementList.add(new javax.xml.namespace.QName("http://axis.de", "query"));

				elementList.add(localQuery == null ? null
						: org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localQuery));
			}
			if (localDatabaseTracker) {
				elementList.add(new javax.xml.namespace.QName("http://axis.de", "database"));

				elementList.add(localDatabase == null ? null
						: org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localDatabase));
			}

			return new org.apache.axis2.databinding.utils.reader.ADBXMLStreamReaderImpl(qName, elementList.toArray(),
					attribList.toArray());

		}

		/**
		 * Factory class that keeps the parse method
		 */
		public static class Factory {

			/**
			 * static method to create the object Precondition: If this object is an
			 * element, the current or next start element starts this object and any
			 * intervening reader events are ignorable If this object is not an element, it
			 * is a complex type and the reader is at the event just after the outer start
			 * element Postcondition: If this object is an element, the reader is positioned
			 * at its end element If this object is a complex type, the reader is positioned
			 * at the end element of its outer element
			 */
			public static QueryDB parse(javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception {
				QueryDB object = new QueryDB();

				java.lang.String nillableValue = null;
				try {

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type") != null) {
						java.lang.String fullTypeName = reader
								.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type");
						if (fullTypeName != null) {
							java.lang.String nsPrefix = null;
							if (fullTypeName.indexOf(":") > -1) {
								nsPrefix = fullTypeName.substring(0, fullTypeName.indexOf(":"));
							}
							nsPrefix = nsPrefix == null ? "" : nsPrefix;

							java.lang.String type = fullTypeName.substring(fullTypeName.indexOf(":") + 1);

							if (!"queryDB".equals(type)) {
								// find namespace for the prefix
								java.lang.String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
								return (QueryDB) ExtensionMapper.getTypeObject(nsUri, type, reader);
							}

						}

					}

					// Note all attributes that were handled. Used to differ normal attributes
					// from anyAttributes.

					reader.next();

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()
							&& new javax.xml.namespace.QName("http://axis.de", "query").equals(reader.getName())) {

						nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
						if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {

							java.lang.String content = reader.getElementText();

							object.setQuery(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

						} else {

							reader.getElementText(); // throw away text nodes if any.
						}

						reader.next();

					} // End of if for expected property start element

					else {

					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()
							&& new javax.xml.namespace.QName("http://axis.de", "database").equals(reader.getName())) {

						nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
						if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {

							java.lang.String content = reader.getElementText();

							object.setDatabase(
									org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

						} else {

							reader.getElementText(); // throw away text nodes if any.
						}

						reader.next();

					} // End of if for expected property start element

					else {

					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement())
						// A start element we are not expecting indicates a trailing invalid property
						throw new org.apache.axis2.databinding.ADBException(
								"Unexpected subelement " + reader.getLocalName());

				} catch (javax.xml.stream.XMLStreamException e) {
					throw new java.lang.Exception(e);
				}

				return object;
			}

		}// end of factory class

	}

	public static class SQLExceptionE implements org.apache.axis2.databinding.ADBBean {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public static final javax.xml.namespace.QName MY_QNAME = new javax.xml.namespace.QName("http://axis.de",
				"SQLException", "ns1");

		private static java.lang.String generatePrefix(java.lang.String namespace) {
			if (namespace.equals("http://axis.de")) {
				return "ns1";
			}
			return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
		}

		/**
		 * field for SQLException
		 */

		protected SQLException localSQLException;

		/*
		 * This tracker boolean wil be used to detect whether the user called the set
		 * method for this attribute. It will be used to determine whether to include
		 * this field in the serialized XML
		 */
		protected boolean localSQLExceptionTracker = false;

		/**
		 * Auto generated getter method
		 * 
		 * @return SQLException
		 */
		public SQLException getSQLException() {
			return localSQLException;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param SQLException
		 */
		public void setSQLException(SQLException param) {

			if (param != null) {
				// update the setting tracker
				localSQLExceptionTracker = true;
			} else {
				localSQLExceptionTracker = true;

			}

			this.localSQLException = param;

		}

		/**
		 * isReaderMTOMAware
		 * 
		 * @return true if the reader supports MTOM
		 */
		public static boolean isReaderMTOMAware(javax.xml.stream.XMLStreamReader reader) {
			boolean isReaderMTOMAware = false;

			try {
				isReaderMTOMAware = java.lang.Boolean.TRUE
						.equals(reader.getProperty(org.apache.axiom.om.OMConstants.IS_DATA_HANDLERS_AWARE));
			} catch (java.lang.IllegalArgumentException e) {
				isReaderMTOMAware = false;
			}
			return isReaderMTOMAware;
		}

		/**
		 *
		 * @param parentQName
		 * @param factory
		 * @return org.apache.axiom.om.OMElement
		 */
		public org.apache.axiom.om.OMElement getOMElement(final javax.xml.namespace.QName parentQName,
				final org.apache.axiom.om.OMFactory factory) throws org.apache.axis2.databinding.ADBException {

			org.apache.axiom.om.OMDataSource dataSource = new org.apache.axis2.databinding.ADBDataSource(this,
					MY_QNAME) {

				public void serialize(org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter)
						throws javax.xml.stream.XMLStreamException {
					SQLExceptionE.this.serialize(MY_QNAME, factory, xmlWriter);
				}
			};
			return new org.apache.axiom.om.impl.llom.OMSourcedElementImpl(MY_QNAME, factory, dataSource);

		}

		public void serialize(final javax.xml.namespace.QName parentQName, final org.apache.axiom.om.OMFactory factory,
				org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException {
			serialize(parentQName, factory, xmlWriter, false);
		}

		public void serialize(final javax.xml.namespace.QName parentQName, final org.apache.axiom.om.OMFactory factory,
				org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter, boolean serializeType)
				throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException {

			java.lang.String prefix = null;
			java.lang.String namespace = null;

			prefix = parentQName.getPrefix();
			namespace = parentQName.getNamespaceURI();

			if ((namespace != null) && (namespace.trim().length() > 0)) {
				java.lang.String writerPrefix = xmlWriter.getPrefix(namespace);
				if (writerPrefix != null) {
					xmlWriter.writeStartElement(namespace, parentQName.getLocalPart());
				} else {
					if (prefix == null) {
						prefix = generatePrefix(namespace);
					}

					xmlWriter.writeStartElement(prefix, parentQName.getLocalPart(), namespace);
					xmlWriter.writeNamespace(prefix, namespace);
					xmlWriter.setPrefix(prefix, namespace);
				}
			} else {
				xmlWriter.writeStartElement(parentQName.getLocalPart());
			}

			if (serializeType) {

				java.lang.String namespacePrefix = registerPrefix(xmlWriter, "http://axis.de");
				if ((namespacePrefix != null) && (namespacePrefix.trim().length() > 0)) {
					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type",
							namespacePrefix + ":SQLException", xmlWriter);
				} else {
					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type", "SQLException",
							xmlWriter);
				}

			}
			if (localSQLExceptionTracker) {
				if (localSQLException == null) {

					java.lang.String namespace2 = "http://axis.de";

					if (!namespace2.equals("")) {
						java.lang.String prefix2 = xmlWriter.getPrefix(namespace2);

						if (prefix2 == null) {
							prefix2 = generatePrefix(namespace2);

							xmlWriter.writeStartElement(prefix2, "SQLException", namespace2);
							xmlWriter.writeNamespace(prefix2, namespace2);
							xmlWriter.setPrefix(prefix2, namespace2);

						} else {
							xmlWriter.writeStartElement(namespace2, "SQLException");
						}

					} else {
						xmlWriter.writeStartElement("SQLException");
					}

					// write the nil attribute
					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
					xmlWriter.writeEndElement();
				} else {
					localSQLException.serialize(new javax.xml.namespace.QName("http://axis.de", "SQLException"),
							factory, xmlWriter);
				}
			}
			xmlWriter.writeEndElement();

		}

		/**
		 * Util method to write an attribute with the ns prefix
		 */
		private void writeAttribute(java.lang.String prefix, java.lang.String namespace, java.lang.String attName,
				java.lang.String attValue, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException {
			if (xmlWriter.getPrefix(namespace) == null) {
				xmlWriter.writeNamespace(prefix, namespace);
				xmlWriter.setPrefix(prefix, namespace);

			}

			xmlWriter.writeAttribute(namespace, attName, attValue);

		}

		/**
		 * Register a namespace prefix
		 */
		private java.lang.String registerPrefix(javax.xml.stream.XMLStreamWriter xmlWriter, java.lang.String namespace)
				throws javax.xml.stream.XMLStreamException {
			java.lang.String prefix = xmlWriter.getPrefix(namespace);

			if (prefix == null) {
				prefix = generatePrefix(namespace);

				while (xmlWriter.getNamespaceContext().getNamespaceURI(prefix) != null) {
					prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
				}

				xmlWriter.writeNamespace(prefix, namespace);
				xmlWriter.setPrefix(prefix, namespace);
			}

			return prefix;
		}

		/**
		 * databinding method to get an XML representation of this object
		 *
		 */
		public javax.xml.stream.XMLStreamReader getPullParser(javax.xml.namespace.QName qName)
				throws org.apache.axis2.databinding.ADBException {

			java.util.ArrayList<Object> elementList = new java.util.ArrayList<Object>();
			java.util.ArrayList<Object> attribList = new java.util.ArrayList<Object>();

			if (localSQLExceptionTracker) {
				elementList.add(new javax.xml.namespace.QName("http://axis.de", "SQLException"));

				elementList.add(localSQLException == null ? null : localSQLException);
			}

			return new org.apache.axis2.databinding.utils.reader.ADBXMLStreamReaderImpl(qName, elementList.toArray(),
					attribList.toArray());

		}

		/**
		 * Factory class that keeps the parse method
		 */
		public static class Factory {

			/**
			 * static method to create the object Precondition: If this object is an
			 * element, the current or next start element starts this object and any
			 * intervening reader events are ignorable If this object is not an element, it
			 * is a complex type and the reader is at the event just after the outer start
			 * element Postcondition: If this object is an element, the reader is positioned
			 * at its end element If this object is a complex type, the reader is positioned
			 * at the end element of its outer element
			 */
			public static SQLExceptionE parse(javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception {
				SQLExceptionE object = new SQLExceptionE();

				java.lang.String nillableValue = null;
				try {

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
					if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
						// Skip the element and report the null value. It cannot have subelements.
						while (!reader.isEndElement())
							reader.next();

						return null;

					}

					if (reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type") != null) {
						java.lang.String fullTypeName = reader
								.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type");
						if (fullTypeName != null) {
							java.lang.String nsPrefix = null;
							if (fullTypeName.indexOf(":") > -1) {
								nsPrefix = fullTypeName.substring(0, fullTypeName.indexOf(":"));
							}
							nsPrefix = nsPrefix == null ? "" : nsPrefix;

							java.lang.String type = fullTypeName.substring(fullTypeName.indexOf(":") + 1);

							if (!"SQLException".equals(type)) {
								// find namespace for the prefix
								java.lang.String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
								return (SQLExceptionE) ExtensionMapper.getTypeObject(nsUri, type, reader);
							}

						}

					}

					// Note all attributes that were handled. Used to differ normal attributes
					// from anyAttributes.

					reader.next();

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement() && new javax.xml.namespace.QName("http://axis.de", "SQLException")
							.equals(reader.getName())) {

						nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
						if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
							object.setSQLException(null);
							reader.next();

							reader.next();

						} else {

							object.setSQLException(SQLException.Factory.parse(reader));

							reader.next();
						}
					} // End of if for expected property start element

					else {

					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement())
						// A start element we are not expecting indicates a trailing invalid property
						throw new org.apache.axis2.databinding.ADBException(
								"Unexpected subelement " + reader.getLocalName());

				} catch (javax.xml.stream.XMLStreamException e) {
					throw new java.lang.Exception(e);
				}

				return object;
			}

		}// end of factory class

	}

	public static class PreparedQueryDB implements org.apache.axis2.databinding.ADBBean {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public static final javax.xml.namespace.QName MY_QNAME = new javax.xml.namespace.QName("http://axis.de",
				"preparedQueryDB", "ns1");

		private static java.lang.String generatePrefix(java.lang.String namespace) {
			if (namespace.equals("http://axis.de")) {
				return "ns1";
			}
			return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
		}

		/**
		 * field for Query
		 */

		protected PreparedQuery localQuery;

		/*
		 * This tracker boolean wil be used to detect whether the user called the set
		 * method for this attribute. It will be used to determine whether to include
		 * this field in the serialized XML
		 */
		protected boolean localQueryTracker = false;

		/**
		 * Auto generated getter method
		 * 
		 * @return PreparedQuery
		 */
		public PreparedQuery getQuery() {
			return localQuery;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param Query
		 */
		public void setQuery(PreparedQuery param) {

			if (param != null) {
				// update the setting tracker
				localQueryTracker = true;
			} else {
				localQueryTracker = true;

			}

			this.localQuery = param;

		}

		/**
		 * field for Database
		 */

		protected java.lang.String localDatabase;

		/*
		 * This tracker boolean wil be used to detect whether the user called the set
		 * method for this attribute. It will be used to determine whether to include
		 * this field in the serialized XML
		 */
		protected boolean localDatabaseTracker = false;

		/**
		 * Auto generated getter method
		 * 
		 * @return java.lang.String
		 */
		public java.lang.String getDatabase() {
			return localDatabase;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param Database
		 */
		public void setDatabase(java.lang.String param) {

			if (param != null) {
				// update the setting tracker
				localDatabaseTracker = true;
			} else {
				localDatabaseTracker = true;

			}

			this.localDatabase = param;

		}

		/**
		 * isReaderMTOMAware
		 * 
		 * @return true if the reader supports MTOM
		 */
		public static boolean isReaderMTOMAware(javax.xml.stream.XMLStreamReader reader) {
			boolean isReaderMTOMAware = false;

			try {
				isReaderMTOMAware = java.lang.Boolean.TRUE
						.equals(reader.getProperty(org.apache.axiom.om.OMConstants.IS_DATA_HANDLERS_AWARE));
			} catch (java.lang.IllegalArgumentException e) {
				isReaderMTOMAware = false;
			}
			return isReaderMTOMAware;
		}

		/**
		 *
		 * @param parentQName
		 * @param factory
		 * @return org.apache.axiom.om.OMElement
		 */
		public org.apache.axiom.om.OMElement getOMElement(final javax.xml.namespace.QName parentQName,
				final org.apache.axiom.om.OMFactory factory) throws org.apache.axis2.databinding.ADBException {

			org.apache.axiom.om.OMDataSource dataSource = new org.apache.axis2.databinding.ADBDataSource(this,
					MY_QNAME) {

				public void serialize(org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter)
						throws javax.xml.stream.XMLStreamException {
					PreparedQueryDB.this.serialize(MY_QNAME, factory, xmlWriter);
				}
			};
			return new org.apache.axiom.om.impl.llom.OMSourcedElementImpl(MY_QNAME, factory, dataSource);

		}

		public void serialize(final javax.xml.namespace.QName parentQName, final org.apache.axiom.om.OMFactory factory,
				org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException {
			serialize(parentQName, factory, xmlWriter, false);
		}

		public void serialize(final javax.xml.namespace.QName parentQName, final org.apache.axiom.om.OMFactory factory,
				org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter, boolean serializeType)
				throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException {

			java.lang.String prefix = null;
			java.lang.String namespace = null;

			prefix = parentQName.getPrefix();
			namespace = parentQName.getNamespaceURI();

			if ((namespace != null) && (namespace.trim().length() > 0)) {
				java.lang.String writerPrefix = xmlWriter.getPrefix(namespace);
				if (writerPrefix != null) {
					xmlWriter.writeStartElement(namespace, parentQName.getLocalPart());
				} else {
					if (prefix == null) {
						prefix = generatePrefix(namespace);
					}

					xmlWriter.writeStartElement(prefix, parentQName.getLocalPart(), namespace);
					xmlWriter.writeNamespace(prefix, namespace);
					xmlWriter.setPrefix(prefix, namespace);
				}
			} else {
				xmlWriter.writeStartElement(parentQName.getLocalPart());
			}

			if (serializeType) {

				java.lang.String namespacePrefix = registerPrefix(xmlWriter, "http://axis.de");
				if ((namespacePrefix != null) && (namespacePrefix.trim().length() > 0)) {
					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type",
							namespacePrefix + ":preparedQueryDB", xmlWriter);
				} else {
					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type", "preparedQueryDB",
							xmlWriter);
				}

			}
			if (localQueryTracker) {
				if (localQuery == null) {

					java.lang.String namespace2 = "http://axis.de";

					if (!namespace2.equals("")) {
						java.lang.String prefix2 = xmlWriter.getPrefix(namespace2);

						if (prefix2 == null) {
							prefix2 = generatePrefix(namespace2);

							xmlWriter.writeStartElement(prefix2, "query", namespace2);
							xmlWriter.writeNamespace(prefix2, namespace2);
							xmlWriter.setPrefix(prefix2, namespace2);

						} else {
							xmlWriter.writeStartElement(namespace2, "query");
						}

					} else {
						xmlWriter.writeStartElement("query");
					}

					// write the nil attribute
					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
					xmlWriter.writeEndElement();
				} else {
					localQuery.serialize(new javax.xml.namespace.QName("http://axis.de", "query"), factory, xmlWriter);
				}
			}
			if (localDatabaseTracker) {
				namespace = "http://axis.de";
				if (!namespace.equals("")) {
					prefix = xmlWriter.getPrefix(namespace);

					if (prefix == null) {
						prefix = generatePrefix(namespace);

						xmlWriter.writeStartElement(prefix, "database", namespace);
						xmlWriter.writeNamespace(prefix, namespace);
						xmlWriter.setPrefix(prefix, namespace);

					} else {
						xmlWriter.writeStartElement(namespace, "database");
					}

				} else {
					xmlWriter.writeStartElement("database");
				}

				if (localDatabase == null) {
					// write the nil attribute

					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);

				} else {

					xmlWriter.writeCharacters(localDatabase);

				}

				xmlWriter.writeEndElement();
			}
			xmlWriter.writeEndElement();

		}

		/**
		 * Util method to write an attribute with the ns prefix
		 */
		private void writeAttribute(java.lang.String prefix, java.lang.String namespace, java.lang.String attName,
				java.lang.String attValue, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException {
			if (xmlWriter.getPrefix(namespace) == null) {
				xmlWriter.writeNamespace(prefix, namespace);
				xmlWriter.setPrefix(prefix, namespace);

			}

			xmlWriter.writeAttribute(namespace, attName, attValue);

		}

		/**
		 * Register a namespace prefix
		 */
		private java.lang.String registerPrefix(javax.xml.stream.XMLStreamWriter xmlWriter, java.lang.String namespace)
				throws javax.xml.stream.XMLStreamException {
			java.lang.String prefix = xmlWriter.getPrefix(namespace);

			if (prefix == null) {
				prefix = generatePrefix(namespace);

				while (xmlWriter.getNamespaceContext().getNamespaceURI(prefix) != null) {
					prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
				}

				xmlWriter.writeNamespace(prefix, namespace);
				xmlWriter.setPrefix(prefix, namespace);
			}

			return prefix;
		}

		/**
		 * databinding method to get an XML representation of this object
		 *
		 */
		public javax.xml.stream.XMLStreamReader getPullParser(javax.xml.namespace.QName qName)
				throws org.apache.axis2.databinding.ADBException {

			java.util.ArrayList<Object> elementList = new java.util.ArrayList<Object>();
			java.util.ArrayList<Object> attribList = new java.util.ArrayList<Object>();

			if (localQueryTracker) {
				elementList.add(new javax.xml.namespace.QName("http://axis.de", "query"));

				elementList.add(localQuery == null ? null : localQuery);
			}
			if (localDatabaseTracker) {
				elementList.add(new javax.xml.namespace.QName("http://axis.de", "database"));

				elementList.add(localDatabase == null ? null
						: org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localDatabase));
			}

			return new org.apache.axis2.databinding.utils.reader.ADBXMLStreamReaderImpl(qName, elementList.toArray(),
					attribList.toArray());

		}

		/**
		 * Factory class that keeps the parse method
		 */
		public static class Factory {

			/**
			 * static method to create the object Precondition: If this object is an
			 * element, the current or next start element starts this object and any
			 * intervening reader events are ignorable If this object is not an element, it
			 * is a complex type and the reader is at the event just after the outer start
			 * element Postcondition: If this object is an element, the reader is positioned
			 * at its end element If this object is a complex type, the reader is positioned
			 * at the end element of its outer element
			 */
			public static PreparedQueryDB parse(javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception {
				PreparedQueryDB object = new PreparedQueryDB();

				java.lang.String nillableValue = null;
				try {

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type") != null) {
						java.lang.String fullTypeName = reader
								.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type");
						if (fullTypeName != null) {
							java.lang.String nsPrefix = null;
							if (fullTypeName.indexOf(":") > -1) {
								nsPrefix = fullTypeName.substring(0, fullTypeName.indexOf(":"));
							}
							nsPrefix = nsPrefix == null ? "" : nsPrefix;

							java.lang.String type = fullTypeName.substring(fullTypeName.indexOf(":") + 1);

							if (!"preparedQueryDB".equals(type)) {
								// find namespace for the prefix
								java.lang.String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
								return (PreparedQueryDB) ExtensionMapper.getTypeObject(nsUri, type, reader);
							}

						}

					}

					// Note all attributes that were handled. Used to differ normal attributes
					// from anyAttributes.

					reader.next();

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()
							&& new javax.xml.namespace.QName("http://axis.de", "query").equals(reader.getName())) {

						nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
						if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
							object.setQuery(null);
							reader.next();

							reader.next();

						} else {

							object.setQuery(PreparedQuery.Factory.parse(reader));

							reader.next();
						}
					} // End of if for expected property start element

					else {

					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()
							&& new javax.xml.namespace.QName("http://axis.de", "database").equals(reader.getName())) {

						nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
						if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {

							java.lang.String content = reader.getElementText();

							object.setDatabase(
									org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

						} else {

							reader.getElementText(); // throw away text nodes if any.
						}

						reader.next();

					} // End of if for expected property start element

					else {

					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement())
						// A start element we are not expecting indicates a trailing invalid property
						throw new org.apache.axis2.databinding.ADBException(
								"Unexpected subelement " + reader.getLocalName());

				} catch (javax.xml.stream.XMLStreamException e) {
					throw new java.lang.Exception(e);
				}

				return object;
			}

		}// end of factory class

	}

	public static class DBColumn implements org.apache.axis2.databinding.ADBBean {
		/*
		 * This type was generated from the piece of schema that had name = DBColumn
		 * Namespace URI = http://axis.de/xsd Namespace Prefix = ns3
		 */

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		private static java.lang.String generatePrefix(java.lang.String namespace) {
			if (namespace.equals("http://axis.de/xsd")) {
				return "ns3";
			}
			return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
		}

		/**
		 * field for Column This was an Array!
		 */

		protected java.lang.String[] localColumn;

		/*
		 * This tracker boolean wil be used to detect whether the user called the set
		 * method for this attribute. It will be used to determine whether to include
		 * this field in the serialized XML
		 */
		protected boolean localColumnTracker = false;

		/**
		 * Auto generated getter method
		 * 
		 * @return java.lang.String[]
		 */
		public java.lang.String[] getColumn() {
			return localColumn;
		}

		/**
		 * validate the array for Column
		 */
		protected void validateColumn(java.lang.String[] param) {

		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param Column
		 */
		public void setColumn(java.lang.String[] param) {

			validateColumn(param);

			if (param != null) {
				// update the setting tracker
				localColumnTracker = true;
			} else {
				localColumnTracker = true;

			}

			this.localColumn = param;
		}

		/**
		 * Auto generated add method for the array for convenience
		 * 
		 * @param param java.lang.String
		 */
		public void addColumn(java.lang.String param) {
			if (localColumn == null) {
				localColumn = new java.lang.String[] {};
			}

			// update the setting tracker
			localColumnTracker = true;

			java.util.List<String> list = org.apache.axis2.databinding.utils.ConverterUtil.toList(localColumn);
			list.add(param);
			this.localColumn = (java.lang.String[]) list.toArray(new java.lang.String[list.size()]);

		}

		/**
		 * field for Lenght
		 */

		protected int localLenght;

		/*
		 * This tracker boolean wil be used to detect whether the user called the set
		 * method for this attribute. It will be used to determine whether to include
		 * this field in the serialized XML
		 */
		protected boolean localLenghtTracker = false;

		/**
		 * Auto generated getter method
		 * 
		 * @return int
		 */
		public int getLenght() {
			return localLenght;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param Lenght
		 */
		public void setLenght(int param) {

			// setting primitive attribute tracker to true

			if (param == java.lang.Integer.MIN_VALUE) {
				localLenghtTracker = false;

			} else {
				localLenghtTracker = true;
			}

			this.localLenght = param;

		}

		/**
		 * isReaderMTOMAware
		 * 
		 * @return true if the reader supports MTOM
		 */
		public static boolean isReaderMTOMAware(javax.xml.stream.XMLStreamReader reader) {
			boolean isReaderMTOMAware = false;

			try {
				isReaderMTOMAware = java.lang.Boolean.TRUE
						.equals(reader.getProperty(org.apache.axiom.om.OMConstants.IS_DATA_HANDLERS_AWARE));
			} catch (java.lang.IllegalArgumentException e) {
				isReaderMTOMAware = false;
			}
			return isReaderMTOMAware;
		}

		/**
		 *
		 * @param parentQName
		 * @param factory
		 * @return org.apache.axiom.om.OMElement
		 */
		public org.apache.axiom.om.OMElement getOMElement(final javax.xml.namespace.QName parentQName,
				final org.apache.axiom.om.OMFactory factory) throws org.apache.axis2.databinding.ADBException {

			org.apache.axiom.om.OMDataSource dataSource = new org.apache.axis2.databinding.ADBDataSource(this,
					parentQName) {

				public void serialize(org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter)
						throws javax.xml.stream.XMLStreamException {
					DBColumn.this.serialize(parentQName, factory, xmlWriter);
				}
			};
			return new org.apache.axiom.om.impl.llom.OMSourcedElementImpl(parentQName, factory, dataSource);

		}

		public void serialize(final javax.xml.namespace.QName parentQName, final org.apache.axiom.om.OMFactory factory,
				org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException {
			serialize(parentQName, factory, xmlWriter, false);
		}

		public void serialize(final javax.xml.namespace.QName parentQName, final org.apache.axiom.om.OMFactory factory,
				org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter, boolean serializeType)
				throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException {

			java.lang.String prefix = null;
			java.lang.String namespace = null;

			prefix = parentQName.getPrefix();
			namespace = parentQName.getNamespaceURI();

			if ((namespace != null) && (namespace.trim().length() > 0)) {
				java.lang.String writerPrefix = xmlWriter.getPrefix(namespace);
				if (writerPrefix != null) {
					xmlWriter.writeStartElement(namespace, parentQName.getLocalPart());
				} else {
					if (prefix == null) {
						prefix = generatePrefix(namespace);
					}

					xmlWriter.writeStartElement(prefix, parentQName.getLocalPart(), namespace);
					xmlWriter.writeNamespace(prefix, namespace);
					xmlWriter.setPrefix(prefix, namespace);
				}
			} else {
				xmlWriter.writeStartElement(parentQName.getLocalPart());
			}

			if (serializeType) {

				java.lang.String namespacePrefix = registerPrefix(xmlWriter, "http://axis.de/xsd");
				if ((namespacePrefix != null) && (namespacePrefix.trim().length() > 0)) {
					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type",
							namespacePrefix + ":DBColumn", xmlWriter);
				} else {
					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type", "DBColumn", xmlWriter);
				}

			}
			if (localColumnTracker) {
				if (localColumn != null) {
					namespace = "http://axis.de/xsd";
					boolean emptyNamespace = namespace == null || namespace.length() == 0;
					prefix = emptyNamespace ? null : xmlWriter.getPrefix(namespace);
					for (int i = 0; i < localColumn.length; i++) {

						if (localColumn[i] != null) {

							if (!emptyNamespace) {
								if (prefix == null) {
									java.lang.String prefix2 = generatePrefix(namespace);

									xmlWriter.writeStartElement(prefix2, "column", namespace);
									xmlWriter.writeNamespace(prefix2, namespace);
									xmlWriter.setPrefix(prefix2, namespace);

								} else {
									xmlWriter.writeStartElement(namespace, "column");
								}

							} else {
								xmlWriter.writeStartElement("column");
							}

							xmlWriter.writeCharacters(
									org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localColumn[i]));

							xmlWriter.writeEndElement();

						} else {

							// write null attribute
							namespace = "http://axis.de/xsd";
							if (!namespace.equals("")) {
								prefix = xmlWriter.getPrefix(namespace);

								if (prefix == null) {
									prefix = generatePrefix(namespace);

									xmlWriter.writeStartElement(prefix, "column", namespace);
									xmlWriter.writeNamespace(prefix, namespace);
									xmlWriter.setPrefix(prefix, namespace);

								} else {
									xmlWriter.writeStartElement(namespace, "column");
								}

							} else {
								xmlWriter.writeStartElement("column");
							}
							writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
							xmlWriter.writeEndElement();

						}

					}
				} else {

					// write the null attribute
					// write null attribute
					java.lang.String namespace2 = "http://axis.de/xsd";
					if (!namespace2.equals("")) {
						java.lang.String prefix2 = xmlWriter.getPrefix(namespace2);

						if (prefix2 == null) {
							prefix2 = generatePrefix(namespace2);

							xmlWriter.writeStartElement(prefix2, "column", namespace2);
							xmlWriter.writeNamespace(prefix2, namespace2);
							xmlWriter.setPrefix(prefix2, namespace2);

						} else {
							xmlWriter.writeStartElement(namespace2, "column");
						}

					} else {
						xmlWriter.writeStartElement("column");
					}

					// write the nil attribute
					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
					xmlWriter.writeEndElement();

				}

			}
			if (localLenghtTracker) {
				namespace = "http://axis.de/xsd";
				if (!namespace.equals("")) {
					prefix = xmlWriter.getPrefix(namespace);

					if (prefix == null) {
						prefix = generatePrefix(namespace);

						xmlWriter.writeStartElement(prefix, "lenght", namespace);
						xmlWriter.writeNamespace(prefix, namespace);
						xmlWriter.setPrefix(prefix, namespace);

					} else {
						xmlWriter.writeStartElement(namespace, "lenght");
					}

				} else {
					xmlWriter.writeStartElement("lenght");
				}

				if (localLenght == java.lang.Integer.MIN_VALUE) {

					throw new org.apache.axis2.databinding.ADBException("lenght cannot be null!!");

				} else {
					xmlWriter.writeCharacters(
							org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localLenght));
				}

				xmlWriter.writeEndElement();
			}
			xmlWriter.writeEndElement();

		}

		/**
		 * Util method to write an attribute with the ns prefix
		 */
		private void writeAttribute(java.lang.String prefix, java.lang.String namespace, java.lang.String attName,
				java.lang.String attValue, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException {
			if (xmlWriter.getPrefix(namespace) == null) {
				xmlWriter.writeNamespace(prefix, namespace);
				xmlWriter.setPrefix(prefix, namespace);

			}

			xmlWriter.writeAttribute(namespace, attName, attValue);

		}

		/**
		 * Register a namespace prefix
		 */
		private java.lang.String registerPrefix(javax.xml.stream.XMLStreamWriter xmlWriter, java.lang.String namespace)
				throws javax.xml.stream.XMLStreamException {
			java.lang.String prefix = xmlWriter.getPrefix(namespace);

			if (prefix == null) {
				prefix = generatePrefix(namespace);

				while (xmlWriter.getNamespaceContext().getNamespaceURI(prefix) != null) {
					prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
				}

				xmlWriter.writeNamespace(prefix, namespace);
				xmlWriter.setPrefix(prefix, namespace);
			}

			return prefix;
		}

		/**
		 * databinding method to get an XML representation of this object
		 *
		 */
		public javax.xml.stream.XMLStreamReader getPullParser(javax.xml.namespace.QName qName)
				throws org.apache.axis2.databinding.ADBException {

			java.util.ArrayList<Object> elementList = new java.util.ArrayList<Object>();
			java.util.ArrayList<Object> attribList = new java.util.ArrayList<Object>();

			if (localColumnTracker) {
				if (localColumn != null) {
					for (int i = 0; i < localColumn.length; i++) {

						if (localColumn[i] != null) {
							elementList.add(new javax.xml.namespace.QName("http://axis.de/xsd", "column"));
							elementList.add(
									org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localColumn[i]));
						} else {

							elementList.add(new javax.xml.namespace.QName("http://axis.de/xsd", "column"));
							elementList.add(null);

						}

					}
				} else {

					elementList.add(new javax.xml.namespace.QName("http://axis.de/xsd", "column"));
					elementList.add(null);

				}

			}
			if (localLenghtTracker) {
				elementList.add(new javax.xml.namespace.QName("http://axis.de/xsd", "lenght"));

				elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localLenght));
			}

			return new org.apache.axis2.databinding.utils.reader.ADBXMLStreamReaderImpl(qName, elementList.toArray(),
					attribList.toArray());

		}

		/**
		 * Factory class that keeps the parse method
		 */
		public static class Factory {

			/**
			 * static method to create the object Precondition: If this object is an
			 * element, the current or next start element starts this object and any
			 * intervening reader events are ignorable If this object is not an element, it
			 * is a complex type and the reader is at the event just after the outer start
			 * element Postcondition: If this object is an element, the reader is positioned
			 * at its end element If this object is a complex type, the reader is positioned
			 * at the end element of its outer element
			 */
			public static DBColumn parse(javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception {
				DBColumn object = new DBColumn();

				java.lang.String nillableValue = null;
				try {

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type") != null) {
						java.lang.String fullTypeName = reader
								.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type");
						if (fullTypeName != null) {
							java.lang.String nsPrefix = null;
							if (fullTypeName.indexOf(":") > -1) {
								nsPrefix = fullTypeName.substring(0, fullTypeName.indexOf(":"));
							}
							nsPrefix = nsPrefix == null ? "" : nsPrefix;

							java.lang.String type = fullTypeName.substring(fullTypeName.indexOf(":") + 1);

							if (!"DBColumn".equals(type)) {
								// find namespace for the prefix
								java.lang.String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
								return (DBColumn) ExtensionMapper.getTypeObject(nsUri, type, reader);
							}

						}

					}

					// Note all attributes that were handled. Used to differ normal attributes
					// from anyAttributes.

					reader.next();

					java.util.ArrayList<String> list1 = new java.util.ArrayList<String>();

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()
							&& new javax.xml.namespace.QName("http://axis.de/xsd", "column").equals(reader.getName())) {

						// Process the array and step past its final element's end.

						nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
						if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
							list1.add(null);

							reader.next();
						} else {
							list1.add(reader.getElementText());
						}
						// loop until we find a start element that is not part of this array
						boolean loopDone1 = false;
						while (!loopDone1) {
							// Ensure we are at the EndElement
							while (!reader.isEndElement()) {
								reader.next();
							}
							// Step out of this element
							reader.next();
							// Step to next element event.
							while (!reader.isStartElement() && !reader.isEndElement())
								reader.next();
							if (reader.isEndElement()) {
								// two continuous end elements means we are exiting the xml structure
								loopDone1 = true;
							} else {
								if (new javax.xml.namespace.QName("http://axis.de/xsd", "column")
										.equals(reader.getName())) {

									nillableValue = reader
											.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
									if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
										list1.add(null);

										reader.next();
									} else {
										list1.add(reader.getElementText());
									}
								} else {
									loopDone1 = true;
								}
							}
						}
						// call the converter utility to convert and set the array

						object.setColumn((java.lang.String[]) list1.toArray(new java.lang.String[list1.size()]));

					} // End of if for expected property start element

					else {

					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()
							&& new javax.xml.namespace.QName("http://axis.de/xsd", "lenght").equals(reader.getName())) {

						java.lang.String content = reader.getElementText();

						object.setLenght(org.apache.axis2.databinding.utils.ConverterUtil.convertToInt(content));

						reader.next();

					} // End of if for expected property start element

					else {

						object.setLenght(java.lang.Integer.MIN_VALUE);

					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement())
						// A start element we are not expecting indicates a trailing invalid property
						throw new org.apache.axis2.databinding.ADBException(
								"Unexpected subelement " + reader.getLocalName());

				} catch (javax.xml.stream.XMLStreamException e) {
					throw new java.lang.Exception(e);
				}

				return object;
			}

		}// end of factory class

	}

	public static class PreparedQuery implements org.apache.axis2.databinding.ADBBean {
		/*
		 * This type was generated from the piece of schema that had name =
		 * PreparedQuery Namespace URI = http://axis.de/xsd Namespace Prefix = ns3
		 */

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		private static java.lang.String generatePrefix(java.lang.String namespace) {
			if (namespace.equals("http://axis.de/xsd")) {
				return "ns3";
			}
			return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
		}

		/**
		 * field for Parameters This was an Array!
		 */

		protected java.lang.String[] localParameters;

		/*
		 * This tracker boolean wil be used to detect whether the user called the set
		 * method for this attribute. It will be used to determine whether to include
		 * this field in the serialized XML
		 */
		protected boolean localParametersTracker = false;

		/**
		 * Auto generated getter method
		 * 
		 * @return java.lang.String[]
		 */
		public java.lang.String[] getParameters() {
			return localParameters;
		}

		/**
		 * validate the array for Parameters
		 */
		protected void validateParameters(java.lang.String[] param) {

		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param Parameters
		 */
		public void setParameters(java.lang.String[] param) {

			validateParameters(param);

			if (param != null) {
				// update the setting tracker
				localParametersTracker = true;
			} else {
				localParametersTracker = true;

			}

			this.localParameters = param;
		}

		/**
		 * Auto generated add method for the array for convenience
		 * 
		 * @param param java.lang.String
		 */
		public void addParameters(java.lang.String param) {
			if (localParameters == null) {
				localParameters = new java.lang.String[] {};
			}

			// update the setting tracker
			localParametersTracker = true;

			java.util.List<String> list = org.apache.axis2.databinding.utils.ConverterUtil.toList(localParameters);
			list.add(param);
			this.localParameters = (java.lang.String[]) list.toArray(new java.lang.String[list.size()]);

		}

		/**
		 * field for Query
		 */

		protected java.lang.String localQuery;

		/*
		 * This tracker boolean wil be used to detect whether the user called the set
		 * method for this attribute. It will be used to determine whether to include
		 * this field in the serialized XML
		 */
		protected boolean localQueryTracker = false;

		/**
		 * Auto generated getter method
		 * 
		 * @return java.lang.String
		 */
		public java.lang.String getQuery() {
			return localQuery;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param Query
		 */
		public void setQuery(java.lang.String param) {

			if (param != null) {
				// update the setting tracker
				localQueryTracker = true;
			} else {
				localQueryTracker = true;

			}

			this.localQuery = param;

		}

		/**
		 * isReaderMTOMAware
		 * 
		 * @return true if the reader supports MTOM
		 */
		public static boolean isReaderMTOMAware(javax.xml.stream.XMLStreamReader reader) {
			boolean isReaderMTOMAware = false;

			try {
				isReaderMTOMAware = java.lang.Boolean.TRUE
						.equals(reader.getProperty(org.apache.axiom.om.OMConstants.IS_DATA_HANDLERS_AWARE));
			} catch (java.lang.IllegalArgumentException e) {
				isReaderMTOMAware = false;
			}
			return isReaderMTOMAware;
		}

		/**
		 *
		 * @param parentQName
		 * @param factory
		 * @return org.apache.axiom.om.OMElement
		 */
		public org.apache.axiom.om.OMElement getOMElement(final javax.xml.namespace.QName parentQName,
				final org.apache.axiom.om.OMFactory factory) throws org.apache.axis2.databinding.ADBException {

			org.apache.axiom.om.OMDataSource dataSource = new org.apache.axis2.databinding.ADBDataSource(this,
					parentQName) {

				public void serialize(org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter)
						throws javax.xml.stream.XMLStreamException {
					PreparedQuery.this.serialize(parentQName, factory, xmlWriter);
				}
			};
			return new org.apache.axiom.om.impl.llom.OMSourcedElementImpl(parentQName, factory, dataSource);

		}

		public void serialize(final javax.xml.namespace.QName parentQName, final org.apache.axiom.om.OMFactory factory,
				org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException {
			serialize(parentQName, factory, xmlWriter, false);
		}

		public void serialize(final javax.xml.namespace.QName parentQName, final org.apache.axiom.om.OMFactory factory,
				org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter, boolean serializeType)
				throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException {

			java.lang.String prefix = null;
			java.lang.String namespace = null;

			prefix = parentQName.getPrefix();
			namespace = parentQName.getNamespaceURI();

			if ((namespace != null) && (namespace.trim().length() > 0)) {
				java.lang.String writerPrefix = xmlWriter.getPrefix(namespace);
				if (writerPrefix != null) {
					xmlWriter.writeStartElement(namespace, parentQName.getLocalPart());
				} else {
					if (prefix == null) {
						prefix = generatePrefix(namespace);
					}

					xmlWriter.writeStartElement(prefix, parentQName.getLocalPart(), namespace);
					xmlWriter.writeNamespace(prefix, namespace);
					xmlWriter.setPrefix(prefix, namespace);
				}
			} else {
				xmlWriter.writeStartElement(parentQName.getLocalPart());
			}

			if (serializeType) {

				java.lang.String namespacePrefix = registerPrefix(xmlWriter, "http://axis.de/xsd");
				if ((namespacePrefix != null) && (namespacePrefix.trim().length() > 0)) {
					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type",
							namespacePrefix + ":PreparedQuery", xmlWriter);
				} else {
					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type", "PreparedQuery",
							xmlWriter);
				}

			}
			if (localParametersTracker) {
				if (localParameters != null) {
					namespace = "http://axis.de/xsd";
					boolean emptyNamespace = namespace == null || namespace.length() == 0;
					prefix = emptyNamespace ? null : xmlWriter.getPrefix(namespace);
					for (int i = 0; i < localParameters.length; i++) {

						if (localParameters[i] != null) {

							if (!emptyNamespace) {
								if (prefix == null) {
									java.lang.String prefix2 = generatePrefix(namespace);

									xmlWriter.writeStartElement(prefix2, "parameters", namespace);
									xmlWriter.writeNamespace(prefix2, namespace);
									xmlWriter.setPrefix(prefix2, namespace);

								} else {
									xmlWriter.writeStartElement(namespace, "parameters");
								}

							} else {
								xmlWriter.writeStartElement("parameters");
							}

							xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil
									.convertToString(localParameters[i]));

							xmlWriter.writeEndElement();

						} else {

							// write null attribute
							namespace = "http://axis.de/xsd";
							if (!namespace.equals("")) {
								prefix = xmlWriter.getPrefix(namespace);

								if (prefix == null) {
									prefix = generatePrefix(namespace);

									xmlWriter.writeStartElement(prefix, "parameters", namespace);
									xmlWriter.writeNamespace(prefix, namespace);
									xmlWriter.setPrefix(prefix, namespace);

								} else {
									xmlWriter.writeStartElement(namespace, "parameters");
								}

							} else {
								xmlWriter.writeStartElement("parameters");
							}
							writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
							xmlWriter.writeEndElement();

						}

					}
				} else {

					// write the null attribute
					// write null attribute
					java.lang.String namespace2 = "http://axis.de/xsd";
					if (!namespace2.equals("")) {
						java.lang.String prefix2 = xmlWriter.getPrefix(namespace2);

						if (prefix2 == null) {
							prefix2 = generatePrefix(namespace2);

							xmlWriter.writeStartElement(prefix2, "parameters", namespace2);
							xmlWriter.writeNamespace(prefix2, namespace2);
							xmlWriter.setPrefix(prefix2, namespace2);

						} else {
							xmlWriter.writeStartElement(namespace2, "parameters");
						}

					} else {
						xmlWriter.writeStartElement("parameters");
					}

					// write the nil attribute
					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
					xmlWriter.writeEndElement();

				}

			}
			if (localQueryTracker) {
				namespace = "http://axis.de/xsd";
				if (!namespace.equals("")) {
					prefix = xmlWriter.getPrefix(namespace);

					if (prefix == null) {
						prefix = generatePrefix(namespace);

						xmlWriter.writeStartElement(prefix, "query", namespace);
						xmlWriter.writeNamespace(prefix, namespace);
						xmlWriter.setPrefix(prefix, namespace);

					} else {
						xmlWriter.writeStartElement(namespace, "query");
					}

				} else {
					xmlWriter.writeStartElement("query");
				}

				if (localQuery == null) {
					// write the nil attribute

					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);

				} else {

					xmlWriter.writeCharacters(localQuery);

				}

				xmlWriter.writeEndElement();
			}
			xmlWriter.writeEndElement();

		}

		/**
		 * Util method to write an attribute with the ns prefix
		 */
		private void writeAttribute(java.lang.String prefix, java.lang.String namespace, java.lang.String attName,
				java.lang.String attValue, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException {
			if (xmlWriter.getPrefix(namespace) == null) {
				xmlWriter.writeNamespace(prefix, namespace);
				xmlWriter.setPrefix(prefix, namespace);

			}

			xmlWriter.writeAttribute(namespace, attName, attValue);

		}

		/**
		 * Register a namespace prefix
		 */
		private java.lang.String registerPrefix(javax.xml.stream.XMLStreamWriter xmlWriter, java.lang.String namespace)
				throws javax.xml.stream.XMLStreamException {
			java.lang.String prefix = xmlWriter.getPrefix(namespace);

			if (prefix == null) {
				prefix = generatePrefix(namespace);

				while (xmlWriter.getNamespaceContext().getNamespaceURI(prefix) != null) {
					prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
				}

				xmlWriter.writeNamespace(prefix, namespace);
				xmlWriter.setPrefix(prefix, namespace);
			}

			return prefix;
		}

		/**
		 * databinding method to get an XML representation of this object
		 *
		 */
		public javax.xml.stream.XMLStreamReader getPullParser(javax.xml.namespace.QName qName)
				throws org.apache.axis2.databinding.ADBException {

			java.util.ArrayList<Object> elementList = new java.util.ArrayList<Object>();
			java.util.ArrayList<Object> attribList = new java.util.ArrayList<Object>();

			if (localParametersTracker) {
				if (localParameters != null) {
					for (int i = 0; i < localParameters.length; i++) {

						if (localParameters[i] != null) {
							elementList.add(new javax.xml.namespace.QName("http://axis.de/xsd", "parameters"));
							elementList.add(org.apache.axis2.databinding.utils.ConverterUtil
									.convertToString(localParameters[i]));
						} else {

							elementList.add(new javax.xml.namespace.QName("http://axis.de/xsd", "parameters"));
							elementList.add(null);

						}

					}
				} else {

					elementList.add(new javax.xml.namespace.QName("http://axis.de/xsd", "parameters"));
					elementList.add(null);

				}

			}
			if (localQueryTracker) {
				elementList.add(new javax.xml.namespace.QName("http://axis.de/xsd", "query"));

				elementList.add(localQuery == null ? null
						: org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localQuery));
			}

			return new org.apache.axis2.databinding.utils.reader.ADBXMLStreamReaderImpl(qName, elementList.toArray(),
					attribList.toArray());

		}

		/**
		 * Factory class that keeps the parse method
		 */
		public static class Factory {

			/**
			 * static method to create the object Precondition: If this object is an
			 * element, the current or next start element starts this object and any
			 * intervening reader events are ignorable If this object is not an element, it
			 * is a complex type and the reader is at the event just after the outer start
			 * element Postcondition: If this object is an element, the reader is positioned
			 * at its end element If this object is a complex type, the reader is positioned
			 * at the end element of its outer element
			 */
			public static PreparedQuery parse(javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception {
				PreparedQuery object = new PreparedQuery();

				java.lang.String nillableValue = null;
				try {

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type") != null) {
						java.lang.String fullTypeName = reader
								.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type");
						if (fullTypeName != null) {
							java.lang.String nsPrefix = null;
							if (fullTypeName.indexOf(":") > -1) {
								nsPrefix = fullTypeName.substring(0, fullTypeName.indexOf(":"));
							}
							nsPrefix = nsPrefix == null ? "" : nsPrefix;

							java.lang.String type = fullTypeName.substring(fullTypeName.indexOf(":") + 1);

							if (!"PreparedQuery".equals(type)) {
								// find namespace for the prefix
								java.lang.String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
								return (PreparedQuery) ExtensionMapper.getTypeObject(nsUri, type, reader);
							}

						}

					}

					// Note all attributes that were handled. Used to differ normal attributes
					// from anyAttributes.

					reader.next();

					java.util.ArrayList<String> list1 = new java.util.ArrayList<String>();

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement() && new javax.xml.namespace.QName("http://axis.de/xsd", "parameters")
							.equals(reader.getName())) {

						// Process the array and step past its final element's end.

						nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
						if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
							list1.add(null);

							reader.next();
						} else {
							list1.add(reader.getElementText());
						}
						// loop until we find a start element that is not part of this array
						boolean loopDone1 = false;
						while (!loopDone1) {
							// Ensure we are at the EndElement
							while (!reader.isEndElement()) {
								reader.next();
							}
							// Step out of this element
							reader.next();
							// Step to next element event.
							while (!reader.isStartElement() && !reader.isEndElement())
								reader.next();
							if (reader.isEndElement()) {
								// two continuous end elements means we are exiting the xml structure
								loopDone1 = true;
							} else {
								if (new javax.xml.namespace.QName("http://axis.de/xsd", "parameters")
										.equals(reader.getName())) {

									nillableValue = reader
											.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
									if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
										list1.add(null);

										reader.next();
									} else {
										list1.add(reader.getElementText());
									}
								} else {
									loopDone1 = true;
								}
							}
						}
						// call the converter utility to convert and set the array

						object.setParameters((java.lang.String[]) list1.toArray(new java.lang.String[list1.size()]));

					} // End of if for expected property start element

					else {

					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()
							&& new javax.xml.namespace.QName("http://axis.de/xsd", "query").equals(reader.getName())) {

						nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
						if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {

							java.lang.String content = reader.getElementText();

							object.setQuery(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

						} else {

							reader.getElementText(); // throw away text nodes if any.
						}

						reader.next();

					} // End of if for expected property start element

					else {

					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement())
						// A start element we are not expecting indicates a trailing invalid property
						throw new org.apache.axis2.databinding.ADBException(
								"Unexpected subelement " + reader.getLocalName());

				} catch (javax.xml.stream.XMLStreamException e) {
					throw new java.lang.Exception(e);
				}

				return object;
			}

		}// end of factory class

	}

	public static class ExtensionMapper {

		public static java.lang.Object getTypeObject(java.lang.String namespaceURI, java.lang.String typeName,
				javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception {

			if ("http://axis.de/xsd".equals(namespaceURI) && "PreparedQuery".equals(typeName)) {

				return PreparedQuery.Factory.parse(reader);

			}

			if ("http://axis.de/xsd".equals(namespaceURI) && "DBColumn".equals(typeName)) {

				return DBColumn.Factory.parse(reader);

			}

			if ("http://sql.java/xsd".equals(namespaceURI) && "SQLException".equals(typeName)) {

				return SQLException.Factory.parse(reader);

			}

			if ("http://axis.de".equals(namespaceURI) && "Exception".equals(typeName)) {

				return Exception.Factory.parse(reader);

			}

			throw new org.apache.axis2.databinding.ADBException("Unsupported type " + namespaceURI + " " + typeName);
		}

	}

	public static class ClassNotFoundException implements org.apache.axis2.databinding.ADBBean {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public static final javax.xml.namespace.QName MY_QNAME = new javax.xml.namespace.QName("http://axis.de",
				"ClassNotFoundException", "ns1");

		private static java.lang.String generatePrefix(java.lang.String namespace) {
			if (namespace.equals("http://axis.de")) {
				return "ns1";
			}
			return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
		}

		/**
		 * field for ClassNotFoundException
		 */

		protected java.lang.Object localClassNotFoundException;

		/*
		 * This tracker boolean wil be used to detect whether the user called the set
		 * method for this attribute. It will be used to determine whether to include
		 * this field in the serialized XML
		 */
		protected boolean localClassNotFoundExceptionTracker = false;

		/**
		 * Auto generated getter method
		 * 
		 * @return java.lang.Object
		 */
		public java.lang.Object getClassNotFoundException() {
			return localClassNotFoundException;
		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param ClassNotFoundException
		 */
		public void setClassNotFoundException(java.lang.Object param) {

			if (param != null) {
				// update the setting tracker
				localClassNotFoundExceptionTracker = true;
			} else {
				localClassNotFoundExceptionTracker = true;

			}

			this.localClassNotFoundException = param;

		}

		/**
		 * isReaderMTOMAware
		 * 
		 * @return true if the reader supports MTOM
		 */
		public static boolean isReaderMTOMAware(javax.xml.stream.XMLStreamReader reader) {
			boolean isReaderMTOMAware = false;

			try {
				isReaderMTOMAware = java.lang.Boolean.TRUE
						.equals(reader.getProperty(org.apache.axiom.om.OMConstants.IS_DATA_HANDLERS_AWARE));
			} catch (java.lang.IllegalArgumentException e) {
				isReaderMTOMAware = false;
			}
			return isReaderMTOMAware;
		}

		/**
		 *
		 * @param parentQName
		 * @param factory
		 * @return org.apache.axiom.om.OMElement
		 */
		public org.apache.axiom.om.OMElement getOMElement(final javax.xml.namespace.QName parentQName,
				final org.apache.axiom.om.OMFactory factory) throws org.apache.axis2.databinding.ADBException {

			org.apache.axiom.om.OMDataSource dataSource = new org.apache.axis2.databinding.ADBDataSource(this,
					MY_QNAME) {

				public void serialize(org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter)
						throws javax.xml.stream.XMLStreamException {
					ClassNotFoundException.this.serialize(MY_QNAME, factory, xmlWriter);
				}
			};
			return new org.apache.axiom.om.impl.llom.OMSourcedElementImpl(MY_QNAME, factory, dataSource);

		}

		public void serialize(final javax.xml.namespace.QName parentQName, final org.apache.axiom.om.OMFactory factory,
				org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException {
			serialize(parentQName, factory, xmlWriter, false);
		}

		public void serialize(final javax.xml.namespace.QName parentQName, final org.apache.axiom.om.OMFactory factory,
				org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter, boolean serializeType)
				throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException {

			java.lang.String prefix = null;
			java.lang.String namespace = null;

			prefix = parentQName.getPrefix();
			namespace = parentQName.getNamespaceURI();

			if ((namespace != null) && (namespace.trim().length() > 0)) {
				java.lang.String writerPrefix = xmlWriter.getPrefix(namespace);
				if (writerPrefix != null) {
					xmlWriter.writeStartElement(namespace, parentQName.getLocalPart());
				} else {
					if (prefix == null) {
						prefix = generatePrefix(namespace);
					}

					xmlWriter.writeStartElement(prefix, parentQName.getLocalPart(), namespace);
					xmlWriter.writeNamespace(prefix, namespace);
					xmlWriter.setPrefix(prefix, namespace);
				}
			} else {
				xmlWriter.writeStartElement(parentQName.getLocalPart());
			}

			if (serializeType) {

				java.lang.String namespacePrefix = registerPrefix(xmlWriter, "http://axis.de");
				if ((namespacePrefix != null) && (namespacePrefix.trim().length() > 0)) {
					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type",
							namespacePrefix + ":ClassNotFoundException", xmlWriter);
				} else {
					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type", "ClassNotFoundException",
							xmlWriter);
				}

			}
			if (localClassNotFoundExceptionTracker) {

				if (localClassNotFoundException != null) {
					if (localClassNotFoundException instanceof org.apache.axis2.databinding.ADBBean) {
						((org.apache.axis2.databinding.ADBBean) localClassNotFoundException).serialize(
								new javax.xml.namespace.QName("http://axis.de", "ClassNotFoundException"), factory,
								xmlWriter, true);
					} else {
						java.lang.String namespace2 = "http://axis.de";
						if (!namespace2.equals("")) {
							java.lang.String prefix2 = xmlWriter.getPrefix(namespace2);

							if (prefix2 == null) {
								prefix2 = generatePrefix(namespace2);

								xmlWriter.writeStartElement(prefix2, "ClassNotFoundException", namespace2);
								xmlWriter.writeNamespace(prefix2, namespace2);
								xmlWriter.setPrefix(prefix2, namespace2);

							} else {
								xmlWriter.writeStartElement(namespace2, "ClassNotFoundException");
							}

						} else {
							xmlWriter.writeStartElement("ClassNotFoundException");
						}
						org.apache.axis2.databinding.utils.ConverterUtil.serializeAnyType(localClassNotFoundException,
								xmlWriter);
						xmlWriter.writeEndElement();
					}
				} else {

					// write null attribute
					java.lang.String namespace2 = "http://axis.de";
					if (!namespace2.equals("")) {
						java.lang.String prefix2 = xmlWriter.getPrefix(namespace2);

						if (prefix2 == null) {
							prefix2 = generatePrefix(namespace2);

							xmlWriter.writeStartElement(prefix2, "ClassNotFoundException", namespace2);
							xmlWriter.writeNamespace(prefix2, namespace2);
							xmlWriter.setPrefix(prefix2, namespace2);

						} else {
							xmlWriter.writeStartElement(namespace2, "ClassNotFoundException");
						}

					} else {
						xmlWriter.writeStartElement("ClassNotFoundException");
					}

					// write the nil attribute
					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
					xmlWriter.writeEndElement();

				}

			}
			xmlWriter.writeEndElement();

		}

		/**
		 * Util method to write an attribute with the ns prefix
		 */
		private void writeAttribute(java.lang.String prefix, java.lang.String namespace, java.lang.String attName,
				java.lang.String attValue, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException {
			if (xmlWriter.getPrefix(namespace) == null) {
				xmlWriter.writeNamespace(prefix, namespace);
				xmlWriter.setPrefix(prefix, namespace);

			}

			xmlWriter.writeAttribute(namespace, attName, attValue);

		}

		/**
		 * Register a namespace prefix
		 */
		private java.lang.String registerPrefix(javax.xml.stream.XMLStreamWriter xmlWriter, java.lang.String namespace)
				throws javax.xml.stream.XMLStreamException {
			java.lang.String prefix = xmlWriter.getPrefix(namespace);

			if (prefix == null) {
				prefix = generatePrefix(namespace);

				while (xmlWriter.getNamespaceContext().getNamespaceURI(prefix) != null) {
					prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
				}

				xmlWriter.writeNamespace(prefix, namespace);
				xmlWriter.setPrefix(prefix, namespace);
			}

			return prefix;
		}

		/**
		 * databinding method to get an XML representation of this object
		 *
		 */
		public javax.xml.stream.XMLStreamReader getPullParser(javax.xml.namespace.QName qName)
				throws org.apache.axis2.databinding.ADBException {

			java.util.ArrayList<Object> elementList = new java.util.ArrayList<Object>();
			java.util.ArrayList<Object> attribList = new java.util.ArrayList<Object>();

			if (localClassNotFoundExceptionTracker) {
				elementList.add(new javax.xml.namespace.QName("http://axis.de", "ClassNotFoundException"));

				elementList.add(localClassNotFoundException == null ? null : localClassNotFoundException);
			}

			return new org.apache.axis2.databinding.utils.reader.ADBXMLStreamReaderImpl(qName, elementList.toArray(),
					attribList.toArray());

		}

		/**
		 * Factory class that keeps the parse method
		 */
		public static class Factory {

			/**
			 * static method to create the object Precondition: If this object is an
			 * element, the current or next start element starts this object and any
			 * intervening reader events are ignorable If this object is not an element, it
			 * is a complex type and the reader is at the event just after the outer start
			 * element Postcondition: If this object is an element, the reader is positioned
			 * at its end element If this object is a complex type, the reader is positioned
			 * at the end element of its outer element
			 */
			public static ClassNotFoundException parse(javax.xml.stream.XMLStreamReader reader)
					throws java.lang.Exception {
				ClassNotFoundException object = new ClassNotFoundException();

				java.lang.String nillableValue = null;
				try {

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
					if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
						// Skip the element and report the null value. It cannot have subelements.
						while (!reader.isEndElement())
							reader.next();

						return null;

					}

					if (reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type") != null) {
						java.lang.String fullTypeName = reader
								.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type");
						if (fullTypeName != null) {
							java.lang.String nsPrefix = null;
							if (fullTypeName.indexOf(":") > -1) {
								nsPrefix = fullTypeName.substring(0, fullTypeName.indexOf(":"));
							}
							nsPrefix = nsPrefix == null ? "" : nsPrefix;

							java.lang.String type = fullTypeName.substring(fullTypeName.indexOf(":") + 1);

							if (!"ClassNotFoundException".equals(type)) {
								// find namespace for the prefix
								java.lang.String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
								return (ClassNotFoundException) ExtensionMapper.getTypeObject(nsUri, type, reader);
							}

						}

					}

					// Note all attributes that were handled. Used to differ normal attributes
					// from anyAttributes.

					reader.next();

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()
							&& new javax.xml.namespace.QName("http://axis.de", "ClassNotFoundException")
									.equals(reader.getName())) {

						object.setClassNotFoundException(org.apache.axis2.databinding.utils.ConverterUtil
								.getAnyTypeObject(reader, ExtensionMapper.class));

						reader.next();

					} // End of if for expected property start element

					else {

					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement())
						// A start element we are not expecting indicates a trailing invalid property
						throw new org.apache.axis2.databinding.ADBException(
								"Unexpected subelement " + reader.getLocalName());

				} catch (javax.xml.stream.XMLStreamException e) {
					throw new java.lang.Exception(e);
				}

				return object;
			}

		}// end of factory class

	}

	public static class PreparedQueryDBResponse implements org.apache.axis2.databinding.ADBBean {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public static final javax.xml.namespace.QName MY_QNAME = new javax.xml.namespace.QName("http://axis.de",
				"preparedQueryDBResponse", "ns1");

		private static java.lang.String generatePrefix(java.lang.String namespace) {
			if (namespace.equals("http://axis.de")) {
				return "ns1";
			}
			return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
		}

		/**
		 * field for _return This was an Array!
		 */

		protected DBColumn[] local_return;

		/*
		 * This tracker boolean wil be used to detect whether the user called the set
		 * method for this attribute. It will be used to determine whether to include
		 * this field in the serialized XML
		 */
		protected boolean local_returnTracker = false;

		/**
		 * Auto generated getter method
		 * 
		 * @return DBColumn[]
		 */
		public DBColumn[] get_return() {
			return local_return;
		}

		/**
		 * validate the array for _return
		 */
		protected void validate_return(DBColumn[] param) {

		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param _return
		 */
		public void set_return(DBColumn[] param) {

			validate_return(param);

			if (param != null) {
				// update the setting tracker
				local_returnTracker = true;
			} else {
				local_returnTracker = true;

			}

			this.local_return = param;
		}

		/**
		 * Auto generated add method for the array for convenience
		 * 
		 * @param param DBColumn
		 */
		public void add_return(DBColumn param) {
			if (local_return == null) {
				local_return = new DBColumn[] {};
			}

			// update the setting tracker
			local_returnTracker = true;

			java.util.List<DBColumn> list = org.apache.axis2.databinding.utils.ConverterUtil.toList(local_return);
			list.add(param);
			this.local_return = (DBColumn[]) list.toArray(new DBColumn[list.size()]);

		}

		/**
		 * isReaderMTOMAware
		 * 
		 * @return true if the reader supports MTOM
		 */
		public static boolean isReaderMTOMAware(javax.xml.stream.XMLStreamReader reader) {
			boolean isReaderMTOMAware = false;

			try {
				isReaderMTOMAware = java.lang.Boolean.TRUE
						.equals(reader.getProperty(org.apache.axiom.om.OMConstants.IS_DATA_HANDLERS_AWARE));
			} catch (java.lang.IllegalArgumentException e) {
				isReaderMTOMAware = false;
			}
			return isReaderMTOMAware;
		}

		/**
		 *
		 * @param parentQName
		 * @param factory
		 * @return org.apache.axiom.om.OMElement
		 */
		public org.apache.axiom.om.OMElement getOMElement(final javax.xml.namespace.QName parentQName,
				final org.apache.axiom.om.OMFactory factory) throws org.apache.axis2.databinding.ADBException {

			org.apache.axiom.om.OMDataSource dataSource = new org.apache.axis2.databinding.ADBDataSource(this,
					MY_QNAME) {

				public void serialize(org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter)
						throws javax.xml.stream.XMLStreamException {
					PreparedQueryDBResponse.this.serialize(MY_QNAME, factory, xmlWriter);
				}
			};
			return new org.apache.axiom.om.impl.llom.OMSourcedElementImpl(MY_QNAME, factory, dataSource);

		}

		public void serialize(final javax.xml.namespace.QName parentQName, final org.apache.axiom.om.OMFactory factory,
				org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException {
			serialize(parentQName, factory, xmlWriter, false);
		}

		public void serialize(final javax.xml.namespace.QName parentQName, final org.apache.axiom.om.OMFactory factory,
				org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter, boolean serializeType)
				throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException {

			java.lang.String prefix = null;
			java.lang.String namespace = null;

			prefix = parentQName.getPrefix();
			namespace = parentQName.getNamespaceURI();

			if ((namespace != null) && (namespace.trim().length() > 0)) {
				java.lang.String writerPrefix = xmlWriter.getPrefix(namespace);
				if (writerPrefix != null) {
					xmlWriter.writeStartElement(namespace, parentQName.getLocalPart());
				} else {
					if (prefix == null) {
						prefix = generatePrefix(namespace);
					}

					xmlWriter.writeStartElement(prefix, parentQName.getLocalPart(), namespace);
					xmlWriter.writeNamespace(prefix, namespace);
					xmlWriter.setPrefix(prefix, namespace);
				}
			} else {
				xmlWriter.writeStartElement(parentQName.getLocalPart());
			}

			if (serializeType) {

				java.lang.String namespacePrefix = registerPrefix(xmlWriter, "http://axis.de");
				if ((namespacePrefix != null) && (namespacePrefix.trim().length() > 0)) {
					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type",
							namespacePrefix + ":preparedQueryDBResponse", xmlWriter);
				} else {
					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type",
							"preparedQueryDBResponse", xmlWriter);
				}

			}
			if (local_returnTracker) {
				if (local_return != null) {
					for (int i = 0; i < local_return.length; i++) {
						if (local_return[i] != null) {
							local_return[i].serialize(new javax.xml.namespace.QName("http://axis.de", "return"),
									factory, xmlWriter);
						} else {

							// write null attribute
							java.lang.String namespace2 = "http://axis.de";
							if (!namespace2.equals("")) {
								java.lang.String prefix2 = xmlWriter.getPrefix(namespace2);

								if (prefix2 == null) {
									prefix2 = generatePrefix(namespace2);

									xmlWriter.writeStartElement(prefix2, "return", namespace2);
									xmlWriter.writeNamespace(prefix2, namespace2);
									xmlWriter.setPrefix(prefix2, namespace2);

								} else {
									xmlWriter.writeStartElement(namespace2, "return");
								}

							} else {
								xmlWriter.writeStartElement("return");
							}

							// write the nil attribute
							writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
							xmlWriter.writeEndElement();

						}

					}
				} else {

					// write null attribute
					java.lang.String namespace2 = "http://axis.de";
					if (!namespace2.equals("")) {
						java.lang.String prefix2 = xmlWriter.getPrefix(namespace2);

						if (prefix2 == null) {
							prefix2 = generatePrefix(namespace2);

							xmlWriter.writeStartElement(prefix2, "return", namespace2);
							xmlWriter.writeNamespace(prefix2, namespace2);
							xmlWriter.setPrefix(prefix2, namespace2);

						} else {
							xmlWriter.writeStartElement(namespace2, "return");
						}

					} else {
						xmlWriter.writeStartElement("return");
					}

					// write the nil attribute
					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
					xmlWriter.writeEndElement();

				}
			}
			xmlWriter.writeEndElement();

		}

		/**
		 * Util method to write an attribute with the ns prefix
		 */
		private void writeAttribute(java.lang.String prefix, java.lang.String namespace, java.lang.String attName,
				java.lang.String attValue, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException {
			if (xmlWriter.getPrefix(namespace) == null) {
				xmlWriter.writeNamespace(prefix, namespace);
				xmlWriter.setPrefix(prefix, namespace);

			}

			xmlWriter.writeAttribute(namespace, attName, attValue);

		}

		/**
		 * Register a namespace prefix
		 */
		private java.lang.String registerPrefix(javax.xml.stream.XMLStreamWriter xmlWriter, java.lang.String namespace)
				throws javax.xml.stream.XMLStreamException {
			java.lang.String prefix = xmlWriter.getPrefix(namespace);

			if (prefix == null) {
				prefix = generatePrefix(namespace);

				while (xmlWriter.getNamespaceContext().getNamespaceURI(prefix) != null) {
					prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
				}

				xmlWriter.writeNamespace(prefix, namespace);
				xmlWriter.setPrefix(prefix, namespace);
			}

			return prefix;
		}

		/**
		 * databinding method to get an XML representation of this object
		 *
		 */
		public javax.xml.stream.XMLStreamReader getPullParser(javax.xml.namespace.QName qName)
				throws org.apache.axis2.databinding.ADBException {

			java.util.ArrayList<Object> elementList = new java.util.ArrayList<Object>();
			java.util.ArrayList<Object> attribList = new java.util.ArrayList<Object>();

			if (local_returnTracker) {
				if (local_return != null) {
					for (int i = 0; i < local_return.length; i++) {

						if (local_return[i] != null) {
							elementList.add(new javax.xml.namespace.QName("http://axis.de", "return"));
							elementList.add(local_return[i]);
						} else {

							elementList.add(new javax.xml.namespace.QName("http://axis.de", "return"));
							elementList.add(null);

						}

					}
				} else {

					elementList.add(new javax.xml.namespace.QName("http://axis.de", "return"));
					elementList.add(local_return);

				}

			}

			return new org.apache.axis2.databinding.utils.reader.ADBXMLStreamReaderImpl(qName, elementList.toArray(),
					attribList.toArray());

		}

		/**
		 * Factory class that keeps the parse method
		 */
		public static class Factory {

			/**
			 * static method to create the object Precondition: If this object is an
			 * element, the current or next start element starts this object and any
			 * intervening reader events are ignorable If this object is not an element, it
			 * is a complex type and the reader is at the event just after the outer start
			 * element Postcondition: If this object is an element, the reader is positioned
			 * at its end element If this object is a complex type, the reader is positioned
			 * at the end element of its outer element
			 */
			public static PreparedQueryDBResponse parse(javax.xml.stream.XMLStreamReader reader)
					throws java.lang.Exception {
				PreparedQueryDBResponse object = new PreparedQueryDBResponse();

				java.lang.String nillableValue = null;
				try {

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type") != null) {
						java.lang.String fullTypeName = reader
								.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type");
						if (fullTypeName != null) {
							java.lang.String nsPrefix = null;
							if (fullTypeName.indexOf(":") > -1) {
								nsPrefix = fullTypeName.substring(0, fullTypeName.indexOf(":"));
							}
							nsPrefix = nsPrefix == null ? "" : nsPrefix;

							java.lang.String type = fullTypeName.substring(fullTypeName.indexOf(":") + 1);

							if (!"preparedQueryDBResponse".equals(type)) {
								// find namespace for the prefix
								java.lang.String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
								return (PreparedQueryDBResponse) ExtensionMapper.getTypeObject(nsUri, type, reader);
							}

						}

					}

					// Note all attributes that were handled. Used to differ normal attributes
					// from anyAttributes.

					reader.next();

					java.util.ArrayList<DBColumn> list1 = new java.util.ArrayList<DBColumn>();

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()
							&& new javax.xml.namespace.QName("http://axis.de", "return").equals(reader.getName())) {

						// Process the array and step past its final element's end.

						nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
						if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
							list1.add(null);
							reader.next();
						} else {
							list1.add(DBColumn.Factory.parse(reader));
						}
						// loop until we find a start element that is not part of this array
						boolean loopDone1 = false;
						while (!loopDone1) {
							// We should be at the end element, but make sure
							while (!reader.isEndElement())
								reader.next();
							// Step out of this element
							reader.next();
							// Step to next element event.
							while (!reader.isStartElement() && !reader.isEndElement())
								reader.next();
							if (reader.isEndElement()) {
								// two continuous end elements means we are exiting the xml structure
								loopDone1 = true;
							} else {
								if (new javax.xml.namespace.QName("http://axis.de", "return")
										.equals(reader.getName())) {

									nillableValue = reader
											.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
									if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
										list1.add(null);
										reader.next();
									} else {
										list1.add(DBColumn.Factory.parse(reader));
									}
								} else {
									loopDone1 = true;
								}
							}
						}
						// call the converter utility to convert and set the array

						object.set_return((DBColumn[]) org.apache.axis2.databinding.utils.ConverterUtil
								.convertToArray(DBColumn.class, list1));

					} // End of if for expected property start element

					else {

					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement())
						// A start element we are not expecting indicates a trailing invalid property
						throw new org.apache.axis2.databinding.ADBException(
								"Unexpected subelement " + reader.getLocalName());

				} catch (javax.xml.stream.XMLStreamException e) {
					throw new java.lang.Exception(e);
				}

				return object;
			}

		}// end of factory class

	}

	public static class QueryDBResponse implements org.apache.axis2.databinding.ADBBean {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public static final javax.xml.namespace.QName MY_QNAME = new javax.xml.namespace.QName("http://axis.de",
				"queryDBResponse", "ns1");

		private static java.lang.String generatePrefix(java.lang.String namespace) {
			if (namespace.equals("http://axis.de")) {
				return "ns1";
			}
			return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
		}

		/**
		 * field for _return This was an Array!
		 */

		protected DBColumn[] local_return;

		/*
		 * This tracker boolean wil be used to detect whether the user called the set
		 * method for this attribute. It will be used to determine whether to include
		 * this field in the serialized XML
		 */
		protected boolean local_returnTracker = false;

		/**
		 * Auto generated getter method
		 * 
		 * @return DBColumn[]
		 */
		public DBColumn[] get_return() {
			return local_return;
		}

		/**
		 * validate the array for _return
		 */
		protected void validate_return(DBColumn[] param) {

		}

		/**
		 * Auto generated setter method
		 * 
		 * @param param _return
		 */
		public void set_return(DBColumn[] param) {

			validate_return(param);

			if (param != null) {
				// update the setting tracker
				local_returnTracker = true;
			} else {
				local_returnTracker = true;

			}

			this.local_return = param;
		}

		/**
		 * Auto generated add method for the array for convenience
		 * 
		 * @param param DBColumn
		 */
		public void add_return(DBColumn param) {
			if (local_return == null) {
				local_return = new DBColumn[] {};
			}

			// update the setting tracker
			local_returnTracker = true;

			java.util.List<DBColumn> list = org.apache.axis2.databinding.utils.ConverterUtil.toList(local_return);
			list.add(param);
			this.local_return = (DBColumn[]) list.toArray(new DBColumn[list.size()]);

		}

		/**
		 * isReaderMTOMAware
		 * 
		 * @return true if the reader supports MTOM
		 */
		public static boolean isReaderMTOMAware(javax.xml.stream.XMLStreamReader reader) {
			boolean isReaderMTOMAware = false;

			try {
				isReaderMTOMAware = java.lang.Boolean.TRUE
						.equals(reader.getProperty(org.apache.axiom.om.OMConstants.IS_DATA_HANDLERS_AWARE));
			} catch (java.lang.IllegalArgumentException e) {
				isReaderMTOMAware = false;
			}
			return isReaderMTOMAware;
		}

		/**
		 *
		 * @param parentQName
		 * @param factory
		 * @return org.apache.axiom.om.OMElement
		 */
		public org.apache.axiom.om.OMElement getOMElement(final javax.xml.namespace.QName parentQName,
				final org.apache.axiom.om.OMFactory factory) throws org.apache.axis2.databinding.ADBException {

			org.apache.axiom.om.OMDataSource dataSource = new org.apache.axis2.databinding.ADBDataSource(this,
					MY_QNAME) {

				public void serialize(org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter)
						throws javax.xml.stream.XMLStreamException {
					QueryDBResponse.this.serialize(MY_QNAME, factory, xmlWriter);
				}
			};
			return new org.apache.axiom.om.impl.llom.OMSourcedElementImpl(MY_QNAME, factory, dataSource);

		}

		public void serialize(final javax.xml.namespace.QName parentQName, final org.apache.axiom.om.OMFactory factory,
				org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException {
			serialize(parentQName, factory, xmlWriter, false);
		}

		public void serialize(final javax.xml.namespace.QName parentQName, final org.apache.axiom.om.OMFactory factory,
				org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter, boolean serializeType)
				throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException {

			java.lang.String prefix = null;
			java.lang.String namespace = null;

			prefix = parentQName.getPrefix();
			namespace = parentQName.getNamespaceURI();

			if ((namespace != null) && (namespace.trim().length() > 0)) {
				java.lang.String writerPrefix = xmlWriter.getPrefix(namespace);
				if (writerPrefix != null) {
					xmlWriter.writeStartElement(namespace, parentQName.getLocalPart());
				} else {
					if (prefix == null) {
						prefix = generatePrefix(namespace);
					}

					xmlWriter.writeStartElement(prefix, parentQName.getLocalPart(), namespace);
					xmlWriter.writeNamespace(prefix, namespace);
					xmlWriter.setPrefix(prefix, namespace);
				}
			} else {
				xmlWriter.writeStartElement(parentQName.getLocalPart());
			}

			if (serializeType) {

				java.lang.String namespacePrefix = registerPrefix(xmlWriter, "http://axis.de");
				if ((namespacePrefix != null) && (namespacePrefix.trim().length() > 0)) {
					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type",
							namespacePrefix + ":queryDBResponse", xmlWriter);
				} else {
					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type", "queryDBResponse",
							xmlWriter);
				}

			}
			if (local_returnTracker) {
				if (local_return != null) {
					for (int i = 0; i < local_return.length; i++) {
						if (local_return[i] != null) {
							local_return[i].serialize(new javax.xml.namespace.QName("http://axis.de", "return"),
									factory, xmlWriter);
						} else {

							// write null attribute
							java.lang.String namespace2 = "http://axis.de";
							if (!namespace2.equals("")) {
								java.lang.String prefix2 = xmlWriter.getPrefix(namespace2);

								if (prefix2 == null) {
									prefix2 = generatePrefix(namespace2);

									xmlWriter.writeStartElement(prefix2, "return", namespace2);
									xmlWriter.writeNamespace(prefix2, namespace2);
									xmlWriter.setPrefix(prefix2, namespace2);

								} else {
									xmlWriter.writeStartElement(namespace2, "return");
								}

							} else {
								xmlWriter.writeStartElement("return");
							}

							// write the nil attribute
							writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
							xmlWriter.writeEndElement();

						}

					}
				} else {

					// write null attribute
					java.lang.String namespace2 = "http://axis.de";
					if (!namespace2.equals("")) {
						java.lang.String prefix2 = xmlWriter.getPrefix(namespace2);

						if (prefix2 == null) {
							prefix2 = generatePrefix(namespace2);

							xmlWriter.writeStartElement(prefix2, "return", namespace2);
							xmlWriter.writeNamespace(prefix2, namespace2);
							xmlWriter.setPrefix(prefix2, namespace2);

						} else {
							xmlWriter.writeStartElement(namespace2, "return");
						}

					} else {
						xmlWriter.writeStartElement("return");
					}

					// write the nil attribute
					writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
					xmlWriter.writeEndElement();

				}
			}
			xmlWriter.writeEndElement();

		}

		/**
		 * Util method to write an attribute with the ns prefix
		 */
		private void writeAttribute(java.lang.String prefix, java.lang.String namespace, java.lang.String attName,
				java.lang.String attValue, javax.xml.stream.XMLStreamWriter xmlWriter)
				throws javax.xml.stream.XMLStreamException {
			if (xmlWriter.getPrefix(namespace) == null) {
				xmlWriter.writeNamespace(prefix, namespace);
				xmlWriter.setPrefix(prefix, namespace);

			}

			xmlWriter.writeAttribute(namespace, attName, attValue);

		}

		/**
		 * Register a namespace prefix
		 */
		private java.lang.String registerPrefix(javax.xml.stream.XMLStreamWriter xmlWriter, java.lang.String namespace)
				throws javax.xml.stream.XMLStreamException {
			java.lang.String prefix = xmlWriter.getPrefix(namespace);

			if (prefix == null) {
				prefix = generatePrefix(namespace);

				while (xmlWriter.getNamespaceContext().getNamespaceURI(prefix) != null) {
					prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
				}

				xmlWriter.writeNamespace(prefix, namespace);
				xmlWriter.setPrefix(prefix, namespace);
			}

			return prefix;
		}

		/**
		 * databinding method to get an XML representation of this object
		 *
		 */
		public javax.xml.stream.XMLStreamReader getPullParser(javax.xml.namespace.QName qName)
				throws org.apache.axis2.databinding.ADBException {

			java.util.ArrayList<Object> elementList = new java.util.ArrayList<Object>();
			java.util.ArrayList<Object> attribList = new java.util.ArrayList<Object>();

			if (local_returnTracker) {
				if (local_return != null) {
					for (int i = 0; i < local_return.length; i++) {

						if (local_return[i] != null) {
							elementList.add(new javax.xml.namespace.QName("http://axis.de", "return"));
							elementList.add(local_return[i]);
						} else {

							elementList.add(new javax.xml.namespace.QName("http://axis.de", "return"));
							elementList.add(null);

						}

					}
				} else {

					elementList.add(new javax.xml.namespace.QName("http://axis.de", "return"));
					elementList.add(local_return);

				}

			}

			return new org.apache.axis2.databinding.utils.reader.ADBXMLStreamReaderImpl(qName, elementList.toArray(),
					attribList.toArray());

		}

		/**
		 * Factory class that keeps the parse method
		 */
		public static class Factory {

			/**
			 * static method to create the object Precondition: If this object is an
			 * element, the current or next start element starts this object and any
			 * intervening reader events are ignorable If this object is not an element, it
			 * is a complex type and the reader is at the event just after the outer start
			 * element Postcondition: If this object is an element, the reader is positioned
			 * at its end element If this object is a complex type, the reader is positioned
			 * at the end element of its outer element
			 */
			public static QueryDBResponse parse(javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception {
				QueryDBResponse object = new QueryDBResponse();

				java.lang.String nillableValue = null;
				try {

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type") != null) {
						java.lang.String fullTypeName = reader
								.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type");
						if (fullTypeName != null) {
							java.lang.String nsPrefix = null;
							if (fullTypeName.indexOf(":") > -1) {
								nsPrefix = fullTypeName.substring(0, fullTypeName.indexOf(":"));
							}
							nsPrefix = nsPrefix == null ? "" : nsPrefix;

							java.lang.String type = fullTypeName.substring(fullTypeName.indexOf(":") + 1);

							if (!"queryDBResponse".equals(type)) {
								// find namespace for the prefix
								java.lang.String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
								return (QueryDBResponse) ExtensionMapper.getTypeObject(nsUri, type, reader);
							}

						}

					}

					// Note all attributes that were handled. Used to differ normal attributes
					// from anyAttributes.

					reader.next();

					java.util.ArrayList<DBColumn> list1 = new java.util.ArrayList<DBColumn>();

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement()
							&& new javax.xml.namespace.QName("http://axis.de", "return").equals(reader.getName())) {

						// Process the array and step past its final element's end.

						nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
						if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
							list1.add(null);
							reader.next();
						} else {
							list1.add(DBColumn.Factory.parse(reader));
						}
						// loop until we find a start element that is not part of this array
						boolean loopDone1 = false;
						while (!loopDone1) {
							// We should be at the end element, but make sure
							while (!reader.isEndElement())
								reader.next();
							// Step out of this element
							reader.next();
							// Step to next element event.
							while (!reader.isStartElement() && !reader.isEndElement())
								reader.next();
							if (reader.isEndElement()) {
								// two continuous end elements means we are exiting the xml structure
								loopDone1 = true;
							} else {
								if (new javax.xml.namespace.QName("http://axis.de", "return")
										.equals(reader.getName())) {

									nillableValue = reader
											.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
									if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
										list1.add(null);
										reader.next();
									} else {
										list1.add(DBColumn.Factory.parse(reader));
									}
								} else {
									loopDone1 = true;
								}
							}
						}
						// call the converter utility to convert and set the array

						object.set_return((DBColumn[]) org.apache.axis2.databinding.utils.ConverterUtil
								.convertToArray(DBColumn.class, list1));

					} // End of if for expected property start element

					else {

					}

					while (!reader.isStartElement() && !reader.isEndElement())
						reader.next();

					if (reader.isStartElement())
						// A start element we are not expecting indicates a trailing invalid property
						throw new org.apache.axis2.databinding.ADBException(
								"Unexpected subelement " + reader.getLocalName());

				} catch (javax.xml.stream.XMLStreamException e) {
					throw new java.lang.Exception(e);
				}

				return object;
			}

		}// end of factory class

	}

	private org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory,
			VerySimpleDBServiceStub.QueryDB param, boolean optimizeContent) throws org.apache.axis2.AxisFault {

		try {

			org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
			emptyEnvelope.getBody().addChild(param.getOMElement(VerySimpleDBServiceStub.QueryDB.MY_QNAME, factory));
			return emptyEnvelope;
		} catch (org.apache.axis2.databinding.ADBException e) {
			throw org.apache.axis2.AxisFault.makeFault(e);
		}

	}

	/* methods to provide back word compatibility */

	private org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory,
			VerySimpleDBServiceStub.PreparedQueryDB param, boolean optimizeContent) throws org.apache.axis2.AxisFault {

		try {

			org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
			emptyEnvelope.getBody()
					.addChild(param.getOMElement(VerySimpleDBServiceStub.PreparedQueryDB.MY_QNAME, factory));
			return emptyEnvelope;
		} catch (org.apache.axis2.databinding.ADBException e) {
			throw org.apache.axis2.AxisFault.makeFault(e);
		}

	}

	/* methods to provide back word compatibility */

	/**
	 * get the default envelope
	 */

	private java.lang.Object fromOM(org.apache.axiom.om.OMElement param, java.lang.Class<?> type,
			java.util.Map<String, String> extraNamespaces) throws org.apache.axis2.AxisFault {

		try {

			if (VerySimpleDBServiceStub.QueryDB.class.equals(type)) {

				return VerySimpleDBServiceStub.QueryDB.Factory.parse(param.getXMLStreamReaderWithoutCaching());

			}

			if (VerySimpleDBServiceStub.QueryDBResponse.class.equals(type)) {

				return VerySimpleDBServiceStub.QueryDBResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());

			}

			if (VerySimpleDBServiceStub.ClassNotFoundException.class.equals(type)) {

				return VerySimpleDBServiceStub.ClassNotFoundException.Factory
						.parse(param.getXMLStreamReaderWithoutCaching());

			}

			if (VerySimpleDBServiceStub.SQLExceptionE.class.equals(type)) {

				return VerySimpleDBServiceStub.SQLExceptionE.Factory.parse(param.getXMLStreamReaderWithoutCaching());

			}

			if (VerySimpleDBServiceStub.IllegalAccessException.class.equals(type)) {

				return VerySimpleDBServiceStub.IllegalAccessException.Factory
						.parse(param.getXMLStreamReaderWithoutCaching());

			}

			if (VerySimpleDBServiceStub.InstantiationException.class.equals(type)) {

				return VerySimpleDBServiceStub.InstantiationException.Factory
						.parse(param.getXMLStreamReaderWithoutCaching());

			}

			if (VerySimpleDBServiceStub.PreparedQueryDB.class.equals(type)) {

				return VerySimpleDBServiceStub.PreparedQueryDB.Factory.parse(param.getXMLStreamReaderWithoutCaching());

			}

			if (VerySimpleDBServiceStub.PreparedQueryDBResponse.class.equals(type)) {

				return VerySimpleDBServiceStub.PreparedQueryDBResponse.Factory
						.parse(param.getXMLStreamReaderWithoutCaching());

			}

			if (VerySimpleDBServiceStub.ClassNotFoundException.class.equals(type)) {

				return VerySimpleDBServiceStub.ClassNotFoundException.Factory
						.parse(param.getXMLStreamReaderWithoutCaching());

			}

			if (VerySimpleDBServiceStub.SQLExceptionE.class.equals(type)) {

				return VerySimpleDBServiceStub.SQLExceptionE.Factory.parse(param.getXMLStreamReaderWithoutCaching());

			}

			if (VerySimpleDBServiceStub.IllegalAccessException.class.equals(type)) {

				return VerySimpleDBServiceStub.IllegalAccessException.Factory
						.parse(param.getXMLStreamReaderWithoutCaching());

			}

			if (VerySimpleDBServiceStub.InstantiationException.class.equals(type)) {

				return VerySimpleDBServiceStub.InstantiationException.Factory
						.parse(param.getXMLStreamReaderWithoutCaching());

			}

			if (VerySimpleDBServiceStub.GetColumnNamesResponse.class.equals(type)) {

				return VerySimpleDBServiceStub.GetColumnNamesResponse.Factory
						.parse(param.getXMLStreamReaderWithoutCaching());

			}

		} catch (java.lang.Exception e) {
			throw org.apache.axis2.AxisFault.makeFault(e);
		}
		return null;
	}
}
