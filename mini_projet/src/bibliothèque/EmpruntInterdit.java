package bibliothèque;

@SuppressWarnings("serial")
public class EmpruntInterdit extends Exception {

	public EmpruntInterdit() {
		super();
	}

	public EmpruntInterdit(String message) {
		super(message);

	}

}
