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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class InterfaceLecteur extends Application{
	  private static final String BDD = "Bibliotheque";
	  private static final String url = "jdbc:mysql://localhost:3306/" + BDD;
	  private static final String username = "root";
	  private static final String password = "";
	  
	  private TextField cinField;
	  private TextField nomField;
	  private TextField prenomField;
	  private TextField sommeField;
	  private TextField adresse_emailField;
	  private TextField préférenceField;
	  private TextField dateCreationAbonnementField;
	  private TextField fraisAbonnementField;

	  private static TableView<Lecteur>  tableLecteurs;
	  

		@Override
	    public void start(Stage primaryStage) {
	        primaryStage.setTitle("Gestion des Lecteurs");
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
	        cinField = addLabelAndTextField(formulairePanel, "CIN* ", 0, "cinField");
	        prenomField = addLabelAndTextField(formulairePanel, "Prenom* ", 1, "prenomField");
	        nomField = addLabelAndTextField(formulairePanel, "Nom* ", 2, "nomField");
	        sommeField = addLabelAndTextField(formulairePanel, "Somme", 3, "sommeField");
	        adresse_emailField = addLabelAndTextField(formulairePanel, "Adresse email", 4, "adresse_emailField");
	        préférenceField = addLabelAndTextField(formulairePanel, "Préférence", 5, "préférenceField");
	        dateCreationAbonnementField = addLabelAndTextField(formulairePanel, "Date de création de l'abonnement", 6, "dateCreationAbonnementField");
	        fraisAbonnementField = addLabelAndTextField(formulairePanel, "Frais de l'abonnement", 7, "fraisAbonnementField");
	        
	        // Adding buttons
	        Button ajouterButton = new Button("Ajouter");
	        Button supprimerButton = new Button("Supprimer");
	        Button rechercherButton = new Button("Rechercher");
	        Button afficherButton = new Button("Afficher");
	        afficherButton.setPrefWidth(100); 

	        ajouterButton.setOnAction(e -> {
	        	  String cin = cinField.getText(); 
                  String nom = nomField.getText();
                  String prenom = prenomField.getText();
                  String sommeCumule =sommeField.getText(); 
                  String adresseEmail = adresse_emailField.getText();
                  String préférence = préférenceField.getText();
                  String dateCreationAbonnement = dateCreationAbonnementField.getText(); 
                  String fraisAbonnement =fraisAbonnementField.getText(); 
                  
               	  if (cin.isEmpty() && nom.isEmpty() && prenom.isEmpty() && sommeCumule.isEmpty() && adresseEmail.isEmpty() && 
               			préférence.isEmpty() && dateCreationAbonnement.isEmpty() &&  fraisAbonnement.isEmpty() ) {
	        		  Alert alert = new Alert(Alert.AlertType.ERROR);
	                  alert.setTitle("Erreur");
	                  alert.setHeaderText(null);
	                  alert.setContentText("Champs vide ! Veuillez saisir les champs .");
	                  alert.showAndWait();
		    	  }
 
                  DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/d");

                  Abonnement abonnement = new Abonnement(LocalDate.parse(dateCreationAbonnement,formatter), Double.parseDouble(fraisAbonnement));

                  Lecteur lecteur;
                  if (!adresseEmail.isEmpty() && !préférence.isEmpty()) {
                      lecteur = new LecteurFidèle(Long.parseLong(cin), prenom, nom, abonnement, Double.parseDouble(sommeCumule), adresseEmail, préférence);
                  } else {
                      lecteur = new Lecteur(Long.parseLong(cin), prenom, nom, abonnement,Double.parseDouble(sommeCumule));
                  }

                  try {
                      Connection con = DriverManager.getConnection(url, username, password);
                      System.out.println("Connected!");
                      ajouterLecteur(con, lecteur);
                  } catch (SQLException ex) {
                   	System.out.println("not connected ! ");
                  }

	        });
	        
	        supprimerButton.setOnAction(e -> {
	        	 String cin = cinField.getText();
                 
             	  if (cin.isEmpty() ) {
  	        		  Alert alert = new Alert(Alert.AlertType.ERROR);
  	                  alert.setTitle("Erreur");
  	                  alert.setHeaderText(null);
  	                  alert.setContentText("Champs CIN vide ! Veuillez saisir le CIN du lecteur à supprimer .");
  	                  alert.showAndWait();
  		    	  }
   
                  Lecteur lecteur = new Lecteur(Long.parseLong(cin));
              
                 try (Connection con = DriverManager.getConnection(url, username, password)) {
                     System.out.println("Connected!");
                     supprimerLecteur(con, lecteur);
                 } catch (SQLException ex) {
                 	System.out.println("not connected ! ");
                 }
	        });

	        rechercherButton.setOnAction(e ->  {
		        String nomText = nomField.getText();
	            String prenomText =  prenomField.getText();
	            String cinText = cinField.getText();
	            rechercherLecteur(nomText, prenomText, cinText);
            });
	        
	        afficherButton.setOnAction(e -> afficherLecteurs());

	        
	        HBox boutonsPanel = new HBox(10);
	        boutonsPanel.getChildren().addAll(ajouterButton, supprimerButton, rechercherButton, afficherButton);

	        // Adding the form and buttons to the main panel
	        mainPanel.add(formulairePanel, 0, 0);
	        mainPanel.add(boutonsPanel, 0, 1);

	        // Adding the table view
	        tableLecteurs = new TableView<>();
	        TableColumn<Lecteur, Long> cinColumn = new TableColumn<>("CIN");
	        TableColumn<Lecteur, String> prenomColumn = new TableColumn<>("Prenom");
	        TableColumn<Lecteur, String> nomColumn = new TableColumn<>("Nom");

	        cinColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getCIN()));
	        prenomColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPrenom()));
	        nomColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNom()));
	        
	        cinColumn.setPrefWidth(100);
	        prenomColumn.setPrefWidth(120);
	        nomColumn.setPrefWidth(120);

	        tableLecteurs.getColumns().addAll(cinColumn, prenomColumn, nomColumn);
	        tableLecteurs.setRowFactory(tv -> {
	            TableRow<Lecteur> row = new TableRow<>();
	            row.setOnMouseClicked(event -> {
	                if (event.getClickCount() == 2 && !row.isEmpty()) {
	                    Lecteur rowData = row.getItem();
	                }
	            });
	            return row;
	        });

	        Label noContentLabel = new Label("Cliquez sur 'Afficher' pour afficher le contenu.");
	        noContentLabel.setStyle("-fx-font-size: 14;");
	        tableLecteurs.setPlaceholder(noContentLabel);
	        
	        mainPanel.add(tableLecteurs, 0,2 );
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
	            System.out.println("Erreur lors de l'ajout de l'abonnement.");
	        }

	        return idAbonnement;
	    }
	   
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
		            	System.out.println("L'ajout du lecteur a réussi !");
		            	Alert alertInformation = new Alert(Alert.AlertType.INFORMATION);
		                alertInformation.setTitle("Information");
		                alertInformation.setHeaderText(null);
		                alertInformation.setContentText("Lecteur ajouté avec succès.");
		                alertInformation.showAndWait();
		
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
		    	 Alert alert = new Alert(Alert.AlertType.ERROR);
	             alert.setTitle("Erreur");
	             alert.setHeaderText(null);
	             alert.setContentText("La fonction d'ajout n'est pas réalisée ! ");
	             alert.showAndWait();
		    }
		}
	    
		
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
	    
		private static void supprimerLecteur(Connection con, Lecteur lecteur) {
			    try {
			        if (!lecteurExiste(con, lecteur)) {
			            System.out.println("Le lecteur n'existe pas dans la base de données.");
			            Alert alertInformation = new Alert(Alert.AlertType.INFORMATION);
		                alertInformation.setTitle("Information");
		                alertInformation.setHeaderText(null);
		                alertInformation.setContentText("Le lecteur n'existe pas dans la base de données.");
		                alertInformation.showAndWait();
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
			            Alert alertInformation = new Alert(Alert.AlertType.INFORMATION);
	                    alertInformation.setTitle("Information");
	                    alertInformation.setHeaderText(null);
	                    alertInformation.setContentText("Le lecteur a été supprimé avec succès !");
	                    alertInformation.showAndWait();
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
			        System.out.println("Erreur lors de la suppression du lecteur.");
		            Alert alert = new Alert(Alert.AlertType.ERROR);
		            alert.setTitle("Erreur");
		            alert.setHeaderText(null);
		            alert.setContentText("La fonction de suppression n'est pas réalisée ! ");
		            alert.showAndWait();
			    }
			}
	    
	    private static void rechercherLecteur(String nom, String prenom, String cinText) {
		    try {
		    	if (nom.isEmpty() && prenom.isEmpty() && cinText.isEmpty() ) {
	        		  Alert alert = new Alert(Alert.AlertType.ERROR);
	                  alert.setTitle("Erreur");
	                  alert.setHeaderText(null);
	                  alert.setContentText("Champs vide ! Veuillez saisir le nom , le prenom ou le CIN du lecteur à chercher .");
	                  alert.showAndWait();
		    	}
		    	
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
	            String query = queryBuilder.toString();
	            ObservableList<Lecteur> lecteursList = FXCollections.observableArrayList();
	            
	            try (Connection con = DriverManager.getConnection(url, username, password);
	                    Statement stmt = con.createStatement();
	                    ResultSet resultSet = stmt.executeQuery(query)) {

	            	while (resultSet.next()) {
	            		long cin = resultSet.getLong("CIN");
	    	            String prenom_lecteur= resultSet.getString("prenom");
	    	            String nom_lecteur = resultSet.getString("nom");

	                    Lecteur lecteur = new Lecteur(cin,prenom_lecteur,nom_lecteur);

	                    lecteursList.add(lecteur);
	                }

	            	tableLecteurs.setItems(lecteursList);

		        }
		    } catch (SQLException | NumberFormatException e) {
		    	Alert alert = new Alert(Alert.AlertType.ERROR);
	            alert.setTitle("Erreur");
	            alert.setHeaderText(null);
	            alert.setContentText("La fonction de recherche n'est pas réalisée ! ");
	            alert.showAndWait();
		    }
		}
	    
	    private static void afficherLecteurs() {
		    String query = "SELECT * FROM Lecteur";
		    ObservableList<Lecteur> lecteursList = FXCollections.observableArrayList();
		    
		    try (Connection con = DriverManager.getConnection(url, username, password);
	    	         Statement stmt = con.createStatement();
	    	         ResultSet resultSet = stmt.executeQuery(query)) {

		    	while (resultSet.next()) {
    	            long cin = resultSet.getLong("CIN");
    	            String prenom = resultSet.getString("prenom");
    	            String nom = resultSet.getString("nom");

    	            Lecteur lecteur = new Lecteur(cin,prenom,nom);
    	           
    	            lecteursList.add(lecteur);
    	        }

		    	tableLecteurs.setItems(lecteursList);

		    } catch (SQLException e) {
		        System.out.println("Erreur lors de la récupération des lecteurs.");
		    }
		}


}