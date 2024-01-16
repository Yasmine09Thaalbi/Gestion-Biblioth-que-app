package bibliothèque;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.*;
import java.time.LocalDate;

public class InterfaceEmpruntRetour extends Application {

	 private static final String BDD = "Bibliotheque";
	 private static final String url = "jdbc:mysql://localhost:3306/" + BDD;
	 private static final String username = "root";
	 private static final String password = "";

	 private TextField livreField, lecteurField, retourLivreField, retourLecteurField;

   
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Emprunt et Retour");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(8);
        grid.setHgap(10);

        // Emprunt Section
        Label empruntLabel = new Label("Emprunt");
        empruntLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 18");
        GridPane.setConstraints(empruntLabel, 0, 0);

        Label livreLabel = new Label("Titre Livre:");
        GridPane.setConstraints(livreLabel, 0, 1);
        livreField = new TextField();
        livreField.setPromptText("Titre du livre");
        GridPane.setConstraints(livreField, 1, 1);

        Label lecteurLabel = new Label("CIN Lecteur:");
        GridPane.setConstraints(lecteurLabel, 0, 2);
        lecteurField = new TextField();
        lecteurField.setPromptText("CIN du lecteur");
        GridPane.setConstraints(lecteurField, 1, 2);

        Button emprunterButton = new Button("Emprunter");
        emprunterButton.setMinWidth(80);
        GridPane.setConstraints(emprunterButton, 1, 3);

        // Retour Section
        Label retourLabel = new Label("Retour");
        retourLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 18");
        GridPane.setConstraints(retourLabel, 2, 0);

        Label retourLivreLabel = new Label("Titre Livre:");
        GridPane.setConstraints(retourLivreLabel, 2, 1);
        retourLivreField = new TextField();
        retourLivreField.setPromptText("Titre du livre");
        GridPane.setConstraints(retourLivreField, 3, 1);

        Label retourLecteurLabel = new Label("CIN Lecteur:");
        GridPane.setConstraints(retourLecteurLabel, 2, 2);
        retourLecteurField = new TextField();
        retourLecteurField.setPromptText("CIN du lecteur");
        GridPane.setConstraints(retourLecteurField, 3, 2);

        Button retournerButton = new Button("Retourner");
        retournerButton.setMinWidth(80);
        GridPane.setConstraints(retournerButton, 3, 3);

        grid.getChildren().addAll(
                empruntLabel, livreLabel, livreField, lecteurLabel, lecteurField, emprunterButton,
                retourLabel, retourLivreLabel, retourLivreField, retourLecteurLabel, retourLecteurField, retournerButton
        );
        
        grid.setAlignment(Pos.CENTER);
        grid.setStyle("-fx-background-color: #afd8f5;");

        emprunterButton.setOnAction(e -> {
        	 String titreLivre = livreField.getText();
             String cinLecteur = lecteurField.getText();
             
             try (Connection con = DriverManager.getConnection(url, username, password)) {
                 System.out.println("Connected!");
                 if (!cinLecteur.isEmpty()) {
                     emprunterLivre(con, titreLivre, Long.parseLong(cinLecteur));
                 } else {
                     // Handle the case when cinLecteur is empty
                     Alert alert = new Alert(Alert.AlertType.ERROR);
                     alert.setTitle("Erreur");
                     alert.setHeaderText(null);
                     alert.setContentText("Le champ CIN du lecteur est vide. Veuillez saisir le CIN du lecteur.");
                     alert.showAndWait();
                 }

             } catch (SQLException ex) {
             	System.out.println("not connected ! ");
             }
        });
        
        retournerButton.setOnAction(e -> {
        	String titreLivreRetour = retourLivreField.getText();
            String cinLecteurRetour = retourLecteurField.getText();
            
            try (Connection con = DriverManager.getConnection(url, username, password)) {
                System.out.println("Connected!");
                if (!cinLecteurRetour.isEmpty()) {
                	retournerLivre(con, titreLivreRetour,(long) Long.parseLong(cinLecteurRetour));
                } else {
                    // Handle the case when cinLecteur is empty
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Erreur");
                    alert.setHeaderText(null);
                    alert.setContentText("Le champ CIN du lecteur est vide. Veuillez saisir le CIN du lecteur.");
                    alert.showAndWait();
                }
                
            } catch (SQLException ex) {
            	System.out.println("not connected ! ");
            }
            
        });

