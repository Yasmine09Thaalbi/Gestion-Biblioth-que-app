package bibliothèque;

import javafx.beans.property.StringProperty;

public class Livre {
	private static long code_c = 0;
	private long code ;
	private String titre ;
	private String auteur ; 
	private long ISBN;
	
	public Livre( String t , String a){
		this.titre = t ;
		this.auteur = a ; 
		this.code = code_c ++; 
	}
	
	public Livre() {
		this.code = code_c ++ ; 
	}
	
	
	//Constructeur:  public Livre(String titre, String auteur, long ISBN) 
	public Livre(String t, String a, long iSBN) {
		this.titre = t;
		this.auteur = a;
		this.ISBN = iSBN;
		this.code = code_c ++; 
	}
	

	public long getCode() {
		return code;
	}

	public void setCode(long code) {
		this.code = code;
	}

	public void setISBN(long iSBN) {
		ISBN = iSBN;
	}
	
	
	

	public void setAuteur(String auteur) {
		this.auteur = auteur;
	}

	public void setTitre(String titre) {
		this.titre = titre;
	}

	public long getISBN() {
		return ISBN;
	}

	public String getAuteur() {
		return auteur;
	}
	
	

	public String getTitre() {
		return titre;
	}
	
	@Override
	public String toString() {
		return "Livre [code=" + code + ", titre=" + titre + ", auteur=" + auteur + ", isbn=" + ISBN + "]";
	}

	int compare (Livre L1) {
		return this.titre.compareToIgnoreCase(L1.titre);
	}
	
	static int compare ( Livre L1 , Livre L2 ) {
		return L1.titre.compareToIgnoreCase(L2.titre);
	}
	
	
	public static void main( String[] args ) {
		Livre L1 = new Livre("hugo " , "miserables");
		Livre L2 = new Livre("John Smith", "The Adventures of Java");
		Livre L3 = new Livre() ;
		Livre L4 = new Livre("John Smith", "The Adventures of Java" , 444);
		Livre L5 = new Livre("John Smith", "The Adventures of Java" , 444);
		
		
		
		
		System.out.println(L1);
		System.out.println(L2);
		System.out.println(L3);
		System.out.println(L4);
		System.out.println(L5);

		
		System.out.println(L1.compare(L2));
		System.out.println(compare(L1 , L2 ));
		
	}

}
