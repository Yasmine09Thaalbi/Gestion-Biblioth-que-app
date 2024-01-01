package bibliothèque;

import java.util.ArrayList;

public class TestLivres2 {
	public static void main ( String[] args) {
		ArrayList<Livre> liste = new ArrayList<>();
		liste.add(new Livre("Les misérables" , "Victor Hugo"));
		liste.add(new Livre("L'étranger" , "Albert Camus"));
		liste.add(new Livre("Stupeur et Tremblements" , "Amélie Nothomb"));
		liste.add(new Livre("Le Mur" , "Jean Paul Sartre"));
		liste.add(new Livre("Notre Dame de Paris" , "Victor Hugo"));
		System.out.println(liste);
		
		liste.sort((o1,o2) -> o1.compare(o2));
		
		System.out.println("les livres   dont   l'auteur   est   Hugo  : ");
		
		for (int i=0 ; i<liste.size() ; i++) {
			if (liste.get(i).getAuteur().contains("Hugo"))
				System.out.println(liste.get(i));
		}
		
		System.out.println("les livres   dont   le titre   starts   with L  : ");
		for (Livre l:liste) {
			if (l.getTitre().startsWith("l") || l.getTitre().startsWith("L")  ) 
				System.out.println(l);
		}
		
		Livre livreDeMusso = new Livre("Les Misérables de Musso", "Musso");
		ajouter(liste, livreDeMusso);
	    System.out.println("Après ajout du livre de Musso :");
	    System.out.println(liste);
		

		
		
	}
	
	
	private static void ajouter(ArrayList<Livre> liste, Livre l) {
		 boolean livrePresent = false;

		 if (l.getAuteur().equals("Musso")) {
			    for (int i = 0; i < liste.size(); i++) {
			        Livre livreCourant = liste.get(i);

			        // Si on trouve un livre de Mussot, ajouter le livre de Musso à la fin
			        if (livreCourant.getAuteur().equals("Musso")) {
			            livrePresent = true;
			        }

			        // Si on trouve un livre d'un auteur différent, le remplacer par le livre de Musso
			        if (!livreCourant.getAuteur().equals("Musso")) {
			            liste.set(i, l); // set (position , element) 
			            return; // On a trouvé un livre d'un auteur différent, on peut sortir de la boucle
			        }
			    }

			    // Si on n'a pas trouvé de livre d'un auteur différent, ajouter le livre de Musso à la fin
			    if (!livrePresent) {
			        liste.add(l);
			    }
			    
			    System.out.println("ajout impossible ! ");
			
			 
		 }
		   
	}

	
	


	
}
