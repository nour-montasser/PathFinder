package org.example.pathfinder.Service;

import org.example.pathfinder.Model.JobOffer;
import org.example.pathfinder.App.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JobOfferService implements Services<JobOffer> {
    private Connection cnx;

    public JobOfferService() {
        cnx = DatabaseConnection.getInstance().getCnx();
    }

    @Override
    public void add(JobOffer jobOffer) {
        String req = "INSERT INTO Job_offer (id_user, title, description, datePosted, type, number_of_spots, required_education, required_experience, skills) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement stm = cnx.prepareStatement(req);
            stm.setLong(1, jobOffer.getIdUser());
            stm.setString(2, jobOffer.getTitle());
            stm.setString(3, jobOffer.getDescription());
            stm.setTimestamp(4, jobOffer.getDatePosted());
            stm.setString(5, jobOffer.getType());
            stm.setInt(6, jobOffer.getNumberOfSpots());
            stm.setString(7, jobOffer.getRequiredEducation());
            stm.setString(8, jobOffer.getRequiredExperience());
            stm.setString(9, jobOffer.getSkills());
            stm.executeUpdate();
            System.out.println("Job offer added successfully.");
        } catch (SQLException e) {
            throw new RuntimeException("Error adding job offer: " + e.getMessage(), e);
        }
    }

    @Override
    public void update(JobOffer jobOffer) {
        String req = "UPDATE Job_offer SET title = ?, description = ?, type = ?, number_of_spots = ?, required_education = ?, required_experience = ?, skills = ? WHERE id_offer = ?";
        try {
            PreparedStatement stm = cnx.prepareStatement(req);
            stm.setString(1, jobOffer.getTitle());
            stm.setString(2, jobOffer.getDescription());
            stm.setString(3, jobOffer.getType());
            stm.setInt(4, jobOffer.getNumberOfSpots());
            stm.setString(5, jobOffer.getRequiredEducation());
            stm.setString(6, jobOffer.getRequiredExperience());
            stm.setString(7, jobOffer.getSkills());
            stm.setLong(8, jobOffer.getIdOffer());
            stm.executeUpdate();
            System.out.println("Job offer modified successfully.");
        } catch (SQLException e) {
            throw new RuntimeException("Error modifying job offer: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(JobOffer jobOffer) {
        String req = "DELETE FROM Job_offer WHERE id_offer = ?";
        try {
            PreparedStatement stm = cnx.prepareStatement(req);
            stm.setLong(1, jobOffer.getIdOffer());
            stm.executeUpdate();
            System.out.println("Job offer deleted successfully.");
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting job offer: " + e.getMessage(), e);
        }
    }

    @Override
    public List<JobOffer> getall() {
        List<JobOffer> jobOffers = new ArrayList<>();
        String req = "SELECT * FROM Job_offer";
        try {
            Statement stm = cnx.createStatement();
            ResultSet rs = stm.executeQuery(req);
            while (rs.next()) {
                JobOffer jobOffer = new JobOffer(
                        rs.getLong("id_user"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getString("type"),
                        rs.getInt("number_of_spots"),
                        rs.getString("required_education"),
                        rs.getString("required_experience"),
                        rs.getString("skills")
                );
                jobOffer.setIdOffer(rs.getLong("id_offer"));
                jobOffer.setDatePosted(rs.getTimestamp("datePosted"));
                jobOffers.add(jobOffer);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving job offers: " + e.getMessage(), e);
        }
        return jobOffers;
    }

    @Override
    public JobOffer getone() {
        String req = "SELECT * FROM Job_offer LIMIT 1";
        try {
            Statement stm = cnx.createStatement();
            ResultSet rs = stm.executeQuery(req);
            if (rs.next()) {
                JobOffer jobOffer = new JobOffer(
                        rs.getLong("id_user"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getString("type"),
                        rs.getInt("number_of_spots"),
                        rs.getString("required_education"),
                        rs.getString("required_experience"),
                        rs.getString("skills")
                );
                jobOffer.setIdOffer(rs.getLong("id_offer"));
                jobOffer.setDatePosted(rs.getTimestamp("datePosted"));
                return jobOffer;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving job offer: " + e.getMessage(), e);
        }
        return null;
    }
}
