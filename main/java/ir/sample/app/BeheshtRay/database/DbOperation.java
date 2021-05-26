package ir.sample.app.BeheshtRay.database;


import ir.sample.app.BeheshtRay.models.Comment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

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
            String checkSql = "INSERT INTO comments(commentid,teachername,teacheracademicgroup,teacheremail,teacherlessons,studentname,studentfaculty,studentid,studentgender) VALUES (?,?,?,?,?,?,?,?,?)";
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
            System.out.println("\n\nState:");
            pstmt.executeUpdate();
            pstmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
