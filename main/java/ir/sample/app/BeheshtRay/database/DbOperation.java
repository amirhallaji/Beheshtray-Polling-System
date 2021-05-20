package ir.sample.app.BeheshtRay.database;


import ir.sample.app.BeheshtRay.models.Comment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class DbOperation {

    public static void registerPelak(Comment comment, String number, Connection connection) {
        try {
            String count = "SELECT COUNT(*) FROM numberpelak";
            PreparedStatement pcount = connection.prepareStatement(count);
            ResultSet rcount = pcount.executeQuery();
            int countnum = 0;
            while (rcount.next()) {
                countnum = Integer.parseInt(rcount.getString(1));
            }
            String checkSql = "INSERT INTO numberpelak(number,first,second,third,type,harf,name,bedehi,id,show) VALUES (?,?,?,?,?,?,?,?,?,?)";
            PreparedStatement pstmt = connection.prepareStatement(checkSql);
            pstmt.setString(1, number);
            pstmt.setString(2, comment.first);
            pstmt.setString(3, comment.second);
            pstmt.setString(4, comment.third);
            pstmt.setString(5, comment.type);
            pstmt.setString(6, comment.harf);
            pstmt.setString(7, comment.name);
            pstmt.setString(8, String.valueOf(comment.bedehi));
            pstmt.setString(9, "idpelak" + String.valueOf(countnum));
            pstmt.setString(10, String.valueOf(true));
            pstmt.executeUpdate();
            pstmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Pelak> retrievePelaks(String number, Connection connection) {
        try {
            String checkSql = "SELECT first,second,third,type,harf,name,bedehi,id,show FROM numberpelak where number=? and show=?";
            PreparedStatement pstmt = connection.prepareStatement(checkSql);
            pstmt.setString(1, number);
            pstmt.setString(2, String.valueOf(true));
            ResultSet resultSet = pstmt.executeQuery();
            String data[] = new String[10];
            ArrayList<Pelak> pelaks = new ArrayList<>();
            while (resultSet.next()) {
                Pelak pelak = new Pelak();
                for (int i = 1; i <= 9; i++) {
                    data[i] = resultSet.getString(i);
                }
                pelak.first = data[1];
                pelak.second = data[2];
                pelak.third = data[3];
                pelak.type = data[4];
                pelak.harf = data[5];
                pelak.name = data[6];
                pelak.bedehi = Integer.parseInt(data[7]);
                pelak.id = data[8];
                if (Boolean.valueOf(data[9])) {
                    pelaks.add(pelak);
                }
            }
            return pelaks;
        } catch (Exception e) {
            return null;
        }
    }

    public static Pelak retrievePelak(String pelakid, Connection connection) {
        try {
            String checkSql = "SELECT first,second,third,type,harf,name,bedehi,id,show FROM numberpelak where id=?";
            PreparedStatement pstmt = connection.prepareStatement(checkSql);
            pstmt.setString(1, pelakid);
            ResultSet resultSet = pstmt.executeQuery();
            String data[] = new String[10];
            ArrayList<Pelak> pelaks = new ArrayList<>();
            while (resultSet.next()) {
                Pelak pelak = new Pelak();
                for (int i = 1; i <= 9; i++) {
                    data[i] = resultSet.getString(i);
                }
                pelak.first = data[1];
                pelak.second = data[2];
                pelak.third = data[3];
                pelak.type = data[4];
                pelak.harf = data[5];
                pelak.name = data[6];
                pelak.bedehi = Integer.parseInt(data[7]);
                pelak.id = data[8];
                if (Boolean.valueOf(data[9])) {
                    pelaks.add(pelak);
                }
            }
            return pelaks.get(0);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void deletepelak(String pelakid, Connection connection) {
        try {
            String checkSql = "UPDATE numberpelak set show=? where id=?";
            PreparedStatement pstmt = connection.prepareStatement(checkSql);
            pstmt.setString(1, String.valueOf(false));
            pstmt.setString(2, pelakid);
            pstmt.executeUpdate();
            pstmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void editpelak(Pelak pelak, Connection connection) {
        try {
            String checkSql = "UPDATE numberpelak set first=?,second=?,third=?,type=?,harf=?,name=? where id=?";
            PreparedStatement pstmt = connection.prepareStatement(checkSql);
            pstmt.setString(1, pelak.first);
            pstmt.setString(2, pelak.second);
            pstmt.setString(3, pelak.third);
            pstmt.setString(4, pelak.type);
            pstmt.setString(5, pelak.harf);
            pstmt.setString(6, pelak.name);
            pstmt.setString(7, pelak.id);
            pstmt.executeUpdate();
            pstmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void delete() {
        String d = "";
        try {
            Connection connection = DatabaseManager.getConnection();
            String checkSql = "DELETE FROM numberpelak;";
            PreparedStatement pstmt = connection.prepareStatement(checkSql);
            pstmt.executeQuery();
            pstmt.close();
            connection.close();
        } catch (Exception e) {
            System.out.println("deleted");
        }
    }
}
