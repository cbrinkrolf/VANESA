package database.brenda.Files;

public class BrendaDataPath {

	public BrendaDataPath(){
		
	}
	
	public java.net.URL getPath(String file){
		
		java.net.URL url = this.getClass().getResource(file);
		return url;
	}
	
}
