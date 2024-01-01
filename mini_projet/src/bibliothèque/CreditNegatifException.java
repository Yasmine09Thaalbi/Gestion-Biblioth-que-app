package bibliothèque;

@SuppressWarnings("serial")
public class CreditNegatifException extends Exception{

	public CreditNegatifException() {
		super();
	}

	public CreditNegatifException(String message) {
		super(message);
	}

}
