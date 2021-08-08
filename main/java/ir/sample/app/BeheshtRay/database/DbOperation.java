package ir.sample.app.BeheshtRay.database;


import ir.sample.app.BeheshtRay.models.Faculty;
import ir.sample.app.BeheshtRay.models.Feedback;
import ir.sample.app.BeheshtRay.models.Student;
import ir.sample.app.BeheshtRay.models.Teacher;
import ir.sample.app.BeheshtRay.services.BeheshtRayService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.util.ArrayList;

public class DbOperation {


    /**
     * Send the data of new registered users to database
     * TABLE: STUDENT
     *
     * @param student
     * @param connection
     */
    public static void sendUserInfo(Student student, Connection connection) {
        try {
            String checkSql = "INSERT INTO student(first_name, last_name, student_id, faculty_id, user_id, gender, photo_url, karma) VALUES (?,?,?,?,?,?,?,?)";
            PreparedStatement pstmt = connection.prepareStatement(checkSql);
            pstmt.setString(1, student.getStudentFirstName());
            pstmt.setString(2, student.getStudentLastName());
            pstmt.setString(3, student.getStudentId());
            pstmt.setInt(4, student.getStudentFacultyId());
            pstmt.setString(5, student.getUserId());
            pstmt.setString(6, student.getStudentGender());
            pstmt.setString(7, student.getStudentPhotoURL());
            pstmt.setString(8, student.getUserKarma());
            pstmt.executeUpdate();
            pstmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateUserInfo(Student student, Connection connection) {
        try {
            String checkSql = "UPDATE student SET first_name=?, last_name=?, student_id=?, faculty_id=?, gender=?, photo_url=? WHERE user_id=?";
            PreparedStatement pstmt = connection.prepareStatement(checkSql);
            pstmt.setString(1, student.getStudentFirstName());
            pstmt.setString(2, student.getStudentLastName());
            pstmt.setString(3, student.getStudentId());
            pstmt.setInt(4, student.getStudentFacultyId());
            pstmt.setString(5, student.getStudentGender());
            pstmt.setString(6, student.getStudentPhotoURL());
            pstmt.setString(7, student.getUserId());
            pstmt.executeUpdate();
            pstmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void sendFeedback(Feedback feedback, Connection connection) {
        try {
            String checkSql1 = "SELECT MAX(feedback_id) + 1 FROM feedback";
            PreparedStatement pstmt1 = connection.prepareStatement(checkSql1);
            ResultSet resultSet = pstmt1.executeQuery();
            resultSet.next();
            int reset = resultSet.getInt(1);
            pstmt1.close();


            String checkSql2 = "ALTER SEQUENCE feedback_feedback_id_seq RESTART WITH " + reset;
            PreparedStatement pstmt2 = connection.prepareStatement(checkSql2);
            pstmt2.executeUpdate();
            pstmt2.close();


            String checkSql3 = "INSERT INTO feedback(teaching_id, user_id, score_1, score_2, score_3, score_4, student_score, extended_feedback, persian_date, created_date, up_votes, down_votes, feedback_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, DEFAULT, ?, ?, DEFAULT)";
            PreparedStatement pstmt3 = connection.prepareStatement(checkSql3);
            pstmt3.setInt(1, feedback.getTeachingId());
            pstmt3.setString(2, feedback.getUserId());

            pstmt3.setDouble(3, feedback.getScore1());
            pstmt3.setDouble(4, feedback.getScore2());
            pstmt3.setDouble(5, feedback.getScore3());
            pstmt3.setDouble(6, feedback.getScore4());

            pstmt3.setString(7, feedback.getStudentScore());
            pstmt3.setString(8, feedback.getExtendedFeedback());
            pstmt3.setString(9, feedback.getPersianDate());

            pstmt3.setInt(10, feedback.getUpVotes());
            pstmt3.setInt(11, feedback.getDownVotes());

//            System.out.println(feedback);
            pstmt3.executeUpdate();

            pstmt3.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void removeFeedback(int feedbackId, Connection connection) {
        String checkSql = "DELETE FROM feedback WHERE feedback_id=?";
        PreparedStatement pstmt = null;
        try {
            pstmt = connection.prepareStatement(checkSql);
            pstmt.setInt(1, feedbackId);
            pstmt.executeUpdate();
            pstmt.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }


    public static void sendVote(String userId, int feedbackId, boolean isUpVote, Connection connection) {
        try {

            String checkSql = "INSERT INTO vote(user_id, feedback_id, vote_status) VALUES (?, ?, ?)";
            PreparedStatement pstmt = connection.prepareStatement(checkSql);
            pstmt.setString(1, userId);
            pstmt.setInt(2, feedbackId);
            pstmt.setBoolean(3, isUpVote);
            pstmt.executeUpdate();
            pstmt.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void revertVote(String userId, int feedbackId, Connection connection) {
        try {
            String checkSql = "UPDATE vote SET vote_status = NOT vote_status WHERE user_id=? AND feedback_id=?";
            PreparedStatement pstmt = connection.prepareStatement(checkSql);
            pstmt.setString(1, userId);
            pstmt.setInt(2, feedbackId);
            pstmt.executeUpdate();
            pstmt.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteVote(String userId, int feedbackId, Connection connection) {
        try {

            String checkSql = "DELETE FROM vote WHERE user_id=? AND feedback_id=?";
            PreparedStatement pstmt = connection.prepareStatement(checkSql);
            pstmt.setString(1, userId);
            pstmt.setInt(2, feedbackId);
            pstmt.executeUpdate();
            pstmt.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Return ID of the selected faculty by getting its name
     * TABLE: FACULTY
     *
     * @param faculty_name
     * @param connection
     * @return
     */
    public static int retrieveFacultyIdByName(String faculty_name, Connection connection) {
        String checkSql = "SELECT faculty_id FROM faculty WHERE faculty_name=?";
        PreparedStatement pstmt = null;
        try {
            pstmt = connection.prepareStatement(checkSql);
            pstmt.setString(1, faculty_name);
            ResultSet resultSet = pstmt.executeQuery();
            resultSet.next();
            int res = resultSet.getInt(1);
            pstmt.close();
            return res;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return 0;
    }

    public static ArrayList<Student> retrieveStudentByUserId(String user_id, Connection connection) {
        String checkSql = "SELECT *, faculty_name FROM student INNER JOIN faculty ON student.faculty_id = faculty.faculty_id WHERE user_id=?";
        PreparedStatement pstmt = null;
        try {
            pstmt = connection.prepareStatement(checkSql);
            pstmt.setString(1, user_id);
            ResultSet resultSet = pstmt.executeQuery();
            resultSet.next();

            ArrayList<Student> students = new ArrayList<>();
            Student student = new Student(user_id);

            student.setStudentFirstName(resultSet.getString(1));
            student.setStudentLastName(resultSet.getString(2));
            student.setStudentId(resultSet.getString(3));
            student.setStudentFacultyId(resultSet.getInt(4));
            student.setStudentGender(resultSet.getString(6));
            student.setStudentPhotoURL(resultSet.getString(7));
            student.setUserKarma(resultSet.getString(8));
            student.setStudentFacultyName(resultSet.getString(9));
            students.add(student);

            pstmt.close();
            resultSet.close();

            return students;


        } catch (SQLException throwables) {
            return null;
        }
    }


    public static ArrayList<Teacher> retrieveTheMostFamousTeachers(int facultyId, boolean isLimited, Connection connection) {
        try {
            String postfix = isLimited ? " limit 5" : " limit 20";

            String checkSql = "SELECT teacher_name, photo_url,faculty_id, (AVG(score_1) + AVG(score_2) + AVG(score_3) + AVG(score_4))/4.0 AS over_all_average FROM teacher INNER JOIN feedback f ON teacher.teaching_id = f.teaching_id WHERE teacher.faculty_id=? GROUP BY teacher_name, photo_url, faculty_id ORDER BY over_all_average DESC" + postfix;
            PreparedStatement pstmt = connection.prepareStatement(checkSql);
            pstmt.setInt(1, facultyId);
            ResultSet resultSet = pstmt.executeQuery();
            ArrayList<Teacher> teachers = new ArrayList<>();
            while (resultSet.next()) {
                Teacher teacher = new Teacher();
                teacher.setTeacherName(resultSet.getString(1));
                teacher.setPhotoURL(resultSet.getString(2));
                teacher.setFacultyId(resultSet.getInt(3));
                teachers.add(teacher);
            }

            pstmt.close();
            resultSet.close();

            return teachers;
        } catch (Exception e) {
            return null;
        }
    }

    public static ArrayList<Feedback> retrieveTheMostVotedFeedbacks(int facultyId, boolean isLimited, Connection connection) {
        try {
            String postfix = isLimited ? " limit 3" : " limit 20";

            String checkSql = "SELECT f.up_votes, f.down_votes, f.persian_date, f.student_score, t.lesson_name, t.teacher_name,f.extended_feedback, t.teaching_id, f.feedback_id ,(f.up_votes-f.down_votes) AS diff FROM feedback f INNER JOIN teacher t ON t.teaching_id=f.teaching_id WHERE t.faculty_id=? AND extended_feedback IS NOT NULL ORDER BY diff DESC" + postfix;
            PreparedStatement pstmt = connection.prepareStatement(checkSql);
            pstmt.setInt(1, facultyId);
            ResultSet resultSet = pstmt.executeQuery();
            ArrayList<Feedback> feedbacks = new ArrayList<>();
            while (resultSet.next()) {
                Feedback feedback = new Feedback();
                feedback.setUpVotes(resultSet.getInt(1));
                feedback.setDownVotes(resultSet.getInt(2));
                feedback.setPersianDate(resultSet.getString(3));
                feedback.setStudentScore(resultSet.getString(4));
                feedback.setLessonName(resultSet.getString(5));
                feedback.setTeacherName(resultSet.getString(6));
                feedback.setExtendedFeedback(resultSet.getString(7));
                feedback.setTeachingId(resultSet.getInt(8));
                feedback.setFeedbackId(resultSet.getInt(9));
                feedbacks.add(feedback);
            }

            pstmt.close();
            resultSet.close();

            return feedbacks;
        } catch (Exception e) {
            return null;
        }
    }

    public static ArrayList<Teacher> retrieveTeachersList(String userID, boolean isLimited, Connection connection) {
        try {

            String postfix = isLimited ? " limit 3" : "";

            String checkSql = "SELECT teaching_id, teacher_name, lesson_name, t.photo_url FROM teacher t INNER JOIN student s ON t.faculty_id = s.faculty_id WHERE user_id=?" + postfix;
            PreparedStatement pstmt = connection.prepareStatement(checkSql);
            pstmt.setString(1, userID);
            ResultSet resultSet = pstmt.executeQuery();

            ArrayList<Teacher> teachers = new ArrayList<>();
            while (resultSet.next()) {
                Teacher teacher = new Teacher();
                teacher.setTeachingId(resultSet.getInt(1));
                teacher.setTeacherName(resultSet.getString(2));
                teacher.setLessonName(resultSet.getString(3));
                teacher.setPhotoURL(resultSet.getString(4));
                teachers.add(teacher);
            }

            pstmt.close();
            resultSet.close();

            return teachers;
        } catch (Exception e) {
            return null;
        }

    }


    public static ArrayList<Faculty> retrieveFacultyByUserId(String user_id, Connection connection) {
        String checkSql = "SELECT f.faculty_name, f.photo_url FROM faculty f INNER JOIN student s on f.faculty_id = s.faculty_id WHERE user_id=?";
        PreparedStatement pstmt = null;
        try {
            pstmt = connection.prepareStatement(checkSql);
            pstmt.setString(1, user_id);
            ResultSet resultSet = pstmt.executeQuery();
            resultSet.next();

            ArrayList<Faculty> faculties = new ArrayList<>();
            Faculty faculty = new Faculty();

            faculty.setFacultyName(resultSet.getString(1));
            faculty.setPhotoURL(resultSet.getString(2));

            faculties.add(faculty);

            pstmt.close();
            resultSet.close();

            return faculties;


        } catch (SQLException throwables) {
            return null;
        }
    }


    public static ArrayList<Feedback> retrieveMyFeedbacks(String userID, boolean isExtended, Connection connection) {
        try {

            String checkSql = isExtended ? "SELECT feedback_id, persian_date, student_score, t.lesson_name, t.teacher_name, up_votes, down_votes, extended_feedback, (score_1+score_2+score_3+score_4)/4.0 AS average FROM feedback f INNER JOIN student s ON f.user_id = s.user_id INNER JOIN teacher t ON f.teaching_id = t.teaching_id WHERE f.user_id = ? AND f.extended_feedback IS NOT NULL ORDER BY f.created_date DESC"
                    : "SELECT feedback_id, persian_date, student_score, t.lesson_name, t.teacher_name, (score_1+score_2+score_3+score_4)/4.0 AS average FROM feedback f INNER JOIN student s ON f.user_id = s.user_id INNER JOIN teacher t ON f.teaching_id = t.teaching_id WHERE f.user_id = ? AND f.extended_feedback IS NULL ORDER BY f.created_date DESC";

            PreparedStatement pstmt = connection.prepareStatement(checkSql);
            pstmt.setString(1, userID);
            ResultSet resultSet = pstmt.executeQuery();

            ArrayList<Feedback> feedbacks = new ArrayList<>();
            while (resultSet.next()) {
                Feedback feedback = new Feedback();

                if (isExtended) {
                    feedback.setFeedbackId(resultSet.getInt(1));
                    feedback.setPersianDate(resultSet.getString(2));
                    feedback.setStudentScore(resultSet.getString(3));
                    feedback.setLessonName(resultSet.getString(4));
                    feedback.setTeacherName(resultSet.getString(5));
                    feedback.setUpVotes(resultSet.getInt(6));
                    feedback.setDownVotes(resultSet.getInt(7));
                    feedback.setExtendedFeedback(resultSet.getString(8));
                    feedback.setAverageScore(resultSet.getDouble(9));


                } else {
                    feedback.setFeedbackId(resultSet.getInt(1));
                    feedback.setPersianDate(resultSet.getString(2));
                    feedback.setStudentScore(resultSet.getString(3));
                    feedback.setLessonName(resultSet.getString(4));
                    feedback.setTeacherName(resultSet.getString(5));
                    feedback.setAverageScore(resultSet.getDouble(6));

                }
                feedbacks.add(feedback);
            }

            pstmt.close();
            resultSet.close();

            return feedbacks;
        } catch (Exception e) {
            return null;
        }

    }


    public static ArrayList<Student> retrieveMyKarma(String userID, Connection connection) {
        try {
            String checkSql = "SELECT SUM(up_votes) - SUM(down_votes)  FROM feedback f WHERE f.user_id = ? AND f.extended_feedback IS NOT NULL GROUP BY f.user_id";
            PreparedStatement pstmt = connection.prepareStatement(checkSql);
            pstmt.setString(1, userID);
            ResultSet resultSet = pstmt.executeQuery();
            ArrayList<Student> students = new ArrayList<>();
            Student student = new Student();
            resultSet.next();
            student.setUserKarma(resultSet.getString(1));
            students.add(student);
            pstmt.close();
            resultSet.close();

            return students;
        } catch (Exception e) {
            ArrayList<Student> students = new ArrayList<>();
            Student student = new Student();
            student.setUserKarma("0");
            students.add(student);
            return students;
        }

    }


    public static ArrayList<Teacher> retrieveTeacherInfoByTeachingId(int teachingId, Connection connection) {
        try {
            String checkSql = "SELECT * FROM teacher WHERE teaching_id=?";
            PreparedStatement pstmt = connection.prepareStatement(checkSql);
            pstmt.setInt(1, teachingId);
            ResultSet resultSet = pstmt.executeQuery();
            ArrayList<Teacher> teachers = new ArrayList<>();
            resultSet.next();
            Teacher teacher = new Teacher();
            teacher.setTeacherName(resultSet.getString(1));
            teacher.setLessonName(resultSet.getString(2));
            teacher.setEmail(resultSet.getString(3));
            teacher.setAcademicGroup(resultSet.getString(4));
            teacher.setPhotoURL(resultSet.getString(5));
            teacher.setFacultyId(resultSet.getInt(6));
            teacher.setTeachingId(resultSet.getInt(7));
            teachers.add(teacher);

            pstmt.close();
            resultSet.close();

            return teachers;
        } catch (Exception e) {
            return null;
        }
    }


    public static ArrayList<Teacher> retrieveOtherLessonsByTeacherInfo(String lessonName, String teacherName, int facultyId, Connection connection) {
        try {

            String checkSql = "SELECT teaching_id, lesson_name FROM teacher WHERE lesson_name != ? AND teacher_name=? AND  faculty_id=?";
            PreparedStatement pstmt = connection.prepareStatement(checkSql);
            pstmt.setString(1, lessonName);
            pstmt.setString(2, teacherName);
            pstmt.setInt(3, facultyId);

            ResultSet resultSet = pstmt.executeQuery();

            ArrayList<Teacher> teachers = new ArrayList<>();
            while (resultSet.next()) {
                Teacher teacher = new Teacher();
                teacher.setTeachingId(resultSet.getInt(1));
                teacher.setLessonName(resultSet.getString(2));
                teachers.add(teacher);
            }

            pstmt.close();
            resultSet.close();

            return teachers;
        } catch (Exception e) {
            return null;
        }
    }


    public static ArrayList<Feedback> retrieveFeedbackByTeachingId(int teachingId, boolean isSortByDate, Connection connection) {
        try {

            String checkSql = isSortByDate ? "SELECT feedback_id, persian_date, student_score, up_votes, down_votes, extended_feedback FROM feedback WHERE teaching_id=? AND extended_feedback IS NOT NULL ORDER BY created_date DESC" :
                    "SELECT feedback_id, persian_date, student_score, up_votes, down_votes, extended_feedback FROM feedback WHERE teaching_id=? AND extended_feedback IS NOT NULL ORDER BY up_votes-down_votes DESC";


            PreparedStatement pstmt = connection.prepareStatement(checkSql);
            pstmt.setInt(1, teachingId);
            ResultSet resultSet = pstmt.executeQuery();

            ArrayList<Feedback> feedbacks = new ArrayList<>();
            while (resultSet.next()) {
                Feedback feedback = new Feedback();

                feedback.setFeedbackId(resultSet.getInt(1));
                feedback.setPersianDate(resultSet.getString(2));
                feedback.setStudentScore(resultSet.getString(3));
                feedback.setUpVotes(resultSet.getInt(4));
                feedback.setDownVotes(resultSet.getInt(5));
                feedback.setExtendedFeedback(resultSet.getString(6));

                feedbacks.add(feedback);
            }

            pstmt.close();
            resultSet.close();

            return feedbacks;
        } catch (Exception e) {
            return null;
        }
    }


    public static ArrayList<Feedback> retrieveScoreByTeachingId(int teachingId, Connection connection) {
        try {

            String checkSql = "SELECT AVG(score_1) AS score_1, AVG(score_2) AS score_2, AVG(score_3) AS score_3, AVG(score_4) AS score_4, AVG((score_1 + score_2 + score_3 + score_4)/4.0) AS average , count(*) AS counter FROM feedback WHERE teaching_id=? group by teaching_id";
            PreparedStatement pstmt = connection.prepareStatement(checkSql);
            pstmt.setInt(1, teachingId);
            ResultSet resultSet = pstmt.executeQuery();

            ArrayList<Feedback> feedbacks = new ArrayList<>();
            resultSet.next();
            Feedback feedback = new Feedback();

            feedback.setScore1(resultSet.getDouble(1));
            feedback.setScore2(resultSet.getDouble(2));
            feedback.setScore3(resultSet.getDouble(3));
            feedback.setScore4(resultSet.getDouble(4));
            feedback.setAverageScore(resultSet.getDouble(5));
            feedback.setNumberOfParticipants(resultSet.getString(6));

            feedbacks.add(feedback);

            pstmt.close();
            resultSet.close();

            return feedbacks;
        } catch (Exception e) {
            return null;
        }
    }


    public static ArrayList<Teacher> retrieveSearchedTeachersList(String searchText, String userId, Connection connection) {

        try {

            String checkSql = "SELECT teaching_id, teacher_name, lesson_name, teacher.photo_url FROM teacher INNER JOIN student ON student.faculty_id = teacher.faculty_id WHERE (teacher_name LIKE ? OR lesson_name LIKE ? OR email iLIKE ?) AND student.user_id = ?";
            PreparedStatement pstmt = connection.prepareStatement(checkSql);
            searchText = "%" + searchText + "%";
            pstmt.setString(1, searchText);
            pstmt.setString(2, searchText);
            pstmt.setString(3, searchText);
            pstmt.setString(4, userId);

            ResultSet resultSet = pstmt.executeQuery();

            ArrayList<Teacher> teachers = new ArrayList<>();
            while (resultSet.next()) {
                Teacher teacher = new Teacher();
                teacher.setTeachingId(resultSet.getInt(1));
                teacher.setTeacherName(resultSet.getString(2));
                teacher.setLessonName(resultSet.getString(3));
                teacher.setPhotoURL(resultSet.getString(4));
                teachers.add(teacher);
            }

            pstmt.close();
            resultSet.close();

            return teachers;
        } catch (Exception e) {
            return null;
        }
    }


    public static ArrayList<Teacher> retrieveAllLessonsByTeacherName(String teacherName, int facultyId, Connection connection) {
        try {

            String checkSql = "SELECT teaching_id, teacher_name, lesson_name, photo_url FROM teacher WHERE teacher_name=? AND  faculty_id=?";
            PreparedStatement pstmt = connection.prepareStatement(checkSql);

            pstmt.setString(1, teacherName);
            pstmt.setInt(2, facultyId);

            ResultSet resultSet = pstmt.executeQuery();

            ArrayList<Teacher> teachers = new ArrayList<>();
            while (resultSet.next()) {
                Teacher teacher = new Teacher();
                teacher.setTeachingId(resultSet.getInt(1));
                teacher.setTeacherName(resultSet.getString(2));
                teacher.setLessonName(resultSet.getString(3));
                teacher.setPhotoURL(resultSet.getString(4));
//                System.out.println(teacher);
                teachers.add(teacher);
            }

            pstmt.close();
            resultSet.close();

            return teachers;
        } catch (Exception e) {
            return null;
        }
    }


    public static Boolean retrieveVoteStatus(String userId, int feedbackId , Connection connection) {

        try {

//            System.out.println(userId);
//            System.out.println(feedbackId);

            String checkSql = "SELECT vote_status FROM vote WHERE user_id=? AND feedback_id=?";
            PreparedStatement pstmt = connection.prepareStatement(checkSql);

            pstmt.setString(1, userId);
            pstmt.setInt(2, feedbackId);

            ResultSet resultSet = pstmt.executeQuery();

            resultSet.next();
            boolean result = resultSet.getBoolean(1);



            pstmt.close();
            resultSet.close();

            return result;


        } catch (Exception e) {
//            e.printStackTrace();
            return null;
        }

    }



    public static int applyUpVoteFeedback(int feedbackId,boolean isIncremental, Connection connection) {
        try {
            String checkSql1 = isIncremental ? "UPDATE feedback SET up_votes = up_votes + 1 WHERE feedback_id=?" : "UPDATE feedback SET up_votes = up_votes - 1 WHERE feedback_id=?";
            PreparedStatement pstmt1 = connection.prepareStatement(checkSql1);
            pstmt1.setInt(1, feedbackId);
            pstmt1.executeUpdate();
            pstmt1.close();


            String checkSql2 = "SELECT up_votes FROM feedback  WHERE feedback_id=?";
            PreparedStatement pstmt2 = connection.prepareStatement(checkSql2);
            pstmt2.setInt(1, feedbackId);
            ResultSet resultSet = pstmt2.executeQuery();
            resultSet.next();

            int result = resultSet.getInt(1);

            resultSet.close();
            pstmt2.close();
            return result;

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }


    public static int applyDownVoteFeedback(int feedbackId,boolean isIncremental, Connection connection) {
        try {
            String checkSql1 = isIncremental ? "UPDATE feedback SET down_votes = down_votes + 1 WHERE feedback_id=?" : "UPDATE feedback SET down_votes = down_votes - 1 WHERE feedback_id=?";
            PreparedStatement pstmt1 = connection.prepareStatement(checkSql1);
            pstmt1.setInt(1, feedbackId);
            pstmt1.executeUpdate();
            pstmt1.close();


            String checkSql2 = "SELECT down_votes FROM feedback  WHERE feedback_id=?";
            PreparedStatement pstmt2 = connection.prepareStatement(checkSql2);
            pstmt2.setInt(1, feedbackId);
            ResultSet resultSet = pstmt2.executeQuery();
            resultSet.next();

            int result = resultSet.getInt(1);

            resultSet.close();
            pstmt2.close();
            return result;

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }


    public static ArrayList<Feedback> retrieveTheLeastVotedFeedbacks(int facultyId, Connection connection) {
        try {

            String checkSql = "SELECT f.up_votes, f.down_votes, f.persian_date, f.student_score, t.lesson_name, t.teacher_name,f.extended_feedback, t.teaching_id, f.feedback_id ,(f.up_votes-f.down_votes) AS diff FROM feedback f INNER JOIN teacher t ON t.teaching_id=f.teaching_id WHERE t.faculty_id=? AND extended_feedback IS NOT NULL ORDER BY diff LIMIT 10";
            PreparedStatement pstmt = connection.prepareStatement(checkSql);
            pstmt.setInt(1, facultyId);
            ResultSet resultSet = pstmt.executeQuery();
            ArrayList<Feedback> feedbacks = new ArrayList<>();
            while (resultSet.next()) {
                Feedback feedback = new Feedback();
                feedback.setUpVotes(resultSet.getInt(1));
                feedback.setDownVotes(resultSet.getInt(2));
                feedback.setPersianDate(resultSet.getString(3));
                feedback.setStudentScore(resultSet.getString(4));
                feedback.setLessonName(resultSet.getString(5));
                feedback.setTeacherName(resultSet.getString(6));
                feedback.setExtendedFeedback(resultSet.getString(7));
                feedback.setTeachingId(resultSet.getInt(8));
                feedback.setFeedbackId(resultSet.getInt(9));
                feedbacks.add(feedback);
            }

            pstmt.close();
            resultSet.close();

            return feedbacks;
        } catch (Exception e) {
            return null;
        }

    }


    public static ArrayList<Teacher> retrieveBestTAs(int facultyId, Connection connection) {
        try {

            String checkSql = "SELECT t.teaching_id, teacher_name, lesson_name, photo_url, AVG(score_3) AS over_all_ta_score FROM teacher t INNER JOIN feedback f ON t.teaching_id = f.teaching_id WHERE t.faculty_id=? GROUP BY teacher_name, lesson_name,photo_url, t.teaching_id ORDER BY over_all_ta_score DESC LIMIT 10";
            PreparedStatement pstmt = connection.prepareStatement(checkSql);

            pstmt.setInt(1, facultyId);

            ResultSet resultSet = pstmt.executeQuery();

            ArrayList<Teacher> teachers = new ArrayList<>();
            while (resultSet.next()) {
                Teacher teacher = new Teacher();
                teacher.setTeachingId(resultSet.getInt(1));
                teacher.setTeacherName(resultSet.getString(2));
                teacher.setLessonName(resultSet.getString(3));
                teacher.setPhotoURL(resultSet.getString(4));
                teacher.setAverageScore(BeheshtRayService.convertToEnglishDigits(BigDecimal.valueOf(resultSet.getDouble(5)).setScale(3, RoundingMode.HALF_UP).toString()));
                teacher.setNumberTitle("میانگین امتیاز:");
                teachers.add(teacher);
            }

            pstmt.close();
            resultSet.close();

            return teachers;
        } catch (Exception e) {
            return null;
        }
    }

    public static ArrayList<Teacher> retrieveBestLessons(int facultyId, Connection connection) {
        try {

            String checkSql = "SELECT t.teaching_id, teacher_name, lesson_name, photo_url, (AVG(score_1) + AVG(score_2) + AVG(score_4) + AVG(score_3))/4.0 AS over_all_average FROM teacher t INNER JOIN feedback f ON t.teaching_id = f.teaching_id WHERE t.faculty_id=? GROUP BY teacher_name, lesson_name,photo_url, t.teaching_id ORDER BY over_all_average DESC LIMIT 10";
            PreparedStatement pstmt = connection.prepareStatement(checkSql);

            pstmt.setInt(1, facultyId);

            ResultSet resultSet = pstmt.executeQuery();

            ArrayList<Teacher> teachers = new ArrayList<>();
            while (resultSet.next()) {
                Teacher teacher = new Teacher();
                teacher.setTeachingId(resultSet.getInt(1));
                teacher.setTeacherName(resultSet.getString(2));
                teacher.setLessonName(resultSet.getString(3));
                teacher.setPhotoURL(resultSet.getString(4));
                teacher.setAverageScore(BeheshtRayService.convertToEnglishDigits(BigDecimal.valueOf(resultSet.getDouble(5)).setScale(3, RoundingMode.HALF_UP).toString()));
                teacher.setNumberTitle("میانگین امتیاز:");
                teachers.add(teacher);
            }

            pstmt.close();
            resultSet.close();

            return teachers;
        } catch (Exception e) {
            return null;
        }
    }

    public static ArrayList<Teacher> retrieveTheMostLeastCommentedLessons(int facultyId,boolean isMost, Connection connection) {
        try {

            String checkSql = isMost ? "SELECT t.teaching_id, teacher_name, lesson_name, photo_url, count(*) AS count FROM teacher t INNER JOIN feedback f ON t.teaching_id = f.teaching_id WHERE t.faculty_id=? AND f.extended_feedback IS NOT NULL GROUP BY teacher_name, lesson_name,photo_url, t.teaching_id ORDER BY count DESC LIMIT 10" :
                    "SELECT t.teaching_id, teacher_name, lesson_name, photo_url, count(*) AS count FROM teacher t INNER JOIN feedback f ON t.teaching_id = f.teaching_id WHERE t.faculty_id=? AND f.extended_feedback IS NOT NULL GROUP BY teacher_name, lesson_name,photo_url, t.teaching_id ORDER BY count LIMIT 10";
            PreparedStatement pstmt = connection.prepareStatement(checkSql);

            pstmt.setInt(1, facultyId);

            ResultSet resultSet = pstmt.executeQuery();

            ArrayList<Teacher> teachers = new ArrayList<>();
            while (resultSet.next()) {
                Teacher teacher = new Teacher();
                teacher.setTeachingId(resultSet.getInt(1));
                teacher.setTeacherName(resultSet.getString(2));
                teacher.setLessonName(resultSet.getString(3));
                teacher.setPhotoURL(resultSet.getString(4));
                teacher.setAverageScore(BeheshtRayService.convertToEnglishDigits(resultSet.getString(5)));
                teacher.setNumberTitle("تعداد نظرات:");
                teachers.add(teacher);
            }

            pstmt.close();
            resultSet.close();

            return teachers;
        } catch (Exception e) {
            return null;
        }
    }


    public static ArrayList<Teacher> retrieveTheGeneralLessons(Connection connection) {
        try {

            String checkSql = "SELECT teaching_id, teacher_name, lesson_name, photo_url FROM teacher WHERE teacher.faculty_id=10 GROUP BY teaching_id, teacher_name, lesson_name,photo_url ORDER BY lesson_name\n";
            PreparedStatement pstmt = connection.prepareStatement(checkSql);

            ResultSet resultSet = pstmt.executeQuery();

            ArrayList<Teacher> teachers = new ArrayList<>();
            while (resultSet.next()) {
                Teacher teacher = new Teacher();
                teacher.setTeachingId(resultSet.getInt(1));
                teacher.setTeacherName(resultSet.getString(2));
                teacher.setLessonName(resultSet.getString(3));
                teacher.setPhotoURL(resultSet.getString(4));
                teachers.add(teacher);
            }

            pstmt.close();
            resultSet.close();

            return teachers;
        } catch (Exception e) {
            return null;
        }
    }
}
