package edu.gmu.cs321;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.File;
import java.sql.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.cs321.Workflow;

public class ReviewScreen extends Application {
    private TextField requesterName;
    private TextField requesterAddress;
    private TextField requesterSSN;
    private TextField deceasedName;
    private TextField deceasedAddress;
    private DatePicker dateOfDeath;
    private TextField countryOfOrigin;
    private TextField proofOfRelationshipFile;
    private TextField deathRecordFile;
    private Label messageLabel;

    private List<Pair<String, GenealogyRequestForm>> forms;
    private int currentIndex = 0;
    private String currentFormId;

    @Override
    public void start(Stage primaryStage) {
        forms = fetchFormsFromDB();
        if (forms.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "No forms available to review.");
            alert.showAndWait();
            return;
        }

        requesterName = new TextField();
        requesterAddress = new TextField();
        requesterSSN = new TextField();
        deceasedName = new TextField();
        deceasedAddress = new TextField();
        dateOfDeath = new DatePicker();
        countryOfOrigin = new TextField();
        proofOfRelationshipFile = new TextField();
        deathRecordFile = new TextField();
        messageLabel = new Label();

        populateFields(forms.get(currentIndex));

        Button sendButton = new Button("Send to Approver");
        sendButton.setOnAction(e -> {
            saveFormEdits(currentFormId);
            if (sendToApprover(currentFormId)) {
                messageLabel.setText("Form sent to approval.");
            } else {
                messageLabel.setText("Failed to send form.");
            }
        });

        Button nextButton = new Button("Next");
        nextButton.setOnAction(e -> {
            if (currentIndex + 1 < forms.size()) {
                currentIndex++;
                populateFields(forms.get(currentIndex));
                messageLabel.setText("");
            }
        });

        VBox vbox = new VBox(10,
            new Label("Requester Name"), requesterName,
            new Label("Requester Address"), requesterAddress,
            new Label("Requester SSN"), requesterSSN,
            new Label("Deceased Name"), deceasedName,
            new Label("Deceased Address"), deceasedAddress,
            new Label("Date of Death"), dateOfDeath,
            new Label("Country of Origin"), countryOfOrigin,
            new Label("Proof of Relationship File"), proofOfRelationshipFile,
            new Label("Death Record File"), deathRecordFile,
            new HBox(10, sendButton, nextButton),
            messageLabel
        );

        vbox.setAlignment(Pos.CENTER);
        vbox.setStyle("-fx-padding: 20; -fx-background-color: #f4f4f4;");

        primaryStage.setScene(new Scene(vbox, 600, 600));
        primaryStage.setTitle("Review Screen");
        primaryStage.show();
    }

    private void populateFields(Pair<String, GenealogyRequestForm> pair) {
        currentFormId = pair.getKey();
        GenealogyRequestForm form = pair.getValue();

        requesterName.setText(form.getRequesterName());
        requesterAddress.setText(form.getRequesterAddress());
        requesterSSN.setText(form.getRequesterSSN());
        deceasedName.setText(form.getDeceasedName());
        deceasedAddress.setText(form.getDeceasedAddress());
        dateOfDeath.setValue(form.getDateOfDeath().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        countryOfOrigin.setText(form.getCountryOfOrigin());
        proofOfRelationshipFile.setText(form.getProofOfRelationshipFile().getPath());
        deathRecordFile.setText(form.getDeathRecordFile().getPath());
    }

    private List<Pair<String, GenealogyRequestForm>> fetchFormsFromDB() {
        List<Pair<String, GenealogyRequestForm>> result = new ArrayList<>();
        String sql = """
            SELECT f.form_id, f.requester_name, f.requester_address, f.requester_ssn,
                   f.deceased_name, f.deceased_address, f.death_date,
                   f.country_of_origin, f.proof_of_relationship_path, f.death_record_path
              FROM OD_FormData f
              JOIN workflow_db.workflow_records w ON f.form_id = w.form_id
             WHERE w.next_step = 'Review'
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

    private void saveFormEdits(String formId) {
        String sql = """
            UPDATE OD_FormData SET
                requester_name = ?, requester_address = ?, requester_ssn = ?,
                deceased_name = ?, deceased_address = ?, death_date = ?,
                country_of_origin = ?, proof_of_relationship_path = ?, death_record_path = ?
            WHERE form_id = ?
        """;
    
        try (Connection conn = GenealogyFormDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
    
            stmt.setString(1, requesterName.getText());
            stmt.setString(2, requesterAddress.getText());
            stmt.setString(3, requesterSSN.getText());
            stmt.setString(4, deceasedName.getText());
            stmt.setString(5, deceasedAddress.getText());
            stmt.setDate(6, java.sql.Date.valueOf(dateOfDeath.getValue()));
            stmt.setString(7, countryOfOrigin.getText());
            stmt.setString(8, proofOfRelationshipFile.getText());
            stmt.setString(9, deathRecordFile.getText());
            stmt.setString(10, formId);
    
            stmt.executeUpdate();
            System.out.println("Form updates saved to database.");
    
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private boolean sendToApprover(String formId) {
    try {
        Workflow workflow = new Workflow();

        // First, delete the current "Review" step
        try (Connection conn = GenealogyFormDatabase.getConnection();
             PreparedStatement deleteStmt = conn.prepareStatement(
                 "DELETE FROM workflow_db.workflow_records WHERE form_id = ? AND next_step = 'Review'")) {
            deleteStmt.setString(1, formId);
            deleteStmt.executeUpdate();
        }

        // Then, add a new workflow step for "Approve"
        int result = workflow.AddWFItem(Integer.parseInt(formId), "Approve");
        workflow.closeConnection();

        return result == 0;
    } catch (Exception e) {
        e.printStackTrace();
        return false;
    }
}


    public static void main(String[] args) {
        launch(args);
    }
}

