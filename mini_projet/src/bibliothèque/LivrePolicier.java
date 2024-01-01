package bibliothèque;

public class LivrePolicier extends Livre {
	private String descriptif ;
	private String nomDetective  ;
	private String nomVictime ;
	
	public LivrePolicier() {
		super();
	}
	
	
	
	
	public LivrePolicier(String t, String a,long isbn ,  String descriptif, String nomDetective, String nomVictime) {
		super(t, a , isbn );
		this.descriptif = descriptif;
		this.nomDetective = nomDetective;
		this.nomVictime = nomVictime;
	}


	public String getDescriptif() {
		return descriptif;
	}


	public void setDescriptif(String descriptif) {
		this.descriptif = descriptif;
	}


	@Override
	public long getCode() {
		return super.getCode();
	}


	public String getNomDetective() {
		return nomDetective;
	}


	public void setNomDetective(String nomDetective) {
		this.nomDetective = nomDetective;
	}


	public String getNomVictime() {
		return nomVictime;
	}


	public void setNomVictime(String nomVictime) {
		this.nomVictime = nomVictime;
	}


	@Override
	public String toString() {
		return "LivrePolicier [descriptif=" + descriptif + ", nomDetective=" + nomDetective + ", nomVictime="
				+ nomVictime + ", getISBN()=" + getISBN() + ", getAuteur()=" + getAuteur() + ", getTitre()="
				+ getTitre() + ", toString()=" + super.toString() + ", getClass()=" + getClass() + ", hashCode()="
				+ hashCode() + "]";
	}
	
	

	
	
	
	

}
