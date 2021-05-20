package ir.sample.app.BeheshtRay.database;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.swing.JOptionPane;

public class DatabaseManager {

    public static String host = getHost();
    public static String port = getPort();
    public static String dbname = "azadrah";
    public static String user = "postgres";
    public static String pass = "3brGbHEV!gcKYH*m";

    public static String url = "jdbc:postgresql://" + host + ":" + port + "/" + dbname + "?user=" + user + "&password=" + pass;

    static String getHost() {
        if (System.getenv("DATABASE_HOST") != null && !System.getenv("DATABASE_HOST").equals("")) {
            return System.getenv("DATABASE_HOST");
        } else {
            return "sbu.appsan.ir";
        }
    }

    static String getPort() {
        if (System.getenv("DATABASE_PORT") != null && !System.getenv("DATABASE_PORT").equals("")) {
            return System.getenv("DATABASE_PORT");
        } else {
            return "3254";
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
