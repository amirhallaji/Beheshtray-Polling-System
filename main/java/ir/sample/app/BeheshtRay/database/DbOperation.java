package ir.sample.app.BeheshtRay.database;


import ir.sample.app.BeheshtRay.models.Comment;
import ir.sample.app.BeheshtRay.models.Feedback;
import ir.sample.app.BeheshtRay.models.Student;
import ir.sample.app.BeheshtRay.models.Teacher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class DbOperation {


    public static void sendFeedBack(Feedback feedback, Connection connection){
        try {
            String count = "SELECT COUNT(*) FROM feedbacks"; // TODO dbOperation
            PreparedStatement pcount = connection.prepareStatement(count);
            ResultSet rcount = pcount.executeQuery();
            int countnum = 0;
            while (rcount.next()) {
                countnum = Integer.parseInt(rcount.getString(1));
            }
            String checkSql = "INSERT INTO feedbacks(teacher_name, lesson_name, score_1, score_2, score_3, score_4, score_ave, student_score, extended_feedback, userid) VALUES (?,?,?,?,?,?,?,?,?,?)";
            PreparedStatement pstmt = connection.prepareStatement(checkSql);
            pstmt.setString(1, feedback.teacher_name);
            pstmt.setString(2, feedback.lesson_name);
            pstmt.setString(3, feedback.score1);
            pstmt.setString(4, feedback.score2);
            pstmt.setString(5, feedback.score3);
            pstmt.setString(6, feedback.score4);
            pstmt.setString(7, feedback.score_ave);
            pstmt.setString(8, feedback.student_score);
            pstmt.setString(9, feedback.extended_feedback);
            pstmt.setString(10, feedback.user_id);
//            pstmt.setString(11, String.valueOf(feedback.feedback_key));
            System.out.println("\n\nState:");
            pstmt.executeUpdate();
            pstmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
