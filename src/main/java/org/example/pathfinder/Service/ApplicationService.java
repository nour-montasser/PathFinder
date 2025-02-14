package org.example.pathfinder.Service;


import org.example.pathfinder.Model.ApplicationJob;
import org.example.pathfinder.App.DatabaseConnection;

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
        String req = "INSERT INTO Application_job (job_offer_id, id_user, date_application, status) VALUES (?, ?, ?, ?)";
        try {
            PreparedStatement stm = cnx.prepareStatement(req);
            stm.setLong(1, applicationJob.getJobOfferId());
            stm.setLong(2,1);
            stm.setTimestamp(3, applicationJob.getDateApplication());
            stm.setString(4, applicationJob.getStatus());
            stm.executeUpdate();
            System.out.println("Application added successfully.");
        } catch (SQLException e) {
            throw new RuntimeException("Error adding application: " + e.getMessage(), e);
        }
    }

    @Override
    public void update(ApplicationJob applicationJob) {
        String req = "UPDATE Application_job SET status = ? WHERE application_id = ?";
        try {
            PreparedStatement stm = cnx.prepareStatement(req);
            stm.setString(1, applicationJob.getStatus());
            stm.setLong(2, applicationJob.getApplicationId());
            stm.executeUpdate();
            System.out.println("Application updated successfully.");
        } catch (SQLException e) {
            throw new RuntimeException("Error updating application: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(ApplicationJob applicationJob) {
        String req = "DELETE FROM Application_job WHERE application_id = ?";
        try {
            PreparedStatement stm = cnx.prepareStatement(req);
            stm.setLong(1, applicationJob.getApplicationId());
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
                        rs.getLong("id_user")
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

    @Override
    public ApplicationJob getone() {
        String req = "SELECT * FROM Application_job LIMIT 1";
        try {
            Statement stm = cnx.createStatement();
            ResultSet rs = stm.executeQuery(req);
            if (rs.next()) {
                ApplicationJob applicationJob = new ApplicationJob(
                        rs.getLong("job_offer_id"),
                        rs.getLong("id_user")
                );
                applicationJob.setApplicationId(rs.getLong("application_id"));
                applicationJob.setDateApplication(rs.getTimestamp("date_application"));
                applicationJob.setStatus(rs.getString("status"));
                return applicationJob;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving application: " + e.getMessage(), e);
        }
        return null;
    }

    public List<ApplicationJob> getApplicationsForJobOffer(Long jobOfferId) throws SQLException {
        List<ApplicationJob> applications = new ArrayList<>();

        // SQL query to fetch applications where job_offer_id = jobOfferId and user_id = 1
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
                applications.add(application);
            }
        }

        return applications;
    }
}
