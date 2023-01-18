package dataMapping.dataImport;

/**
 * This class extends Exception to be capable of some Excel specific errors
 * @author dborck
 *
 */
public class ExcelException extends Exception {
	
	private static final long serialVersionUID = 1L;

	public ExcelException () {
	}

	public ExcelException (String message) {
		super (message);
	}

	public ExcelException (Throwable cause) 	{
		super (cause);
	}

	public ExcelException (String message, Throwable cause) 	{
		super (message, cause);
	}
}