        Scene scene = new Scene(grid, 600, 250);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    
    
    public static void main(String[] args) {
        launch(args);
    }
    
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
    
    private static boolean isLivreAlreadyEmprunte(Connection con, long cinLecteur) throws SQLException {
        try {
            long abonnementId = getAbonnementId(con, cinLecteur);

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
        	 Alert alert = new Alert(Alert.AlertType.ERROR);
             alert.setTitle("Erreur");
             alert.setHeaderText(null);
             alert.setContentText("Erreur lors de la vérification de l'emprunt du livre.");
             alert.showAndWait();
        }
        return false;
    }
    
    private static void emprunterLivre(Connection con, String titreLivre, long cinLecteur) {
        try {
          	if (titreLivre.isEmpty() && Long.toString(cinLecteur).isEmpty()) {
      		  Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.setHeaderText(null);
                alert.setContentText("Champs vide ! Veuillez saisir le titre du livre à emprunter et le CIN du lecteur .");
                alert.showAndWait();
      		
      	     }
        	
            // Étape 1 : Vérifier si l'abonnement est expiré
            LocalDate dateAbonnement = getDateAbonnement(con, cinLecteur);
            if (dateAbonnement != null && dateAbonnement.plusYears(1).isBefore(LocalDate.now())) {
            	 Alert alert = new Alert(Alert.AlertType.ERROR);
                 alert.setTitle("Erreur");
                 alert.setHeaderText(null);
                 alert.setContentText("L'abonnement est expiré. Renouvelez l'abonnement.");
                 alert.showAndWait();
                return;
            }

            // Étape 2 : Vérifier si le lecteur a déjà emprunté un livre et doit le retourner
            boolean livreDejaEmprunte = isLivreAlreadyEmprunte(con, cinLecteur);
            if (livreDejaEmprunte) {
            	System.out.println("deja emprunt");
            	 Alert alert = new Alert(Alert.AlertType.ERROR);
                 alert.setTitle("Erreur");
                 alert.setHeaderText(null);
                 alert.setContentText("Vous avez déjà emprunté un livre. Veuillez le retourner avant d'en emprunter un autre.");
                 alert.showAndWait();
                return;
            }
            
            // Étape 3 : Effectuer l'emprunt
            try {
                doEmprunt(con, cinLecteur, titreLivre);

            } catch (Exception e) {
                Alert alertError = new Alert(Alert.AlertType.ERROR);
                alertError.setTitle("Erreur");
                alertError.setHeaderText(null);
                alertError.setContentText("Erreur lors de l'emprunt. Veuillez réessayer. Détails : " + e.getMessage());
                alertError.showAndWait();
            }
            
        } catch (SQLException | NumberFormatException e ) {
        	Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("La fonction de l'emprunt n'est pas réalisée ! ");
            alert.showAndWait();
        }
    }
    
    
    
    
    
