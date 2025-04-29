package edu.gmu.cs321;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.UUID;
//import java.util.LinkedList;
//import java.util.Queue;
//import java.io.File;

public class ReviewScreen extends Application {

    // All these for the genealogy form object
    private TextField requesterName;
    private TextField requesterAddress;
    private TextField requesterSSN;
    private TextField deceasedName;
    private TextField deceasedAddress;
    private TextField dateOfDeath;
    private TextField countryOfOrigin;
    private TextField proofOfRelationshipFile; // Keep as a text field so can be edited?
    private TextField deathRecordFile;
    // Just for show doesn't have to match right now
    private final String formID = UUID.randomUUID().toString();
    private Label messageLabel;

    // Currently, we do not have a queue object to store gene form objects in, but that needs to be implemented
    //private Queue<GenealogyRequestForm> approvalQueue = new LinkedList();

    @Override
    public void start(Stage primaryStage) {
        Label introMessageLabel = new Label("Please ensure the validity of all form data");

        requesterName = new TextField();
        requesterName.setPromptText("Requester Name");
        // TODO: make these ugly fields not be the entire length of the screen... setPrefWidth is not working
        //requesterName.setPrefWidth(200);

        requesterAddress = new TextField();
        requesterAddress.setPromptText("Requester Address");

        requesterSSN = new TextField();
        requesterSSN.setPromptText("Requester SSN");

        deceasedName = new TextField();
        deceasedName.setPromptText("Deceased Name");

        deceasedAddress = new TextField();
        deceasedAddress.setPromptText("Deceased Address");

        dateOfDeath = new TextField();
        dateOfDeath.setPromptText("Date of Death");

        countryOfOrigin = new TextField();
        countryOfOrigin.setPromptText("Country of Origin");

        proofOfRelationshipFile = new TextField();
        proofOfRelationshipFile.setPromptText("FILE: Proof of Relationship File");

        deathRecordFile = new TextField();
        deathRecordFile.setPromptText("FILE: Death Record File");

        Label formIDLabel = new Label("Gene Form ID: " + formID);

        // Just to display to user that button was pressed
        messageLabel = new Label();
        messageLabel.setAlignment(Pos.BOTTOM_CENTER);

        Button sendButton = new Button("Send to Approver");
        sendButton.setPrefSize(130, 35); 
        // Does nothing currently but print out a message notifying the user that it was sent
        // button should push to approval queue when real functionality is finished
        sendButton.setOnAction(e -> messageLabel.setText("Form was sent to approval"));

        HBox buttonBox = new HBox(sendButton);
        buttonBox.setAlignment(Pos.BOTTOM_RIGHT);
        buttonBox.setStyle("-fx-padding: 0 10px 10px 0;"); 

        VBox vbox = new VBox(10, introMessageLabel, requesterName, requesterAddress, requesterSSN, deceasedName, deceasedAddress, dateOfDeath, countryOfOrigin, proofOfRelationshipFile, deathRecordFile, formIDLabel, buttonBox, messageLabel);
        vbox.setSpacing(10);
        vbox.setStyle("-fx-background-color:rgb(244, 215, 141);");

        Scene scene = new Scene(vbox, 1280, 720);
        primaryStage.setTitle("Data Reviewer Screen");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
