package edu.gmu.cs321;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

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

    private Date convertToDate(LocalDate localDate) {
    return java.sql.Date.valueOf(localDate);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Genealogy Records Request");

        VBox root = new VBox(20);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #1e1e1e;");

        // Section: Requester Information
        TitledPane requesterPane = createRequesterInfoSection();
        TitledPane deceasedPane = createDeceasedInfoSection();
        TitledPane recordsPane = createSupportingRecordsSection();

        // Buttons
        HBox buttonBox = new HBox(10);
        Button saveButton = new Button("Save Progress");
        Button submitButton = new Button("Submit");

        saveButton.setStyle("-fx-background-color: #555555; -fx-text-fill: white;");
        submitButton.setStyle("-fx-background-color: #b22222; -fx-text-fill: white;");

        buttonBox.getChildren().addAll(saveButton, submitButton);

        root.getChildren().addAll(requesterPane, deceasedPane, recordsPane, buttonBox);

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();

        

        submitButton.setOnAction(e -> {
            GenealogyRequestForm form = new GenealogyRequestForm(
                requesterNameField.getText(),
                requesterAddressField.getText(),
                requesterSSNField.getText(),
                deceasedNameField.getText(),
                deceasedAddressField.getText(),
                convertToDate(deathDatePicker.getValue()),
                countryBox.getValue(),
                proofOfRelationshipFile,
                deathRecordFile

            );
        
            reviewerQueue.add(form);
            System.out.println("Form submitted and added to reviewer queue.");
            System.out.println("Current Queue Size: " + reviewerQueue.size());
            System.out.println("Last Added: " + form);
        });
    }

    private TitledPane createRequesterInfoSection() {
        GridPane grid = createGrid();

        requesterNameField = new TextField();
        requesterAddressField = new TextField();
        requesterSSNField = new TextField();

        addToGrid(grid, "Full Name:", requesterNameField, 0);
        addToGrid(grid, "Address:", requesterAddressField, 1);
        addToGrid(grid, "SSN:", requesterSSNField, 2);

        TitledPane pane = new TitledPane("Requester Information", grid);
        pane.setExpanded(true);
        return pane;
    }

    private TitledPane createDeceasedInfoSection() {
        GridPane grid = createGrid();

        deceasedNameField = new TextField();
        deceasedAddressField = new TextField();
        deathDatePicker = new DatePicker();
        countryBox = new ComboBox<>();
        countryBox.getItems().addAll("Select Country", "Mexico", "Canada", "UK", "Other");
        countryBox.getSelectionModel().selectFirst();

        addToGrid(grid, "Full Name:", deceasedNameField, 0);
        addToGrid(grid, "Address:", deceasedAddressField, 1);
        addToGrid(grid, "Date of Death:", deathDatePicker, 2);
        addToGrid(grid, "Country of Origin:", countryBox, 3);

        TitledPane pane = new TitledPane("Deceased Information", grid);
        countryBox.setStyle("-fx-background-color:rgb(154, 136, 136); -fx-text-fill: white;");
        pane.setExpanded(true);
        return pane;
    }

    private TitledPane createSupportingRecordsSection() {
        GridPane grid = createGrid();

        Button proofButton = new Button("Attach...");
        Button deathButton = new Button("Attach...");

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Image and PDF Files", "*.png", "*.jpg", "*.jpeg", "*.pdf")
        );

        proofButton.setOnAction(e -> {
            File selectedFile = fileChooser.showOpenDialog(null);
            if (selectedFile != null) {
                proofOfRelationshipFile = selectedFile;
                proofButton.setText("Attached ✔");
            }
        });

        deathButton.setOnAction(e -> {
            File selectedFile = fileChooser.showOpenDialog(null);
            if (selectedFile != null) {
                deathRecordFile = selectedFile;
                deathButton.setText("Attached ✔");
            }
        });

        addToGrid(grid, "Proof of Relationship:", proofButton, 0);
        addToGrid(grid, "Death Record:", deathButton, 1);

        TitledPane pane = new TitledPane("Supporting Records", grid);
        pane.setExpanded(true);
        return pane;
    }

    private GridPane createGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10));
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(30);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(70);
        grid.getColumnConstraints().addAll(col1, col2);
        return grid;
    }

    private void addToGrid(GridPane grid, String label, Control field, int row) {
        Label lbl = new Label(label);
        lbl.setStyle("-fx-text-fill:rgb(46, 43, 43);");
        field.setStyle("-fx-background-color: #2c2c2c; -fx-text-fill: white;");
        grid.add(lbl, 0, row);
        grid.add(field, 1, row);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
