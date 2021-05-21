package ir.sample.app.BeheshtRay.database;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.swing.JOptionPane;

public class DatabaseManager {

    public static String host = getHost();
    public static String port = getPort();
    public static String dbname = "postgres";
    public static String user = "a_soltani";
    public static String pass = "49d56sa8wa4s1dy";

    public static String url = "jdbc:postgresql://" + host + ":" + port + "/" + dbname + "?user=" + user + "&password=" + pass;

    static String getHost() {
        if (System.getenv("DATABASE_HOST") != null && !System.getenv("DATABASE_HOST").equals("")) {
            return System.getenv("DATABASE_HOST");
        } else {
            return "162.55.105.142";
        }
    }

    static String getPort() {
        if (System.getenv("DATABASE_PORT") != null && !System.getenv("DATABASE_PORT").equals("")) {
            return System.getenv("DATABASE_PORT");
        } else {
            return "55432";
        }
    }

    static {
        try {
            System.out.println("-------------- DATABASE_URL: " + url);
            DriverManager.registerDriver(new org.postgresql.Driver());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(url);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Can not connect to the database:\n" + e.getMessage());
        }

        return null;
    }

    public static void closeConnection(Connection connection) {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
