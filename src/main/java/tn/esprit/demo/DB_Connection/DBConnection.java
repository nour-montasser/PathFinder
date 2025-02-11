package tn.esprit.demo.DB_Connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL="jdbc:mysql://localhost:3306/pathfinder";
    private static final String USER="root";
    private static final String Password="";
    // etape1:creer une instance static de meme type que la classe
    private static DBConnection instance;
    private Connection cnx;



    // etape1:rendre le constructeur prive
    private DBConnection()
    {
        try {
            this.cnx= DriverManager.getConnection(URL,USER,Password);
            System.out.println("Connected ");
        } catch (SQLException e) {

            System.err.println("erreu:" +e.getMessage());
        }
    }
    //3eme etape: creer une methode statique pour recuperer l'instance
    public static DBConnection getInstance()
    {   if (instance == null) instance = new DBConnection();
        return instance;
    }

    public Connection getCnx() {
        return cnx;
    }
}
