package org.example.pathfinder.Service;

import org.example.pathfinder.Model.ApplicationService;
import org.example.pathfinder.App.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;

public class ApplicationServiceService implements Services<ApplicationService> {
    private final Connection cnx;

    public ApplicationServiceService() {
        this.cnx = DatabaseConnection.getInstance().getCnx();
    }

    // Before adding the ApplicationService, verify if id_service exists
    public boolean isServiceExists(int Idservice) {
        String query = "SELECT COUNT(*) FROM serviceoffre WHERE id_service = ?"; // Use your actual service table name here

        try (PreparedStatement stmt = cnx.prepareStatement(query)) {
            stmt.setInt(1, Idservice);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error checking service existence: " + e.getMessage());
        }

        return false;
    }

    // ✅ Add an ApplicationService from the UI form
    public void add(ApplicationService app) {
        // Ensure the id_service exists before inserting
        if (!isServiceExists(app.getIdService())) {
            throw new RuntimeException("The specified service ID does not exist in the service table.");
        }

        String req = "INSERT INTO applicationservice (price_offre, id_user, status, id_service) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stm = cnx.prepareStatement(req, Statement.RETURN_GENERATED_KEYS)) {
            stm.setDouble(1, app.getPriceOffre());
            stm.setInt(2, app.getIdUser());
            stm.setString(3, app.getStatus());
            stm.setInt(4, app.getIdService());

            int affectedRows = stm.executeUpdate();
            if (affectedRows > 0) {
                ResultSet generatedKeys = stm.getGeneratedKeys();
                if (generatedKeys.next()) {
                    app.setIdApplication(generatedKeys.getInt(1)); // Set the generated ID into the ApplicationService object
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error adding application: " + e.getMessage());
        }
    }


    // ✅ Fetch all application services
    public List<ApplicationService> getAll() {
        List<ApplicationService> apps = new ArrayList<>();
        String req = "SELECT * FROM applicationservice";

        try (Statement stm = cnx.createStatement();
             ResultSet rs = stm.executeQuery(req)) {

            while (rs.next()) {
                apps.add(mapResultSetToApplicationService(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching applications: " + e.getMessage());
        }
        return apps;
    }

    // ✅ Fetch applications by ServiceOffre ID
    public List<ApplicationService> getApplicationsByService(int serviceId) {
        List<ApplicationService> apps = new ArrayList<>();
        String req = "SELECT * FROM applicationservice WHERE id_service = ?";

        try (PreparedStatement stm = cnx.prepareStatement(req)) {
            stm.setInt(1, serviceId);
            ResultSet rs = stm.executeQuery();

            while (rs.next()) {
                apps.add(mapResultSetToApplicationService(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching applications for service ID " + serviceId + ": " + e.getMessage());
        }
        return apps;
    }

    // ✅ Update the status of an application (e.g., Accept/Reject)
    public void update(ApplicationService app) {
        String req = "UPDATE applicationservice SET status = ?, price_offre = ? WHERE id_app = ?";

        try (PreparedStatement stm = cnx.prepareStatement(req)) {
            stm.setString(1, app.getStatus());
            stm.setDouble(2, app.getPriceOffre());
            stm.setInt(3, app.getIdApplication());
            stm.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating application: " + e.getMessage());
        }
    }

    // ✅ Delete an application
    public void delete(int id) {
        String req = "DELETE FROM applicationservice WHERE id_app = ?";

        try (PreparedStatement stm = cnx.prepareStatement(req)) {
            stm.setInt(1, id);
            stm.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting application: " + e.getMessage());
        }
    }

    // ✅ Fetch a single application by ID
    public ApplicationService getById(int id) {
        String query = "SELECT * FROM applicationservice WHERE id_app = ?";

        try (PreparedStatement preparedStatement = cnx.prepareStatement(query)) {
            preparedStatement.setInt(1, id);
            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next()) {
                return mapResultSetToApplicationService(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching application by ID: " + e.getMessage());
        }
        return null;
    }

    // ✅ Map ResultSet data to an ApplicationService object
    private ApplicationService mapResultSetToApplicationService(ResultSet rs) throws SQLException {
        return new ApplicationService(
                rs.getInt("id_app"),
                rs.getDouble("price_offre"),
                rs.getInt("id_user"),
                rs.getString("status"),
                rs.getInt("id_service")
        );
    }

    public void saveApplication(ApplicationService application) {
        String sql = "INSERT INTO application_service (service_id, status) VALUES (?, ?)";

        try (PreparedStatement stmt = cnx.prepareStatement(sql)) {
            stmt.setInt(1, application.getIdService());  // Set foreign key
            stmt.setString(2, application.getStatus());  // Set status as "Pending"
            stmt.executeUpdate(); // Execute query

            System.out.println("Application saved successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
