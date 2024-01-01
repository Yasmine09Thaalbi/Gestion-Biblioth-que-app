package bibliothèque;

import java.util.Scanner;

public class Main {
	public static void main(String[] args) {
		String chaine ; 
		Scanner x = new Scanner (System.in);
		System.out.println("donner une chaine :");
		chaine = x.nextLine() ; 
		
		String[] tab = chaine.split(" ");


		String nom = tab[1];
		String prenom =tab[2];
		String cin = tab[0];
		Long Cin = Long.parseLong(cin) ;
		
		Lecteur L1 = new Lecteur (Cin, nom , prenom );
		System.out.println(L1);
	    

		x.close();
		
		
	}

}
