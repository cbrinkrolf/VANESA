package save.graphPicture;

import java.io.File;

public class EPSFilter extends SuffixAwareFilter {

	@Override
	public boolean accept(File f) {
		String suffix = getSuffix(f);

		if (suffix != null)
			return super.accept(f) || suffix.equals("eps");
		return false;
	}

	@Override
	public String getDescription() {

		return "EPS Files (*.eps)";
	}

}
