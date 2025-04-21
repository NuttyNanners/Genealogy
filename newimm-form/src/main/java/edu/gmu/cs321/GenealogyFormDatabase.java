package edu.gmu.cs321;

import java.io.File;
import java.sql.*;
import java.util.Date;
import java.util.UUID;

public class GenealogyFormDatabase {
    private static final String URL = "jdbc:mysql://localhost:3306/cs321";
    private static final String USER = "root";
    private static final String PASSWORD = "#Addison989";

    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean insertFormData(GenealogyRequestForm form, String formId) {
        String insertOD = "INSERT INTO OD (form_id) VALUES (?)";
        String insertFormData = """
            INSERT INTO OD_FormData (
                form_id, requester_name, requester_address, requester_ssn,
                deceased_name, deceased_address, death_date, country_of_origin,
                proof_of_relationship_path, death_record_path
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = getConnection()) {
            if (conn == null)
                return false;

            conn.setAutoCommit(false);

            try (PreparedStatement odStmt = conn.prepareStatement(insertOD);
                 PreparedStatement dataStmt = conn.prepareStatement(insertFormData)) {

                odStmt.setString(1, formId);
                odStmt.executeUpdate();

                dataStmt.setString(1, formId);
                dataStmt.setString(2, form.getRequesterName());
                dataStmt.setString(3, form.getRequesterAddress());
                dataStmt.setString(4, form.getRequesterSSN());
                dataStmt.setString(5, form.getDeceasedName());
                dataStmt.setString(6, form.getDeceasedAddress());
                dataStmt.setDate(7, new java.sql.Date(form.getDateOfDeath().getTime()));
                dataStmt.setString(8, form.getCountryOfOrigin());
                dataStmt.setString(9, form.getProofOfRelationshipFile().getPath());
                dataStmt.setString(10, form.getDeathRecordFile().getPath());

                dataStmt.executeUpdate();
                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
            }

        } catch (SQLException e){
            e.printStackTrace();
        }

        return false;
    }

    public static GenealogyRequestForm getFormDataByID(String formId) {
        String query = "SELECT * FROM OD_FormData WHERE form_id = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, formId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new GenealogyRequestForm(
                    rs.getString("requester_name"),
                    rs.getString("requester_address"),
                    rs.getString("requester_ssn"),
                    rs.getString("deceased_name"),
                    rs.getString("deceased_address"),
                    rs.getDate("death_date"),
                    rs.getString("country_of_origin"),
                    new File(rs.getString("proof_of_relationship_path")),
                    new File(rs.getString("death_record_path"))
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        String basePath = System.getProperty("user.dir") + File.separator + "data";
        GenealogyRequestForm form = new GenealogyRequestForm(
            "Gorilla Marco",
            "123 Zootopia St",
            "223-35-9999",
            "Marco the Gorilla",
            "456 Zoonited Road",
            new java.util.Date(),
            "USA",
            new File(basePath, "gorilla.png"),
            new File(basePath, "deathRecord.pdf")
        );

        //String formId = UUID.randomUUID().toString();
        if (insertFormData(form, form.getFormID()))
            System.out.println("Form inserted with ID: " + form.getFormID());

        GenealogyRequestForm loaded = getFormDataByID(form.getFormID());
        if (loaded != null)
            System.out.println("Loaded form for: " + loaded.getRequesterName());
    }
}