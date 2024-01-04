package bibliothèque;

import java.time.LocalDate;
import java.util.ArrayList;

public class test {
	public static void main(String[] args) {
		
	    Bibliotheque bib=new Bibliotheque();
	
	    //Constructeur:  public Livre(String titre, String auteur, long ISBN) 
	
    	Livre l1=new Livre("Stupeur et tremblement", "Nathalie Nothomb",12578945677L);
    	bib.liste_livres.add(l1);
	    Livre l2=new LivreRomantique("Les mis�rables", "Victor Hugo",1236547896541L,"histo","Jean Valgean");
	    bib.liste_livres.add(l2);
		Livre l3=new LivrePolicier("meurtre","Hercule Poirot ",7412589637418L,"Le crime de l'orient express", "Agatha Christie","ines");
		bib.liste_livres.add(l3);
        Livre l4=new LivrePolicier("meurtre","Hercule Poirot ",7894561236547L,"Mort sur le Nil", "Agatha Christie","ines");
        bib.liste_livres.add(l4);
        Livre l5=new Livre("Crime et Chat�ment", "Fyodor Dostoevsky",1234567891230L);
        bib.liste_livres.add(l5);
        Livre l6=new LivreRomantique("Orgueuil et Pr�jug�s", "Jane Austen",3216549871230L,"histoire","Elizabeth");
        bib.liste_livres.add(l6);
        Livre l7=new LivreRomantique("Emma", "Jane Austen",1487956412347L,"longue histoire","Emma");
        bib.liste_livres.add(l7);
        Livre l8=new LivreRomantique("Orgueuil et Pr�jug�s", "Jane Austen",3216549871230L,"histoire","Elizabeth");
        bib.liste_livres.add(l8);
        Livre l9=new LivreRomantique("Orgueuil et Pr�jug�s", "Jane Austen",3216549871230L,"histoire","Elizabeth");
        bib.liste_livres.add(l9);
        Livre l10=new LivreScienceFiction("La plan�te des singes", "Pierre Boulle",2589631478520L,2500,"Plan�te Soror");
        bib.liste_livres.add(l10);
        Livre l11=new Livre("Stupeur et tremblement", "Nathalie Nothomb",12578945677L);
        bib.liste_livres.add(l11);
        Livre l12=new Livre("Le Mur", "Jean Paul Sartre",7412589637418L);
        bib.liste_livres.add(l12);
        

        
        for (Livre livre: bib.liste_livres) {
        	bib.ajouter_livre( livre);
        }
        
        /*
        //affichage de la liste des livres
       for (Livre livre: bib.liste_livres) {
        	System.out.println(livre);
        	
	 
        }
       
       System.out.println();

        */
        
        /******************Liste de lecteurs***********************/
        
        // lecteur1
        ArrayList<DetailEmprunt> listeEmprunts1=new ArrayList<>();
        listeEmprunts1.add(new DetailEmprunt(l1, LocalDate.of(2022,2, 1)));
        listeEmprunts1.add(new DetailEmprunt(l2, LocalDate.of(2022,2, 9)));
        listeEmprunts1.add(new DetailEmprunt(l3, LocalDate.of(2022,2, 17)));
        listeEmprunts1.add(new DetailEmprunt(l4, LocalDate.of(2022,3, 5)));
        Abonnement a1= new Abonnement  (LocalDate.of(2022,2, 1) ,listeEmprunts1);
        Lecteur lecteur1= new Lecteur(782456789,"Ines","Slim",a1,20);
        bib.lecteurs.add(lecteur1);
        
        // lecteur2
        ArrayList<DetailEmprunt> listeEmprunts2=new ArrayList<>();
        listeEmprunts2.add(new DetailEmprunt(l1, LocalDate.of(2022,9, 1)));
        listeEmprunts2.add(new DetailEmprunt(l2, LocalDate.of(2022,9, 9)));
        listeEmprunts2.add(new DetailEmprunt(l3, LocalDate.of(2022,9, 17)));
        listeEmprunts2.add(new DetailEmprunt(l4, LocalDate.of(2022,10, 5)));
        listeEmprunts2.add(new DetailEmprunt(l5, LocalDate.of(2022,10, 11)));
        listeEmprunts2.add(new DetailEmprunt(l6, LocalDate.of(2022,10, 18)));
        listeEmprunts2.add(new DetailEmprunt(l5, LocalDate.of(2022,10, 25)));
        Abonnement a2= new Abonnement    (LocalDate.of(2022,9, 1),listeEmprunts2 );
        Lecteur lecteur2= new Lecteur(254567899,"Aymen","Ben Salah",a2,20);
        bib.lecteurs.add(lecteur2);
 
        // lecteur3
        ArrayList<DetailEmprunt> listeEmprunts3=new ArrayList<>();
        listeEmprunts3.add(new DetailEmprunt(l5, LocalDate.of(2022,10, 1)));
        Abonnement a3= new Abonnement (LocalDate.of(2022,10, 1) ,listeEmprunts3 );
        Lecteur lecteur3= new Lecteur(254566899,"Imen","Massoudi",a3,20);
        bib.lecteurs.add(lecteur3);
        
	     // lecteur4
	     ArrayList<DetailEmprunt> listeEmprunts4=new ArrayList<>();
	     listeEmprunts1.add(new DetailEmprunt(l4, LocalDate.of(2022,3, 1)));
	     Abonnement a4= new Abonnement (LocalDate.of(2022,2, 1) ,listeEmprunts4 );
	     Lecteur lecteur4= new LecteurFidèle(264567899,"Selim","Ben Aissa",a4,20,"ii@gmail.com","Romantique");
	     bib.lecteurs.add(lecteur4);
	     
	     /*
         System.out.println("la liste des lecteurs fidèles");
	     ArrayList<LecteurFidèle> lesfideles=bib.lecteurs_fidèles();
	     for(LecteurFidèle c:lesfideles) 
	    	 System.out.println(c);
	     System.out.println();
	     
	     
	     System.out.println("le nombre de livres par catégorie");
	     bib.categories_livres();
	     System.out.println();
	     
	     
	     //test de la fonction Abonnements_epuises()
	     System.out.println("la liste des clients dont l'abonnement est épuisé ");
	     for(Lecteur d:bib.Abonnements_epuises()) 
	    	 System.out.println(d);
	     
	     */
	     
	     Abonnement a5= new Abonnement (LocalDate.of(2022,10, 1) ,listeEmprunts3 );
    	 @SuppressWarnings("unused")
		Lecteur lecteur5= new Lecteur(254566899,"Imen","Massoudi",a5,20,5);
	     try {
	    	
			 //double d = lecteur5.calculer_credit(30);
			 //System.out.println(d);
			 bib.emprunter_livre(254566899 , l12);
			 
		} /*catch (CreditNegatifException e) {
			System.out.println(e.getMessage());
		}*/
	     
	     catch (EmpruntInterdit e) {
			System.out.println(e.getMessage());
	     }
	     
	     System.out.println(bib.lecteurs);
        
       }       

}
