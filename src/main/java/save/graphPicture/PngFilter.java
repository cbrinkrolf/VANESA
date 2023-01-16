package save.graphPicture;

import java.io.File;

public class PngFilter extends SuffixAwareFilter{

	@Override
	public boolean accept(File f) {
		String suffix = getSuffix(f);
		
		if(suffix!=null) return super.accept(f) || suffix.equals("png");
		return false;
	}

	@Override
	public String getDescription() {
	
		return "PNG Files (*.png)";
	}

}
