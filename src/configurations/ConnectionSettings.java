/***************************************************************
 * Copyright (c) Benjamin Kormeier 2006-2010.                  *
 * All rights reserved.                                        *
 ***************************************************************/
package configurations;

import database.Connection.DBconnection;

/**
 * @author Benjamin Kormeier
 * @version 1.0 20.10.2010
 */
public class ConnectionSettings 
{
	private static DBconnection db_connection=null;
	private static boolean useInternetConnection=true;
	
	private static String web_service_url=new String(); 
	
	private static String proxy_host=new String();
	private static String proxy_port=new String();

	private static String fileDirectory=new String();

	public static String getFileDirectory()
	{
		return fileDirectory;
	}

	public static void setFileDirectory(String fileDirectory)
	{
		ConnectionSettings.fileDirectory=fileDirectory;
	}

	/**
	 * @return the db_connection
	 */
	public static DBconnection getDBConnection()
	{
		return db_connection;
	}

	/**
	 * @param db_connection the db_connection to set
	 */
	public static void setDBConnection(DBconnection db_connection)
	{
		ConnectionSettings.db_connection=db_connection;
	}

	/**
	 * @return the useInternetConnection
	 */
	public static boolean useInternetConnection()
	{
		return useInternetConnection;
	}

	/**
	 * @param useInternetConnection the useInternetConnection to set
	 */
	public static void setInternetConnection(boolean useInternetConnection)
	{
		ConnectionSettings.useInternetConnection=useInternetConnection;
	}

	/**
	 * @return the proxy_host
	 */
	public static String getProxyHost()
	{
		return proxy_host;
	}

	/**
	 * @param proxy_host the proxy_host to set
	 */
	public static void setProxyHost(String proxy_host)
	{
		System.setProperty("http.proxyHost", proxy_host);
		ConnectionSettings.proxy_host=proxy_host;
	}

	/**
	 * @return the proxy_port
	 */
	public static String getProxyPort()
	{
		return proxy_port;
	}

	/**
	 * @param proxy_port the proxy_port to set
	 */
	public static void setProxyPort(String proxy_port)
	{
		System.setProperty("http.proxyPort", proxy_port);
		ConnectionSettings.proxy_port=proxy_port;
	}

	/**
	 * @return the web_service_url
	 */
	public static String getWebServiceUrl()
	{
		return web_service_url;
	}

	/**
	 * @param web_service_url the web_service_url to set
	 */
	public static void setWebServiceUrl(String web_service_url)
	{
		ConnectionSettings.web_service_url=web_service_url;
	}
}
