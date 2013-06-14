package io;

import java.io.File;
import javax.swing.filechooser.*;

public class MyFileFilter extends FileFilter {

	private String ending;
	private String description;
	
	public MyFileFilter(String ending, String description){
		this.ending=ending;
		this.description=description;
	}
	
	
	private static String getExtension(File f) {
	        String ext = null;
	        String s = f.getName();
	        int i = s.lastIndexOf('.');

	        if (i > 0 &&  i < s.length() - 1) {
	            ext = s.substring(i+1).toLowerCase();
	        }
	        return ext;
   }
	
	
	@Override
	public boolean accept(File f) {

		if (f.isDirectory()) {
			return true;
		}

		String extension = getExtension(f);
		if (extension != null) {
			if (extension.equals(ending)) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

	@Override
	public String getDescription() {		
		return description;
	}
}
