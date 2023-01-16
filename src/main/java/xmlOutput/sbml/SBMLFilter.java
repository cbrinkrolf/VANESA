package xmlOutput.sbml;

import java.io.File;

import save.graphPicture.SuffixAwareFilter;

public class SBMLFilter extends SuffixAwareFilter{

	@Override
	public boolean accept(File f) {
		String suffix = getSuffix(f);
		
		if(suffix!=null) return super.accept(f) || suffix.equals("gml");
		return false;
	}

	@Override
	public String getDescription() {
	
		return "Graph Markup Language (*.gml)";
	}

}
