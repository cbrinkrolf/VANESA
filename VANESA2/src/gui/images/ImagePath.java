package gui.images;

/** 
 * This class is to get the path to image in jar and normal form of the application
 * 
 * fast thead-safe singleton implementation 
 * @see http://www.theserverside.de/singleton-pattern-in-java/
 * 
 * @author jeff
 */
public class ImagePath {
	
	private static ImagePath instance = new ImagePath();
	
	private ImagePath(){
	}
	
	public java.net.URL getPath(String image){
		java.net.URL url = this.getClass().getResource(image);
		if (url == null)
        {
			 System.err.println("Couldn't find file: " + image);
        }
		return url;
	}
	
	
	public static ImagePath getInstance() {
	        return instance;
	}

}
