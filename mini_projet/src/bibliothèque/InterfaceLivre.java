package bibliothèque;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class InterfaceLivre extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String BDD = "Bibliotheque";
	private static final String url = "jdbc:mysql://localhost:3306/" + BDD;
	private static final String username = "root";
	private static final String password = "";
	

	private static JTable tableLivres;
	
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
                System.out.println("l'ajout du livre ! ");

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
        }  catch (SQLException  e) {
            e.printStackTrace();
    
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
           
        }
    }

	 // Méthode pour afficher tous les livres de la table Livre
    private static void afficherLivres(Connection con) {
        String query = "SELECT * FROM Livre";
        try (Statement stmt = con.createStatement();
             ResultSet resultSet = stmt.executeQuery(query)) {

            DefaultTableModel tableModel = new DefaultTableModel();
            tableModel.setColumnIdentifiers(new String[]{"ISBN", "Titre", "Auteur"});
            while (resultSet.next()) {
                long isbn = resultSet.getLong("ISBN");
                String titre = resultSet.getString("titre");
                String auteur = resultSet.getString("auteur");

                Object[] row = {isbn, titre, auteur};
                tableModel.addRow(row);
            }

            tableLivres.setModel(tableModel);

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Erreur lors de la récupération des livres.");
        }
    }


    // Méthode pour rechercher les livres par titre, auteur ou premières lettres du titre
    private static void rechercherLivre(Connection con, String titre, String auteur, String isbnText) {
        try {
            // Construire la requête de recherche en fonction des paramètres fournis
            StringBuilder queryBuilder = new StringBuilder("SELECT * FROM Livre WHERE");
            if (titre != null && !titre.isEmpty()) {
                queryBuilder.append(" titre LIKE '%").append(titre).append("%' AND");
            }
            if (auteur != null && !auteur.isEmpty()) {
                queryBuilder.append(" auteur LIKE '%").append(auteur).append("%' AND");
            }
            if (isbnText != null && !isbnText.isEmpty()) {
                long isbn = Long.parseLong(isbnText);
                queryBuilder.append(" ISBN = ").append(isbn).append(" AND");
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
                tableModel.setColumnIdentifiers(new String[]{"ISBN", "Titre", "Auteur"});
                while (resultSet.next()) {
                    long isbn = resultSet.getLong("ISBN");
                    String titreResult = resultSet.getString("titre");
                    String auteurResult = resultSet.getString("auteur");

                    Object[] row = {isbn, titreResult, auteurResult};
                    tableModel.addRow(row);
                }

                // Assurez-vous que tableLivres est correctement initialisée et associée à votre interface utilisateur
                tableLivres.setModel(tableModel);

            }
        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Champs vide ! Veuillez saisir le livre à chercher .", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
    

    private JTextField titreField, auteurField, isbnField, descriptifField, nomDetectiveField, nomVictimeField, nomField, anneeField, espaceField;
    
    public InterfaceLivre() {
        setTitle("Gestion des Livres");
        setSize(800, 600);
 
        // Utilisation de GridBagLayout pour le panneau principal
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); 

        JPanel formulairePanel = new JPanel();
        formulairePanel.setLayout(new GridLayout(10, 2));

        // Ajout des champs au formulaire
        JLabel titleLabel = new JLabel("Titre* :");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 15));  
        formulairePanel.add(titleLabel);
        titreField = new JTextField();
        titreField.setColumns(20);
        titreField.setPreferredSize(new Dimension(150, 25));
        formulairePanel.add(titreField);

        JLabel auteurLabel = new JLabel("Auteur* :");
        auteurLabel.setFont(new Font("Arial", Font.BOLD, 15));  
        formulairePanel.add(auteurLabel);
        auteurField = new JTextField();
        auteurField.setColumns(20);
        auteurField.setPreferredSize(new Dimension(150, 25));
        formulairePanel.add(auteurField);
        
        
        JLabel isbnLabel = new JLabel("ISBN* :");
        isbnLabel.setFont(new Font("Arial", Font.BOLD, 15));  
        formulairePanel.add(isbnLabel);
        isbnField = new JTextField();
        isbnField.setColumns(20);
        isbnField.setPreferredSize(new Dimension(150, 25));
        formulairePanel.add(isbnField);

        
        
        JLabel descriptifLabel = new JLabel("Descriptif :");
        descriptifLabel.setFont(new Font("Arial", Font.BOLD, 15));  
        formulairePanel.add(descriptifLabel);
        descriptifField = new JTextField();
        descriptifField.setColumns(20);
        isbnField.setPreferredSize(new Dimension(150, 25));
        formulairePanel.add(descriptifField);
        
        
        JLabel nomLabel = new JLabel("Nom :");
        nomLabel.setFont(new Font("Arial", Font.BOLD, 15));  
        formulairePanel.add(nomLabel);
        nomField = new JTextField();
        nomField.setColumns(20);
        nomField.setPreferredSize(new Dimension(150, 25));
        formulairePanel.add(nomField);

        
        JLabel nomDetectiveLabel = new JLabel("Nom du détective :");
        nomDetectiveLabel.setFont(new Font("Arial", Font.BOLD, 15));  
        formulairePanel.add(nomDetectiveLabel);
        nomDetectiveField = new JTextField();
        nomDetectiveField.setColumns(20);
        nomDetectiveField.setPreferredSize(new Dimension(150, 25));
        formulairePanel.add(nomDetectiveField);

        
        JLabel nomVictimeLabel = new JLabel("Nom de la victime :");
        nomVictimeLabel.setFont(new Font("Arial", Font.BOLD, 15));  
        formulairePanel.add(nomVictimeLabel);
        nomVictimeField = new JTextField();
        nomVictimeField.setColumns(20);
        nomVictimeField.setPreferredSize(new Dimension(150, 25));
        formulairePanel.add(nomVictimeField);


        JLabel anneeLabel = new JLabel("Année :");
        anneeLabel.setFont(new Font("Arial", Font.BOLD, 15));  
        formulairePanel.add(anneeLabel);
        anneeField = new JTextField();
        anneeField.setColumns(20);
        anneeField.setPreferredSize(new Dimension(150, 25));
        formulairePanel.add(anneeField);

        
        JLabel espaceLabel = new JLabel("Espace :");
        espaceLabel.setFont(new Font("Arial", Font.BOLD, 15));  
        formulairePanel.add(espaceLabel);
        espaceField = new JTextField();
        espaceField.setColumns(20);
        espaceField.setPreferredSize(new Dimension(150, 25));
        formulairePanel.add(espaceField);

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
            	 // Récupérer les valeurs des champs de texte
                String titre = titreField.getText();
                String auteur = auteurField.getText();
                long isbn = Long.parseLong(isbnField.getText()); // Vous pouvez gérer les erreurs de conversion
                String descriptif = descriptifField.getText();
                String nomDetective = nomDetectiveField.getText();
                String nomVictime = nomVictimeField.getText();
                String nom = nomField.getText();
                String anneeText = anneeField.getText();
                String espace = espaceField.getText();

                // Créer un objet Livre avec les valeurs récupérées
                Livre livre;
                if (!descriptif.isEmpty() && !nomDetective.isEmpty() && !nomVictime.isEmpty()) {
                    livre = new LivrePolicier( titre, auteur, isbn,descriptif, nomDetective, nomVictime);
                } else if (!descriptif.isEmpty() && !nom.isEmpty()) {
                    livre = new LivreRomantique(titre, auteur,  isbn,descriptif, nom);
                } else if (!anneeText.isEmpty() && !espace.isEmpty()) {
                    livre = new LivreScienceFiction(titre, auteur,isbn,Integer.parseInt(anneeText),espace );
                } else {
                    livre = new Livre(titre, auteur,isbn);
                }

                // Appeler la méthode pour ajouter le livre
                try {
                    Connection con = DriverManager.getConnection(url, username, password);
                    System.out.println("Connected!");
                    ajouterLivre(con, livre);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(InterfaceLivre.this, "Erreur lors de l'ajout du livre.", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        supprimerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	 // Récupérer les valeurs des champs de texte
                String titre = titreField.getText();
                String auteur = auteurField.getText();
                String isbnText = isbnField.getText(); // Vous pouvez gérer les erreurs de conversion
                String descriptif = descriptifField.getText();
                String nomDetective = nomDetectiveField.getText();
                String nomVictime = nomVictimeField.getText();
                String nom = nomField.getText();
                String anneeText = anneeField.getText();
                String espace = espaceField.getText();

                // Créer un objet Livre avec les valeurs récupérées
                Livre livre;
                if (!descriptif.isEmpty() && !nomDetective.isEmpty() && !nomVictime.isEmpty()) {
                    livre = new LivrePolicier(descriptif, nomDetective, Long.parseLong(isbnText), titre, auteur, nomVictime);
                } else if (!descriptif.isEmpty() && !nom.isEmpty()) {
                    livre = new LivreRomantique(titre, auteur,  Long.parseLong(isbnText),descriptif, nom);
                } else if (!anneeText.isEmpty() && !espace.isEmpty()) {
                    livre = new LivreScienceFiction(titre, auteur,Long.parseLong(isbnText),Integer.parseInt(anneeText),espace );
                } else {
                    livre = new Livre(titre, auteur,Long.parseLong(isbnText));
                }

                // Appeler la méthode pour ajouter le livre
                try {
                    Connection con = DriverManager.getConnection(url, username, password);
                    System.out.println("Connected!");
                    supprimerLivre(con, livre);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(InterfaceLivre.this, "Erreur lors de la suppression du livre.", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
              
            }
        });

        rechercherButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String titreRecherche = titreField.getText();
                String auteurRecherche = auteurField.getText();
                String isbnRecherche = isbnField.getText();

                try {
                    Connection con = DriverManager.getConnection(url, username, password);
                    System.out.println("Connected!");
                    rechercherLivre(con, titreRecherche, auteurRecherche, isbnRecherche);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(InterfaceLivre.this, "Erreur lors de la recherche du livre.", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        afficherButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	   try {
                       Connection con = DriverManager.getConnection(url, username, password);
                       System.out.println("Connected!");
                       afficherLivres(con);
                   } catch (SQLException ex) {
                       ex.printStackTrace();
                       JOptionPane.showMessageDialog(InterfaceLivre.this, "Erreur lors de l'affichage des livres.", "Erreur", JOptionPane.ERROR_MESSAGE);
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
        formulairePanel.setBackground(new Color(175, 216, 245));

        // Ajout des boutonsPanel au mainPanel avec des contraintes pour centrer
        gbc.gridy = -1;
        mainPanel.add(boutonsPanel, gbc);
        mainPanel.setBackground(new Color(175, 216, 245));
        boutonsPanel.setBackground(new Color(175, 216, 245));

    	tableLivres = new JTable();
    	tableLivres.setRowHeight(30); 
    	JScrollPane sp=new JScrollPane(tableLivres); 
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
                new InterfaceLivre().setVisible(true);
            }
        });
    }
}
