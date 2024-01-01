package bibliothèque;

import java.time.LocalDate;
import java.util.ArrayList;

public class Abonnement {
	LocalDate date_création;
	double frais ;
	ArrayList<DetailEmprunt> liste_emprunt ;
	


	public Abonnement(LocalDate date_création, double frais) {
		super();
		this.date_création = date_création;
		this.frais = frais;
	}


	public Abonnement(LocalDate date_création) {
		super();
		this.date_création = date_création;
	}


	public Abonnement(LocalDate d, ArrayList<DetailEmprunt> liste) {
		this.date_création = d;
		this.liste_emprunt = liste;
	}


	public Abonnement(LocalDate date_création, double frais, ArrayList<DetailEmprunt> liste_emprunt) {
		this.date_création = date_création;
		this.frais = frais;
		this.liste_emprunt = liste_emprunt;
	}


	public LocalDate getDate_création() {
		return date_création;
	}


	public void setDate_création(LocalDate date_création) {
		this.date_création = date_création;
	}


	public double getFrais() {
		return frais;
	}


	public void setFrais(double frais) {
		this.frais = frais;
	} 
	
	
	
	
	
	
	
	
	

}
