package ir.sample.app.BeheshtRay.database;


import ir.sample.app.BeheshtRay.models.Comment;
import ir.sample.app.BeheshtRay.models.Student;
import ir.sample.app.BeheshtRay.models.Teacher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class DbOperation {


    public static void sendComment(Comment comment, Connection connection) {
        try {
            String count = "SELECT COUNT(*) FROM comments"; // TODO dbOperation
            PreparedStatement pcount = connection.prepareStatement(count);
            ResultSet rcount = pcount.executeQuery();
            int countnum = 0;
            while (rcount.next()) {
                countnum = Integer.parseInt(rcount.getString(1));
            }
            String checkSql = "INSERT INTO comments(commentId,teacherName,teacherAcademicGroup,teacherEmail,teacherLessons,studentName,studentFaculty,studentId,studentGender,show) VALUES (?,?,?,?,?,?,?,?,?,?)";
            PreparedStatement pstmt = connection.prepareStatement(checkSql);
            pstmt.setString(1, comment.commentId);
            pstmt.setString(2, comment.commentTeacher.teacherName);
            pstmt.setString(3, comment.commentTeacher.teacherAcademicGroup);
            pstmt.setString(4, comment.commentTeacher.teacherEmail);
            pstmt.setString(5, comment.commentTeacher.teacherLessons);
            pstmt.setString(6, comment.commentStudent.studentName);
            pstmt.setString(7, comment.commentStudent.studentFaculty);
            pstmt.setString(8, comment.commentStudent.studentId);
            pstmt.setString(9, comment.commentStudent.studentGender);
            pstmt.setString(10, String.valueOf(true));
            System.out.println("\n\nState:");
            pstmt.executeUpdate();
            pstmt.close();
        } catch (Exception e) {
            e.printStackTrace();
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
