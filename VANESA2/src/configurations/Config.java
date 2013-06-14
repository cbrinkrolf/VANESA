package configurations;

import java.io.File;
import java.security.CodeSource;
import java.security.ProtectionDomain;

public class Config
{
	// directories
	private static String sLocation;  	 // location for the folder where the jar(or bin-Directory) is 
	private static String sDirConfig;	 // location for config
			
	private static ProtectionDomain pDomain;
	private static CodeSource cSource;
	
	private static void init()
	{
		pDomain = new Config().getClass().getProtectionDomain();
		cSource = pDomain.getCodeSource();
		sLocation = cSource.getLocation().toString();
		
		int index = sLocation.indexOf("jar");
		if(index>0)
		{	// if software starts as a jar
			sLocation = sLocation.substring(5, index);
			index = sLocation.lastIndexOf(File.separator);
			sLocation = sLocation.substring(0, index) + File.separator;
		}
		else
		{	// software starts with a script (uses bin directory)
			sLocation = sLocation.substring(5, sLocation.length() - 4);
		}
		
		if(sLocation.indexOf(":")>0) 
			sLocation = sLocation.substring(sLocation.indexOf(":")-1);
		
		sDirConfig = sLocation + "config";
	}


	public static String getSLocation()
	{
		init();
		return sLocation;
	}


	public static String getSDirConfig()
	{
		init();
		return sDirConfig.replace("%20", " ");
	}
	
	public static boolean mkDir()
	{
		init();
		File dir = new File(getSDirConfig());
		
		if(dir.exists() && dir.isDirectory()){
			
			return true;
		}else{
			
			return dir.mkdirs();}
	}
	
}