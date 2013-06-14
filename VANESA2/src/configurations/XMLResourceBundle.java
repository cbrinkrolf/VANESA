/***************************************************************
 * Copyright (c) Benjamin Kormeier 2006-2009.                  *
 * All rights reserved.                                        *
 ***************************************************************/
package configurations;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Properties;

/**
 * @author Benjamin Kormeier
 * @version 1.0 06.08.2008
 */
public class XMLResourceBundle
{
	private ClassLoader loader=null;
	private Locale locale=null;
	private Properties props=new Properties();
	private final String SUFFIX=new String(".properties.xml");
	
	
	/**
	 *  Sole constructor.
	 */
	public XMLResourceBundle()
	{
		loader=ClassLoader.getSystemClassLoader();
		props = new Properties();
	}
	
	/**
	 * Rresource bundle using the specified base name, the default locale, and the caller's class loader.
	 * @param baseName - the base name of the resource bundle, a fully qualified class name 
	 */
	public XMLResourceBundle(String baseName)
	{
		this(baseName,null,null);
	}
	
	/**
	 * Resource bundle using the specified base name and locale, and the caller's class loader.
	 * @param baseName - the base name of the resource bundle, a fully qualified class name 
	 * @param locale - the locale for which a resource bundle is desired 
	 */
	public XMLResourceBundle(String baseName, Locale locale)
	{
		this(baseName,locale,null);
	}
	
	/**
	 * Resource bundle using the specified base name, locale, and class loader.
	 * @param baseName - the base name of the resource bundle, a fully qualified class name
	 * @param locale - the locale for which a resource bundle is desired 
	 * @param classloader-the class loader from which to load the resource bundle
	 */
	public XMLResourceBundle(String baseName, Locale locale, ClassLoader classloader)
	{
		this.locale=locale;
		
		if(loader==null)
			loader=ClassLoader.getSystemClassLoader();
		else
			loader=classloader;
		
		if(locale==null)
			locale=Locale.getDefault();
		
		props = new Properties();
	  	
		try
		{
			URL url=loader.getResource(baseName+locale.getLanguage()+"_"+locale.getCountry()+SUFFIX);
			
			if(url==null)
			{
				url=loader.getResource(baseName+"_"+locale.getLanguage()+SUFFIX);
				
				if(url==null)
					url=loader.getResource(baseName+SUFFIX);
			}
			
			
			URLConnection connection=url.openConnection();
			props.loadFromXML(connection.getInputStream());
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	    
	}
	
	/**
	 * Gets an object for the given key from this resource bundle or one of its parents.
	 *  This method first tries to obtain the object from this resource bundle using handleGetObject. 
	 *  If not successful, and the parent resource bundle is not null, it calls the parent's getObject method. If still not successful, it throws a MissingResourceException. 
	 * @param key
	 * @return
	 */
	public Object getObject(String key)
	{
		return handleGetObject(key);
	}
	
	/**
	 * Gets a string for the given key from this resource bundle or one of its parents. Calling this method is equivalent to calling. 
	 * @param key - the key for the desired object 
	 * @return the object for the given key 
	 */
	public String getString(String key)
	{
		return props.getProperty(key);
	}
	
	/**
	 *  Returns the locale of this resource bundle. This method can be used after a call to getBundle() to determine whether the resource bundle returned really corresponds to the requested locale or is a fallback. 
	 * @return the locale of this resource bundle
	 */
	public Locale getLocale()
	{
		return locale;
	}
	
	/** 
	 *  Returns an enumeration of the keys.
	 */
	@SuppressWarnings("unchecked")
	public Enumeration<String> getKeys()
	{
		return (Enumeration<String>)props.stringPropertyNames();
	}
	
	/**
	 * Gets an object for the given key from this resource bundle. Returns null if this resource bundle does not contain an object for the given key. 
	 * @param key - the key for the desired object 
	 * @return the object for the given key, or null 
	 */
	protected Object handleGetObject(String key)
	{
		return props.get(key);
	}

}
