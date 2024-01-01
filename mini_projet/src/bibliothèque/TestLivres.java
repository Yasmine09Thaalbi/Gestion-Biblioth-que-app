package bibliothèque;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Scanner;


public class TestLivres {
	public static void main (String[] args) { 
		Livre[] livres = new Livre[3];
		
		remplir_tableau(livres,3);
		System.out.println("avant tri");
		afficher_tableau(livres,3);
		tri_tableau(livres,3);
		System.out.println("aprés tri");
		afficher_tableau(livres,3);
		
		Arrays.sort(livres, new Comparator<Livre>(){

			@Override
			public int compare(Livre o1, Livre o2) {
				return o1.compare(o2);
			}
			
		});
		
		Arrays.sort(livres, (o1, o2) -> o1.compare(o2));
		afficher_tableau(livres,3);
		
	}
		
		
		private static void tri_tableau(Livre[] livres, int n) {
			boolean trie =false;
			while(!trie) {
				trie=true;
				for(int i=0 ; i<n-1;i++) {
					if(livres[i].compare(livres[i+1]) > 0) {
						Livre Aux = livres[i];
						livres[i]=livres[i+1];
						livres[i+1]=Aux;
						trie=false;
					}
				}
			}
		
	}


		private static void remplir_tableau(Livre[] livres, int n) {
			Scanner x = new Scanner(System.in);
			for ( int i=0 ; i< n ; i++) {
				String titre = x.next();
				String auteur = x.next();
				livres[i] = new Livre(titre , auteur); 
				
			}
		    x.close();
		}


		private static void afficher_tableau(Livre[] livres, int n) {
			for ( Livre l:livres) {
				System.out.println(l);
			}
				
			
		}

		
		
	


}
