package save.graphPicture;

import java.io.File;

public class JpegFilter extends SuffixAwareFilter{

	@Override
	public boolean accept(File f) {
		String suffix = getSuffix(f);
		
		if(suffix!=null) return super.accept(f) || suffix.equals("jpeg");
		return false;
	}

	@Override
	public String getDescription() {
	
		return "JPEG Files (*.jpeg)";
	}

}
