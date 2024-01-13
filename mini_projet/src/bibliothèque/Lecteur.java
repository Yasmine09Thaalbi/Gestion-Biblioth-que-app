package bibliothèque;

public class Lecteur {
	private long CIN;
	private String prenom;
	private String nom;
	Abonnement Ab; 
	private double somme_cumle; //credit ( frais - somme versé)
	

	double calculer_credit(double somme ) throws CreditNegatifException{
		
		double credit = Ab.frais - this.somme_cumle -somme ;
		if ( credit < 0 )
			throw new CreditNegatifException("credit négatif ");
		
		this.somme_cumle+=somme; 
		
		return credit ;
	}
	
	
	
	
	public Lecteur(long cIN) {
		super();
		CIN = cIN;
	}




	public Lecteur(long cIN, String prenom, String nom, Abonnement ab , double j , double som ) {
		CIN = cIN;
		this.prenom = prenom;
		this.nom = nom;
		Ab = ab;
		ab.frais = j ; 
		this.somme_cumle =som ;
	}





	public Lecteur(long c, String p, String n) {
		this.CIN = c;
		this.prenom = p;
		this.nom = n;

	}
	
	

	
	
	public Lecteur(long cIN, String prenom, String nom, Abonnement ab, double somme_cumle) {
		CIN = cIN;
		this.prenom = prenom;
		this.nom = nom;
		Ab = ab;
		this.somme_cumle = somme_cumle;
	}



	@Override
	public String toString() {
		return "Lecteur [CIN=" + CIN + ", nom=" + nom + ", prenom=" + prenom + ", Ab=" + Ab + "]";
	}



	public long getCIN() {
		return CIN;
	}
	
	
	
	public double frais_Abonnement() {
		return Ab.frais; 
	}


	public String getNom() {
		return nom;
	}


	public String getPrenom() {
		return prenom;
	}


	public Abonnement getAbonnement() {
		return Ab;
	}


	public double getSomme_cumle() {
		return somme_cumle;
	}




	public void setCIN(long cIN) {
		CIN = cIN;
	}




	public void setPrenom(String prenom) {
		this.prenom = prenom;
	}




	public void setNom(String nom) {
		this.nom = nom;
	}




	public void setAb(Abonnement ab) {
		Ab = ab;
	}




	public void setSomme_cumle(double somme_cumle) {
		this.somme_cumle = somme_cumle;
	}
	
	



}
