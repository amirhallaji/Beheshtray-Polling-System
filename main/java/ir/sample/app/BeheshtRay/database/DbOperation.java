package ir.sample.app.BeheshtRay.database;


import ir.sample.app.BeheshtRay.models.Feedback;
import ir.sample.app.BeheshtRay.models.Student;
import ir.sample.app.BeheshtRay.models.Teacher;

import java.sql.*;
import java.util.ArrayList;

public class DbOperation {


    public static void sendFeedBack(Feedback feedback, Connection connection) {
        try {
            String count = "SELECT COUNT(*) FROM feedbacks"; // TODO dbOperation
            PreparedStatement pcount = connection.prepareStatement(count);
            ResultSet rcount = pcount.executeQuery();
            int countnum = 0;
            while (rcount.next()) {
                countnum = Integer.parseInt(rcount.getString(1));
            }
            String checkSql = "INSERT INTO feedbacks(teacher_name, lesson_name, score_1, score_2, score_3, score_4, score_ave, student_score, extended_feedback, userid, date_number, upvotes, downvotes, feedback_id, created_time) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
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
            pstmt.setString(11, feedback.date_number);
            pstmt.setString(12, feedback.upvotes);
            pstmt.setString(13, feedback.downvotes);
            pstmt.setString(14, feedback.feedback_id);
            pstmt.setString(15, feedback.created_time);
//            pstmt.setString(14, feedback.feedback_id);
//            pstmt.setString(11, String.valueOf(feedback.feedback_key));
            System.out.println("\n\nState:");
            pstmt.executeUpdate();
            pstmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static ArrayList<Feedback> retrieveFeedbacksBySelf(String userid, Connection connection) {
        try {
            String checkSql = "SELECT * FROM feedbacks WHERE userid=? and extended_feedback IS NOT NULL ORDER BY created_time DESC";
            PreparedStatement pstmt = connection.prepareStatement(checkSql);
            pstmt.setString(1, userid);
            ResultSet resultSet = pstmt.executeQuery();
            String data[] = new String[16];
            ArrayList<Feedback> feedbacks = new ArrayList<>();
            while (resultSet.next()) {
                Feedback feedback = new Feedback();
                for (int i = 1; i <= 15; i++) {
                    data[i] = resultSet.getString(i);
                }
                feedback.teacher_name = data[1];
                feedback.lesson_name = data[2];
                feedback.score1 = data[3];
                feedback.score2 = data[4];
                feedback.score3 = data[5];
                feedback.score4 = data[6];
                feedback.score_ave = data[7];
                feedback.student_score = data[8];
                feedback.extended_feedback = data[9];
                feedback.user_id = data[10];
                feedback.date_number = data[11];
                feedback.upvotes = data[12];
                feedback.downvotes = data[13];
                feedback.feedback_id = data[14];
                feedback.created_time = data[15];
                feedbacks.add(feedback);

            }
            return feedbacks;
        } catch (Exception e) {
            return null;

        }
    }


    public static ArrayList<Feedback> retrieveFeedbacksBySelf2(String userid, Connection connection) {
        try {
            String checkSql = "SELECT * FROM feedbacks WHERE userid=?";
            PreparedStatement pstmt = connection.prepareStatement(checkSql);
            pstmt.setString(1, userid);
            ResultSet resultSet = pstmt.executeQuery();
//            String data[] = new String[15];
            ArrayList<Feedback> feedbacks = new ArrayList<>();
            while (resultSet.next()) {
                Feedback feedback = new Feedback();
//                for (int i = 1; i <= 14; i++) {
//                    data[i] = resultSet.getString(i);
//                }
////                feedback.teacher_name = data[1];
////                feedback.lesson_name = data[2];
////                feedback.score1 = data[3];
////                feedback.score2 = data[4];
////                feedback.score3 = data[5];
////                feedback.score4 = data[6];
////                feedback.score_ave = data[7];
////                feedback.student_score = data[8];
////                feedback.extended_feedback = data[9];
////                feedback.user_id = data[10];
////                feedback.date_number = data[11];
////                feedback.upvotes = data[12];
////                feedback.downvotes = data[13];
//                feedback.feedback_id = data[14];
                feedbacks.add(feedback);

            }
            return feedbacks;
        } catch (Exception e) {
            return null;

        }
    }


    public static ArrayList<Feedback> retrieveFeedbacksByFeedbackId(String feedback_id, Connection connection) {
        try {
            String checkSql = "SELECT * FROM feedbacks WHERE feedback_id=? and extended_feedback IS NOT NULL";
            PreparedStatement pstmt = connection.prepareStatement(checkSql);
            pstmt.setString(1, feedback_id);
            ResultSet resultSet = pstmt.executeQuery();
            String data[] = new String[16];
            ArrayList<Feedback> feedbacks = new ArrayList<>();
            while (resultSet.next()) {
                Feedback feedback = new Feedback();
                for (int i = 1; i <= 15; i++) {
                    data[i] = resultSet.getString(i);
                }
                feedback.teacher_name = data[1];
                feedback.lesson_name = data[2];
                feedback.score1 = data[3];
                feedback.score2 = data[4];
                feedback.score3 = data[5];
                feedback.score4 = data[6];
                feedback.score_ave = data[7];
                feedback.student_score = data[8];
                feedback.extended_feedback = data[9];
                feedback.user_id = data[10];
                feedback.date_number = data[11];
                feedback.upvotes = data[12];
                feedback.downvotes = data[13];
                feedback.feedback_id = data[14];
                feedback.created_time = data[15];
                feedbacks.add(feedback);

            }
            return feedbacks;
        } catch (Exception e) {
            return null;

        }
    }

    public static void updateUpvotes(String feedback_id, String new_upvote, Connection connection) {
        try {
            String checkSql = "UPDATE feedbacks SET upvotes=? WHERE feedback_id=?";
            PreparedStatement pstmt = connection.prepareStatement(checkSql);
            pstmt.setString(1, new_upvote);
            pstmt.setString(2, feedback_id);
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateDownvotes(String feedback_id, String new_downvote, Connection connection) {
        try {
            String checkSql = "UPDATE feedbacks SET downvotes=? WHERE feedback_id=?";
            PreparedStatement pstmt = connection.prepareStatement(checkSql);
            pstmt.setString(1, new_downvote);
            pstmt.setString(2, feedback_id);
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static ArrayList<Feedback> retrieveFeedbacksByTeacher(String lesson_name, String teacher_name, Connection connection) {
        try {
            String checkSql = "SELECT * FROM feedbacks WHERE teacher_name=? and lesson_name=? and extended_feedback IS NOT NULL ORDER BY created_time DESC";
            PreparedStatement pstmt = connection.prepareStatement(checkSql);
            pstmt.setString(1, teacher_name);
            pstmt.setString(2, lesson_name);
            ResultSet resultSet = pstmt.executeQuery();
            String data[] = new String[15];
            ArrayList<Feedback> feedbacks = new ArrayList<>();
            while (resultSet.next()) {
                Feedback feedback = new Feedback();
                for (int i = 1; i <= 14; i++) {
                    data[i] = resultSet.getString(i);
                }
                feedback.teacher_name = data[1];
                feedback.lesson_name = data[2];
                feedback.score1 = data[3];
                feedback.score2 = data[4];
                feedback.score3 = data[5];
                feedback.score4 = data[6];
                feedback.score_ave = data[7];
                feedback.student_score = data[8];
                feedback.extended_feedback = data[9];
                feedback.user_id = data[10];
                feedback.date_number = data[11];
                feedback.upvotes = data[12];
                feedback.downvotes = data[13];
                feedback.feedback_id = data[14];
                feedbacks.add(feedback);

            }
            return feedbacks;
        } catch (Exception e) {
            return null;
        }
    }

    public static ArrayList<Teacher> retrieveTeachers(Connection connection) {
        try {
            String checkSql = "SELECT * FROM teachers ORDER BY teacher_name";
            PreparedStatement pstmt = connection.prepareStatement(checkSql);
            ResultSet resultSet = pstmt.executeQuery();
//            System.out.println(resultSet.next());
//            System.out.println(resultSet.next());
//            System.out.println(resultSet.next());
//            System.out.println(resultSet.next());
//            System.out.println(resultSet.next());
            String[] data = new String[12];
            ArrayList<Teacher> teachers = new ArrayList<>();
            while (resultSet.next()) {
//                System.out.println("Hello");
                Teacher teacher = new Teacher();
                for (int i = 1; i <= 11; i++) {
                    data[i] = resultSet.getString(i);
                }
//                System.out.println("data: " + data[1]);
                teacher.teacher_name = data[1];
                teacher.lesson_name = data[2];
                teacher.teacher_email = data[3];
                teacher.teacher_academic_group = data[4];
                teacher.teacher_key = data[10];
                teacher.teacher_photo = data[11];
//                System.out.println(teacher.teacher_name);
                teachers.add(teacher);
            }
            return teachers;
        } catch (Exception e) {
            return null;
        }

    }


    public static ArrayList<Teacher> retrieveTeacherByKey(String teacher_key, Connection connection) {
        try {
            String checkSql = "SELECT * FROM teachers WHERE teacher_key=?";
            PreparedStatement pstmt = connection.prepareStatement(checkSql);
            pstmt.setString(1, teacher_key);
            ResultSet resultSet = pstmt.executeQuery();
            String[] data = new String[12];
            ArrayList<Teacher> teachers = new ArrayList<>();
            while (resultSet.next()) {
                System.out.println("Hello");
                Teacher teacher = new Teacher();
                for (int i = 1; i <= 11; i++) {
                    data[i] = resultSet.getString(i);
                }
//                System.out.println("data: " + data[1]);
                teacher.teacher_name = data[1];
                teacher.lesson_name = data[2];
                teacher.teacher_email = data[3];
                teacher.teacher_academic_group = data[4];
                teacher.teacher_key = data[10];
                teacher.teacher_photo = data[11];
                teachers.add(teacher);
//                System.out.println(teacher.teacher_name);
            }
            return teachers;
        } catch (Exception e) {
            return null;
        }

    }

    public static String retrieveTeacherURLImage(String teacher_name, Connection connection) {
        try {
            String checkSql = "SELECT * FROM teachers WHERE teacher_name=?";
            PreparedStatement pstmt = connection.prepareStatement(checkSql);
            pstmt.setString(1, teacher_name);
            ResultSet resultSet = pstmt.executeQuery();
            return resultSet.getString(11);
        } catch (Exception e) {
            return null;
        }
    }


    public static void sendUserInfo(Student student, Connection connection) {
        try {
            String count = "SELECT COUNT(*) FROM students"; // TODO dbOperation
            PreparedStatement pcount = connection.prepareStatement(count);
            ResultSet rcount = pcount.executeQuery();
            int countnum = 0;
            while (rcount.next()) {
                countnum = Integer.parseInt(rcount.getString(1));
            }
            String checkSql = "INSERT INTO feedbacks(student_name, student_id, student_faculty, student_gender, student_photo, student_upvotes, student_downvotes, user_id) VALUES (?,?,?,?,?,?,?,?)";
            PreparedStatement pstmt = connection.prepareStatement(checkSql);
            pstmt.setString(1, student.student_name);
            pstmt.setString(2, student.student_id);
            pstmt.setString(3, student.student_faculty);
            pstmt.setString(4, student.student_gender);
            pstmt.setString(5, student.student_photo);
            pstmt.setString(6, student.student_upvotes);
            pstmt.setString(7, student.student_downvotes);
            pstmt.setString(8, student.user_id);

            pstmt.executeUpdate();
            pstmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static ArrayList<Student> retrieveVoteStatus(String user_id, Connection connection) {
        try {
            String checkSql = "SELECT * FROM students WHERE user_id=?";
            PreparedStatement pstmt = connection.prepareStatement(checkSql);
            pstmt.setString(1, user_id);
            ResultSet resultSet = pstmt.executeQuery();
            String data[] = new String[9];
            ArrayList<Student> students = new ArrayList<>();
            while (resultSet.next()) {
                Student student = new Student();
                for (int i = 1; i <= 8; i++) {
                    data[i] = resultSet.getString(i);
                }

                student.student_name = data[1];
                student.student_id = data[2];
                student.student_faculty = data[3];
                student.student_gender = data[4];
                student.student_photo = data[5];
                student.student_upvotes = data[6];
                student.student_downvotes = data[7];
                student.user_id = data[8];

                students.add(student);

            }
            if(students.size() == 0){
                Student student = new Student();
                student.student_name = "کاربر مهمان";
                student.student_id = "کاربر مهمان";
                student.student_faculty = "دانشگاه شهید بهشتی";
                student.student_photo = "https://s4.uupload.ir/files/guest_xt0.jpg";
                student.student_gender = "مرد";
                student.student_upvotes = "۰";
                student.student_downvotes = "۰";
                students.add(student);
            }
            return students;
        } catch (Exception e) {
            return null;

        }

    }


    public static void updateUpvotesListForUser(String user_id, String student_upvotes, Connection connection) {
        try {
            String checkSql = "UPDATE students SET student_upvotes=? WHERE user_id=?";
            PreparedStatement pstmt = connection.prepareStatement(checkSql);
            pstmt.setString(1, student_upvotes);
            pstmt.setString(2, user_id);
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateDownvotesListForUser(String user_id, String student_downvotes, Connection connection) {
        try {
            String checkSql = "UPDATE students SET student_downvotes=? WHERE user_id=?";
            PreparedStatement pstmt = connection.prepareStatement(checkSql);
            pstmt.setString(1, student_downvotes);
            pstmt.setString(2, user_id);
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteExtendedVote(String feedback_id, Connection connection) {
        try {
            String checkSql = "UPDATE feedbacks SET extended_feedback=NULL WHERE feedback_id=?";
            PreparedStatement pstmt = connection.prepareStatement(checkSql);
            pstmt.setString(1, feedback_id);
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}





