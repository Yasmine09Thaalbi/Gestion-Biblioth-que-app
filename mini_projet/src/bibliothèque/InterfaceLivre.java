package bibliothèque;

import javafx.application.Application;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.*;

public class InterfaceLivre extends Application {
    private static final String BDD = "Bibliotheque";
    private static final String url = "jdbc:mysql://localhost:3306/" + BDD;
    private static final String username = "root";
    private static final String password = "";
    
    private TextField titreField;
    private TextField auteurField;
    private TextField isbnField;
    private TextField descriptifField;
    private TextField nomField;
    private TextField nomDetectiveField;
    private TextField nomVictimeField;
    private TextField anneeField;
    private TextField espaceField;

    private static TableView<Livre> tableLivres;

	@Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Gestion des Livres");
        primaryStage.setWidth(800);
        primaryStage.setHeight(600);

        // Using GridPane for the main panel
        GridPane mainPanel = new GridPane();
        mainPanel.setPadding(new Insets(10));
        mainPanel.setHgap(10);
        mainPanel.setVgap(10);

        GridPane formulairePanel = new GridPane();
        formulairePanel.setHgap(10);
        formulairePanel.setVgap(10);

        // Adding fields to the form
        titreField = addLabelAndTextField(formulairePanel, "Titre* ", 0, "titreField");
        auteurField = addLabelAndTextField(formulairePanel, "Auteur* ", 1, "auteurField");
        isbnField = addLabelAndTextField(formulairePanel, "ISBN* ", 2, "isbnField");
        descriptifField = addLabelAndTextField(formulairePanel, "Descriptif ", 3, "descriptifField");
        nomField = addLabelAndTextField(formulairePanel, "Nom ", 4, "nomField");
        nomDetectiveField = addLabelAndTextField(formulairePanel, "Nom du détective ", 5, "nomDetectiveField");
        nomVictimeField = addLabelAndTextField(formulairePanel, "Nom de la victime ", 6, "nomVictimeField");
        anneeField = addLabelAndTextField(formulairePanel, "Année ", 7, "anneeField");
        espaceField = addLabelAndTextField(formulairePanel, "Espace ", 8, "espaceField");


        // Adding buttons
        Button ajouterButton = new Button("Ajouter");
        Button supprimerButton = new Button("Supprimer");
        Button rechercherButton = new Button("Rechercher");
        Button afficherButton = new Button("Afficher");

        ajouterButton.setOnAction(e -> {
            String titre = titreField.getText();
            String auteur = auteurField.getText();
            long isbn = Long.parseLong(isbnField.getText()); 
            String descriptif = descriptifField.getText();
            String nomDetective = nomDetectiveField.getText();
            String nomVictime = nomVictimeField.getText();
            String nom = nomField.getText();
            String anneeText = anneeField.getText();
            String espace = espaceField.getText();

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

            try {
                Connection con = DriverManager.getConnection(url, username, password);
                System.out.println("Connected!");
                ajouterLivre(con, livre);
            } catch (SQLException ex) {
            	System.out.println("not connected ! ");
            }
        });
        
        supprimerButton.setOnAction(e -> {
            String titre = titreField.getText();
            String auteur = auteurField.getText();
            String isbnText = isbnField.getText(); 
            String descriptif = descriptifField.getText();
            String nomDetective = nomDetectiveField.getText();
            String nomVictime = nomVictimeField.getText();
            String nom = nomField.getText();
            String anneeText = anneeField.getText();
            String espace = espaceField.getText();

            Livre livre;
            if (!descriptifField.getText().isEmpty() && !nomDetectiveField.getText().isEmpty() && !nomVictimeField.getText().isEmpty()) {
                livre = new LivrePolicier(titre,auteur,Long.parseLong(isbnText),descriptif,nomDetective,nomVictime);
                
            } else if (!descriptifField.getText().isEmpty() && !nomField.getText().isEmpty()) {
                livre = new LivreRomantique(titre,auteur,Long.parseLong(isbnText),descriptif,nom);
                
            } else if (!anneeField.getText().isEmpty() && !espaceField.getText().isEmpty()) {
                livre = new LivreScienceFiction(titre,auteur,Long.parseLong(isbnText),Integer.parseInt(anneeText),espace);
                
            } else {
                livre = new Livre(titre,auteur,Long.parseLong(isbnText));
            }

            try (Connection con = DriverManager.getConnection(url, username, password)) {
                System.out.println("Connected!");
                supprimerLivre(con, livre);
            } catch (SQLException ex) {
            	System.out.println("not connected ! ");
            }
            	
        });

        
        rechercherButton.setOnAction(e -> {
            String titreText = titreField.getText();
            String auteurText = auteurField.getText();
            rechercherLivre(titreText, auteurText);
        });
        
        
        afficherButton.setOnAction(e -> afficherLivres());

        
        HBox boutonsPanel = new HBox(10);
        boutonsPanel.getChildren().addAll(ajouterButton, supprimerButton, rechercherButton, afficherButton);

