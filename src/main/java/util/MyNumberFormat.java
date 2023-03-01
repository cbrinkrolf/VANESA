package util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class MyNumberFormat {
	public static DecimalFormat getIntegerFormat() {
		DecimalFormat df = new DecimalFormat();
		DecimalFormatSymbols dfs = new DecimalFormatSymbols();
		dfs.setGroupingSeparator('\'');
		df.setMaximumFractionDigits(0);
		df.setDecimalFormatSymbols(dfs);
		return df;
	}

	public static DecimalFormat getDecimalFormat() {
		DecimalFormat df = new DecimalFormat();
		DecimalFormatSymbols dfs = new DecimalFormatSymbols();
		dfs.setDecimalSeparator('.');
		dfs.setGroupingSeparator('\'');
		df.setMaximumFractionDigits(20);
		df.setDecimalFormatSymbols(dfs);
		return df;

	}

}
