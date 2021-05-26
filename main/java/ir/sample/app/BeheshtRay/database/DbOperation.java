package ir.sample.app.BeheshtRay.database;


import ir.sample.app.BeheshtRay.models.Feedback;

import java.sql.Array;
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
            String checkSql = "INSERT INTO feedbacks(teacher_name, lesson_name, score_1, score_2, score_3, score_4, score_ave, student_score, extended_feedback, userid, feedback_id) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
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
            pstmt.setString(11, feedback.feedback_id);
//            pstmt.setString(11, String.valueOf(feedback.feedback_key));
            System.out.println("\n\nState:");
            pstmt.executeUpdate();
            pstmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static ArrayList<Feedback> retrieveFeedbacks(String feedbackId, Connection connection) {
        try {
            String checkSql = "SELECT teacher_name, lesson_name, score_1, score_2, score_3, score_4, score_ave, student_score, extended_feedback, userid, date, upvotes, downvotes FROM feedbacks WHERE feedback_id=?";
            PreparedStatement pstmt = connection.prepareStatement(checkSql);
            pstmt.setString(1, feedbackId);
            ResultSet resultSet = pstmt.executeQuery();
            String data[] = new String[14];
            ArrayList <Feedback> feedbacks = new ArrayList<>();
            while (resultSet.next()) {
                Feedback feedback = new Feedback();
                for (int i = 1; i <= 10; i++) {
                    data[i] = resultSet.getString(i);
                }
                feedback.teacher_name= data[1];
                feedback.lesson_name = data[2];
                feedback.score1= data[3];
                feedback.score2 = data[4];
                feedback.score3 = data[5];
                feedback.score4 = data[6];
                feedback.score_ave = data[7];
                feedback.student_score = data[8];
                feedback.extended_feedback = data[9];
                feedback.user_id = data[10];
                feedback.date = data[11];
                feedback.upvotes = data[12];
                feedback.downvotes = data[13];
                feedbacks.add(feedback);

            }
            return feedbacks;
        } catch (Exception e) {
            return null;
        }
    }

}