    private static void doEmprunt(Connection con, long cinLecteur, String titreLivre) {
        try {
        	 // Vérifier si le livre existe dans la table Livre
            String queryCheckLivre = "SELECT * FROM Livre WHERE titre = ?";
            try (PreparedStatement preparedStatementCheckLivre = con.prepareStatement(queryCheckLivre)) {
                preparedStatementCheckLivre.setString(1, titreLivre);
                try (ResultSet resultSetCheckLivre = preparedStatementCheckLivre.executeQuery()) {
                    if (!resultSetCheckLivre.next()) {
                        Alert alertNoLivre = new Alert(Alert.AlertType.ERROR);
                        alertNoLivre.setTitle("Erreur");
                        alertNoLivre.setHeaderText(null);
                        alertNoLivre.setContentText("Aucun livre correspondant trouvé.");
                        alertNoLivre.showAndWait();
                        return;
                    }
                }
            }
            
            // Retrieve book details from the Livre table
            String queryDetailsLivre = "SELECT * FROM Livre WHERE titre = ?";
            try (PreparedStatement preparedStatementDetailsLivre = con.prepareStatement(queryDetailsLivre)) {
                preparedStatementDetailsLivre.setString(1, titreLivre);

                try (ResultSet resultSetDetailsLivre = preparedStatementDetailsLivre.executeQuery()) {
                    if (resultSetDetailsLivre.next()) {
                        long code = resultSetDetailsLivre.getLong("code");
                        String titre = resultSetDetailsLivre.getString("titre");
                        String auteur = resultSetDetailsLivre.getString("auteur");
                        long isbn = resultSetDetailsLivre.getLong("isbn");

                        // Create a JSON object with book details
                        JSONObject detailsLivreJson = new JSONObject();
                        detailsLivreJson.put("code", code);
                        detailsLivreJson.put("titre", titre);
                        detailsLivreJson.put("auteur", auteur);
                        detailsLivreJson.put("isbn", isbn);
                        
                        
                      // Ajouter une vérification pour savoir si le livre appartient à la table livreromantique
                        String queryLivreromantique = "SELECT * FROM livreromantique WHERE code = ?";
                        try (PreparedStatement preparedStatementLivreromantique = con.prepareStatement(queryLivreromantique)) {
                            preparedStatementLivreromantique.setLong(1, code);

                            try (ResultSet resultSetLivreromantique = preparedStatementLivreromantique.executeQuery()) {
                                if (resultSetLivreromantique.next()) {
                                    // Le livre appartient à la table livreromantique, vous pouvez extraire les valeurs spécifiques
                                    String descriptifRomantique = resultSetLivreromantique.getString("descriptif");
                                    String nomRomantique = resultSetLivreromantique.getString("nom");

                                    // Ajoutez ces valeurs à votre objet JSON
                                    detailsLivreJson.put("descriptifRomantique", descriptifRomantique);
                                    detailsLivreJson.put("nomRomantique", nomRomantique);
                                }
                            }
                        }
                        
                     // Ajouter une vérification pour savoir si le livre appartient à la table livreromantique
                        String queryLivrepolicier  = "SELECT * FROM livrepolicier WHERE code = ?";
                        try (PreparedStatement preparedStatementLivrepolicier = con.prepareStatement(queryLivrepolicier)) {
                            preparedStatementLivrepolicier.setLong(1, code);

                            try (ResultSet resultSetLivrepolicier = preparedStatementLivrepolicier.executeQuery()) {
                                if (resultSetLivrepolicier.next()) {
                                    String descriptifPolicier = resultSetLivrepolicier.getString("descriptif");
                                    String nomDetective= resultSetLivrepolicier.getString("nomDetective");
                                    String nomVictime = resultSetLivrepolicier.getString("nomVictime");

                                    // Ajoutez ces valeurs à votre objet JSON
                                    detailsLivreJson.put("descriptifPolicier", descriptifPolicier);
                                    detailsLivreJson.put("nomDetective", nomDetective);
                                    detailsLivreJson.put("nomVictime", nomVictime);
                                }
                            }
                        }
                        
                     // Ajouter une vérification pour savoir si le livre appartient à la table livreromantique
                        String queryLivresciencefiction  = "SELECT * FROM livresciencefiction WHERE code = ?";
                        try (PreparedStatement preparedStatementLivresciencefiction = con.prepareStatement(queryLivresciencefiction)) {
                            preparedStatementLivresciencefiction.setLong(1, code);

                            try (ResultSet resultSetLivresciencefiction = preparedStatementLivresciencefiction.executeQuery()) {
                                if (resultSetLivresciencefiction.next()) {
                                    int annee = resultSetLivresciencefiction.getInt("annee");
                                    String espace= resultSetLivresciencefiction.getString("espace");

                                    // Ajoutez ces valeurs à votre objet JSON
                                    detailsLivreJson.put("annee", annee);
                                    detailsLivreJson.put("espace", espace);
                                  
                                }
                            }
                        }

                        // Perform insertion into the detailemprunt table with the book field of type JSON
                        String queryEmprunt = "INSERT INTO detailemprunt (abonnement_id, livre, dateEmprunt, dateRetour) VALUES (?, ?, CURRENT_DATE, DATE_ADD(CURRENT_DATE, INTERVAL 7 DAY))";
                        try (PreparedStatement preparedStatementEmprunt = con.prepareStatement(queryEmprunt)) {
                            preparedStatementEmprunt.setLong(1, getAbonnementId(con, cinLecteur));
                            preparedStatementEmprunt.setString(2, detailsLivreJson.toString());

                            int rowsAffectedEmprunt = preparedStatementEmprunt.executeUpdate();
                            
                            // Delete the book from the Livre table
                            if (rowsAffectedEmprunt > 0) {
                                Alert alertInformation = new Alert(Alert.AlertType.INFORMATION);
                                alertInformation.setTitle("Information");
                                alertInformation.setHeaderText(null);
                                alertInformation.setContentText("Emprunt du livre reussi !");
                                alertInformation.showAndWait();
                                
                                String queryDeleteLivre = "DELETE FROM Livre WHERE titre = ?";
                                try (PreparedStatement preparedStatementDeleteLivre = con.prepareStatement(queryDeleteLivre)) {
                                    preparedStatementDeleteLivre.setString(1, titreLivre);
                                    preparedStatementDeleteLivre.executeUpdate();
                                }

                                // Check and delete from additional tables if necessary (livreromatique, livrepolicier, livresciencefiction)
                                String[] tables = {"livreromantique", "livrepolicier", "livresciencefiction"};
                                for (String table : tables) {
                                    String queryDeleteFromTable = "DELETE FROM " + table + " WHERE code = ?";
                                    try (PreparedStatement preparedStatementDeleteFromTable = con.prepareStatement(queryDeleteFromTable)) {
                                        preparedStatementDeleteFromTable.setLong(1, code);
                                        preparedStatementDeleteFromTable.executeUpdate();
                                    }
                                }
                            }
                        }
                    }
                }
            }
            Alert alertInformation = new Alert(Alert.AlertType.INFORMATION);
            alertInformation.setTitle("Information");
            alertInformation.setHeaderText(null);
            alertInformation.setContentText("L'emprunt a été effectué avec succès.");
            alertInformation.showAndWait();
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'emprunt du livre : " + e.getMessage());
        }
    }

    
    
    
    private static void mettreAJourDetailemprunt(Connection con, String titreLivre) throws SQLException {
        String query = "UPDATE detailemprunt SET dateRetour = CURRENT_DATE WHERE livre->>'$.titre' = ?";
        try (PreparedStatement preparedStatement = con.prepareStatement(query)) {
            preparedStatement.setString(1, titreLivre);

            preparedStatement.executeUpdate();
        }
    }

    
    private static void retournerLivre(Connection con, String titreLivre,long cinLecteur) {
        try {
             // Mettre à jour la table detailemprunt
             mettreAJourDetailemprunt(con, titreLivre);
             
             // Récupérer les détails du livre à partir de la table detailemprunt
             String queryDetailsLivre = "SELECT livre FROM detailemprunt WHERE livre->>'$.titre' = ? AND abonnement_id = ?";
             try (PreparedStatement preparedStatementDetailsLivre = con.prepareStatement(queryDetailsLivre)) {
                 preparedStatementDetailsLivre.setString(1, titreLivre);
                 preparedStatementDetailsLivre.setLong(2, (long) getAbonnementId(con, cinLecteur));

            

                 try (ResultSet resultSetDetailsLivre = preparedStatementDetailsLivre.executeQuery()) {
                     if (resultSetDetailsLivre.next()){
                         String livreJsonString = resultSetDetailsLivre.getString("livre");
                         System.out.println(livreJsonString);

                         // Convertir la chaîne en objet JSON
                         JSONObject detailsLivreJson = JSONObject.parse(livreJsonString);
                         System.out.println(detailsLivreJson);


                         // Réinsérer le livre dans la table Livre
                         String queryInsererLivre = "INSERT INTO Livre (code, titre, auteur, isbn) VALUES (?, ?, ?, ?)";
                         try (PreparedStatement preparedStatementInsererLivre = con.prepareStatement(queryInsererLivre)) {
                             preparedStatementInsererLivre.setLong(1, detailsLivreJson.getLong("code"));
                             preparedStatementInsererLivre.setString(2, (String) detailsLivreJson.get("titre"));
                             preparedStatementInsererLivre.setString(3, (String) detailsLivreJson.get("auteur"));
                             preparedStatementInsererLivre.setLong(4, detailsLivreJson.getLong("isbn"));

                             preparedStatementInsererLivre.executeUpdate();
                         }

                         // Réinsérer le livre dans la table appropriée (romantique / policier / science fiction)
                         if (detailsLivreJson.containsKey("descriptifRomantique")) {
                             String queryInsererLivreRomantique = "INSERT INTO livreromantique (code, descriptif, nom) VALUES (?, ?, ?)";
                             try (PreparedStatement preparedStatementInsererLivreRomantique = con.prepareStatement(queryInsererLivreRomantique)) {
                                 preparedStatementInsererLivreRomantique.setLong(1, detailsLivreJson.getLong("code"));
                                 preparedStatementInsererLivreRomantique.setString(2, (String) detailsLivreJson.get("descriptifRomantique"));
                                 preparedStatementInsererLivreRomantique.setString(3, (String) detailsLivreJson.get("nomRomantique"));

                                 preparedStatementInsererLivreRomantique.executeUpdate();
                             }
                         } else if (detailsLivreJson.containsKey("descriptifPolicier")) {
                             String queryInsererLivrePolicier = "INSERT INTO livrepolicier (code, descriptif, nomDetective, nomVictime) VALUES (?, ?, ?, ?)";
                             try (PreparedStatement preparedStatementInsererLivrePolicier = con.prepareStatement(queryInsererLivrePolicier)) {
                                 preparedStatementInsererLivrePolicier.setLong(1, detailsLivreJson.getLong("code"));
                                 preparedStatementInsererLivrePolicier.setString(2, (String) detailsLivreJson.get("descriptifPolicier"));
                                 preparedStatementInsererLivrePolicier.setString(3, (String) detailsLivreJson.get("nomDetective"));
                                 preparedStatementInsererLivrePolicier.setString(4, (String) detailsLivreJson.get("nomVictime"));

                                 preparedStatementInsererLivrePolicier.executeUpdate();
                             }
                         } else if (detailsLivreJson.containsKey("annee")) {
                             String queryInsererLivreScienceFiction = "INSERT INTO livresciencefiction (code, annee, espace) VALUES (?, ?, ?)";
                             try (PreparedStatement preparedStatementInsererLivreScienceFiction = con.prepareStatement(queryInsererLivreScienceFiction)) {
                                 preparedStatementInsererLivreScienceFiction.setLong(1, detailsLivreJson.getLong("code"));
                                 preparedStatementInsererLivreScienceFiction.setInt(2, (int) detailsLivreJson.get("annee"));
                                 preparedStatementInsererLivreScienceFiction.setString(3, (String) detailsLivreJson.get("espace"));

                                 preparedStatementInsererLivreScienceFiction.executeUpdate();
                             }
                         }
                     }
                 }
             }
             
             Alert alertInformation = new Alert(Alert.AlertType.INFORMATION);
             alertInformation.setTitle("Information");
             alertInformation.setHeaderText(null);
             alertInformation.setContentText("Le livre a été retourné avec succès.");
             alertInformation.showAndWait();


        } catch (SQLException e) {
        	System.out.println("erreur retour !");

        }
    }

    
}

