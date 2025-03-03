package org.example.pathfinder.Service;

import javafx.application.Application;
import org.example.pathfinder.Model.ApplicationJob;
import org.example.pathfinder.App.DatabaseConnection;
import org.example.pathfinder.Model.JobOffer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ApplicationService implements Services<ApplicationJob> {
    private Connection cnx;

    public ApplicationService() {
        cnx = DatabaseConnection.getInstance().getCnx();
    }


    @Override
    public void add(ApplicationJob applicationJob) {
        String req = "INSERT INTO Application_job (job_offer_id, id_user, date_application, status, cv_id) VALUES (?, ?, ?, ?, ?)";
        try {
            PreparedStatement stm = cnx.prepareStatement(req);
            stm.setLong(1, applicationJob.getJobOfferId());
            stm.setLong(2, applicationJob.getIdUser());
            stm.setTimestamp(3, applicationJob.getDateApplication());
            stm.setString(4, applicationJob.getStatus());
            stm.setLong(5, applicationJob.getCvId()); // Adding CV ID
            stm.executeUpdate();
            System.out.println("Application added successfully.");
        } catch (SQLException e) {
            throw new RuntimeException("Error adding application: " + e.getMessage(), e);
        }
    }

    @Override
    public void update(ApplicationJob applicationJob) {
        String req = "UPDATE Application_job SET status = ?, cv_id = ? WHERE application_id = ?";
        try {
            PreparedStatement stm = cnx.prepareStatement(req);
            stm.setString(1, applicationJob.getStatus());
            stm.setLong(2, applicationJob.getCvId()); // Update CV ID if necessary
            stm.setLong(3, applicationJob.getApplicationId());
            stm.executeUpdate();
            System.out.println("Application updated successfully.");
        } catch (SQLException e) {
            throw new RuntimeException("Error updating application: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(long id) {
        String req = "DELETE FROM Application_job WHERE application_id = ?";
        try {
            PreparedStatement stm = cnx.prepareStatement(req);
            stm.setLong(1, id);
            stm.executeUpdate();
            System.out.println("Application deleted successfully.");
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting application: " + e.getMessage(), e);
        }
    }

    @Override
    public List<ApplicationJob> getall() {
        List<ApplicationJob> applications = new ArrayList<>();
        String req = "SELECT * FROM Application_job";
        try {
            Statement stm = cnx.createStatement();
            ResultSet rs = stm.executeQuery(req);
            while (rs.next()) {
                ApplicationJob applicationJob = new ApplicationJob(
                        rs.getLong("job_offer_id"),
                        rs.getLong("id_user"),
                        rs.getLong("cv_id")
                );
                applicationJob.setApplicationId(rs.getLong("application_id"));
                applicationJob.setDateApplication(rs.getTimestamp("date_application"));
                applicationJob.setStatus(rs.getString("status"));
                applicationJob.setCvId(rs.getLong("cv_id")); // Retrieve CV ID
                applications.add(applicationJob);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving applications: " + e.getMessage(), e);
        }
        return applications;
    }

    @Override
    public ApplicationJob getone() {
        String req = "SELECT * FROM Application_job ORDER BY application_id DESC LIMIT 1";
        try {
            Statement stm = cnx.createStatement();
            ResultSet rs = stm.executeQuery(req);
            if (rs.next()) {
                ApplicationJob applicationJob = new ApplicationJob(
                        rs.getLong("job_offer_id"),
                        rs.getLong("id_user"),
                        rs.getLong("cv_id")
                );
                applicationJob.setApplicationId(rs.getLong("application_id"));
                applicationJob.setDateApplication(rs.getTimestamp("date_application"));
                applicationJob.setStatus(rs.getString("status"));
                applicationJob.setCvId(rs.getLong("cv_id")); // Retrieve CV ID
                return applicationJob;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving application: " + e.getMessage(), e);
        }
        return null;
    }

    public List<ApplicationJob> getApplicationsForJobOffer(Long jobOfferId) throws SQLException {
        List<ApplicationJob> applications = new ArrayList<>();

        // SQL query to fetch applications where job_offer_id = jobOfferId
        String query = "SELECT * FROM Application_job WHERE job_offer_id = ?";

        try (PreparedStatement stmt = cnx.prepareStatement(query)) {
            stmt.setLong(1, jobOfferId);
            ResultSet resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                ApplicationJob application = new ApplicationJob();
                application.setApplicationId(resultSet.getLong("application_id"));
                application.setJobOfferId(resultSet.getLong("job_offer_id"));
                application.setIdUser(resultSet.getLong("id_user"));
                application.setDateApplication(resultSet.getTimestamp("date_application"));
                application.setStatus(resultSet.getString("status"));
                application.setCvId(resultSet.getLong("cv_id")); // Retrieve CV ID
                applications.add(application);
            }
        }

        return applications;
    }

    public List<String> getUserCVTitles(Long userId) {
        List<String> cvTitleList = new ArrayList<>();
        String query = "SELECT title FROM CV WHERE id_user = ?";

        try (PreparedStatement stmt = cnx.prepareStatement(query)) {
            stmt.setLong(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                cvTitleList.add(rs.getString("title"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving CV titles: " + e.getMessage(), e);
        }
        return cvTitleList;
    }

    public List<Long> getUserCVIds(Long userId) {
        List<Long> cvIdList = new ArrayList<>();
        String query = "SELECT id_cv FROM CV WHERE id_user = ?";

        try (PreparedStatement stmt = cnx.prepareStatement(query)) {
            stmt.setLong(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                cvIdList.add(rs.getLong("id_cv"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving CV IDs: " + e.getMessage(), e);
        }
        return cvIdList;
    }

    public String getCVTitleById(Long cvId) {
        String cvTitle = "";
        String query = "SELECT title FROM CV WHERE id_cv = ?";

        try (PreparedStatement stmt = cnx.prepareStatement(query)) {
            stmt.setLong(1, cvId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                cvTitle = rs.getString("title");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving CV title: " + e.getMessage(), e);
        }

        return cvTitle;
    }

    public JobOffer getJobOfferById(Long jobOfferId) {
        JobOffer jobOffer = null;
        String query = "SELECT * FROM Job_offer WHERE id_offer = ?";

        try (PreparedStatement stmt = cnx.prepareStatement(query)) {
            stmt.setLong(1, jobOfferId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                jobOffer = new JobOffer();
                jobOffer.setIdOffer(rs.getLong("id_offer"));
                jobOffer.setTitle(rs.getString("title"));
                jobOffer.setIdUser(rs.getLong("id_user"));
                jobOffer.setDescription(rs.getString("description"));
                jobOffer.setDatePosted(rs.getTimestamp("datePosted"));
                jobOffer.setType(rs.getString("type"));
                jobOffer.setNumberOfSpots(rs.getInt("number_of_spots"));
                jobOffer.setRequiredEducation(rs.getString("required_education"));
                jobOffer.setRequiredExperience(rs.getString("required_experience"));
                jobOffer.setSkills(rs.getString("skills"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving JobOffer: " + e.getMessage(), e);
        }

        return jobOffer;
    }



    public List<ApplicationJob> getApplicationsForUser(Long userId) throws SQLException {
        List<ApplicationJob> applications = new ArrayList<>();

        // SQL query to fetch applications where job_offer_id = jobOfferId
        String query = "SELECT * FROM Application_job WHERE id_user = ?";

        try (PreparedStatement stmt = cnx.prepareStatement(query)) {
            stmt.setLong(1, userId);
            ResultSet resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                ApplicationJob application = new ApplicationJob();
                application.setApplicationId(resultSet.getLong("application_id"));
                application.setJobOfferId(resultSet.getLong("job_offer_id"));
                application.setIdUser(resultSet.getLong("id_user"));
                application.setDateApplication(resultSet.getTimestamp("date_application"));
                application.setStatus(resultSet.getString("status"));
                application.setCvId(resultSet.getLong("cv_id")); // Retrieve CV ID
                applications.add(application);
            }
        }

        return applications;
    }

    public ApplicationJob getApplicationByJobOfferAndUser(Long jobOfferId, Long userId) {
        ApplicationJob application = null;
        String query = "SELECT * FROM Application_job WHERE job_offer_id = ? AND id_user = ?";

        try (PreparedStatement statement = cnx.prepareStatement(query)) {
            statement.setLong(1, jobOfferId);
            statement.setLong(2, userId);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                application = new ApplicationJob(
                        resultSet.getLong("application_id"),
                        resultSet.getLong("job_offer_id"),
                        resultSet.getLong("id_user"),
                        resultSet.getLong("cv_id")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return application;
    }
    public String getUserNameById(Long userId) {
        String query = "SELECT name FROM app_user WHERE id_user = ?";
        try (PreparedStatement stmt = cnx.prepareStatement(query)) {

            stmt.setLong(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getString("name");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Unknown User";
    }
    public String getUserProfilePicture(Long userId) {
        String query = "SELECT image FROM app_user WHERE id_user = ?";
        try (
             PreparedStatement stmt = cnx.prepareStatement(query)) {

            stmt.setLong(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getString("photo"); // Return the photo path directly
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<ApplicationJob> getByStatus(String status) {
        List<ApplicationJob> applications = new ArrayList<>();
        String req = "SELECT * FROM Application_job";

        if (!status.equalsIgnoreCase("All")) {
            req += " WHERE status = ?";
        }

        try {
            PreparedStatement pst = cnx.prepareStatement(req);
            if (!status.equalsIgnoreCase("All")) {
                pst.setString(1, status);
            }

            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                ApplicationJob applicationJob = new ApplicationJob(
                        rs.getLong("job_offer_id"),
                        rs.getLong("id_user"),
                        rs.getLong("cv_id")
                );
                applicationJob.setApplicationId(rs.getLong("application_id"));
                applicationJob.setDateApplication(rs.getTimestamp("date_application"));
                applicationJob.setStatus(rs.getString("status"));
                applications.add(applicationJob);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving applications: " + e.getMessage(), e);
        }
        return applications;
    }

    public boolean hasUserAppliedForJob(JobOffer jobOffer, long userId) {
        String query = "SELECT COUNT(*) FROM Application_job a WHERE a.job_offer_id = ? AND a.id_user = ?";

        try (PreparedStatement stmt = cnx.prepareStatement(query)) {
            // Set the parameters for job offer ID and user ID
            stmt.setLong(1, jobOffer.getIdOffer());  // jobOfferId
            stmt.setLong(2, userId);  // userId

            // Execute the query
            ResultSet rs = stmt.executeQuery();

            // Check if the count is greater than 0 (meaning the user has applied)
            if (rs.next()) {
                return rs.getInt(1) > 0;  // Returns true if count is greater than 0
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;  // Return false if no result or error
    }

    public ApplicationJob getApplicationByUserAndSkillTest(long userId, long skillTestId) {
        ApplicationJob application = null;
        String query = "SELECT * FROM Application_job app " +
                "JOIN SkillTest st ON app.job_offer_id = st.id_job_offer " +
                "WHERE app.id_user = ? AND st.id_test = ?";

        try (PreparedStatement statement = cnx.prepareStatement(query)) {
            // Set parameters for userId and skillTestId
            statement.setLong(1, userId);
            statement.setLong(2, skillTestId);

            // Execute the query
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                // Map the result set to an Application object
                application = new ApplicationJob(
                        rs.getLong("application_id"),
                        rs.getLong("job_offer_id"),
                        rs.getLong("id_user"),
                        rs.getLong("cv_id"),
                        rs.getString("status")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving application by userId and skillTestId: " + e.getMessage());
        }

        return application;
    }




}
