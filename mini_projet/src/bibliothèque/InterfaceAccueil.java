package bibliothèque;

import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class InterfaceAccueil extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Accueil");

        // Titre
        Label titleLabel = new Label("Bienvenue Dans Votre Application De Gestion De Bibliothèque");
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 20;");
        titleLabel.setWrapText(true);

        // Image
        ImageView imageView = new ImageView(new Image("file:C:/Users/Yasmine/eclipse-workspace/mini_projet/src/bibliothèque/biblio.png"));
        imageView.setFitWidth(390);
        imageView.setFitHeight(260);

        // Boutons
        Button livreButton = createButton("Gestion des Livres");
        Button lecteurButton = createButton("Gestion des Lecteurs");
        Button empruntButton = createButton("Gestion Emprunt / Retour");

        // Actions pour les boutons
        livreButton.setOnAction(e -> new InterfaceLivre().start(new Stage()));
        lecteurButton.setOnAction(e -> new InterfaceLecteur().start(new Stage()));
        empruntButton.setOnAction(e -> new InterfaceEmpruntRetour().start(new Stage()));

        // Agencement principal
        GridPane mainPanel = new GridPane();
        mainPanel.setHgap(10);
        mainPanel.setVgap(50);
        mainPanel.setPadding(new Insets(20, 20, 20, 20));

        // Title Label
        mainPanel.add(titleLabel, 0, 0, 3, 1);
        GridPane.setHalignment(titleLabel, HPos.CENTER);

        // Image View
        mainPanel.add(imageView, 0, 1, 3, 1);
        GridPane.setHalignment(imageView, HPos.CENTER);

        // Buttons
        mainPanel.add(livreButton, 0, 2);
        mainPanel.add(lecteurButton, 1, 2);
        mainPanel.add(empruntButton, 2, 2);

        mainPanel.setStyle("-fx-background-color: #afd8f5;");
        mainPanel.setAlignment(Pos.CENTER);

        Scene scene = new Scene(mainPanel, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private Button createButton(String text) {
        Button button = new Button(text);
        button.setPrefSize(250, 70);
        button.setStyle("-fx-font-weight: bold; -fx-font-size: 15;");
        return button;
    }

    public static void main(String[] args) {
        launch(args);
    }
}