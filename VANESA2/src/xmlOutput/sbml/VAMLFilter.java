package xmlOutput.sbml;

import java.io.File;

import save.graphPicture.SuffixAwareFilter;

public class VAMLFilter extends SuffixAwareFilter{

	@Override
	public boolean accept(File f) {
		String suffix = getSuffix(f);
		
		if(suffix!=null) return super.accept(f) || suffix.equals("vaml");
		return false;
	}

	@Override
	public String getDescription() {
	
		return "VANESA Markup Language (*.vaml)";
	}

}
