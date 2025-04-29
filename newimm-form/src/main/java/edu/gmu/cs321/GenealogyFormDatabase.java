/**
 * GenealogyFormDatabase.java
 * Name: Addison Klein
 * G#01331326
 * CS321-009
 * Professor Steven Ernst
 * Spring 2025
 */

package edu.gmu.cs321;

import java.io.File;
import java.sql.*;

/**
 * Database helper for inserting forms and recording approvals/denials.
 */
public class GenealogyFormDatabase {
    //JDBC connection parameters
    private static final String URL      = "jdbc:mysql://localhost:3306/cs321";
    private static final String USER     = "root";
    private static final String PASSWORD = "";

    /**
     * Obtains a database connection.
     * @return Connection or null upon failure.
     */
    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); //Ensures driver is loaded
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Inserts new form record into OD and OD_FormData tables.
     * @param form the form data object
     * @param formId the unique form UUID string
     * @return true on success.
     */
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
            conn.setAutoCommit(false);  // Starts the transaction
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
                conn.commit();      //Commits both inserts
                return true;
            } catch (SQLException e) {
                conn.rollback();        //If error detected, rollback
                e.printStackTrace();
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Updates approval status and the denial reason in OD_FormData.
     * @param formId the target form ID
     * @param accepted true if accepted; false if denied
     * @param denialReason the stated reason for denial, if denied
     * @return true if successfully updated
     */
    public static boolean updateApproval(String formId, boolean accepted, String denialReason) {
        String sql;
        if (accepted)
            sql = "UPDATE OD_FormData SET accepted = ? WHERE form_id = ?";
        else
            sql = "UPDATE OD_FormData SET accepted = ?, denial_reason = ? WHERE form_id = ?";
    
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBoolean(1, accepted);
            if (accepted)
                stmt.setString(2, formId);
            else {
                stmt.setString(2, denialReason);
                stmt.setString(3, formId);
            }

            int rows = stmt.executeUpdate();    // Number of rows affected; exactly one means success
            System.out.println("updateApproval updated rows: " + rows);
            return rows == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Fetches the current approval status and denial reason of a given form.
     * @param formId the UUID of the form
     * @return ApprovalStatus object or null if error/not found
     */
    public static ApprovalStatus fetchApprovalStatus(String formId) {
        String sql = "SELECT accepted, denial_reason FROM OD_FormData WHERE form_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, formId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next())
                    return new ApprovalStatus(
                      rs.getBoolean("accepted"),
                      rs.getString("denial_reason")
                    );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Holds approval flags and denial reason.
     */
    public static class ApprovalStatus {
        private final boolean accepted;
        private final String  denialReason;
        public ApprovalStatus(boolean accepted, String denialReason) {
            this.accepted      = accepted;
            this.denialReason  = denialReason;
        }
        public boolean isAccepted()      { return accepted; }
        public String  getDenialReason() { return denialReason; }
    }
}
