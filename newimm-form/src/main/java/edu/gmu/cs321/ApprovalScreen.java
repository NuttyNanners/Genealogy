package edu.gmu.cs321;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.File;
import java.io.FileInputStream;

public class ApprovalScreen extends Application {

    private GenealogyRequestForm requestForm = new GenealogyRequestForm(
            "Jane Doe", 
            "123 Main St", 
            "123-45-6789", 
            "John Doe", 
            "456 Elm St", 
            new java.util.Date(), 
            "USA", 
            new File("path/to/proofImage.png"), 
            new File("path/to/deathRecord.pdf")
    );

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Request Approval");
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));

        VBox infoBox = new VBox(10);
        infoBox.setPadding(new Insets(10));
        infoBox.setStyle("-fx-border-color: gray; -fx-border-width: 1; -fx-border-radius: 5;");

        File imageFile = requestForm.getProofOfRelationshipFile();
        if (imageFile != null && imageFile.exists()) {
            try {
                Image image = new Image(new FileInputStream(imageFile));
                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(150);
                imageView.setPreserveRatio(true);
                infoBox.getChildren().add(imageView);
            } catch (Exception e) {
                System.out.println("Unable to load image: " + e.getMessage());
            }
        }

        Label requesterNameLabel = new Label("Requester Name: " + requestForm.getRequesterName());
        Label requesterAddressLabel = new Label("Requester Address: " + requestForm.getRequesterAddress());
        Label requesterSSNLabel = new Label("Requester SSN: " + requestForm.getRequesterSSN());
        Label deceasedNameLabel = new Label("Deceased Name: " + requestForm.getDeceasedName());
        Label deceasedAddressLabel = new Label("Deceased Address: " + requestForm.getDeceasedAddress());
        Label dateOfDeathLabel = new Label("Date of Death: " + requestForm.getDateOfDeath().toString());
        Label countryLabel = new Label("Country of Origin: " + requestForm.getCountryOfOrigin());

        infoBox.getChildren().addAll(
                requesterNameLabel,
                requesterAddressLabel,
                requesterSSNLabel,
                deceasedNameLabel,
                deceasedAddressLabel,
                dateOfDeathLabel,
                countryLabel
        );

        HBox topContainer = new HBox(infoBox);
        topContainer.setAlignment(Pos.TOP_LEFT);
        root.setTop(topContainer);

        HBox buttonBox = new HBox(20);
        buttonBox.setPadding(new Insets(10));
        buttonBox.setAlignment(Pos.BOTTOM_RIGHT);

        Button acceptButton = new Button("Accept");
        acceptButton.setStyle("-fx-background-color: green; -fx-text-fill: white; -fx-font-weight: bold;");
        acceptButton.setOnAction(e -> System.out.println("Request Accepted!"));

        Button denyButton = new Button("Deny");
        denyButton.setStyle("-fx-background-color: red; -fx-text-fill: white; -fx-font-weight: bold;");
        denyButton.setOnAction(e -> showDenialScreen());

        buttonBox.getChildren().addAll(acceptButton, denyButton);
        root.setBottom(buttonBox);

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void showDenialScreen() {
        Stage denialStage = new Stage();
        denialStage.setTitle("Deny Request");
        VBox vbox = new VBox(15);
        vbox.setPadding(new Insets(15));
        vbox.setAlignment(Pos.CENTER);
        Label instructionLabel = new Label("Please provide the reason for denial:");
        TextArea reasonArea = new TextArea();
        reasonArea.setPrefRowCount(3);
        reasonArea.setWrapText(true);
        reasonArea.setPromptText("Enter denial reason here...");
        Button submitButton = new Button("Submit");
        submitButton.setOnAction(e -> {
            String denialReason = reasonArea.getText();
            System.out.println("Request Denied. Reason: " + denialReason);
            denialStage.close();
        });
        vbox.getChildren().addAll(instructionLabel, reasonArea, submitButton);
        Scene scene = new Scene(vbox, 350, 200);
        denialStage.setScene(scene);
        denialStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

