package gonOutput;

import java.io.File;

import save.graphPicture.SuffixAwareFilter;

public class GONFilter extends SuffixAwareFilter{

	@Override
	public boolean accept(File f) {
		String suffix = getSuffix(f);
		
		if(suffix!=null) return super.accept(f) || suffix.equals("csml");
		return false;
	}

	@Override
	public String getDescription() {
	
		return "Cell Illustrator (*.csml)";
	}

}
