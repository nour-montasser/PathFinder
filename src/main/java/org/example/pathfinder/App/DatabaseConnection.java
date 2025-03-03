package org.example.pathfinder.App;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class DatabaseConnection {

    Connection cnx;



    public static DatabaseConnection instance;
    public DatabaseConnection(){

        String Url="jdbc:mysql://localhost/pathfinder";
        String Username="root";
        String Password="";

        try {
            cnx= DriverManager.getConnection(Url,Username,Password);
            System.out.println("Connection établie");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public boolean isConnectionClosed() {
        try {
            return cnx == null || cnx.isClosed();
        } catch (SQLException e) {
            System.out.println("❌ Error checking connection status: " + e.getMessage());
            return true; // Assume closed if there's an error
        }
    }


    public static DatabaseConnection getInstance() {
        if(instance==null){
            instance=  new DatabaseConnection();
        }
        return instance;
    }

    public Connection getCnx() {
        return cnx;
    }
}
