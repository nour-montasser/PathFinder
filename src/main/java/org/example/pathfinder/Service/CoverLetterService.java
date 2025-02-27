package org.example.pathfinder.Service;

import org.example.pathfinder.Model.CoverLetter;
import org.example.pathfinder.App.DatabaseConnection;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class CoverLetterService implements Services<CoverLetter> {

    private Connection cnx;
    //gregegegeg
    public CoverLetterService() {
        cnx = DatabaseConnection.getInstance().getCnx();
    }

    @Override
    public void add(CoverLetter coverLetter) {
        String req = "INSERT INTO CoverLetter (id_app, content, subject) VALUES (?, ?, ?)";
        try {
            PreparedStatement stm = cnx.prepareStatement(req);
            stm.setLong(1, coverLetter.getIdApp());
            stm.setString(2, coverLetter.getContent());
            stm.setString(3, coverLetter.getSubject());
            stm.executeUpdate();
            System.out.println("Cover letter added successfully.");
        } catch (SQLException e) {
            throw new RuntimeException("Error adding cover letter: " + e.getMessage(), e);
        }
    }


    @Override
    public void update(CoverLetter coverLetter) {
        String req = "UPDATE CoverLetter SET content = ?, subject = ? WHERE id_CoverLetter = ?";
        try {
            PreparedStatement stm = cnx.prepareStatement(req);
            stm.setString(1, coverLetter.getContent());
            stm.setString(2, coverLetter.getSubject());
            stm.setLong(3, coverLetter.getIdCoverLetter()   );
            stm.executeUpdate();
            System.out.println("Cover letter updated successfully.");
        } catch (SQLException e) {
            throw new RuntimeException("Error updating cover letter: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(long id) {
        String req = "DELETE FROM CoverLetter WHERE id_CoverLetter = ?";
        try {
            PreparedStatement stm = cnx.prepareStatement(req);
            stm.setLong(1, id);
            stm.executeUpdate();
            System.out.println("Cover letter deleted successfully.");
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting cover letter: " + e.getMessage(), e);
        }
    }

    @Override
    public List<CoverLetter> getall() {
        List<CoverLetter> coverLetters = new ArrayList<>();
        String req = "SELECT * FROM CoverLetter";
        try {
            Statement stm = cnx.createStatement();
            ResultSet rs = stm.executeQuery(req);
            while (rs.next()) {
                CoverLetter coverLetter = new CoverLetter(
                        rs.getLong("id_app"),
                        rs.getString("content"),
                        rs.getString("subject")
                );
                coverLetter.setIdCoverLetter(rs.getLong("id_CoverLetter"));
                coverLetters.add(coverLetter);
            }
            return coverLetters;
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving cover letters: " + e.getMessage(), e);
        }
    }

    public CoverLetter getById(Long id) {
        CoverLetter coverLetter = null;
        String req = "SELECT * FROM CoverLetter WHERE id_CoverLetter = ?";
        try {
            PreparedStatement stm = cnx.prepareStatement(req);
            stm.setLong(1, id);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                coverLetter = new CoverLetter(
                        rs.getLong("id_app"),
                        rs.getString("content"),
                        rs.getString("subject")
                );
                coverLetter.setIdCoverLetter(rs.getLong("id_CoverLetter"));
            }
            return coverLetter;
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving cover letter by ID: " + e.getMessage(), e);
        }
    }

    public CoverLetter getone() {
        CoverLetter coverLetter = null;
        String req = "SELECT * FROM CoverLetter LIMIT 1";  // Get the first cover letter (you can change the logic as needed)
        try {
            PreparedStatement stm = cnx.prepareStatement(req);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                coverLetter = new CoverLetter(
                        rs.getLong("id_app"),
                        rs.getString("content"),
                        rs.getString("subject")
                );
                coverLetter.setIdCoverLetter(rs.getLong("id_CoverLetter"));
            }
            return coverLetter;
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving cover letter: " + e.getMessage(), e);
        }
    }

    public CoverLetter getCoverLetterByApplication(Long applicationId) {
        // Fetch the cover letter for a given application ID from the database
        String query = "SELECT * FROM CoverLetter WHERE id_app = ?";
        CoverLetter coverLetter = null;

        try (PreparedStatement stmt = cnx.prepareStatement(query)) {
            stmt.setLong(1, applicationId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                coverLetter = new CoverLetter();
                coverLetter.setSubject(rs.getString("subject"));
                coverLetter.setContent(rs.getString("content"));
                coverLetter.setIdApp(rs.getLong("id_app"));
                coverLetter.setIdCoverLetter(rs.getLong("id_CoverLetter"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving CoverLetter: " + e.getMessage(), e);
        }

        return coverLetter;
    }


    public String getUserDataForCoverLetter(Long userId) {
        StringBuilder userData = new StringBuilder();

        // Query to get user profile and CV information
        String query = "SELECT " +
                "    u.id_user, " +
                "    u.name, " +
                "    u.email, " +
                "    u.password, " +
                "    u.role, " +
                "    p.address, " +
                "    p.birthday, " +
                "    p.phone, " +
                "    p.current_occupation, " +
                "    p.photo, " +
                "    p.bio, " +
                "    c.id_cv, " +
                "    c.title AS cv_title, " +
                "    c.introduction AS cv_introduction, " +
                "    c.languages AS cv_languages, " +
                "    c.date_creation AS cv_creation_date, " +
                "    e.id_experience, " +
                "    e.type AS experience_type, " +
                "    e.position AS experience_position, " +
                "    e.location_name AS experience_location, " +
                "    e.start_date AS experience_start_date, " +
                "    e.end_date AS experience_end_date " +
                "FROM " +
                "    app_user u " +
                "JOIN " +
                "    profile p ON u.id_user = p.id_user " +
                "JOIN " +
                "    CV c ON u.id_user = c.id_user " +
                "LEFT JOIN " +
                "    experience e ON c.id_cv = e.id_cv " +
                "WHERE " +
                "    u.id_user = ?";

        try (PreparedStatement stm = cnx.prepareStatement(query)) {
            stm.setLong(1, userId);
            ResultSet rs = stm.executeQuery();

            boolean hasData = false; // Flag to check if we have data

            // Loop through the result set and build the string
            while (rs.next()) {
                if (!hasData) {
                    hasData = true;
                    userData.append("Profile Information: \n")
                            .append("Address: ").append(rs.getString("address")).append("\n")
                            .append("Phone: ").append(rs.getString("phone")).append("\n")
                            .append("Current Occupation: ").append(rs.getString("current_occupation")).append("\n")
                            .append("Bio: ").append(rs.getString("bio")).append("\n\n");

                    // Adding CV Details
                    userData.append("CV Information: \n")
                            .append("CV Title: ").append(rs.getString("cv_title")).append("\n")
                            .append("Languages: ").append(rs.getString("cv_languages")).append("\n\n");
                }

                // Adding Experience Details (handling multiple experiences)
                String position = rs.getString("experience_position");
                if (position != null) { // Check if experience exists
                    userData.append("Experience: \n")
                            .append("Position: ").append(position).append("\n")
                            .append("Location: ").append(rs.getString("experience_location")).append("\n");

                    // Format date as yyyy-MM-dd
                    Date startDate = rs.getDate("experience_start_date");
                    Date endDate = rs.getDate("experience_end_date");
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

                    userData.append("Start Date: ").append(startDate != null ? sdf.format(startDate) : "N/A").append("\n")
                            .append("End Date: ").append(endDate != null ? sdf.format(endDate) : "N/A").append("\n\n");
                }
            }

            // If no data was found, return a default message
            if (!hasData) {
                userData.append("No profile or CV data found for the user.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return userData.toString();
    }

    public long getJobOfferIdByCoverLetterId(long coverLetterId) {
        long jobOfferId = -1;  // Default value indicating not found
        String query = "SELECT aj.job_offer_id FROM CoverLetter cl " +
                "JOIN Application_job aj ON cl.id_app = aj.application_id " +
                "WHERE cl.id_CoverLetter = ?";

        try (PreparedStatement statement = cnx.prepareStatement(query)) {
            statement.setLong(1, coverLetterId);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                jobOfferId = rs.getLong("job_offer_id");
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving job offer ID by cover letter ID: " + e.getMessage());
        }

        return jobOfferId;
    }
    public long getLatestCoverLetterId() {
        long coverLetterId = -1;  // Default value indicating not found
        String query = "SELECT id_CoverLetter FROM CoverLetter ORDER BY id_CoverLetter DESC LIMIT 1";

        try (PreparedStatement statement = cnx.prepareStatement(query)) {
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                coverLetterId = rs.getLong("id_CoverLetter");
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving the latest cover letter ID: " + e.getMessage());
        }

        return coverLetterId;
    }





}
