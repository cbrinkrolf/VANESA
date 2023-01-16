package biologicalElements;

public class IDAlreadyExistException extends Exception{

	private static final long serialVersionUID = 84155L;
	
	public IDAlreadyExistException(){
	}
	public IDAlreadyExistException(String message){
		super(message);
	}

}
