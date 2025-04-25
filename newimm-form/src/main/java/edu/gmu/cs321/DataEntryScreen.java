package org.openjfx;

import com.cs321.Workflow;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class DataEntryScreen extends Application {

    private TextField requesterNameField;
    private TextField requesterAddressField;
    private TextField requesterSSNField;

    private TextField deceasedNameField;
    private TextField deceasedAddressField;
    private DatePicker deathDatePicker;
    private ComboBox<String> countryBox;
    private File proofOfRelationshipFile;
    private File deathRecordFile;

    private final List<GenealogyRequestForm> reviewerQueue = new ArrayList<>();

    private boolean validateForm() {
        if (requesterNameField.getText().isEmpty() ||
            requesterAddressField.getText().isEmpty() ||
            requesterSSNField.getText().isEmpty() ||
            deceasedNameField.getText().isEmpty() ||
            deceasedAddressField.getText().isEmpty() ||
            deathDatePicker.getValue() == null ||
            countryBox.getValue() == null ||
            proofOfRelationshipFile == null ||
            deathRecordFile == null) {
            
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Form Incomplete");
            alert.setHeaderText("Missing Required Fields");
            alert.setContentText("Please fill out all fields and upload all required files before submitting.");
            alert.showAndWait();
            
            return false;
        }
        return true;
    }

    private List<String> getAllCountries() {
    List<String> countries = new ArrayList<>();
    String[] locales = Locale.getISOCountries();
    for (String countryCode : locales) {
        Locale obj = new Locale("", countryCode);
        String countryName = obj.getDisplayCountry();
        if (!countryName.equalsIgnoreCase("United States")) {
            countries.add(countryName);
        }
    }
    countries.sort(String::compareTo);
    return countries;
}

    

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Genealogy Records Request");

        VBox root = new VBox(20);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #1e1e1e;");

        TitledPane requesterPane = createRequesterInfoSection();
        TitledPane deceasedPane = createDeceasedInfoSection();
        TitledPane recordsPane = createSupportingRecordsSection();

        HBox buttonBox = new HBox(10);
        Button submitButton = new Button("Submit");
        submitButton.setStyle("-fx-background-color: #b22222; -fx-text-fill: white;");
        buttonBox.getChildren().addAll(submitButton);

        root.getChildren().addAll(requesterPane, deceasedPane, recordsPane, buttonBox);

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();

        submitButton.setOnAction(e -> {
            
            if (!validateForm()) {
                return;
            }

            GenealogyRequestForm form = new GenealogyRequestForm(
                requesterNameField.getText(),
                requesterAddressField.getText(),
                requesterSSNField.getText(),
                deceasedNameField.getText(),
                deceasedAddressField.getText(),
                deathDatePicker.getValue(),
                countryBox.getValue(),
                proofOfRelationshipFile,
                deathRecordFile
            );

            reviewerQueue.add(form);
            DatabaseUtil.insertForm(form);

            try {
                Workflow workflow = new Workflow();
                int formID = Math.abs(UUID.randomUUID().toString().hashCode());
                int result = workflow.AddWFItem(formID, "Review");
                System.out.println("Workflow Add Result: " + result);
                workflow.closeConnection();
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            System.out.println("Form submitted and added to reviewer queue.");
            System.out.println("Current Queue Size: " + reviewerQueue.size());
            System.out.println("Last Added: " + form);
        });
    }

    private TitledPane createRequesterInfoSection() {
        requesterNameField = new TextField();
        requesterAddressField = new TextField();
        requesterSSNField = new TextField();

        GridPane grid = createGrid();
        addToGrid(grid, "Name:", requesterNameField, 0);
        addToGrid(grid, "Address:", requesterAddressField, 1);
        addToGrid(grid, "SSN:", requesterSSNField, 2);

        return new TitledPane("Requester Info", grid);
    }

    private TitledPane createDeceasedInfoSection() {
        deceasedNameField = new TextField();
        deceasedAddressField = new TextField();
        deathDatePicker = new DatePicker();
        countryBox = new ComboBox<>();
        for (String country : getAllCountries()) {
            countryBox.getItems().add(country);
        }
        

        GridPane grid = createGrid();
        addToGrid(grid, "Full Name:", deceasedNameField, 0);
        addToGrid(grid, "Last Address:", deceasedAddressField, 1);
        addToGrid(grid, "Date of Death:", deathDatePicker, 2);
        addToGrid(grid, "Country of Origin:", countryBox, 3);

        return new TitledPane("Deceased Info", grid);
    }

    private TitledPane createSupportingRecordsSection() {
        Label relationshipLabel = new Label("No file selected");
        Button chooseRelationshipFile = new Button("Choose Proof of Relationship");
        chooseRelationshipFile.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            File selectedFile = fileChooser.showOpenDialog(null);
            if (selectedFile != null) {
                proofOfRelationshipFile = selectedFile;
                relationshipLabel.setText(selectedFile.getName());
            }
        });

        Label deathRecordLabel = new Label("No file selected");
        Button chooseDeathRecordFile = new Button("Choose Death Record");
        chooseDeathRecordFile.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            File selectedFile = fileChooser.showOpenDialog(null);
            if (selectedFile != null) {
                deathRecordFile = selectedFile;
                deathRecordLabel.setText(selectedFile.getName());
            }
        });

        VBox box = new VBox(10,
            new HBox(10, chooseRelationshipFile, relationshipLabel),
            new HBox(10, chooseDeathRecordFile, deathRecordLabel)
        );

        return new TitledPane("Supporting Records", box);
    }

    private GridPane createGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10));
        return grid;
    }

    private void addToGrid(GridPane grid, String label, Control field, int row) {
        Label l = new Label(label);
        l.setStyle("-fx-text-fill: black;");
        field.setStyle(
        "-fx-text-fill: black;" +
        "-fx-border-color: #ccc;" +
        "-fx-border-radius: 4;" +
        "-fx-background-color: white;"
        );
        grid.add(l, 0, row);
        grid.add(field, 1, row);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
