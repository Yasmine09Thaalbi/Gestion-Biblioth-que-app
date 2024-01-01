package bibliothèque;

public class LivreRomantique extends Livre  {
	private String descriptif ;
	private String nom ;
	
	public LivreRomantique() {
		super();
	}
	
	
	public LivreRomantique(String t, String a,long isbn ,  String descriptif, String nom) {
		super(t, a, isbn);
		this.descriptif = descriptif;
		this.nom = nom;
	}

	

	public String getDescriptif() {
		return descriptif;
	}



	public String getNom() {
		return nom;
	}



	@Override
	public String toString() {
		return "LivreRomantique [descriptif=" + descriptif + ", nom=" + nom + ", getISBN()=" + getISBN()
				+ ", getAuteur()=" + getAuteur() + ", getTitre()=" + getTitre() + ", toString()=" + super.toString()
				+ ", getClass()=" + getClass() + ", hashCode()=" + hashCode() + "]";
	}


	public void setDescriptif(String descriptif) {
		this.descriptif = descriptif;
	}


	public void setNom(String nom) {
		this.nom = nom;
	} 
	
	
	
	
	

}
