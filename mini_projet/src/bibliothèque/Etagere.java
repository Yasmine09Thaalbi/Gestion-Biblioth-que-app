package bibliothèque;

import java.util.ArrayList;

public class Etagere {
	int capacite ; 
	Livre[] tab_livres;
	static ArrayList<Livre> liste_livres ;
	
	
	
	
	
	public static void main ( String[] args) {
		
		Etagere E = new Etagere (5) ; 
		
		System.out.println(E.Nombre_livres1()); 
		System.out.println(E.Nombre_livres2());
		
		Livre l = new Livre("jjjj" , "llll"); // livre à ajouter 
		E.ajouter1(l);
		System.out.println(E.Nombre_livres1());
		//afficher tableau 
	
		
		E.ajouter2(l);
		System.out.println(E.Nombre_livres2());
		System.out.println(liste_livres);
		
		System.out.println(E.getLivre1(1));
		System.out.println(E.getLivre2(1));
		
		int x = E.chercher("kkk" , "ppp");
		System.out.println(x);

		
		
		
		
		
		
		
		
	}
	
	
	public Etagere(int capacite) {
		super();
		this.capacite = capacite;
		
		tab_livres = new Livre [capacite];
		liste_livres =new ArrayList<>();
		
		
		Livre l1 = new Livre ("l'etranger" , "Albert Camus");
		Livre l2 = new Livre ("Stupeur et tremblements" , "Amelie");
		
		
		tab_livres[0] = l1;
		tab_livres[1] = l2 ;
		
		liste_livres.add(l1);
		liste_livres.add(l2);
		
	}
	
	
		// pour tab
		int Nombre_livres1() {
			
			System.out.println(capacite);
			//return tab_livres.length;
			
			
			int s = 0 ; 
			for ( Livre l:tab_livres) {
				if (l!= null ) {
					s++ ; 
				}
			}
			
			return  s ;
	
			
			
		}
		
		// pour liste 
		int Nombre_livres2() {
			
			System.out.println(capacite);
			//return tab_livres.length;
			
			return  liste_livres.size() ; 
			// ( size ) pour la liste toujours retourner les lelts non nuls 
		
	    } 
		
		//pour tab
		void ajouter1(Livre l) {
			int n=Nombre_livres1();
			
			
			if (n< capacite) {
				for ( int i=n ; i>0 ; i-- ) {
					tab_livres[i] = tab_livres[i-1]; 
				}
				
				tab_livres[0]= l ; 
				
			}
			else System.out.println("tableau est plein ! ");
 			
			
		}
		
		//pour liste 
		void ajouter2(Livre l) {
			
			int n=Nombre_livres1();
			
			
			if (n< capacite) {
				liste_livres.add(0, l) ; //add(int index, E e)
				
			}
			else System.out.println("liste est pleine! ");
 			

		}
		
		//pour tab
		Livre getLivre1(int x) {
			if ( x < 0 || x > capacite) {
				return null;
				
			}
			else {
				return tab_livres[x-1];
			}

			
		}
		
		//pour liste 
        Livre getLivre2(int x) {
			if ( x < 0 || x > capacite) {
				return null;
				
			}
			else {
				return liste_livres.get(x-1);
			}

		}
        
        // pour tab 
        int chercher(String t ,String a) {
        	for ( int i=0 ; i<tab_livres.length ; i++ ) {
                if (tab_livres[i] != null && tab_livres[i].getTitre() != null && tab_livres[i].getAuteur() != null) {
                    if (tab_livres[i].getTitre().equals(t) && tab_livres[i].getAuteur().equals(a)) {
                        return i; 
                    }
                }
        			

        	}
        	return 0 ; 
        }
        
        
        // pour liste 
		
		
		
	
	
	
}
