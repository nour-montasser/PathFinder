package org.example.pathfinder.Service;

import org.example.pathfinder.Model.ServiceOffre;
import org.example.pathfinder.App.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceOffreService implements Services<ServiceOffre> {
    private Connection cnx;

    public ServiceOffreService() {
        this.cnx = DatabaseConnection.getInstance().getCnx();
    }

    @Override
    public void add(ServiceOffre serviceOffre) {
        String req = "INSERT INTO serviceoffre (id_user, title, description, date_posted, field, price, required_experience, required_education, skills) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            PreparedStatement stm = cnx.prepareStatement(req);
            stm.setInt(1, serviceOffre.getId_user());  // Add user ID
            stm.setString(2, serviceOffre.getTitle());
            stm.setString(3, serviceOffre.getDescription());
            stm.setDate(4, serviceOffre.getDate_posted());
            stm.setString(5, serviceOffre.getField());
            stm.setDouble(6, serviceOffre.getPrice());
            stm.setString(7, serviceOffre.getRequired_experience());
            stm.setString(8, serviceOffre.getRequired_education());
            stm.setString(9, serviceOffre.getSkills());
            stm.executeUpdate();
            System.out.println("Service added successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(ServiceOffre serviceOffre) {
        String req = "UPDATE serviceoffre SET title = ?, description = ?, date_posted = ?, field = ?, price = ?, required_experience = ?, required_education = ?, skills = ? WHERE id_service = ?";

        try {
            PreparedStatement stm = cnx.prepareStatement(req);
            stm.setString(1, serviceOffre.getTitle());
            stm.setString(2, serviceOffre.getDescription());
            stm.setDate(3, serviceOffre.getDate_posted());
            stm.setString(4, serviceOffre.getField());
            stm.setDouble(5, serviceOffre.getPrice());
            stm.setString(6, serviceOffre.getRequired_experience());
            stm.setString(7, serviceOffre.getRequired_education());
            stm.setString(8, serviceOffre.getSkills());
            stm.setInt(9, serviceOffre.getId_service());
            stm.executeUpdate();
            System.out.println("Service updated successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(int id) {
        String req = "DELETE FROM serviceoffre WHERE id_service = ?";

        try {
            PreparedStatement stm = cnx.prepareStatement(req);
            stm.setInt(1, id);
            int rowsAffected = stm.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Service deleted successfully!");
            } else {
                System.out.println("No service found with the given ID.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ServiceOffre getById(int id) {
        String req = "SELECT * FROM serviceoffre WHERE id_service = ?";
        ServiceOffre serviceOffre = null;

        try {
            PreparedStatement stm = cnx.prepareStatement(req);
            stm.setInt(1, id);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                serviceOffre = new ServiceOffre();
                serviceOffre.setId_service(rs.getInt("id_service"));
                serviceOffre.setTitle(rs.getString("title"));
                serviceOffre.setDescription(rs.getString("description"));
                serviceOffre.setDate_posted(rs.getDate("date_posted"));
                serviceOffre.setField(rs.getString("field"));
                serviceOffre.setPrice(rs.getDouble("price"));
                serviceOffre.setRequired_experience(rs.getString("required_experience"));
                serviceOffre.setRequired_education(rs.getString("required_education"));
                serviceOffre.setSkills(rs.getString("skills"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return serviceOffre;
    }

    @Override
    public List<ServiceOffre> getAll() {
        List<ServiceOffre> services = new ArrayList<>();
        String req = "SELECT * FROM serviceoffre ORDER BY date_posted DESC";

        try {
            Statement stm = cnx.createStatement();
            ResultSet rs = stm.executeQuery(req);

            while (rs.next()) {
                ServiceOffre s = new ServiceOffre();
                s.setId_service(rs.getInt("id_service"));
                s.setTitle(rs.getString("title"));
                s.setDescription(rs.getString("description"));
                s.setDate_posted(rs.getDate("date_posted"));
                s.setField(rs.getString("field"));
                s.setPrice(rs.getDouble("price"));
                s.setRequired_experience(rs.getString("required_experience"));
                s.setRequired_education(rs.getString("required_education"));
                s.setSkills(rs.getString("skills"));
                services.add(s);
            }

            return services;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return services;
    }



}
