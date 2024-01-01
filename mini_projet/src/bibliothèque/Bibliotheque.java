package bibliothèque;

import java.sql.Connection;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Bibliotheque {
	ArrayList<Livre> liste_livres= new ArrayList<>();
	ArrayList<Lecteur> lecteurs  = new ArrayList<>() ;
	Map<Long ,DetailEmprunt> Map_emprunts =  new HashMap <Long ,DetailEmprunt>();
	Map<Long,Integer> mapLivres = new HashMap <Long,Integer>(); 
	
	
	// ça devient ajouterLivre(Connection con, Livre livre)
	void ajouter_livre ( Livre l) {
	    if (mapLivres.containsKey(l.getISBN())) {
	    	int nombre =  mapLivres.get(l.getISBN()) + 1 ; 
	        mapLivres.put(l.getISBN(),nombre);
	    } else {
	        mapLivres.put(l.getISBN(), 1);
	    }
	}
     

	
	void emprunter_livre(long cin, Livre l) throws  EmpruntInterdit {
		for (Lecteur lec:Abonnements_epuises()) {
			if (lec.getCIN() == cin )throw new EmpruntInterdit("interdit d'emprunter!");
			
		}
		if (liste_livres.contains(l) && !Map_emprunts.containsKey(cin )) {
			 if (mapLivres.get(l.getISBN()) == 0 ) {
				 System.out.println("Le livre n'est pas disponible");
				 return;
			 }
			 else {
			 int nombre =  mapLivres.get(l.getISBN()) - 1 ;
			 mapLivres.put(l.getISBN(),nombre);
					
			 DetailEmprunt livre_emprunt =new DetailEmprunt(l);
			 Map_emprunts.put(cin, livre_emprunt);
			 System.out.println("Emprunt ajouté avec succès.");
			 }
		}
		
		else 
			if (Map_emprunts.containsKey(cin )) 
				System.out.println("Le lecteur a deja  emprunt  un livre");
		    
	}
	

	int nombre_total_livres() {
		return liste_livres.size();
		
	}
	
	
	int nombre_livres_empruntés(){
		return Map_emprunts.size();
		
	}
	
	int nombre_livre_retours(){
		int nombre = 0;
		 LocalDate today = LocalDate.now();
		
		for (Map.Entry<Long, DetailEmprunt> entry : Map_emprunts.entrySet()) {
            DetailEmprunt emprunt = entry.getValue();
            LocalDate dateRetour = emprunt.getDateRetour();

            if (dateRetour.isAfter(today) && dateRetour.isBefore(today.plusDays(7))) {
                nombre++;
            }
		}
		
		return nombre;
		
	}
 	       
	/* LA FONCTION  lecteurs_fidèles */ 
	public ArrayList<LecteurFidèle> lecteurs_fidèles(){
		ArrayList<LecteurFidèle> lecteurs_fidèles=new ArrayList<>(); 
		
		for (Lecteur l:lecteurs) {
				int p = Period.between(l.Ab.date_création, LocalDate.now()).getMonths();
				int nb = l.Ab.liste_emprunt.size();
				

		        if (l instanceof LecteurFidèle && (nb / p) > 2) 
		            lecteurs_fidèles.add((LecteurFidèle) l);
		        if(l instanceof LecteurFidèle)
		        	lecteurs_fidèles.add((LecteurFidèle) l);
		        
            }
		return lecteurs_fidèles;
	}
	
	
	
	/* LA FONCTION categories_livres() */ 
	
	 public void categories_livres() {
	        Map<String, Integer> categorieLivres = new HashMap<>();

	        for (Livre livre : liste_livres) {
	            if (livre instanceof LivrePolicier) {
	                categorieLivres.put("Policier", categorieLivres.getOrDefault("Policier", 0) + 1);
	            } else if (livre instanceof LivreRomantique) {
	                categorieLivres.put("Romantique", categorieLivres.getOrDefault("Romantique", 0) + 1);
	            } else if (livre instanceof LivreScienceFiction) {
	                categorieLivres.put("Science Fiction", categorieLivres.getOrDefault("Science Fiction", 0) + 1);
	            } else {
	                categorieLivres.put("Livre Sans catégorie", categorieLivres.getOrDefault("Livre Sans catégorie", 0) + 1);
	            }
	        }

	        // Afficher le nombre de livres par catégorie
	        for (Map.Entry<String, Integer> entry : categorieLivres.entrySet()) {
	            System.out.println("Catégorie: " + entry.getKey() + ", Nombre de livres: " + entry.getValue());
	        }
	
	 }
	 
	 
	 /* LA FONCTION Abonnements_epuises() */ 
	 
	 public  ArrayList<Lecteur> Abonnements_epuises() {
		 ArrayList<Lecteur> liste = new ArrayList<>();
		 LocalDate Aujourd_hui = LocalDate.now();
		 
		 for (Lecteur l:lecteurs) {
			   LocalDate dateCreation = l.Ab.date_création;
		        LocalDate dateExpiration = dateCreation.plusYears(1);

		        if (Aujourd_hui.isAfter(dateExpiration)) {
		            liste.add(l);
		        }
		 }
		 
         return liste;
	 }
	
	 
	 /* méthode qui compte le nombre de livres dont l’auteur est Victor Hugo 
	 et dont le titre commence par L en utilisant une Stream.*/ 
	 
	 
	 public long nb_livres() {
		 return liste_livres.stream().filter(elt -> elt.getAuteur().equalsIgnoreCase("Victor Hugo") && elt.getTitre().startsWith("L")).count();
		 
	 }
	 
	 
	 /*Ecrire une méthode qui trie la liste des lecteurs suivant l’âge 
	 puis suivant l’ordre alphabétique en utilisant Stream. */
	 
	 public List<Lecteur> tri_alphabet() {
		 return lecteurs.stream().sorted(Comparator.comparing(Lecteur::getNom).thenComparing(Lecteur::getPrenom)).toList();
		 
	 }
	 
	 
	 
	 
	
	
	public static void main(String[] args) {
		    Bibliotheque bib = new Bibliotheque();
		 
	      	Livre l1=new Livre("Stupeur et tremblement", "Nathalie Nothomb",12578945677L);
        	bib.liste_livres.add(l1);
		    Livre l2=new Livre("Les misérables", "Victor Hugo",1236547896541L);
		    bib.liste_livres.add(l2);
	        Livre l3=new Livre("Le Mur", "Jean Paul Sartre",7412589637418L);
	        bib.liste_livres.add(l3);
	        Livre l4=new Livre("Notre dame de Paris", "Victor Hugo",7894561236547L);
	        bib.liste_livres.add(l4);
	        Livre l5=new Livre("Crime et Chatiment", "Fyodor Dostoevsky",1234567891230L);
	        bib.liste_livres.add(l5);
	        Livre l6=new Livre("Orgueuil et Préjugés", "Jane Austen",3216549871230L);
	        bib.liste_livres.add(l6);
	        Livre l7=new Livre("Emma", "Jane Austen",1487956412347L);
	        bib.liste_livres.add(l7);
	        Livre l8=new Livre("Orgueuil et Préjugés", "Jane Austen",3216549871230L);
	        bib.liste_livres.add(l8);
	        Livre l9=new Livre("Orgueuil et Préjugés", "Jane Austen",3216549871230L);
	        bib.liste_livres.add(l9);
	        Livre l10=new Livre("que serais je sans toi", "Guillaume Musso",2589631478520L);
	        bib.liste_livres.add(l10);
	        Livre l11=new Livre("Stupeur et tremblement", "Nathalie Nothomb",12578945677L);
	        bib.liste_livres.add(l11);
	        Livre l12=new Livre("Le Mur", "Jean Paul Sartre",7412589637418L);
	        bib.liste_livres.add(l12);		 
		 

	        for (Livre livre: bib.liste_livres) {
	        	bib.ajouter_livre( livre);
	        }
	 	        

	        // Afficher Maplivres
	 	        
	 	    System.out.println(bib.mapLivres);
	 	    
	 	   /******************Liste de lecteurs***********************/
        
	        bib.lecteurs.add(new Lecteur(782456789,"Ines","Slim"));
	        bib.lecteurs.add(new Lecteur(254567899,"Aymen","Ben Salah"));
	        bib.lecteurs.add(new Lecteur(254566899,"Imen","Massoudi"));
	        bib.lecteurs.add(new Lecteur(264567899,"Selim","Ben Aissa"));
	        bib.lecteurs.add(new Lecteur(884567899,"Amine","Ben youssef"));
	        
	        
	        /*bib.emprunter_livre(782456789L, l3);
	        System.out.println(bib.mapLivres);

	        bib.emprunter_livre(884567899L, l6);
	        System.out.println(bib.mapLivres);

	        bib.emprunter_livre(264567899L, l8);
	        System.out.println(bib.mapLivres);
	   
	        bib.emprunter_livre(254566899L, l9);
	        System.out.println(bib.mapLivres);
	
	        bib.emprunter_livre(254567899L,l9 );
	        System.out.println(bib.mapLivres);
	        
	        bib.emprunter_livre(782456789L, l5);*/
	        //System.out.println(bib.mapLivres);
	        //System.out.println(bib.Map_emprunts);
	        
	        
	        /********************************Affichage du  nombre total de livres*******************/
	        
	        System.out.println("Nombre total de livres: "+bib.nombre_total_livres());

	        /********************************Affichage du  nombre de livres empruntes*******************/
	        
            System.out.println("Nombre toatal de livres empruntes: "+bib.nombre_livres_empruntés());
            
        
            
            /********************************Affichage du  nombre total de livres de retour dans les sept jours qui suivent j*******************/ 	       
 	        System.out.println("Nombre  de livres de retour dans les 7 jours suivants: "+bib.nombre_livre_retours());
 	        
 	        
 	       // faites les modifications nécessaires pour afficher 4
 	        LocalDate today = LocalDate.now();
 	        
 	        for (Map.Entry<Long, DetailEmprunt> entry : bib.Map_emprunts.entrySet()) {
 	            DetailEmprunt emprunt = entry.getValue();
 	            LocalDate nouvelleDate = today.plusDays(3);
 	            emprunt.setDateRetour(nouvelleDate);
 	        }
 	        
 	        
 	        
 	       System.out.println("Nombre  de livres de retour dans les 7 jours suivants: "+bib.nombre_livre_retours());

 	       
 	       System.out.println("nombre de livres de victor hugo : " + bib.nb_livres());
 	       
 	       System.out.println(bib.tri_alphabet());

 	       System.out.println(bib.liste_livres);
	    
		
	}



	

}
