package bibliothèque;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class InterfaceEmpruntRetour extends JFrame {
	private static final String BDD = "Bibliotheque";
	private static final String url = "jdbc:mysql://localhost:3306/" + BDD;
	private static final String username = "root";
	private static final String password = "";

    private JTextField livreField, lecteurField;
    private static Map<String, Livre> livresEmpruntes = new HashMap<>();
    
    // Méthode pour emprunter un livre
    private static void emprunterLivre(Connection con, String titreLivre, long cinLecteur) {
        try {
            // Étape 1 : Vérifier si l'abonnement est expiré
            LocalDate dateAbonnement = getDateAbonnement(con, cinLecteur);
            if (dateAbonnement != null && dateAbonnement.plusYears(1).isBefore(LocalDate.now())) {
                JOptionPane.showMessageDialog(null, "L'abonnement est expiré. Renouvelez l'abonnement.", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Étape 2 : Récupérer le code du livre
            String livreCode = getLivreCodeByTitre(con, titreLivre);
            if (livreCode == null) {
                JOptionPane.showMessageDialog(null, "Aucun livre trouvé avec le titre spécifié.", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Étape 3 : Vérifier si le lecteur a déjà emprunté ce livre et doit le retourner
            boolean livreDejaEmprunte = isLivreAlreadyEmprunte(con, cinLecteur);
            if (livreDejaEmprunte) {
                JOptionPane.showMessageDialog(null, "Vous avez déjà emprunté un livre. Veuillez le retourner avant d'en emprunter un autre.", "Alerte", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Étape 4 : Effectuer l'emprunt
            boolean empruntReussi = doEmprunt(con, cinLecteur, titreLivre);
            if (empruntReussi) {
                JOptionPane.showMessageDialog(null, "L'emprunt a été effectué avec succès.", "Succès", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Erreur lors de l'emprunt. Veuillez réessayer.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erreur lors de l'emprunt du livre.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // Récupérer la date de création de l'abonnement du lecteur
    private static LocalDate getDateAbonnement(Connection con, long cinLecteur) throws SQLException {
        String query = "SELECT date_creation FROM Abonnement INNER JOIN Lecteur ON Abonnement.id = Lecteur.Abonnement_id WHERE Lecteur.CIN = ?";
        try (PreparedStatement preparedStatement = con.prepareStatement(query)) {
            preparedStatement.setLong(1, cinLecteur);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getDate("date_creation").toLocalDate();
                }
            }
        }
        return null;
    }

    // Vérifier si le lecteur a déjà emprunté ce livre
    private static boolean isLivreAlreadyEmprunte(Connection con, long cinLecteur) throws SQLException {
        try {
            long abonnementId = getAbonnementId(con, cinLecteur);

            // Effectuer la vérification
            String query = "SELECT EXISTS (SELECT 1 FROM detailemprunt " +
                    "WHERE abonnement_id = ?  AND dateRetour < CURRENT_DATE)";
            try (PreparedStatement preparedStatement = con.prepareStatement(query)) {
                preparedStatement.setLong(1, abonnementId);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getBoolean(1);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erreur lors de la vérification de l'emprunt du livre.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }

    //Get le code du livre 
    private static String getLivreCodeByTitre(Connection con, String titreLivre) throws SQLException {
        String query = "SELECT code FROM Livre WHERE titre = ?";
        try (PreparedStatement preparedStatement = con.prepareStatement(query)) {
            preparedStatement.setString(1, titreLivre);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("code");
                }
            }
        }
        return null;
    }
    
    // Méthode pour récupérer un livre par son code
    private static Livre getLivreByCode(Connection con, String livreCode) throws SQLException {
        // Table générique pour rechercher le livre
        String[] tables = {"livre", "livreromantique", "livrepolicier", "livresciencefiction"};

        for (String table : tables) {
            String query = "SELECT * FROM " + table + " WHERE code = ?";
            try (PreparedStatement preparedStatement = con.prepareStatement(query)) {
                preparedStatement.setString(1, livreCode);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        Livre livre;

                        // Créez une instance appropriée de Livre ou de sa sous-classe
                        if (table.equals("livreromantique")) {
                            livre = new LivreRomantique();
                        } else if (table.equals("livrepolicier")) {
                            livre = new LivrePolicier();
                        } else if (table.equals("livresciencefiction")) {
                            livre = new LivreScienceFiction();
                        } else {
                            livre = new Livre();
                        }

                        // Set des attributs communs à toutes les tables
                        livre.setCode(resultSet.getLong("code"));
                        livre.setTitre(resultSet.getString("titre"));
                        livre.setAuteur(resultSet.getString("auteur"));
                        livre.setISBN(resultSet.getLong("ISBN"));

                        // Set des attributs spécifiques à chaque sous-classe
                        if (livre instanceof LivreRomantique) {
                            ((LivreRomantique) livre).setDescriptif(resultSet.getString("descriptif"));
                            ((LivreRomantique) livre).setNom(resultSet.getString("nom"));
                        } else if (livre instanceof LivrePolicier) {
                            ((LivrePolicier) livre).setDescriptif(resultSet.getString("descriptif"));
                            ((LivrePolicier) livre).setNomDetective(resultSet.getString("nomDetective"));
                            ((LivrePolicier) livre).setNomVictime(resultSet.getString("nomVictime"));
                        } else if (livre instanceof LivreScienceFiction) {
                            ((LivreScienceFiction) livre).setAnnée(resultSet.getInt("annee"));
                            ((LivreScienceFiction) livre).setEspace(resultSet.getString("espace"));
                        }

                        return livre;
                    }
                }
            }
        }

        return null;
    }


    //Effectuer l'emprut du livre 
    private static boolean doEmprunt(Connection con, long cinLecteur, String titreLivre) throws SQLException {
        try {
            // Récupérer les valeurs nécessaires des sous-requêtes
            long abonnementId = getAbonnementId(con, cinLecteur);
            String livreCode = getLivreCodeByTitre(con, titreLivre);

            // Effectuer l'insertion dans la table detailemprunt
            String queryEmprunt = "INSERT INTO detailemprunt (abonnement_id, livre_code, dateEmprunt, dateRetour) VALUES (?, ?, CURRENT_DATE, DATE_ADD(CURRENT_DATE, INTERVAL 7 DAY))";
            try (PreparedStatement preparedStatementEmprunt = con.prepareStatement(queryEmprunt)) {
                preparedStatementEmprunt.setLong(1, abonnementId);
                preparedStatementEmprunt.setString(2, livreCode);

                int rowsAffectedEmprunt = preparedStatementEmprunt.executeUpdate();
                

                // Supprimer le livre de la table Livre après l'emprunt
                if (rowsAffectedEmprunt > 0) {
                	
                	Livre livre = getLivreByCode(con, livreCode);
                    livresEmpruntes.put(livreCode, livre);
                    System.out.println(livresEmpruntes);
                    // Utiliser la méthode générique pour supprimer le livre de la table correspondante
                    supprimerLivreGenre(con, "livre", livreCode);
                    supprimerLivreGenre(con, "livrepolicier", livreCode);
                    supprimerLivreGenre(con, "livreromantique", livreCode);
                    supprimerLivreGenre(con, "livresciencefiction", livreCode);
                }
                return rowsAffectedEmprunt > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Méthode générique pour supprimer un livre de n'importe quelle table en fonction du genre
    private static void supprimerLivreGenre(Connection con, String tableName, String livreCode) throws SQLException {
        String query = "DELETE FROM " + tableName + " WHERE code = ?";
        try (PreparedStatement preparedStatement = con.prepareStatement(query)) {
            preparedStatement.setString(1, livreCode);
            preparedStatement.executeUpdate();
        }
    }

    // Récupérer l'ID de l'abonnement
    private static long getAbonnementId(Connection con, long cinLecteur) throws SQLException {
        String query = "SELECT Abonnement_id FROM Lecteur WHERE CIN = ?";
        try (PreparedStatement preparedStatement = con.prepareStatement(query)) {
            preparedStatement.setLong(1, cinLecteur);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getLong("Abonnement_id");
                }
            }
        }
        return -1;
    }
    
    //Methode pour retourner un livre 
    private static void retournerLivre(Connection con, String titreLivre) {
        try {
            // Rechercher le livre par son titre dans la liste livresEmpruntes
            Livre livre = getLivreByTitreFromMap(titreLivre);

            if (livre != null) {
                String livreCode = String.valueOf(livre.getCode());

                // Ajouter le livre à la table livre
                ajouterLivre(con, livre);

                // Mettre à jour la table detailemprunt
                mettreAJourDetailemprunt(con, livreCode);

                // Retirer le livre de la liste livresEmpruntes
                livresEmpruntes.remove(livreCode);

                JOptionPane.showMessageDialog(null, "Le livre a été retourné avec succès.", "Succès", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Aucun livre emprunté avec le titre spécifié.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erreur lors du retour du livre.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Méthode pour rechercher un livre par son titre dans la liste livresEmpruntes
    private static Livre getLivreByTitreFromMap(String titreLivre) {
        for (Livre livre : livresEmpruntes.values()) {
            if (livre.getTitre().equalsIgnoreCase(titreLivre)) {
                return livre;
            }
        }
        return null;
    }


    private static void ajouterLivre(Connection con, Livre livre) throws SQLException {
        // Insérer le livre dans la table livre
        String query = "INSERT INTO livre (code, titre, auteur, ISBN) VALUES (?, ?, ?, ?)";
        String query01 = "INSERT INTO livreromantique (code, descriptif,nom) VALUES (?, ?, ?)";
        String query02 = "INSERT INTO livrepolicier (code, descriptif, nomDetective, nomVictime) VALUES (?, ?, ?, ?)";
        String query03 = "INSERT INTO livresciencefiction (code, annee, espace) VALUES (?, ?, ?)";
        
        try (PreparedStatement preparedStatement = con.prepareStatement(query)) {
            preparedStatement.setLong(1, livre.getCode());
            preparedStatement.setString(2, livre.getTitre());
            preparedStatement.setString(3, livre.getAuteur());
            preparedStatement.setLong(4, livre.getISBN());

            preparedStatement.executeUpdate();
        }
        
        // Set des attributs spécifiques à chaque sous-classe
        if (livre instanceof LivreRomantique) {
            try (PreparedStatement preparedStatement01 = con.prepareStatement(query01)) {
            	preparedStatement01.setLong(1, ((LivreRomantique)livre).getCode());
            	preparedStatement01.setString(2,((LivreRomantique)livre).getDescriptif());
            	preparedStatement01.setString(3, ((LivreRomantique)livre).getNom());

            	preparedStatement01.executeUpdate();
            }
        } else if (livre instanceof LivrePolicier) {
        	  try (PreparedStatement preparedStatement02 = con.prepareStatement(query02)) {
              	preparedStatement02.setLong(1, ((LivrePolicier)livre).getCode());
              	preparedStatement02.setString(2, ((LivrePolicier)livre).getDescriptif());
              	preparedStatement02.setString(3, ((LivrePolicier)livre).getNomDetective());
              	preparedStatement02.setString(4, ((LivrePolicier)livre).getNomVictime());
              	
              	preparedStatement02.executeUpdate();
              }
        } else if (livre instanceof LivreScienceFiction) {
        	 try (PreparedStatement preparedStatement03 = con.prepareStatement(query03)) {
               	preparedStatement03.setLong(1, ((LivreScienceFiction)livre).getCode());
               	preparedStatement03.setInt(2, ((LivreScienceFiction)livre).getAnnée());
               	preparedStatement03.setString(3, ((LivreScienceFiction)livre).getEspace());

               	preparedStatement03.executeUpdate();
               }
        }
    }

    private static void mettreAJourDetailemprunt(Connection con, String livreCode) throws SQLException {
        // Mettre à jour la table detailemprunt en changeant la date de retour à aujourd'hui
        String query = "UPDATE detailemprunt SET dateRetour = CURRENT_DATE WHERE livre_code = ?";
        try (PreparedStatement preparedStatement = con.prepareStatement(query)) {
            preparedStatement.setString(1, livreCode);

            preparedStatement.executeUpdate();
        }
    }

    


    public InterfaceEmpruntRetour() {
        setTitle("Emprunt et Retour");
        setSize(1000, 400);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JPanel empruntPanel = new JPanel();
        empruntPanel.setLayout(new GridLayout(0, 1)); 

        JLabel empruntLabel = new JLabel("Emprunt");
        empruntLabel.setFont(new Font("Arial", Font.BOLD, 18));
        empruntPanel.add(empruntLabel);

        JLabel livreLabel = new JLabel("Titre Livre :");
        livreLabel.setFont(new Font("Arial", Font.BOLD, 14));
        empruntPanel.add(livreLabel);
        livreField = new JTextField();
        livreField.setColumns(20);
        livreField.setPreferredSize(new Dimension(150, 25));
        empruntPanel.add(livreField);

        JLabel lecteurLabel = new JLabel("CIN Lecteur :");
        lecteurLabel.setFont(new Font("Arial", Font.BOLD, 14));
        empruntPanel.add(lecteurLabel);
        lecteurField = new JTextField();
        lecteurField.setColumns(20);
        lecteurField.setPreferredSize(new Dimension(150, 25));
        empruntPanel.add(lecteurField);
        empruntPanel.setBackground(new Color(204, 229, 255));

        JButton emprunterButton = new JButton("Emprunter");
        emprunterButton.setPreferredSize(new Dimension(80, 30));
        empruntPanel.add(emprunterButton, BorderLayout.EAST);

        // Ajoutez empruntPanel à mainPanel
        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(empruntPanel, gbc);

        JPanel retourPanel = new JPanel();
        retourPanel.setLayout(new GridLayout(0, 1));

        JLabel retourLabel = new JLabel("Retour");
        retourLabel.setFont(new Font("Arial", Font.BOLD, 18));
        retourPanel.add(retourLabel);

        JLabel retourLivreLabel = new JLabel("Titre Livre :");
        retourLivreLabel.setFont(new Font("Arial", Font.BOLD, 14));
        retourPanel.add(retourLivreLabel);
        JTextField retourLivreField = new JTextField();
        retourLivreField.setColumns(20);
        retourLivreField.setPreferredSize(new Dimension(150, 25));
        retourPanel.add(retourLivreField);

        JLabel retourLecteurLabel = new JLabel("CIN Lecteur :");
        retourLecteurLabel.setFont(new Font("Arial", Font.BOLD, 14));
        retourPanel.add(retourLecteurLabel);
        JTextField retourLecteurField = new JTextField();
        retourLecteurField.setColumns(20);
        retourLecteurField.setPreferredSize(new Dimension(150, 25));
        retourPanel.add(retourLecteurField);
        retourPanel.setBackground(new Color(204, 229, 255));

        JButton retournerButton = new JButton("Retourner");
        retournerButton.setPreferredSize(new Dimension(80, 30));
        retourPanel.add(retournerButton);
        
        
        // Ajout des actions aux boutons
        emprunterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	  try {
	                  String titreLivre = livreField.getText();
	                  long cinLecteur = Long.parseLong(lecteurField.getText());
	                    
	                  Connection con = DriverManager.getConnection(url, username, password);
	                  System.out.println("Connected!");
	                  emprunterLivre(con, titreLivre, cinLecteur);
	                } catch (SQLException | NumberFormatException ex) {
	                    ex.printStackTrace();
	                    JOptionPane.showMessageDialog(null, "Erreur lors de l'emprunt. Champs vide ! ", "Erreur", JOptionPane.ERROR_MESSAGE);
	                }

            }

        });

        retournerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	try {
                    String titreLivre = retourLivreField.getText();
                    long cinLecteur = Long.parseLong(retourLecteurField.getText());

                    Connection con = DriverManager.getConnection(url, username, password);
                    System.out.println("Connected!");
                    retournerLivre(con, titreLivre);
                } catch (SQLException | NumberFormatException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Erreur lors du retour du livre. Champs vide ! ", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
    
            }
        });


        // Ajoutez retourPanel à mainPanel
        gbc.gridx = 1;
        mainPanel.add(retourPanel, gbc);
        mainPanel.setBackground(new Color(204, 229, 255));

        getContentPane().add(mainPanel);
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new InterfaceEmpruntRetour().setVisible(true);
        });
        System.out.println(livresEmpruntes);
    }
}
