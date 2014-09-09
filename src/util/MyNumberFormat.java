package util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;

public class MyNumberFormat {

	public static NumberFormat getIntegerFormat() {
		NumberFormat nf = NumberFormat.getInstance();

		nf.setGroupingUsed(false);
		nf.setMaximumFractionDigits(0);
		return nf;
	}

	public static DecimalFormat getDecimalFormat() {
		DecimalFormat df = new DecimalFormat();

		DecimalFormatSymbols dfs = new DecimalFormatSymbols();
		dfs.setDecimalSeparator('.');

		df.setDecimalFormatSymbols(dfs);
		df.setGroupingUsed(false);
		return df;

	}

}
