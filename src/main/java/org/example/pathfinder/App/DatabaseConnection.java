package org.example.pathfinder.App;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private Connection cnx; // Make private
    private static DatabaseConnection instance; // Singleton instance

    public DatabaseConnection() {
        String url = "jdbc:mysql://localhost:3307/projet_3a"; // Database URL
        String username = "pma"; // Database username
        String password = ""; // Database password

        try {
            cnx = DriverManager.getConnection(url, username, password);
            System.out.println("Connexion établie"); // Connection success
        } catch (SQLException e) {
            System.err.println("Erreur de connexion à la base de données : " + e.getMessage());
            throw new RuntimeException(e); // Throw runtime exception for fatal error
        }
    }

    // Singleton method to get the instance
    public static DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    // Getter for the connection
    public Connection getCnx() {
        return cnx;
    }
}
