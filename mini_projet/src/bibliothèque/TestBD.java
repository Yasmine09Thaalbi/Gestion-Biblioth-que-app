package bibliothèque;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.Date;


public class TestBD {
	
	private static final String BDD = "Bibliotheque";
	private static final String url = "jdbc:mysql://localhost:3306/" + BDD;
	private static final String username = "root";
	private static final String password = "";
	
	// Méthode pour vérifier si un livre existe déjà
    private static boolean livreExiste(Connection con, Livre livre) throws SQLException {
        String query = "SELECT COUNT(*) FROM Livre WHERE titre = ? AND auteur = ? AND ISBN = ?";
        try (PreparedStatement preparedStatement = con.prepareStatement(query)) {
            preparedStatement.setString(1, livre.getTitre());
            preparedStatement.setString(2, livre.getAuteur());
            preparedStatement.setLong(3, livre.getISBN());
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    int count = resultSet.getInt(1);
                    return count > 0;
                }
            }
        }
        return false;
    }

	// Méthode pour ajouter un livre
    private static void ajouterLivre(Connection con, Livre livre) {
        try {
            String queryLivre = "INSERT INTO Livre (titre, auteur, ISBN) VALUES ('" + livre.getTitre() + "', '" + livre.getAuteur() + "', " + livre.getISBN() + ")";
            try (Statement stmt = con.createStatement()) {
                stmt.executeUpdate(queryLivre, Statement.RETURN_GENERATED_KEYS);

                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        long generatedCode = generatedKeys.getLong(1);

                        // Si c'est un livre policier, ajouter dans la table LivrePolicier
                        if (livre instanceof LivrePolicier) {
                            String queryPolicier = "INSERT INTO LivrePolicier (code, descriptif, nomDetective, nomVictime) VALUES (" + generatedCode + ", '"
                                    + ((LivrePolicier) livre).getDescriptif() + "', '" + ((LivrePolicier) livre).getNomDetective() + "', '"
                                    + ((LivrePolicier) livre).getNomVictime() + "')";
                            stmt.executeUpdate(queryPolicier);
                            System.out.println("l'ajout du livre Policier ! ");
                        }
                        
                        
                        // Si c'est un livre romantique, ajouter dans la table LivreRomantique
                        if (livre instanceof LivreRomantique) {
                            String queryRomantique = "INSERT INTO LivreRomantique (code, descriptif, nom) VALUES (" + generatedCode + ", '"
                                    + ((LivreRomantique) livre).getDescriptif() + "', '"+ ((LivreRomantique) livre).getNom() + "')";
                            stmt.executeUpdate(queryRomantique);
                            System.out.println("l'ajout du livre Romantique ! ");
                        }
                        
                        // Si c'est un livre Science Fiction, ajouter dans la table LivreScienceFiction
                        if (livre instanceof LivreScienceFiction) {
                            String queryScienceFiction = "INSERT INTO LivreScienceFiction (code, annee, espace) VALUES (" + generatedCode + ", "
                                    + ((LivreScienceFiction) livre).getAnnée() + ", '"+ ((LivreScienceFiction) livre).getEspace() + "')";
                            stmt.executeUpdate(queryScienceFiction);
                            System.out.println("l'ajout du livre Science Fiction ! ");
                        }
                        
                        
                    } else {
                        System.out.println("Erreur lors de la récupération de la clé générée.");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Erreur lors de l'ajout du livre.");
        }
    }

   
    // Méthode pour supprimer un livre
    private static void supprimerLivre(Connection con, Livre livre) {
        try {
            // Vérifier si le livre existe dans la base de données
            if (!livreExiste(con, livre)) {
                System.out.println("Le livre n'existe pas dans la base de données.");
                return;
            }

            // Récupérer le code du livre à supprimer
            String codeLivreQuery = "SELECT code FROM Livre WHERE titre = ? AND auteur = ? AND ISBN = ?";
            long codeLivre = -1;

            try (PreparedStatement pstmt = con.prepareStatement(codeLivreQuery)) {
                pstmt.setString(1, livre.getTitre());
                pstmt.setString(2, livre.getAuteur());
                pstmt.setLong(3, livre.getISBN());

                try (ResultSet resultSet = pstmt.executeQuery()) {
                    if (resultSet.next()) {
                        codeLivre = resultSet.getLong("code");
                    }
                }
            }

            if (codeLivre != -1) {
                // Supprimer les entrées liées au livre dans les tables spécifiques
                if (livre instanceof LivrePolicier) {
                    String queryPolicier = "DELETE FROM LivrePolicier WHERE code = " + codeLivre;
                    try (Statement stmt = con.createStatement()) {
                        stmt.executeUpdate(queryPolicier);
                    }
                } else if (livre instanceof LivreRomantique) {
                    String queryRomantique = "DELETE FROM LivreRomantique WHERE code = " + codeLivre;
                    try (Statement stmt = con.createStatement()) {
                        stmt.executeUpdate(queryRomantique);
                    }
                } else if (livre instanceof LivreScienceFiction) {
                    String queryScienceFiction = "DELETE FROM LivreScienceFiction WHERE code = " + codeLivre;
                    try (Statement stmt = con.createStatement()) {
                        stmt.executeUpdate(queryScienceFiction);
                    }
                }

                // Supprimer l'entrée principale dans la table Livre
                String queryLivre = "DELETE FROM Livre WHERE code = " + codeLivre;
                try (Statement stmt = con.createStatement()) {
                    stmt.executeUpdate(queryLivre);
                    System.out.println("Le livre a été supprimé avec succès !");
                }
            } else {
                System.out.println("Erreur lors de la récupération du code du livre.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Erreur lors de la suppression du livre.");
        }
    }


    // Méthode pour rechercher les livres par titre, auteur ou premières lettres du titre

    
    
    
    
    // Méthode pour ajouter abonnement
    private static long ajouterAbonnement(Connection con, Abonnement abonnement) {
        long idAbonnement = -1;

        try {
            String queryAbonnement = "INSERT INTO Abonnement (date_creation, frais) VALUES (?, ?)";
            try (PreparedStatement pstmt = con.prepareStatement(queryAbonnement, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setObject(1, abonnement.getDate_création());
                pstmt.setDouble(2, abonnement.getFrais());

                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            idAbonnement = generatedKeys.getLong(1);
                            System.out.println("L'ajout de l'abonnement a réussi ! ID de l'abonnement : " + idAbonnement);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Erreur lors de l'ajout de l'abonnement.");
        }

        return idAbonnement;
    }
    
    // Méthode pour ajouter un lecteur 
	 private static void ajouterLecteur(Connection con, Lecteur lecteur) {
	    try {
	        // Ajouter l'abonnement du lecteur
	        long idAbonnement = ajouterAbonnement(con, lecteur.getAbonnement());
	
	        // Ajouter le lecteur
	        String queryLecteur = "INSERT INTO Lecteur (CIN, nom, prenom, Abonnement_id, somme_cumule) VALUES (?, ?, ?, ?, ?)";
	        try (PreparedStatement pstmt = con.prepareStatement(queryLecteur, Statement.RETURN_GENERATED_KEYS)) {
	            pstmt.setLong(1, lecteur.getCIN());
	            pstmt.setString(2, lecteur.getNom());
	            pstmt.setString(3, lecteur.getPrenom());
	            pstmt.setLong(4, idAbonnement);
	            pstmt.setDouble(5, lecteur.getSomme_cumle());
	
	            int rowsAffected = pstmt.executeUpdate();
	            if (rowsAffected > 0) {
	                System.out.println("L'ajout du lecteur avec abonnement a réussi !");
	
	                // Si le lecteur est fidèle, ajouter à la table LecteurFidèle
	                if (lecteur instanceof LecteurFidèle) {
	                    LecteurFidèle lecteurFidele = (LecteurFidèle) lecteur;
	                    String queryFidele = "INSERT INTO LecteurFidele (CIN, adresse_email, preference) VALUES (?, ?, ?)";
	                    try (PreparedStatement pstmtFidele = con.prepareStatement(queryFidele)) {
	                        pstmtFidele.setLong(1, lecteurFidele.getCIN());
	                        pstmtFidele.setString(2, lecteurFidele.getAdresse_email());
	                        pstmtFidele.setString(3, lecteurFidele.getPréférence());
	
	                        int rowsAffectedFidele = pstmtFidele.executeUpdate();
	                        if (rowsAffectedFidele > 0) {
	                            System.out.println("L'ajout du lecteur fidèle a réussi !");
	                        }
	                    }
	                }
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	        System.out.println("Erreur lors de l'ajout du lecteur avec abonnement.");
	    }
	}

	// Méthode pour supprimer un lecteur
	 private static void supprimerLecteur(Connection con, Lecteur lecteur) {
		    try {
		        // Vérifier si le lecteur existe dans la base de données
		        if (!lecteurExiste(con, lecteur)) {
		            System.out.println("Le lecteur n'existe pas dans la base de données.");
		            return;
		        }

		        // Récupérer l'ID de l'abonnement associé au lecteur
		        int idAbonnement = -1;
		        String queryAbonnementId = "SELECT Abonnement_id FROM Lecteur WHERE CIN = ?";
		        try (PreparedStatement pstmtAbonnementId = con.prepareStatement(queryAbonnementId)) {
		            pstmtAbonnementId.setLong(1, lecteur.getCIN());
		            try (ResultSet resultSet = pstmtAbonnementId.executeQuery()) {
		                if (resultSet.next()) {
		                    idAbonnement = resultSet.getInt("Abonnement_id");
		                }
		            }
		        }

		        // Supprimer l'entrée dans la table Lecteur
		        String queryLecteur = "DELETE FROM Lecteur WHERE CIN = ?";
		        try (PreparedStatement pstmtLecteur = con.prepareStatement(queryLecteur)) {
		            pstmtLecteur.setLong(1, lecteur.getCIN());
		            pstmtLecteur.executeUpdate();
		            System.out.println("Le lecteur a été supprimé avec succès !");
		        }

		        // Si le lecteur est fidèle, supprimer l'entrée dans la table LecteurFidele
		        if (lecteur instanceof LecteurFidèle) {
		            String queryFidele = "DELETE FROM LecteurFidele WHERE CIN = ?";
		            try (PreparedStatement pstmtFidele = con.prepareStatement(queryFidele)) {
		                pstmtFidele.setLong(1, lecteur.getCIN());
		                pstmtFidele.executeUpdate();
		            }
		        }

		        // Si l'ID de l'abonnement a été récupéré avec succès
		        if (idAbonnement != -1) {
		            // Supprimer l'abonnement associé à partir de son ID dans la table Abonnement
		            String queryAbonnement = "DELETE FROM Abonnement WHERE id = ?";
		            try (PreparedStatement pstmtAbonnement = con.prepareStatement(queryAbonnement)) {
		                pstmtAbonnement.setInt(1, idAbonnement);
		                pstmtAbonnement.executeUpdate();
		                System.out.println("L'abonnement du lecteur a été supprimé avec succès !");
		            }
		        } else {
		            System.out.println("Erreur lors de la récupération de l'ID de l'abonnement.");
		        }

		    } catch (SQLException e) {
		        e.printStackTrace();
		        System.out.println("Erreur lors de la suppression du lecteur.");
		    }
		}


	
	// Méthode pour vérifier si un lecteur existe déjà
	 private static boolean lecteurExiste(Connection con, Lecteur lecteur) throws SQLException {
	     String query = "SELECT COUNT(*) FROM Lecteur WHERE CIN = ?";
	     try (PreparedStatement preparedStatement = con.prepareStatement(query)) {
	         preparedStatement.setLong(1, lecteur.getCIN());

	         try (ResultSet resultSet = preparedStatement.executeQuery()) {
	             if (resultSet.next()) {
	                 int count = resultSet.getInt(1);
	                 return count > 0;
	             }
	         }
	     }
	     return false;
	 }


    
    
   
	public static void main(String[] args) {
		try {
			Connection con = DriverManager.getConnection(url, username, password); 

	        System.out.println("Connected!");
	        //ajouterLivre(con, new Livre("Stupeur et tremblement", "Nathalie Nothomb",12578945677L));
	        //ajouterLivre(con, new Livre("Crime et Chatiment", "Fyodor Dostoevsky",1234567891230L));
	        //ajouterLivre(con ,new LivrePolicier("meurtre","Hercule Poirot ",7412589637418L,"Le crime de l'orient express", "Agatha Christie","ines"));
	        //ajouterLivre(con , new LivreRomantique("Emma", "Jane Austen",1487956412347L,"longue histoire","Emma"));
	        //ajouterLivre(con ,new LivreScienceFiction("La planete des singes", "Pierre Boulle",2589631478520L,2500,"Plan�te Soror"));
	        //supprimerLivre(con , new Livre("Crime et Chatiment", "Fyodor Dostoevsky",1234567891230L));
	        //supprimerLivre(con , new LivrePolicier("meurtre","Hercule Poirot ",7412589637418L,"Le crime de l'orient express", "Agatha Christie","ines"));
	        
	        Abonnement a4 = new Abonnement(LocalDate.of(2022, 12, 1),10);
	        
	        Lecteur lecteur3 = new LecteurFidèle(264567899,"Selim","Ben Aissa",a4,20,"ii@gmail.com","Romantique");
	        
	       // ajouterLecteur(con, lecteur3);
	        
	        /*supprimerLecteur(con,new Lecteur(782456789 ,"Ines","Slim"));
	        
	        supprimerLecteur(con,new Lecteur(254566899,"Imen","Massoudi"));*/
	        supprimerLecteur(con,lecteur3);
	        
	    } catch (SQLException e) {
	        e.printStackTrace();
	        System.out.println("Erreur lors de la connexion à la base de données.");
	   }

		
	}
}
