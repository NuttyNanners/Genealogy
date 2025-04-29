/**
 * ApprovalScreen.java
 * Name: Addison Klein
 * G#01331326
 * CS321-009
 * Professor Steven Ernst
 * Spring 2025
 */

 package edu.gmu.cs321;

 import javafx.application.Application;
 import javafx.application.HostServices;
 import javafx.collections.FXCollections;
 import javafx.collections.ObservableList;
 import javafx.geometry.Insets;
 import javafx.geometry.Pos;
 import javafx.scene.Scene;
 import javafx.scene.control.*;
 import javafx.scene.control.ListCell;
 import javafx.scene.control.ListView;
 import javafx.scene.image.Image;
 import javafx.scene.image.ImageView;
 import javafx.scene.layout.*;
 import javafx.stage.Stage;
 import javafx.util.Pair;
 
 import java.awt.Desktop;
 import java.io.File;
 import java.io.FileInputStream;
 import java.sql.Connection;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.ArrayList;
 import java.util.Date;
 import java.util.List;
 
 /**
  * Class ApprovalScreen is responsible for managing the acceptance/denial of new requests.
  * Allows to accept new entries, deny them and give an attached reason for denial.
  */
 public class ApprovalScreen extends Application {
     private GenealogyRequestForm requestForm;       // The current form under review
     private String               currentFormId;      // ID of the form currently displayed
     private Button               browseBtn;          // Browse Cases button to switch forms
     private VBox                 infoBox;            // Container for form info and controls
     private Label                statusLabel       = new Label("Status: Pending");  // Shows approval state
     private Label                denialReasonLabel = new Label("Denial Reason: ");  // Shows denial reason
     private HostServices         hostServices;       // Opens URLs when Desktop API not supported
 
     @Override
     public void start(Stage primaryStage) {
         hostServices = getHostServices();
 
         // Directory for sample data (images/PDFs)
         File dataDir = new File(System.getProperty("user.dir"), "data");
         if (!dataDir.exists()) dataDir.mkdirs();
 
         // Creating a sample request form (replace with real DB fetch in production)
         GenealogyRequestForm sample = new GenealogyRequestForm(
             "Jane Doe", "123 Main St", "123-45-6789",
             "John Doe", "456 Elm St", new Date(), "USA",
             new File(dataDir, "janeDoe.png"),
             new File(dataDir, "deathRecord.pdf")
         );
         GenealogyFormDatabase.insertFormData(sample, sample.getFormID());
         requestForm   = sample;
         currentFormId = sample.getFormID();
 
         // Main window setup
         primaryStage.setTitle("Request Approval");
         BorderPane root = new BorderPane();
         root.setPadding(new Insets(20));
 
         // INFO BOX (top region)
         infoBox = new VBox(10);
         infoBox.setPadding(new Insets(10));
         infoBox.setStyle("-fx-border-color: gray; -fx-border-width: 1; -fx-border-radius: 5;");
 
         // Browse Cases button
         browseBtn = new Button("Browse Cases");
         browseBtn.setOnAction(e -> showFormSelector(primaryStage));
 
         // Populate infoBox initially
         refreshDisplay();
 
         root.setTop(infoBox);
 
         // ACTION BUTTONS (bottom region)
         HBox actions = new HBox(20);
         actions.setAlignment(Pos.CENTER_RIGHT);
         actions.setPadding(new Insets(10));
 
         Button acceptBtn = new Button("Accept");
         acceptBtn.setOnAction(e -> {
             // Mark accepted in DB and refresh display
             if (GenealogyFormDatabase.updateApproval(currentFormId, true, null)) {
                 refreshDisplay();
             }
         });
 
         Button denyBtn = new Button("Deny");
         denyBtn.setOnAction(e -> showDenialScreen());
 
         actions.getChildren().addAll(acceptBtn, denyBtn);
         root.setBottom(actions);
 
         primaryStage.setScene(new Scene(root, 800, 600));
         primaryStage.show();
     }
 
     /**
      * Rebuilds the infoBox contents for the currently selected form.
      */
     private void refreshDisplay() {
         infoBox.getChildren().clear();
 
         // Proof-of-relationship image
         File img = requestForm.getProofOfRelationshipFile();
         if (img.exists()) {
             try {
                 ImageView iv = new ImageView(new Image(new FileInputStream(img)));
                 iv.setFitWidth(150);
                 iv.setPreserveRatio(true);
                 infoBox.getChildren().add(iv);
             } catch (Exception e) {
                 System.err.println("Unable to load image: " + e.getMessage());
             }
         }
 
         // Metadata labels
         infoBox.getChildren().addAll(
             new Label("Requester:      " + requestForm.getRequesterName()),
             new Label("Address:        " + requestForm.getRequesterAddress()),
             new Label("SSN:            " + requestForm.getRequesterSSN()),
             new Label("Deceased:       " + requestForm.getDeceasedName()),
             new Label("Deceased Addr:  " + requestForm.getDeceasedAddress()),
             new Label("Date of Death:  " + requestForm.getDateOfDeath()),
             new Label("Country:        " + requestForm.getCountryOfOrigin())
         );
 
         // Button to open death record PDF
         Button viewPdf = new Button("Open Death Record PDF");
         viewPdf.setOnAction(e -> {
             File pdf = requestForm.getDeathRecordFile();
             if (pdf.exists()) {
                 try {
                     if (Desktop.isDesktopSupported()) {
                         Desktop.getDesktop().open(pdf);
                     } else {
                         hostServices.showDocument(pdf.toURI().toString());
                     }
                 } catch (Exception ex) {
                     ex.printStackTrace();
                 }
             }
         });
         infoBox.getChildren().add(viewPdf);
 
         // Update and show approval status & denial reason
         var st = GenealogyFormDatabase.fetchApprovalStatus(currentFormId);
         if (st != null) {
             statusLabel.setText("Status: " + (st.isAccepted() ? "Accepted" : "Denied"));
             denialReasonLabel.setText("Denial Reason: " +
                 (st.getDenialReason() == null ? "" : st.getDenialReason()));
         }
         infoBox.getChildren().addAll(statusLabel, denialReasonLabel);
 
         // Add the Browse Cases button at the end
         infoBox.getChildren().add(browseBtn);
     }
 
     /**
      * Shows a dialog to collect a denial reason and update the form as denied.
      */
     private void showDenialScreen() {
         Stage dlg = new Stage();
         dlg.setTitle("Deny Request");
 
         VBox box = new VBox(10);
         box.setPadding(new Insets(15));
         box.setAlignment(Pos.CENTER);
 
         TextArea reason = new TextArea();
         reason.setWrapText(true);
         reason.setPromptText("Enter denial reason (max 800 chars)");
 
         Button submit = new Button("Submit");
         submit.setOnAction(e -> {
             if (GenealogyFormDatabase.updateApproval(currentFormId, false, reason.getText())) {
                 refreshDisplay();
                 dlg.close();
             }
         });
 
         box.getChildren().addAll(new Label("Reason for denial:"), reason, submit);
         dlg.setScene(new Scene(box, 360, 220));
         dlg.show();
     }
 
     /**
      * Opens a window listing all saved forms (except the current one),
      * allows the user to pick one, and swaps it into view.
      */
     private void showFormSelector(Stage owner) {
         Stage dlg = new Stage();
         dlg.initOwner(owner);
         dlg.setTitle("Select Case");
 
         List<Pair<String, GenealogyRequestForm>> all = fetchFormsFromDB();
         all.removeIf(p -> p.getKey().equals(currentFormId));
 
         ObservableList<Pair<String, GenealogyRequestForm>> items =
             FXCollections.observableArrayList(all);
         ListView<Pair<String, GenealogyRequestForm>> listView = new ListView<>(items);
         listView.setCellFactory(lv -> new ListCell<>() {
             @Override protected void updateItem(Pair<String, GenealogyRequestForm> item, boolean empty) {
                 super.updateItem(item, empty);
                 setText(empty || item == null ? null : item.getValue().toString());
             }
         });
 
         Button select = new Button("Select");
         select.setOnAction(e -> {
             var sel = listView.getSelectionModel().getSelectedItem();
             if (sel != null) {
                 currentFormId = sel.getKey();
                 requestForm   = sel.getValue();
                 refreshDisplay();
                 dlg.close();
             }
         });
 
         VBox box = new VBox(10, listView, select);
         box.setPadding(new Insets(10));
         dlg.setScene(new Scene(box, 400, 600));
         dlg.show();
     }
 
     /**
      * Queries OD_FormData table to build a list of (formId, formObject) pairs.
      */
     private List<Pair<String, GenealogyRequestForm>> fetchFormsFromDB() {
         List<Pair<String, GenealogyRequestForm>> result = new ArrayList<>();
         String sql = """
             SELECT form_id, requester_name, requester_address, requester_ssn,
                    deceased_name, deceased_address, death_date,
                    country_of_origin,
                    proof_of_relationship_path, death_record_path
               FROM OD_FormData
         """;
         try (Connection conn = GenealogyFormDatabase.getConnection();
              PreparedStatement stmt = conn.prepareStatement(sql);
              ResultSet rs = stmt.executeQuery()) {
             while (rs.next()) {
                 GenealogyRequestForm form = new GenealogyRequestForm(
                     rs.getString("requester_name"),
                     rs.getString("requester_address"),
                     rs.getString("requester_ssn"),
                     rs.getString("deceased_name"),
                     rs.getString("deceased_address"),
                     new Date(rs.getDate("death_date").getTime()),
                     rs.getString("country_of_origin"),
                     new File(rs.getString("proof_of_relationship_path")),
                     new File(rs.getString("death_record_path"))
                 );
                 result.add(new Pair<>(rs.getString("form_id"), form));
             }
         } catch (SQLException e) {
             e.printStackTrace();
         }
         return result;
     }
 
     public static void main(String[] args) {
         launch(args);
     }
 } 