package configurations;

public class GraphAlignmentSettings {

//	private boolean internetConnection = false;
	
	public static final String defaultBlastWebServerURL = "???";
	public static String blastWebServerURL = defaultBlastWebServerURL;
	public static int blastLocation = 1;
	public static final String[] blastLocations =
	{
			"use BLAST Web Server",
			"use local BLAST"
	};
	
	
	
	public static final String defaultRWebServerURL = "???";
	public static String rWebServerURL = defaultRWebServerURL;
	public static int mnalignerLocation = 1;
	public static final String[] mnAlignerLocations =
	{
			"run Algorithm on Web Server",
			"run Algorithm on local Rserve",
			"run Java Algorithm (slow)"
	};
	
	
//	private boolean checkSettings(){
//		
//		// checkInternetConnection
//		return true;
//	}
	
	public static String getBlastWebServerURL() {
		return blastWebServerURL;
	}
	public static void setBlastWebServerURL(String blastWebServerURL) {
		GraphAlignmentSettings.blastWebServerURL = blastWebServerURL;
	}
	public static String getRWebServerURL() {
		return rWebServerURL;
	}
	public static void setRWebServerURL(String webServerURL) {
		rWebServerURL = webServerURL;
	}

	public static int getBlastLocation() {
		return blastLocation;
	}

	public static void setBlastLocation(int blastLocation) {
		GraphAlignmentSettings.blastLocation = blastLocation;
	}

	public static int getMnalignerLocation() {
		return mnalignerLocation;
	}

	public static void setMnalignerLocation(int mnalignerLocation) {
		GraphAlignmentSettings.mnalignerLocation = mnalignerLocation;
	}
	
	
	
}
