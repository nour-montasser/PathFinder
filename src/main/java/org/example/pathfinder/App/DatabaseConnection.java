package org.example.pathfinder.App;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static DatabaseConnection instance;
    private Connection cnx;

    private final String URL = "jdbc:mysql://localhost:3306/pathfinder";
    private final String USER = "root"; // Change this if needed
    private final String PASSWORD = ""; // Change this if needed

    private DatabaseConnection() {
        try {
            cnx = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("✅ Connexion à la base de données réussie !");
        } catch (SQLException e) {
            System.err.println("❌ Erreur de connexion à la base : " + e.getMessage());
        }
    }

    public static DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Connection getCnx() {
        try {
            if (cnx == null || cnx.isClosed()) {
                cnx = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("🎯 Nouvelle connexion ouverte !");
            }
        } catch (SQLException e) {
            System.err.println("❌ Impossible d'ouvrir une nouvelle connexion : " + e.getMessage());
        }
        return cnx;
    }
    public void closeConnection() {
        try {
            if (cnx != null && !cnx.isClosed()) {
                cnx.close();
                System.out.println("✅ Connexion fermée !");
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la fermeture de la connexion : " + e.getMessage());
        }
    }

}
