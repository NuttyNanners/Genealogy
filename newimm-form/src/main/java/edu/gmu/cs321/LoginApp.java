/**
 * LoginApp.java
 * Name: Addison Klein
 * G#01331326
 * CS321-009
 * Professor Steven Ernst
 * Spring 2025
 */

package edu.gmu.cs321;

import edu.gmu.cs321.ApprovalScreen;
import edu.gmu.cs321.DataEntryScreen;
import edu.gmu.cs321.ReviewScreen;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * LoginApp class is responsible for managing the login and registration
 * of users. It adds new users with given screen permissions to the DB
 * and also opens the screen that the user has permission to upon
 * successful login.
 */
public class LoginApp extends Application {
    private Stage primaryStage;   // the main application window

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        primaryStage.setTitle("Genealogy Records – Login");
        showLoginScene();       //Displays the login UI
        primaryStage.show();
    }

  /**
   * Builds and displays the login scene.
   * Upon successful authentication, launches the user given screen privilege.
   */
    private void showLoginScene() {
        TextField    userField = new TextField();
        userField.setPromptText("Username");
        PasswordField passField = new PasswordField();
        passField.setPromptText("Password");

        Button signIn    = new Button("Sign In");
        Button register  = new Button("Register");
        HBox  buttons    = new HBox(10, signIn, register);
        buttons.setAlignment(Pos.CENTER);

        VBox layout = new VBox(10,
        new Label("Please sign in or register"),
        userField, passField, buttons
        );
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        signIn.setOnAction(e -> {
        // Trim the username and hash the password with SHA-256 before sending to the database
        String access = AuthDatabase.authenticate(
            userField.getText().trim(),
            hashPassword(passField.getText())
        );
        if (access != null)
            //Navigate to screen based on users access level
            switch (access) {
            case "Approval"  -> new ApprovalScreen().start(primaryStage);
            case "Review"    -> new ReviewScreen().start(primaryStage);
            case "DataEntry" -> new DataEntryScreen().start(primaryStage);
            }
        else
            showAlert("Login Failed", "Invalid username or password.");
        });

        register.setOnAction(e -> showRegisterScene());
        primaryStage.setScene(new Scene(layout, 400, 250));
    }

    /**
     * Builds and displays the registration scene. It then 
     * validates password and calls the database to register.
     */
    private void showRegisterScene() {
        TextField    userField    = new TextField();
        userField.setPromptText("New username");
        PasswordField passField   = new PasswordField();
        passField.setPromptText("Password");
        PasswordField confirm     = new PasswordField();
        confirm.setPromptText("Confirm password");

        ComboBox<String> accessBox = new ComboBox<>();
        accessBox.getItems().addAll("Approval","Review","DataEntry");
        accessBox.getSelectionModel().selectFirst();    // default choice

        Button submit = new Button("Submit");
        Button back   = new Button("Back");
        HBox   buttons= new HBox(10, submit, back);
        buttons.setAlignment(Pos.CENTER);

        VBox layout = new VBox(10,
        new Label("Register new user"),
        userField, passField, confirm, accessBox, buttons
        );
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        submit.setOnAction(e -> {
            // Checks for matching passwords
        if (!passField.getText().equals(confirm.getText())) {
            showAlert("Error","Passwords do not match.");
            return;
        }
        boolean ok = AuthDatabase.registerUser(
            userField.getText().trim(),
            hashPassword(passField.getText()),
            accessBox.getValue()
        );
        if (ok) {
            showAlert("Success","Registration complete.");
            showLoginScene();   // Return to login
        } else
            showAlert("Error","Could not register (username may exist).");
        });
        back.setOnAction(e -> showLoginScene());

        primaryStage.setScene(new Scene(layout, 400, 300));
    }

    /**
     * Displays an informational alert.
     * @param title the title of the alert
     * @param msg the message of the alert
     */
    private void showAlert(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        a.setHeaderText(null);
        a.setTitle(title);
        a.showAndWait();
    }

    /**
     * Computes the SHA256 hash of the given password.
     * @param password the plaintext of the password
     * @return hex encoded sha256 hash.
     */
    private String hashPassword(String password) {
        try {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[]         h  = md.digest(password.getBytes(StandardCharsets.UTF_8));
        StringBuilder  sb = new StringBuilder();
        for (byte b : h) sb.append(String.format("%02x", b));   // Conversion to hex
        return sb.toString();
        } catch (NoSuchAlgorithmException e) {
        throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
