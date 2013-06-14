package moInput;

import java.io.File;

import save.graphPicture.SuffixAwareFilter;

public class MOFilter extends SuffixAwareFilter{

	@Override
	public boolean accept(File f) {
		String suffix = getSuffix(f);
		
		if(suffix!=null) return super.accept(f) || suffix.equals("mo");
		return false;
	}

	@Override
	public String getDescription() {
		return "Modelica File (*.mo)";
	}

}
