/***************************************************************
 * Copyright (c) Benjamin Kormeier 2006-2009.                  *
 * All rights reserved.                                        *
 ***************************************************************/
package configurations;

/**
 * @author Benjamin Kormeier
 * @version 1.0 19.10.2010
 */
public class ResourceLibrary
{
	private static final XMLResourceBundle SETTINGS=new XMLResourceBundle("settings");
		
	public static final String getSettingsResource(String key)
	{
		return SETTINGS.getString(key);
	}
	
}