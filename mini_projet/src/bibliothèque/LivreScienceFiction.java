package bibliothèque;


public class LivreScienceFiction extends Livre {
	private int année; 
	private String espace;
	
	public LivreScienceFiction() {
		super();
	}
	
	public LivreScienceFiction(String t, String a,long isbn , int année, String espace) {
		super(t, a , isbn);
		this.année = année;
		this.espace = espace;
	}


	@Override
	public String toString() {
		return "LivreScienceFiction [année=" + année + ", espace=" + espace + ", getISBN()=" + getISBN()
				+ ", getAuteur()=" + getAuteur() + ", getTitre()=" + getTitre() + ", toString()=" + super.toString()
				+ ", getClass()=" + getClass() + ", hashCode()=" + hashCode() + "]";
	}


	public int getAnnée() {
		return année;
	}


	public String getEspace() {
		return espace;
	}

	public void setAnnée(int année) {
		this.année = année;
	}

	public void setEspace(String espace) {
		this.espace = espace;
	}
	
	
	
	
	
	
	

}
