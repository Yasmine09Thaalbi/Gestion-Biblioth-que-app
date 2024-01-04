package bibliothèque;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;


public class InterfaceLecteur extends JFrame{
		/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
		private static final String BDD = "Bibliotheque";
		private static final String url = "jdbc:mysql://localhost:3306/" + BDD;
		private static final String username = "root";
		private static final String password = "";
		private JTextField cinField, nomField, prenomField, sommeField, adresse_emailField, préférenceField ,dateCreationAbonnementField,fraisAbonnementField;

		private static JTable tableLecteurs;
	  
	  
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
		        String queryLecteur = "INSERT INTO Lecteur (CIN, prenom, nom, Abonnement_id, somme_cumule) VALUES (?, ?, ?, ?, ?)";
		        try (PreparedStatement pstmt = con.prepareStatement(queryLecteur, Statement.RETURN_GENERATED_KEYS)) {
		            pstmt.setLong(1, lecteur.getCIN());
		            pstmt.setString(2, lecteur.getPrenom());
		            pstmt.setString(3, lecteur.getNom());
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
		 
		 
		 // Méthode pour rechercher les lecteurs par cin, nom ou prenom
		 private static void rechercherLecteur(Connection con, String nom, String prenom, String cinText) {
			    try {
			        // Construire la requête de recherche en fonction des paramètres fournis
			        StringBuilder queryBuilder = new StringBuilder("SELECT * FROM Lecteur WHERE");
			        if (nom != null && !nom.isEmpty()) {
			            queryBuilder.append(" nom LIKE '%").append(nom).append("%' AND");
			        }
			        if (prenom != null && !prenom.isEmpty()) {
			            queryBuilder.append(" prenom LIKE '%").append(prenom).append("%' AND");
			        }
			        if (cinText != null && !cinText.isEmpty()) {
			            long cin = Long.parseLong(cinText);
			            queryBuilder.append(" CIN = ").append(cin).append(" AND");
			        }

			        // Supprimer le "AND" en trop à la fin de la requête
			        int lastIndex = queryBuilder.lastIndexOf("AND");
			        if (lastIndex != -1) {
			            queryBuilder.delete(lastIndex, queryBuilder.length());
			        }

			        // Exécuter la requête
			        try (Statement stmt = con.createStatement();
			             ResultSet resultSet = stmt.executeQuery(queryBuilder.toString())) {

			            DefaultTableModel tableModel = new DefaultTableModel();
			            tableModel.setColumnIdentifiers(new String[]{"CIN", "Nom", "Prenom"});
			            while (resultSet.next()) {
			                long cin = resultSet.getLong("CIN");
			                String nomResult = resultSet.getString("nom");
			                String prenomResult = resultSet.getString("prenom");

			                Object[] row = {cin, nomResult, prenomResult};
			                tableModel.addRow(row);
			            }

			            // Assurez-vous que tableLecteurs est correctement initialisée et associée à votre interface utilisateur
			            tableLecteurs.setModel(tableModel);

			        }
			    } catch (SQLException | NumberFormatException e) {
			        e.printStackTrace();
			        JOptionPane.showMessageDialog(null, "Champs vide ! Veuillez saisir le lecteur à chercher.", "Erreur", JOptionPane.ERROR_MESSAGE);
			    }
			}

		// Méthode pour affichager les lecteurs
		 private static void afficherLecteurs(Connection con) {
			    String query = "SELECT * FROM Lecteur";
			    try (Statement stmt = con.createStatement();
			         ResultSet resultSet = stmt.executeQuery(query)) {

			        DefaultTableModel tableModel = new DefaultTableModel();
			        tableModel.setColumnIdentifiers(new String[]{"CIN","Prenom", "Nom"});
			        while (resultSet.next()) {
			            long cin = resultSet.getLong("CIN");
			            String nom = resultSet.getString("prenom");
			            String prenom = resultSet.getString("nom");

			            Object[] row = {cin, nom, prenom};
			            tableModel.addRow(row);
			        }

			        // Assurez-vous que tableLecteurs est correctement initialisée et associée à votre interface utilisateur
			        tableLecteurs.setModel(tableModel);

			    } catch (SQLException e) {
			        e.printStackTrace();
			        System.out.println("Erreur lors de la récupération des lecteurs.");
			    }
			}

	    public InterfaceLecteur() {
	        setTitle("Gestion des Lecteurs");
	        setSize(800, 600);

	        // Utilisation de GridBagLayout pour le panneau principal
	        JPanel mainPanel = new JPanel(new GridBagLayout());
	        GridBagConstraints gbc = new GridBagConstraints();
	        gbc.insets = new Insets(10, 10, 10, 10); // Padding de 10 pixels

	        JPanel formulairePanel = new JPanel();
	        formulairePanel.setLayout(new GridLayout(10, 2));

	        // Ajout des champs au formulaire
	        JLabel cinLabel = new JLabel("CIN* :");
	        cinLabel.setFont(new Font("Arial", Font.BOLD, 15)); 
	        formulairePanel.add(cinLabel);
	        cinField = new JTextField();
	        cinField.setColumns(20);
	        cinField.setPreferredSize(new Dimension(150, 25));
	        formulairePanel.add(cinField);
	        
	        JLabel prenomLabel = new JLabel("Prenom* :");
	        prenomLabel.setFont(new Font("Arial", Font.BOLD, 15)); 
	        formulairePanel.add(prenomLabel);
	        prenomField = new JTextField();
	        prenomField.setColumns(20);
	        prenomField.setPreferredSize(new Dimension(150, 25));
	        formulairePanel.add(prenomField);

	        JLabel nomLabel = new JLabel("Nom* :");
	        nomLabel.setFont(new Font("Arial", Font.BOLD, 15)); 
	        formulairePanel.add(nomLabel);
	        nomField = new JTextField();
	        nomField.setColumns(20);
	        nomField.setPreferredSize(new Dimension(150, 25));
	        formulairePanel.add(nomField);


	        JLabel sommeLabel = new JLabel("Somme cumulé :");
	        sommeLabel.setFont(new Font("Arial", Font.BOLD, 15)); 
	        formulairePanel.add(sommeLabel);
	        sommeField = new JTextField();
	        sommeField.setColumns(20);
	        sommeField.setPreferredSize(new Dimension(150, 25));
	        formulairePanel.add(sommeField);

	        JLabel adresseLabel = new JLabel("Adresse Email :");
	        adresseLabel.setFont(new Font("Arial", Font.BOLD, 15)); 
	        formulairePanel.add(adresseLabel);
	        adresse_emailField = new JTextField();
	        adresse_emailField.setColumns(20);
	        adresse_emailField.setPreferredSize(new Dimension(150, 25));
	        formulairePanel.add(adresse_emailField);

	        JLabel preferenceLabel = new JLabel("Préférence :");
	        preferenceLabel.setFont(new Font("Arial", Font.BOLD, 15)); 
	        formulairePanel.add(preferenceLabel);
	        préférenceField = new JTextField();
	        préférenceField.setColumns(20);
	        préférenceField.setPreferredSize(new Dimension(150, 25));
	        formulairePanel.add(préférenceField);
	        
	        JLabel dateLabel = new JLabel("Date de création de l'abonnement :");
	        dateLabel.setFont(new Font("Arial", Font.BOLD, 15)); 
	        formulairePanel.add(dateLabel);
	        dateCreationAbonnementField = new JTextField();
	        dateCreationAbonnementField.setColumns(20);
	        dateCreationAbonnementField.setPreferredSize(new Dimension(150, 25));
	        formulairePanel.add(dateCreationAbonnementField);

	        JLabel fraisLabel = new JLabel("Frais de l'abonnement :");
	        fraisLabel.setFont(new Font("Arial", Font.BOLD, 15)); 
	        formulairePanel.add(fraisLabel);
	        fraisAbonnementField = new JTextField();
	        fraisAbonnementField.setColumns(20);
	        fraisAbonnementField.setPreferredSize(new Dimension(150, 25));
	        formulairePanel.add(fraisAbonnementField);


	        // Ajout des boutons
	        JButton ajouterButton = new JButton("Ajouter");
	        ajouterButton.setPreferredSize(new Dimension(80, 30));
	        JButton supprimerButton = new JButton("Supprimer");
	        JButton rechercherButton = new JButton("Rechercher");
	        JButton afficherButton = new JButton("Afficher");

	        // Ajout des actions aux boutons
	        ajouterButton.addActionListener(new ActionListener() {
	            @Override
	            public void actionPerformed(ActionEvent e) {
	                try {
	                    long cin = Long.parseLong(cinField.getText()); 
	                    String nom = nomField.getText();
	                    String prenom = prenomField.getText();
	                    double sommeCumule = Double.parseDouble(sommeField.getText()); 
	                    String adresseEmail = adresse_emailField.getText();
	                    String préférence = préférenceField.getText();
	                    String dateCreationAbonnement = dateCreationAbonnementField.getText(); 
	                    double fraisAbonnement = Double.parseDouble(fraisAbonnementField.getText()); 
 
	                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/d");

	                    Abonnement abonnement = new Abonnement(LocalDate.parse(dateCreationAbonnement,formatter), fraisAbonnement);

	                    Lecteur lecteur;
	                    if (!adresseEmail.isEmpty() && !préférence.isEmpty()) {
	                        lecteur = new LecteurFidèle(cin, prenom, nom, abonnement, sommeCumule, adresseEmail, préférence);
	                    } else {
	                        lecteur = new Lecteur(cin, prenom, nom, abonnement,sommeCumule);
	                    }

	                    Connection con = DriverManager.getConnection(url, username, password);
	                    System.out.println("Connected!");
	                    ajouterLecteur(con, lecteur);
	                } catch (SQLException | NumberFormatException ex) {
	                    ex.printStackTrace();
	                    JOptionPane.showMessageDialog(null, "Erreur lors de l'ajout du lecteur. Champs vide ! ", "Erreur", JOptionPane.ERROR_MESSAGE);
	                }

	            	
	            	
	               
	            }
	        });

	        
	        supprimerButton.addActionListener(new ActionListener() {
	            @Override
	            public void actionPerformed(ActionEvent e) {
	            	  try {
	                      long cin = Long.parseLong(cinField.getText());
	                      Lecteur lecteur = new Lecteur(cin);

	                      Connection con = DriverManager.getConnection(url, username, password);
	                      System.out.println("Connected!");
	                      supprimerLecteur(con, lecteur);
	                  } catch (SQLException | NumberFormatException ex) {
	                      ex.printStackTrace();
	                      JOptionPane.showMessageDialog(null, "Erreur lors de la suppression du lecteur. Veuillez saisir le CIN du lecteur à supprimer.", "Erreur", JOptionPane.ERROR_MESSAGE);
	                  }
	                
	            }
	        });

	        rechercherButton.addActionListener(new ActionListener() {
	            @Override
	            public void actionPerformed(ActionEvent e) {
	            	 try {
	                      String nom = nomField.getText();
	                      String prenom = prenomField.getText();
	                      String cinText = cinField.getText();

	                      Connection con = DriverManager.getConnection(url, username, password);
	                      rechercherLecteur(con, nom, prenom, cinText);
	                  } catch (SQLException ex) {
	                      ex.printStackTrace();
	                      JOptionPane.showMessageDialog(null, "Erreur lors de la recherche du lecteur.", "Erreur", JOptionPane.ERROR_MESSAGE);
	                  }
	            }
	        });
	        
	        afficherButton.addActionListener(new ActionListener() {
	            @Override
	            public void actionPerformed(ActionEvent e) {
	            	try {
	                    Connection con = DriverManager.getConnection(url, username, password);
	                    
	                    afficherLecteurs(con);
	                    con.close();
	                } catch (SQLException ex) {
	                    ex.printStackTrace();
	                    JOptionPane.showMessageDialog(null, "Erreur lors de la récupération des lecteurs.", "Erreur", JOptionPane.ERROR_MESSAGE);
	                }
	            }
	        });

	        
	        JPanel boutonsPanel = new JPanel(new GridLayout(1, 4));
	        boutonsPanel.add(ajouterButton);
	        boutonsPanel.add(supprimerButton);
	        boutonsPanel.add(rechercherButton);
	        boutonsPanel.add(afficherButton);

	        // Ajout du formulairePanel au mainPanel avec des contraintes pour centrer
	        gbc.gridx = 0;
	        gbc.gridy = 0;
	        mainPanel.add(formulairePanel, gbc);

	        // Ajout des boutonsPanel au mainPanel avec des contraintes pour centrer
	        gbc.gridy = -1;
	        mainPanel.add(boutonsPanel, gbc);
	        formulairePanel.setBackground(new Color(175, 216, 245));
	        mainPanel.setBackground(new Color(175, 216, 245));
	        
	        tableLecteurs = new JTable();
	        tableLecteurs.setRowHeight(30); 
	    	JScrollPane sp=new JScrollPane(tableLecteurs);  
	    	sp.setBackground(new Color(204, 229, 255));
	    	sp.getViewport().setBackground(new Color(175, 216, 245));
	    	sp.setBorder(BorderFactory.createEmptyBorder());
	    	sp.setViewportBorder(null);
	    	
	        gbc.gridy = -2;
	        mainPanel.add(sp, gbc);
	        
	        // Add an EmptyBorder to mainPanel
	        mainPanel.setBorder(BorderFactory.createEmptyBorder(80, 0, 0, 0));

	        // Add mainPanel to the NORTH position
	        getContentPane().add(BorderLayout.NORTH, mainPanel);
	        // Centrez la fenêtre sur l'écran
	        setLocationRelativeTo(null);
	        
	    }

	    public static void main(String[] args) {
	        SwingUtilities.invokeLater(new Runnable() {
	            public void run() {
	                new InterfaceLecteur().setVisible(true);
	            }
	        });
	    }

}
