package xmlInput.sbml;

import java.io.File;

import save.graphPicture.SuffixAwareFilter;

public class SBMLFilter extends SuffixAwareFilter{

	@Override
	public boolean accept(File f) {
		String suffix = getSuffix(f);
		
		if(suffix!=null) return super.accept(f) || suffix.equals("sbml");
		return false;
	}

	@Override
	public String getDescription() {
	
		return "System Biology Markup Language (*.sbml)";
	}

}