        // Adding the form and buttons to the main panel
        mainPanel.add(formulairePanel, 0, 0);
        mainPanel.add(boutonsPanel, 0, 1);

        // Adding the table view
        tableLivres = new TableView<>();
        TableColumn<Livre, Long> isbnColumn = new TableColumn<>("ISBN");
        TableColumn<Livre, String> titreColumn = new TableColumn<>("Titre");
        TableColumn<Livre, String> auteurColumn = new TableColumn<>("Auteur");
        
        isbnColumn.setPrefWidth(100); 
        titreColumn.setPrefWidth(120); 
        auteurColumn.setPrefWidth(90); 

        // Set overall preferred width for the TableView
        tableLivres.setPrefWidth(isbnColumn.getPrefWidth() + titreColumn.getPrefWidth() + auteurColumn.getPrefWidth());

        isbnColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getISBN()));
        titreColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTitre()));
        auteurColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAuteur()));

        tableLivres.getColumns().addAll(isbnColumn, titreColumn, auteurColumn);
        tableLivres.setRowFactory(tv -> {
            TableRow<Livre> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    Livre rowData = row.getItem();
                }
            });
            return row;
        });

        Label noContentLabel = new Label("Cliquez sur 'Afficher' pour afficher le contenu.");
        noContentLabel.setStyle("-fx-font-size: 14;");
        tableLivres.setPlaceholder(noContentLabel);
        
        mainPanel.add(tableLivres, 0,2 );
        mainPanel.setStyle("-fx-background-color: #afd8f5;");
        mainPanel.setAlignment(Pos.CENTER);

        Scene scene = new Scene(mainPanel, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    private TextField addLabelAndTextField(GridPane gridPane, String labelText, int row, String textFieldId) {
        Label label = new Label(labelText);
        TextField textField = new TextField();
        textField.setId(textFieldId);
        textField.setPromptText(labelText);

        gridPane.add(label, 0, row);
        gridPane.add(textField, 1, row);
		return textField;
    }
    
    
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

    private static void ajouterLivre(Connection con, Livre livre) {
        try {
            String queryLivre = "INSERT INTO Livre (titre, auteur, ISBN) VALUES ('" + livre.getTitre() + "', '" + livre.getAuteur() + "', " + livre.getISBN() + ")";
            try (Statement stmt = con.createStatement()) {
                stmt.executeUpdate(queryLivre, Statement.RETURN_GENERATED_KEYS);
                
                Alert alertInformation = new Alert(Alert.AlertType.INFORMATION);
                alertInformation.setTitle("Information");
                alertInformation.setHeaderText(null);
                alertInformation.setContentText("Livre ajouté avec succès.");
                alertInformation.showAndWait();

                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        long generatedCode = generatedKeys.getLong(1);

                        if (livre instanceof LivrePolicier) {
                            String queryPolicier = "INSERT INTO LivrePolicier (code, descriptif, nomDetective, nomVictime) VALUES (" + generatedCode + ", '"
                                    + ((LivrePolicier) livre).getDescriptif() + "', '" + ((LivrePolicier) livre).getNomDetective() + "', '"
                                    + ((LivrePolicier) livre).getNomVictime() + "')";
                            stmt.executeUpdate(queryPolicier);
                            System.out.println("l'ajout du livre Policier ! ");
                        }
                        
                        if (livre instanceof LivreRomantique) {
                            String queryRomantique = "INSERT INTO LivreRomantique (code, descriptif, nom) VALUES (" + generatedCode + ", '"
                                    + ((LivreRomantique) livre).getDescriptif() + "', '"+ ((LivreRomantique) livre).getNom() + "')";
                            stmt.executeUpdate(queryRomantique);
                            System.out.println("l'ajout du livre Romantique ! ");
                        }
                        
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
        	 Alert alert = new Alert(Alert.AlertType.ERROR);
             alert.setTitle("Erreur");
             alert.setHeaderText(null);
             alert.setContentText("La fonction d'ajout n'est pas réalisée ! ");
             alert.showAndWait();
            
        }
    }


    // Méthode pour supprimer un livre
    private static void supprimerLivre(Connection con, Livre livre) {
        try {
         	if ( livre.getTitre().isEmpty() && livre.getAuteur().isEmpty()  ) {
      		  Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.setHeaderText(null);
                alert.setContentText("Champs vide ! Veuillez saisir le titre ou l'auteur livre à supprimer .");
                alert.showAndWait();
      		
      	    }
        	
        	
            if (!livreExiste(con, livre)) {
                System.out.println("Le livre n'existe pas dans la base de données.");
                Alert alertInformation = new Alert(Alert.AlertType.INFORMATION);
                alertInformation.setTitle("Information");
                alertInformation.setHeaderText(null);
                alertInformation.setContentText("Le livre n'existe pas dans la base de données.");
                alertInformation.showAndWait();
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
                    Alert alertInformation = new Alert(Alert.AlertType.INFORMATION);
                    alertInformation.setTitle("Information");
                    alertInformation.setHeaderText(null);
                    alertInformation.setContentText("Le livre a été supprimé avec succès !");
                    alertInformation.showAndWait();
                    
                }
            } else {
                System.out.println("Erreur lors de la récupération du code du livre.");
            }

        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("La fonction de suppression n'est pas réalisée ! ");
            alert.showAndWait();
           
        }
    }

    
    
    // Méthode pour rechercher les livres par titre, auteur ou premières lettres du titre
    private static void rechercherLivre(String titre, String auteur) {
        try {
        	if (titre.isEmpty() && auteur.isEmpty() ) {
        		  Alert alert = new Alert(Alert.AlertType.ERROR);
                  alert.setTitle("Erreur");
                  alert.setHeaderText(null);
                  alert.setContentText("Champs vide ! Veuillez saisir le titre ou l'auteur livre à chercher .");
                  alert.showAndWait();
        		
        	}
            // Construire la requête de recherche en fonction des paramètres fournis
            StringBuilder queryBuilder = new StringBuilder("SELECT * FROM Livre WHERE");
            if (titre != null && !titre.isEmpty()) {
                queryBuilder.append(" titre LIKE '%").append(titre).append("%' AND");
            }
            if (auteur != null && !auteur.isEmpty()) {
                queryBuilder.append(" auteur LIKE '%").append(auteur).append("%' AND");
            }
            

            // Supprimer le "AND" en trop à la fin de la requête
            int lastIndex = queryBuilder.lastIndexOf("AND");
            if (lastIndex != -1) {
                queryBuilder.delete(lastIndex, queryBuilder.length());
            }

            // Exécuter la requête
            String query = queryBuilder.toString();
            ObservableList<Livre> livresList = FXCollections.observableArrayList();

            try (Connection con = DriverManager.getConnection(url, username, password);
                 Statement stmt = con.createStatement();
                 ResultSet resultSet = stmt.executeQuery(query)) {

                while (resultSet.next()) {
                    long isbn = resultSet.getLong("ISBN");
                    String titreResult = resultSet.getString("titre");
                    String auteurResult = resultSet.getString("auteur");

                    Livre livre = new Livre();
                    livre.setISBN(isbn);
                    livre.setTitre(titreResult);
                    livre.setAuteur(auteurResult);

                    livresList.add(livre);
                }

                tableLivres.setItems(livresList);

            }
        } catch (SQLException | NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("La fonction de recherche n'est pas réalisée ! ");
            alert.showAndWait();
        }
    }

    
    
    private void afficherLivres() {
    	    String query = "SELECT * FROM Livre";
    	    ObservableList<Livre> livresList = FXCollections.observableArrayList();

    	    try (Connection con = DriverManager.getConnection(url, username, password);
    	         Statement stmt = con.createStatement();
    	         ResultSet resultSet = stmt.executeQuery(query)) {

    	        while (resultSet.next()) {
    	            long isbn = resultSet.getLong("ISBN");
    	            String titre = resultSet.getString("titre");
    	            String auteur = resultSet.getString("auteur");

    	            Livre livre = new Livre();
    	            livre.setISBN(isbn);
    	            livre.setTitre(titre);
    	            livre.setAuteur(auteur);

    	            livresList.add(livre);
    	        }

    	        tableLivres.setItems(livresList);

    	    } catch (SQLException e) {
    	        System.out.println("Erreur lors de la récupération des livres.");
    	    }
    }

}

