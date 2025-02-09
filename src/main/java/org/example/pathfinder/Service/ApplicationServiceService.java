package org.example.pathfinder.Service;

import org.example.pathfinder.Model.ApplicationService;
import org.example.pathfinder.App.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ApplicationServiceService {
    Connection cnx;

    public ApplicationServiceService() {
        this.cnx = DatabaseConnection.instance.getCnx();
    }

    public void add(ApplicationService app) {
        String req = "INSERT INTO application_service (price_offre, description, date_application, status) VALUES (?, ?, ?, ?)";

        try {
            PreparedStatement stm = cnx.prepareStatement(req);
            stm.setDouble(1, app.getPriceOffre());
            stm.setString(2, app.getDescription());
            stm.setString(3, app.getDateApplication());
            stm.setString(4, app.getStatus());
            stm.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<ApplicationService> getAll() {
        List<ApplicationService> apps = new ArrayList<>();
        String req = "SELECT * FROM application_service";

        try {
            Statement stm = cnx.createStatement();
            ResultSet rs = stm.executeQuery(req);

            while (rs.next()) {
                ApplicationService app = new ApplicationService(
                        rs.getInt("id_application"),
                        rs.getDouble("price_offre"),
                        rs.getString("description"),
                        rs.getString("date_application"),
                        rs.getString("status")
                );
                apps.add(app);
            }
            return apps;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void update(ApplicationService app) {
        String req = "UPDATE application_service SET status = ? WHERE id_application = ?";

        try {
            PreparedStatement stm = cnx.prepareStatement(req);
            stm.setString(1, app.getStatus());
            stm.setInt(2, app.getIdApplication());
            stm.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void delete(int id) {
        String req = "DELETE FROM application_service WHERE id_application = ?";

        try {
            PreparedStatement stm = cnx.prepareStatement(req);
            stm.setInt(1, id);
            stm.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
