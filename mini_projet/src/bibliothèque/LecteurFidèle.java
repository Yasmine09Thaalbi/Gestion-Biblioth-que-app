package bibliothèque;

public class LecteurFidèle extends Lecteur {
	private String adresse_email;
	private String préférence ;
	
	public LecteurFidèle(long c, String p, String n , Abonnement ab , int j, String a , String pr) {
		super(c, p, n,ab,j);
		this.adresse_email = a;
		this.préférence=pr ;
		

	}
	
	public LecteurFidèle(long c, String p, String n , Abonnement ab , double j, String a , String pr) {
		super(c, p, n,ab,j);
		this.adresse_email = a;
		this.préférence=pr ;
		

	}
	
	
	
	
	public LecteurFidèle(Lecteur l, String email, String préférence2) {
		  super(l.getCIN(), l.getPrenom(), l.getNom(), l.getAbonnement(),l.frais_Abonnement() ) ; 
		  this.adresse_email = email;
		  this.préférence = préférence2;
		
	}
	




	public String getAdresse_email() {
		return adresse_email;
	}




	public void setAdresse_email(String adresse_email) {
		this.adresse_email = adresse_email;
	}




	public String getPréférence() {
		return préférence;
	}




	public void setPréférence(String préférence) {
		this.préférence = préférence;
	}




	@Override 
	public double frais_Abonnement() {
		return Ab.frais* 0.85 ;
		
	}

	@Override
	public String toString() {
		return "LecteurFidèle [adresse_email=" + adresse_email + ", préférence=" + préférence
				 + "," + super.toString() + "]";
	}


	
	
	
	
	

}
