package org.example.pathfinder.Service;

import org.example.pathfinder.Model.ServiceOffre;
import org.example.pathfinder.App.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServiceOffreService implements Services<ServiceOffre> {
    private Connection cnx;

    public ServiceOffreService() {
        this.cnx = DatabaseConnection.getInstance().getCnx();
    }

    @Override
    public void add(ServiceOffre serviceOffre) {
        String req = "INSERT INTO serviceoffre (id_user, title, description, date_posted, field, price, required_education, skills, experience_level) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stm = cnx.prepareStatement(req)) {
            stm.setInt(1, serviceOffre.getId_user());
            stm.setString(2, serviceOffre.getTitle());
            stm.setString(3, serviceOffre.getDescription());
            stm.setTimestamp(4, serviceOffre.getDatePostedAsTimestamp());  // ✅ Convert LocalDateTime → SQL Timestamp
            stm.setString(5, serviceOffre.getField());
            stm.setDouble(6, serviceOffre.getPrice());

            stm.setString(8, serviceOffre.getRequired_education());
            stm.setString(9, serviceOffre.getSkills());
            stm.setString(10, serviceOffre.getExperience_level()); // ✅ Added Experience Level

            stm.setString(12, serviceOffre.getDuration());
            stm.executeUpdate();
            System.out.println("✅ Service added successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(ServiceOffre serviceOffre) {
        String req = "UPDATE serviceoffre SET title = ?, description = ?, date_posted = ?, field = ?, price = ?,  required_education = ?, skills = ?, experience_level = ? WHERE id_service = ?";

        try (PreparedStatement stm = cnx.prepareStatement(req)) {
            stm.setString(1, serviceOffre.getTitle());
            stm.setString(2, serviceOffre.getDescription());
            stm.setTimestamp(3, serviceOffre.getDatePostedAsTimestamp());  // ✅ Convert LocalDateTime → Timestamp
            stm.setString(4, serviceOffre.getField());
            stm.setDouble(5, serviceOffre.getPrice());

            stm.setString(7, serviceOffre.getRequired_education());
            stm.setString(8, serviceOffre.getSkills());
            stm.setString(9, serviceOffre.getExperience_level()); // ✅ Added Experience Level
            stm.setInt(10, serviceOffre.getId_service());

            stm.setString(12, serviceOffre.getDuration());

            stm.executeUpdate();
            System.out.println("✅ Service updated successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(int id) {
        String req = "DELETE FROM serviceoffre WHERE id_service = ?";

        try (PreparedStatement stm = cnx.prepareStatement(req)) {
            stm.setInt(1, id);
            int rowsAffected = stm.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("✅ Service deleted successfully!");
            } else {
                System.out.println("⚠ No service found with the given ID.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ServiceOffre getById(int id) {
        String req = "SELECT * FROM serviceoffre WHERE id_service = ?";
        ServiceOffre serviceOffre = null;

        try (PreparedStatement stm = cnx.prepareStatement(req)) {
            stm.setInt(1, id);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                serviceOffre = mapResultSetToService(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return serviceOffre;
    }

    @Override
    public List<ServiceOffre> getAll() {
        return getServicesByQuery("SELECT * FROM serviceoffre ORDER BY date_posted DESC");
    }

    public List<ServiceOffre> getAllSortedByPrice() {
        return getServicesByQuery("SELECT * FROM serviceoffre ORDER BY price ASC");
    }

    public List<ServiceOffre> getAllSortedByPriceDesc() {
        return getServicesByQuery("SELECT * FROM serviceoffre ORDER BY price DESC");
    }

    public List<ServiceOffre> getAllSortedByDate() {
        return getServicesByQuery("SELECT * FROM serviceoffre ORDER BY date_posted ASC");
    }

    public List<ServiceOffre> getAllSortedByDateDesc() {
        return getServicesByQuery("SELECT * FROM serviceoffre ORDER BY date_posted DESC");
    }

    private List<ServiceOffre> getServicesByQuery(String query) {
        List<ServiceOffre> services = new ArrayList<>();

        try (Statement stm = cnx.createStatement();
             ResultSet rs = stm.executeQuery(query)) {

            while (rs.next()) {
                services.add(mapResultSetToService(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return services;
    }

    private ServiceOffre mapResultSetToService(ResultSet rs) throws SQLException {
        ServiceOffre service = new ServiceOffre();
        service.setId_service(rs.getInt("id_service"));
        service.setTitle(rs.getString("title"));
        service.setDescription(rs.getString("description"));

        Timestamp timestamp = rs.getTimestamp("date_posted");
        if (timestamp != null) {
            service.setDate_posted(timestamp.toLocalDateTime()); // ✅ Convert back to LocalDateTime
        }
        service.setField(rs.getString("field"));
        service.setPrice(rs.getDouble("price"));

        service.setRequired_education(rs.getString("required_education"));
        service.setSkills(rs.getString("skills"));
        service.setExperience_level(rs.getString("experience_level"));

        service.setDuration(rs.getString("duration"));
        return service;
    }

    public Map<String, Double> getRevenueBreakdownForFreelancer(int freelancerId) {
        Map<String, Double> revenueMap = new HashMap<>();
        String query = "SELECT so.title AS service_name, SUM(a.price_offre) AS total_earnings " +
                "FROM applicationservice a " +
                "JOIN serviceoffre so ON a.id_service = so.id_service " +
                "WHERE a.status = 'Accepted' AND so.id_user = ? " +
                "GROUP BY so.title";

        if (DatabaseConnection.getInstance().isConnectionClosed()) {
            System.out.println("⚠ Database connection was closed. Reconnecting...");
            DatabaseConnection.getInstance();
        }

        try (PreparedStatement stm = DatabaseConnection.getInstance().getCnx().prepareStatement(query)) {
            stm.setInt(1, freelancerId);
            ResultSet rs = stm.executeQuery();

            while (rs.next()) {
                String serviceName = rs.getString("service_name");
                double totalEarnings = rs.getDouble("total_earnings");
                System.out.println("📊 Service: " + serviceName + " | Earnings: " + totalEarnings);
                revenueMap.put(serviceName, totalEarnings);
            }

        } catch (SQLException e) {
            throw new RuntimeException("❌ Error fetching revenue breakdown: " + e.getMessage());
        }
        return revenueMap;
    }


    public void updateServiceStatus(int serviceId, String status) {
        String query = "UPDATE serviceoffre SET status = ? WHERE id_service = ?";
        try (PreparedStatement stmt = cnx.prepareStatement(query)) {
            stmt.setString(1, status);
            stmt.setInt(2, serviceId);
            stmt.executeUpdate();

            System.out.println("✅ Service ID " + serviceId + " is now " + status);
        } catch (SQLException e) {
            throw new RuntimeException("Error updating service status: " + e.getMessage());
        }
    }

}

