package bibliothèque;

import java.time.LocalDate;

public class DetailEmprunt {
	Livre livre;
	LocalDate dateEmprunt ;
	LocalDate dateRetour ;
	
	
	
	public DetailEmprunt(Livre livre) {
		this.livre = livre ;
		dateEmprunt = LocalDate.now();
		dateRetour = dateEmprunt.plusDays(7);
	}

	


	public DetailEmprunt(Livre livre, LocalDate dateEmprunt) {
		this.livre = livre;
		this.dateEmprunt = dateEmprunt;
	}




	@Override
	public String toString() {
		return "DetailEmprunt [livre=" + livre + ", dateEmprunt=" + dateEmprunt + ", dateRetour=" + dateRetour + "]";
	}



	public LocalDate getDateRetour() {
		return dateRetour;
	}



	public void setDateRetour(LocalDate dateRetour) {
		this.dateRetour = dateRetour;
	} 
	
	
	
	
	
	
}
