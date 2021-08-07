package ir.sample.app.BeheshtRay.services;

import com.github.mfathi91.time.PersianDate;
import ir.appsan.sdk.*;
import ir.sample.app.BeheshtRay.database.DatabaseManager;
import ir.sample.app.BeheshtRay.database.DbOperation;
import ir.sample.app.BeheshtRay.models.*;
import ir.sample.app.BeheshtRay.views.*;
import org.json.simple.JSONObject;

import java.io.*;
import java.sql.Connection;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class BeheshtRayService extends APSService {


    Connection connection = DatabaseManager.getConnection();

    ArrayList<Student> students = new ArrayList<>();
    CurrentStudentEntity currentStudentEntity = new CurrentStudentEntity();
    Student current_user = null;

    ArrayList<Teacher> teachers = new ArrayList<>();
    CurrentTeacherEntity currentTeacherEntity = new CurrentTeacherEntity();
    Teacher current_teacher = null;

    Feedback current_feedback = null;


    public BeheshtRayService(String channelName) {
        super(channelName);
    }

    @Override
    public String getServiceName() {
        return "app:09384158428:Beheshti Poll Service";
    }

    @Override
    public View onCreateView(String command, JSONObject pageData, String userId) {
        View view = null;

        if (command.startsWith("teacher_info_item")) {
            String teachingId = command.substring(command.indexOf("+") + 1);
            view = new TeacherInfo();
            currentTeacherEntity.currentTeacher = DbOperation.retrieveTeacherInfoByTeachingId(Integer.parseInt(teachingId), connection);
            current_teacher = Objects.requireNonNull(currentTeacherEntity.currentTeacher).get(0);
            currentTeacherEntity.otherLessons = DbOperation.retrieveOtherLessonsByTeacherInfo(current_teacher.getLessonName(), current_teacher.getTeacherName(), current_teacher.getFacultyId(), connection);
            view.setMustacheModel(currentTeacherEntity);

        } else if (command.startsWith("view_all_lessons_clk")) {
            String teacherName = command.substring(command.indexOf("+") + 1, command.indexOf("%"));
            int facultyId = Integer.parseInt(command.substring(command.indexOf("%") + 1));

            view = new FullList();
            SearchPageEntity searchPageEntity = new SearchPageEntity();

            searchPageEntity.teachers_list = DbOperation.retrieveAllLessonsByTeacherName(teacherName, facultyId, connection);
            view.setMustacheModel(searchPageEntity);
        } else if (command.startsWith("comment_click_clk")) {
            String teachingId = command.substring(command.indexOf("+") + 1);

            view = new TeacherComment();
            currentTeacherEntity.currentTeacher = DbOperation.retrieveTeacherInfoByTeachingId(Integer.parseInt(teachingId), connection);
            current_teacher = Objects.requireNonNull(currentTeacherEntity.currentTeacher).get(0);
            currentTeacherEntity.otherLessons = DbOperation.retrieveOtherLessonsByTeacherInfo(current_teacher.getLessonName(), current_teacher.getTeacherName(), current_teacher.getFacultyId(), connection);
            currentTeacherEntity.teacherFeedbacks = DbOperation.retrieveFeedbackByTeachingId(current_teacher.getTeachingId(), true, connection);
            view.setMustacheModel(currentTeacherEntity);
        } else if (command.startsWith("delete_comment")) {
            int feedbackId = Integer.parseInt(command.substring(command.indexOf("+") + 1));
            DbOperation.removeFeedback(feedbackId, connection);
        } else {

            switch (command) {

                case "sign_in_nav":
                    view = new SignIn();
                    view.setMustacheModel(currentStudentEntity);
                    break;

                case "help_sign_in_nav":
                    view = new HelpSignIn();
                    break;

                case "register_nav":
                    view = new Register();
                    break;

                case "help_register_nav":
                    view = new HelpRegister();
                    break;

                case "home_nav":

                case "log_in_btn":
                    view = showHome();
                    break;

                case "poll_nav":
                    view = new Search();
                    SearchPageEntity searchPageEntity = new SearchPageEntity();
                    searchPageEntity.teachers_list = new ArrayList<>(Objects.requireNonNull(DbOperation.retrieveTeachersList(current_user.getUserId(), true, connection)));
                    view.setMustacheModel(searchPageEntity);
                    break;

                case "profile_nav":

                case "profile_info_tab":
                    view = new ProfileInfo();
                    view.setMustacheModel(currentStudentEntity);
                    break;

                case "profile_comment_history_tab_extended_sec":

                case "profile_comment_history_tab":
                    view = new ProfileCommentHistory();
                    FeedbackEntity feedbackEntity = new FeedbackEntity();
                    feedbackEntity.feedbacks = DbOperation.retrieveMyFeedbacks(current_user.getUserId(), true, connection);
                    feedbackEntity.studentKarma = DbOperation.retrieveMyKarma(current_user.getUserId(), connection);
                    view.setMustacheModel(feedbackEntity);
                    break;

                case "profile_comment_history_tab_non_extended_sec":
                    view = new ProfileCommentHistory2();
                    feedbackEntity = new FeedbackEntity();
                    feedbackEntity.feedbacks = DbOperation.retrieveMyFeedbacks(current_user.getUserId(), false, connection);
                    feedbackEntity.studentKarma = DbOperation.retrieveMyKarma(current_user.getUserId(), connection);
                    view.setMustacheModel(feedbackEntity);
                    break;


                case "profile_setting_tab":
                    view = new ProfileSettings();
                    FacultyEntity facultyEntity = new FacultyEntity();
                    facultyEntity.currentFaculty = DbOperation.retrieveFacultyByUserId(current_user.getUserId(), connection);
                    view.setMustacheModel(facultyEntity);
                    break;

                case "edit_info_btn":
                    view = new ProfileEditInfo();
                    view.setMustacheModel(currentStudentEntity);
                    break;

                case "search_full_list_btn":
                    view = new FullList();
                    searchPageEntity = new SearchPageEntity();
                    searchPageEntity.teachers_list = new ArrayList<>(Objects.requireNonNull(DbOperation.retrieveTeachersList(current_user.getUserId(), false, connection)));
                    view.setMustacheModel(searchPageEntity);
                    break;

                case "make_poll_btn":
                    view = new Score1();
                    view.setMustacheModel(currentTeacherEntity);
                    break;

                case "teacher_information_tab":
                    view = new TeacherInfo();
                    view.setMustacheModel(currentTeacherEntity);
                    break;

                case "teacher_comment_tab_last_sec":

                case "teacher_comment_tab":
                    view = ShowTeacherFeedback(true);
                    break;

                case "teacher_score_tab":
                    view = new TeacherScores();
                    ArrayList<Feedback> feedbacks;
                    feedbacks = DbOperation.retrieveScoreByTeachingId(current_teacher.getTeachingId(), connection);
                    if (feedbacks == null) {
                        feedbacks = new ArrayList<>();
                        Feedback feedback = new Feedback();
                        feedback.setScore1Persian("بدون امتیاز");
                        feedback.setScore2Persian("بدون امتیاز");
                        feedback.setScore3Persian("بدون امتیاز");
                        feedback.setScore4Persian("بدون امتیاز");
                        feedback.setAverageScorePersian("بدون امتیاز");
                        feedbacks.add(feedback);
                        currentTeacherEntity.teacherFeedbacks = feedbacks;
                    } else {
                        currentTeacherEntity.teacherFeedbacks = feedbacks;
//                        Objects.requireNonNull(currentTeacherEntity.teacherFeedbacks).get(0).setScore1Persian(convertToEnglishDigits(BigDecimal.valueOf(currentTeacherEntity.teacherFeedbacks.get(0).getScore1()).setScale(2, RoundingMode.HALF_UP).toString()));
//                        Objects.requireNonNull(currentTeacherEntity.teacherFeedbacks).get(0).setScore2Persian(convertToEnglishDigits(BigDecimal.valueOf(currentTeacherEntity.teacherFeedbacks.get(0).getScore2()).setScale(2, RoundingMode.HALF_UP).toString()));
//                        Objects.requireNonNull(currentTeacherEntity.teacherFeedbacks).get(0).setScore3Persian(convertToEnglishDigits(BigDecimal.valueOf(currentTeacherEntity.teacherFeedbacks.get(0).getScore3()).setScale(2, RoundingMode.HALF_UP).toString()));
//                        Objects.requireNonNull(currentTeacherEntity.teacherFeedbacks).get(0).setScore4Persian(convertToEnglishDigits(BigDecimal.valueOf(currentTeacherEntity.teacherFeedbacks.get(0).getScore4()).setScale(2, RoundingMode.HALF_UP).toString()));
//                        Objects.requireNonNull(currentTeacherEntity.teacherFeedbacks).get(0).setAverageScorePersian(convertToEnglishDigits(BigDecimal.valueOf(currentTeacherEntity.teacherFeedbacks.get(0).getAverageScore()).setScale(2, RoundingMode.HALF_UP).toString()));
                    }
                    view.setMustacheModel(currentTeacherEntity);


                    break;

                case "teacher_comment_tab_best_sec":
                    view = ShowTeacherFeedback(false);
                    break;

                case "view_teacher_circular_btn":
                    view = new CircularTeacherList();
                    CircularEntity circularEntity = new CircularEntity();
                    ArrayList<Teacher> teachers = DbOperation.retrieveTheMostFamousTeachers(current_user.getStudentFacultyId(), false, connection);

                    if (teachers != null) {
                        int teachers_size = teachers.size();
                        if (teachers_size <= 5) {
                            circularEntity.teachers1 = new ArrayList<>(Objects.requireNonNull(teachers).subList(0, teachers_size));
                        } else if (teachers_size <= 10) {
                            circularEntity.teachers1 = new ArrayList<>(Objects.requireNonNull(teachers).subList(0, 5));
                            circularEntity.teachers2 = new ArrayList<>(Objects.requireNonNull(teachers).subList(5, teachers_size));
                        } else if (teachers_size <= 15) {
                            circularEntity.teachers1 = new ArrayList<>(Objects.requireNonNull(teachers).subList(0, 5));
                            circularEntity.teachers2 = new ArrayList<>(Objects.requireNonNull(teachers).subList(5, 10));
                            circularEntity.teachers3 = new ArrayList<>(Objects.requireNonNull(teachers).subList(10, teachers_size));
                        } else {
                            circularEntity.teachers1 = new ArrayList<>(Objects.requireNonNull(teachers).subList(0, 5));
                            circularEntity.teachers2 = new ArrayList<>(Objects.requireNonNull(teachers).subList(5, 10));
                            circularEntity.teachers3 = new ArrayList<>(Objects.requireNonNull(teachers).subList(10, 15));
                            circularEntity.teachers4 = new ArrayList<>(Objects.requireNonNull(teachers).subList(15, teachers_size));
                        }

                    }

                    view.setMustacheModel(circularEntity);

                    break;

                case "view_best_comments":
                    view = new FullCommentListView();
                    HomePageEntity homePageEntity = new HomePageEntity();
                    homePageEntity.feedbacks = DbOperation.retrieveTheMostVotedFeedbacks(current_user.getStudentFacultyId(), false, connection);
                    view.setMustacheModel(homePageEntity);
                    break;


                default:
                    students = DbOperation.retrieveStudentByUserId(userId, connection);
                    boolean isNotRegistered = students == null;
                    if (isNotRegistered) {
                        view = new Register();
                    } else {
                        view = new SignIn();
                        current_user = students.get(0);
                        currentStudentEntity.currentStudent = students;
                        view.setMustacheModel(currentStudentEntity);
                    }
            }
        }

        return view;
    }


    @Override
    public Response onUpdate(ViewUpdate update, String updateCommand, JSONObject pageData, String userId) {

        if (updateCommand.startsWith("delete_comment")) {
            int feedbackId = Integer.parseInt(updateCommand.substring(updateCommand.indexOf("+") + 1));
            DbOperation.removeFeedback(feedbackId, connection);
        } else if (updateCommand.startsWith("up_vote_btn")){
            int feedbackId = Integer.parseInt(updateCommand.substring(updateCommand.indexOf("+")));
            Boolean result = DbOperation.retrieveVoteStatus(current_user.getUserId(), feedbackId, connection);

            int newUpVoteValue;
            Integer newDownVoteValue = null;

            if (result != null){
                if (result){
                    newUpVoteValue = DbOperation.applyUpVoteFeedback(feedbackId, false,connection);
                    DbOperation.deleteVote(current_user.getUserId(), feedbackId, connection);

                }else {
                    newUpVoteValue = DbOperation.applyUpVoteFeedback(feedbackId, true,connection);
                    newDownVoteValue = DbOperation.applyDownVoteFeedback(feedbackId, false, connection);
                    DbOperation.revertVote(current_user.getUserId(), feedbackId, connection);
                }

            } else {
                newUpVoteValue = DbOperation.applyUpVoteFeedback(feedbackId, true,connection);
                DbOperation.sendVote(current_user.getUserId(), feedbackId, true, connection);
            }

            if (newDownVoteValue != null) {
                update.addChildUpdate("red_btn_"+feedbackId, "text", BeheshtRayService.convertToEnglishDigits(String.valueOf(newDownVoteValue)) );

            }

            update.addChildUpdate("green_btn_"+feedbackId, "text", BeheshtRayService.convertToEnglishDigits(String.valueOf(newUpVoteValue)));

//            System.out.println(update.getData());
            String newKarma = DbOperation.retrieveMyKarma(current_user.getUserId(), connection).get(0).getUserKarma();
            update.addChildUpdate("user_karma", "text", newKarma);


            return update;

        } else if (updateCommand.startsWith("down_vote_btn")){
            int feedbackId = Integer.parseInt(updateCommand.substring(updateCommand.indexOf("+")));
            Boolean result = DbOperation.retrieveVoteStatus(current_user.getUserId(), feedbackId, connection);

            Integer newUpVoteValue = null;
            int newDownVoteValue;

            if (result != null){
                if (result){
                    newDownVoteValue = DbOperation.applyDownVoteFeedback(feedbackId, true,connection);
                    newUpVoteValue = DbOperation.applyUpVoteFeedback(feedbackId, false, connection);
                    DbOperation.revertVote(current_user.getUserId(), feedbackId, connection);
                }else {
                    newDownVoteValue = DbOperation.applyDownVoteFeedback(feedbackId, false,connection);
                    DbOperation.deleteVote(current_user.getUserId(), feedbackId, connection);
                }
            } else {
                newDownVoteValue = DbOperation.applyDownVoteFeedback(feedbackId, true,connection);
                DbOperation.sendVote(current_user.getUserId(), feedbackId, false, connection);
            }

            if (newUpVoteValue != null) {
                update.addChildUpdate("green_btn_"+feedbackId, "text", BeheshtRayService.convertToEnglishDigits(String.valueOf(newUpVoteValue)) );

            }

            update.addChildUpdate("red_btn_"+feedbackId, "text", BeheshtRayService.convertToEnglishDigits(String.valueOf(newDownVoteValue)));

//            System.out.println(update.getData());
            String newKarma = DbOperation.retrieveMyKarma(current_user.getUserId(), connection).get(0).getUserKarma();
            update.addChildUpdate("user_karma", "text", newKarma);


            return update;

        }


        else {
            switch (updateCommand) {
                case "change_user_info":

                case "register_new_user":
                    String firstName = pageData.get("first_name").toString().trim();
                    String lastName = pageData.get("last_name").toString().trim();
                    String studentId = pageData.get("student_id").toString().trim();
                    String gender = pageData.get("selected_gender").toString();
                    String faculty = pageData.get("selected_faculty").toString();

                    if (firstName.isEmpty() || lastName.isEmpty() || studentId.isEmpty()) {
                        update.addChildUpdate("error_msg", "text", "فیلدها نمیتواند خالی باشد!");
                        return update;
                    } else {
                        Student student = new Student(userId);
                        student.setStudentFirstName(firstName);
                        student.setStudentLastName(lastName);
                        student.setStudentId(studentId);
                        student.setStudentFacultyId(DbOperation.retrieveFacultyIdByName(faculty, connection));
                        student.setStudentFacultyName(faculty);
                        student.setStudentGender(gender);
                        student.setStudentPhotoURL(gender);
                        if (updateCommand.equals("change_user_info")) {
                            DbOperation.updateUserInfo(student, connection);
                        } else {
                            student.setUserKarma("0");
                            DbOperation.sendUserInfo(student, connection);
                        }
                        current_user = student;
                        students.set(0, current_user);
                        currentStudentEntity.currentStudent = students;

                        return showHome();
                    }


                case "next_poll_btn":

                    String rightLegals = pageData.get("right_legals").toString().trim();
                    String transferContent = pageData.get("transfer_content").toString().trim();
                    String taTeam = pageData.get("ta_team").toString().trim();
                    String suitableExercise = pageData.get("suitable_exercise").toString().trim();

                    if (rightLegals.isEmpty() || transferContent.isEmpty() || taTeam.isEmpty() || suitableExercise.isEmpty()) {
                        update.addChildUpdate("error_msg", "text", "فیلدها نمیتواند خالی باشد!");
                        return update;
                    } else {

                        int rightLegalsNum = Integer.parseInt(rightLegals);
                        int transferContentNum = Integer.parseInt(transferContent);
                        int taTeamNum = Integer.parseInt(taTeam);
                        int suitableExerciseNum = Integer.parseInt(suitableExercise);

                        if ((rightLegalsNum > 100 || rightLegalsNum < 0) || (transferContentNum > 100 || transferContentNum < 0) || (taTeamNum > 100 || taTeamNum < 0) || (suitableExerciseNum > 100 || suitableExerciseNum < 0)) {
                            update.addChildUpdate("error_msg", "text", "امتیازات باید در بازه ۰ تا ۱۰۰ باشند!");
                            return update;
                        } else {
                            View view = new Score2();
                            view.setMustacheModel(currentTeacherEntity);

                            current_feedback = new Feedback();
                            current_feedback.setScore1(rightLegalsNum);
                            current_feedback.setScore2(transferContentNum);
                            current_feedback.setScore3(taTeamNum);
                            current_feedback.setScore4(suitableExerciseNum);

                            return view;
                        }
                    }

                case "create_feedback_btn":

                    String studentScore = pageData.get("student_score").toString().trim();
                    String extendedFeedback = pageData.get("extended_feedback").toString().trim();

                    if (studentScore.isEmpty() && !extendedFeedback.isEmpty()) {
                        update.addChildUpdate("error_msg", "text", "نمره حود را وارد کنید!");

                    } else {

                        update.addChildUpdate("error_msg", "text", "");

                        studentScore = studentScore.isEmpty() ? null : studentScore;
                        extendedFeedback = extendedFeedback.isEmpty() ? null : extendedFeedback;

                        current_feedback.setStudentScore(studentScore);
                        current_feedback.setExtendedFeedback(extendedFeedback);


                        /**/
//                    current_feedback.setTeachingId(current_teacher.getTeachingId());
//                    current_feedback.setUserId(current_user.getUserId());
//                    current_feedback.setUpVotes(0);
//                    current_feedback.setDownVotes(0);
//                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
//                    current_feedback.setPersianDate(convertToEnglishDigits(dtf.format(PersianDate.now())));
//                    DbOperation.sendFeedback(current_feedback, connection);

//                    return new SubmitFeedbackDialog();
                    }

                case "send_feedback_btn":
                    current_feedback.setTeachingId(current_teacher.getTeachingId());
                    current_feedback.setUserId(current_user.getUserId());
                    current_feedback.setUpVotes(0);
                    current_feedback.setDownVotes(0);
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
                    current_feedback.setPersianDate(convertToEnglishDigits(dtf.format(PersianDate.now())));
                    DbOperation.sendFeedback(current_feedback, connection);
                    return ShowTeacherFeedback(true);


                case "search_btn":
                    String searchInputText = pageData.get("search_input_text").toString().trim();
                    View view = new SearchResults();
                    SearchPageEntity searchPageEntity = new SearchPageEntity();
                    searchPageEntity.teachers_list = new ArrayList<>(Objects.requireNonNull(DbOperation.retrieveSearchedTeachersList(searchInputText, current_user.getUserId(), connection)));
                    view.setMustacheModel(searchPageEntity);
                    return view;


//
//                current_feedback.setStudentScore();


//                DbOperation.sendFeedback();


                default:
                    break;
            }
        }


//        if ("nextPage".equals(updateCommand)) {
//            System.out.println("json: " + pageData);
//            System.out.println((pageData.get("right_legals").toString()));
//            if (pageData.get("right_legals").toString().trim().equals("") || pageData.get("transfer_content").toString().trim().equals("") || pageData.get("ta_team").toString().trim().equals("") || pageData.get("suitable_exercise").toString().trim().equals("")) {
//                return new ScoreValueErrorDialog();
//            }
//
//            feedback.score1 = Double.parseDouble(pageData.get("right_legals").toString());
//            feedback.score2 = Double.parseDouble(pageData.get("transfer_content").toString());
//            feedback.score3 = Double.parseDouble(pageData.get("ta_team").toString());
//            feedback.score4 = Double.parseDouble(pageData.get("suitable_exercise").toString());
//
//            if (!((feedback.score1 >= 0 && feedback.score1 <= 100) && (feedback.score2 >= 0 && feedback.score2 <= 100) && (feedback.score3 >= 0 && feedback.score3 <= 100) && (feedback.score4 >= 0 && feedback.score4 <= 100))) {
//
//                return new ScoreValueErrorDialog();
//
//            }
//
//
//            feedback.score_avg = convertToEnglishDigits(String.valueOf((feedback.score1 + feedback.score2 + feedback.score3 + feedback.score4) / 4.0));
//            System.out.println("techer name: " + current_teacher.teacher_name);
//            System.out.println("lesson name: " + current_teacher.lesson_name);
//            feedback.teacher_name = current_teacher.teacher_name;
//            feedback.lesson_name = current_teacher.lesson_name;
//            System.out.println("thing: " + feedback.score1);
//            View view = new Score2();
//            view.setMustacheModel(temp);
//            return view;
//        } else if ("createPoll".equals(updateCommand)) {
//            System.out.println("json: " + pageData);
//
////            System.out.println(pageData.get("studentScore").toString());
//
//            if (pageData.get("student_score").toString().trim().equals("")) {
//                return new StudentScoreValueErrorDialog();
//            }
//
//            if (!(Double.parseDouble(pageData.get("student_score").toString()) >= 0 && Double.parseDouble(pageData.get("student_score").toString()) <= 20)) {
//                return new StudentScoreValueErrorDialog();
//            }
//
//            feedback.student_score = convertToEnglishDigits(pageData.get("student_score").toString());
//            feedback.extended_feedback = pageData.get("extendedFeedback").toString();
//            if (feedback.extended_feedback.trim().isEmpty()) {
//                feedback.extended_feedback = null;
//            }
////            feedback.feedback_key = Integer.parseInt(userId);
//            feedback.upvotes = "۰";
//            feedback.downvotes = "۰";
//            feedback.user_id = userId;
//            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
//            feedback.date_number = convertToEnglishDigits(dtf.format(PersianDate.now()));
//            int number = 0;
//            number = Objects.requireNonNull(DbOperation.retrieveFeedbacksBySelf2(userId, connection)).size();
//            feedback.feedback_id = userId + "_" + number;
//            Date date = new Date();
//            feedback.created_time = String.valueOf(date.getTime());
//            feedback.diff_votes = 0;
//
//            DbOperation.sendFeedBack(feedback, connection);
//
//            View view = new TeacherComment();
//            feedback_id = userId;
//
//            CommentEntity commentEntity = new CommentEntity();
//
//            commentEntity.feedbacks = DbOperation.retrieveFeedbacksByTeacher(current_teacher.lesson_name, current_teacher.teacher_name, connection);
//            commentEntity.lesson_name = current_teacher.lesson_name;
//            commentEntity.teacher_name = current_teacher.teacher_name;
//
//            view.setMustacheModel(commentEntity);
//            return view;
//        } else if ("polling".equals(updateCommand)) {
//
//            View view = new Search();
//            SearchPageEntity searchPageEntity = new SearchPageEntity();
////            searchPageEntity.teachers_recent = new ArrayList<>(Objects.requireNonNull(DbOperation.retrieveTeachers(connection)).subList(0, 3));
//            searchPageEntity.teachers_list = new ArrayList<>(Objects.requireNonNull(DbOperation.retrieveTeachers(connection)).subList(0, 3));
//            view.setMustacheModel(searchPageEntity);
//            return view;
//
//        } else if ("seeAll".equals(updateCommand)) {
//            View view = new FullList();
//            teachers = DbOperation.retrieveTeachers(connection);
//            temp.teachers = teachers;
//            System.out.println(teachers);
//            System.out.println("get: " + teachers.get(0).teacher_key);
//            view.setMustacheModel(temp);
//            return view;
//        } else if (updateCommand.startsWith("teacher_profile_info")) {
//            View view = new TeacherInfo();
//            System.out.println("uooo" + updateCommand);
//            String selectedid = updateCommand.substring(updateCommand.indexOf("+") + 1);
//            System.out.println("jhjhjh " + selectedid);
//            teachers = DbOperation.retrieveTeacherByKey(selectedid, connection);
//            current_teacher = teachers.get(0);
//            temp.teachers = teachers;
//            temp.otherLessons = DbOperation.retrieveLessonsByTeacherNotRepetitive(current_teacher.teacher_name, current_teacher.lesson_name, connection);
//            temp.current_lesson = current_teacher.lesson_name;
//            view.setMustacheModel(temp);
//            return view;
//        } else if ("makePoll".equals(updateCommand)) {
//            View view = new Score1();
//            view.setMustacheModel(temp);
//            return view;
//        } else if ("teacherCommentsTab".equals(updateCommand) || "last_comments_teacher".equals(updateCommand)) {
//            View view = new TeacherComment();
////            System.out.println("currentTeacher: " + current_teacher.teacher_name);
//            CommentEntity commentEntity = new CommentEntity();
//            commentEntity.feedbacks = DbOperation.retrieveFeedbacksByTeacher(current_teacher.lesson_name, current_teacher.teacher_name, connection);
//            commentEntity.lesson_name = current_teacher.lesson_name;
//            commentEntity.teacher_name = current_teacher.teacher_name;
//
//            view.setMustacheModel(commentEntity);
////            System.out.println("MAN" + student.feedbacks.get(0).feedback_id);
////            student.image_url = DbOperation.retrieveTeacherURLImage(current_teacher.lesson_name, connection);
//            return view;
//        } else if ("best_comments_teacher".equals(updateCommand)) {
//            View view = new TeacherComment2();
//            CommentEntity commentEntity = new CommentEntity();
//            commentEntity.feedbacks = DbOperation.retrieveFeedbacksByTeacherMostVoted(current_teacher.lesson_name, current_teacher.teacher_name, connection);
//            commentEntity.lesson_name = current_teacher.lesson_name;
//            commentEntity.teacher_name = current_teacher.teacher_name;
//
//            view.setMustacheModel(commentEntity);
//            return view;
//        } else if ("teacherInformationTab".equals(updateCommand)) {
//            View view = new TeacherInfo();
//            view.setMustacheModel(temp);
//            return view;
//        } else if ("teacherScoresTab".equals(updateCommand)) {
//
//            ArrayList<Double> scores = DbOperation.retrieveScoreMagic(current_teacher.teacher_name, current_teacher.lesson_name, connection);
//            temp.score_overall = convertToEnglishDigits(BigDecimal.valueOf(Objects.requireNonNull(scores).get(0)).setScale(2, RoundingMode.HALF_UP).toString()) + "/۱۰۰";
//            temp.score1 = convertToEnglishDigits(BigDecimal.valueOf(scores.get(1)).setScale(2, RoundingMode.HALF_UP).toString()) + "/۱۰۰";
//            temp.score2 = convertToEnglishDigits(BigDecimal.valueOf(scores.get(2)).setScale(2, RoundingMode.HALF_UP).toString()) + "/۱۰۰";
//            temp.score3 = convertToEnglishDigits(BigDecimal.valueOf(scores.get(3)).setScale(2, RoundingMode.HALF_UP).toString()) + "/۱۰۰";
//            temp.score4 = convertToEnglishDigits(BigDecimal.valueOf(scores.get(4)).setScale(2, RoundingMode.HALF_UP).toString()) + "/۱۰۰";
//
//            View view = new TeacherScores();
//            view.setMustacheModel(temp);
//            return view;
//        } else if ("home".equals(updateCommand) || "acceptConditions".equals(updateCommand)) {
//            View view = new Home();
//            //DbOperation.retrieveTheMostFamousTeachers(connection);
////            System.out.println(DbOperation.retrieveTeacherURLImage("حامد ملک", connection));
//            HomePageEntity homePageEntity = new HomePageEntity();
//            homePageEntity.teachers = new ArrayList<>(Objects.requireNonNull(DbOperation.retrieveTheMostFamousTeachers(connection)).subList(0, 5));
//            try {
//                homePageEntity.feedbacks = new ArrayList<>(Objects.requireNonNull(DbOperation.retrieveFeedbacksMostVoted(connection)).subList(0, 3));
//            } catch (Exception e) {
//                try {
//                    homePageEntity.feedbacks = new ArrayList<>(Objects.requireNonNull(DbOperation.retrieveFeedbacksMostVoted(connection)).subList(0, 2));
//                } catch (Exception e1) {
//                    try {
//                        homePageEntity.feedbacks = new ArrayList<>(Objects.requireNonNull(DbOperation.retrieveFeedbacksMostVoted(connection)).subList(0, 1));
//                    } catch (Exception e2) {
//
//                    }
//                }
//            }
//            view.setMustacheModel(homePageEntity);  // query here
//            return view;
//        } else if ("studentCommentHistoryTab".equals(updateCommand)) {
//            View view = new ProfileCommentHistory();
//
//            student.feedbacks = DbOperation.retrieveFeedbacksBySelf(userId, connection);
//            int score = 0;
//            for (Feedback f : student.feedbacks) {
//                score += (Integer.parseInt(f.upvotes) - Integer.parseInt(f.downvotes));
//            }
//
//            if (score > 0) {
//                student.user_karma = "+";
//                student.user_karma += convertToEnglishDigits(String.valueOf(score));
//            } else {
//                student.user_karma = convertToEnglishDigits(String.valueOf(score));
//            }
//            view.setMustacheModel(student);
//            return view;
//        } else if ("studentInformationTab".equals(updateCommand) || "profile_info".equals(updateCommand)) {
//            View view = new ProfileInfo();
////            students = DbOperation.retrieveVoteStatus(userId, connection); //
////            System.out.println("sggg: " + students.get(0).student_name);
//            students.get(0).student_id = convertToEnglishDigits(students.get(0).student_id);
////            tempStudent.students = students;
//            view.setMustacheModel(tempStudent);
//            return view;
//        } else if ("studentSettingsTab".equals(updateCommand)) {
//            View view = new ProfileSettings();
//            view.setMustacheModel(tempStudent);
//            return view;
//        } else if (updateCommand.startsWith("upvote_comment")) {
//
//            String upvotes_status;
//            String downvotes_status;
//            boolean already_upvoted = false;
//            boolean already_downvoted = false;
//            upvotes_status = Objects.requireNonNull(DbOperation.retrieveVoteStatus(userId, connection)).get(0).student_upvotes;
//            downvotes_status = Objects.requireNonNull(DbOperation.retrieveVoteStatus(userId, connection)).get(0).student_downvotes;
//            if (upvotes_status == null) {
//                upvotes_status = "";
//            }
//            if (downvotes_status == null) {
//                downvotes_status = "";
//            }
//
////            System.out.println(upvotes_status);
////            System.out.println(downvotes_status);
//
//            String[] upvote_list = upvotes_status.split(",");
//            String[] downvote_list = downvotes_status.split(",");
//
////            System.out.println(Arrays.toString(upvote_list));
////            System.out.println(Arrays.toString(downvote_list);
//
//
//            String selectedid = updateCommand.substring(updateCommand.indexOf("+") + 1);
//
//            for (String str : upvote_list) {
//                if (str.equals(selectedid)) {
//                    already_upvoted = true;
//                    break;
//                }
//            }
//
//            for (String str : downvote_list) {
//                if (str.equals(selectedid)) {
//                    already_downvoted = true;
//                    break;
//                }
//            }
//
//            if (already_upvoted) {
//                System.out.println("Hello1");
//
//                String upvote;
//                upvote = Objects.requireNonNull(DbOperation.retrieveFeedbacksByFeedbackId(selectedid, connection)).get(0).upvotes;
//                upvote = String.valueOf(Integer.parseInt(upvote) - 1);
//                upvote = convertToEnglishDigits(upvote);
//
//                int upvote_diff_num = Objects.requireNonNull(DbOperation.retrieveFeedbacksByFeedbackId(selectedid, connection)).get(0).diff_votes - 1;
//                DbOperation.updateUpvotes(selectedid, upvote_diff_num, upvote, connection);
//
//                upvotes_status = upvotes_status.replace(selectedid + ",", "");
//                DbOperation.updateUpvotesListForUser(userId, upvotes_status, connection);
//                // already upvoted
//
//            } else if (already_downvoted) {
//                System.out.println("Hello2");
//
//                String upvote;
//                upvote = Objects.requireNonNull(DbOperation.retrieveFeedbacksByFeedbackId(selectedid, connection)).get(0).downvotes;
//                upvote = String.valueOf(Integer.parseInt(upvote) - 1);
//                upvote = convertToEnglishDigits(upvote);
//                int upvote_diff_num = Objects.requireNonNull(DbOperation.retrieveFeedbacksByFeedbackId(selectedid, connection)).get(0).diff_votes + 1;
//                DbOperation.updateDownvotes(selectedid, upvote_diff_num, upvote, connection);
//
//                downvotes_status = downvotes_status.replace(selectedid + ",", "");
//                DbOperation.updateDownvotesListForUser(userId, downvotes_status, connection);
//
//                upvote = Objects.requireNonNull(DbOperation.retrieveFeedbacksByFeedbackId(selectedid, connection)).get(0).upvotes;
//                upvote = String.valueOf(Integer.parseInt(upvote) + 1);
//                upvote = convertToEnglishDigits(upvote);
//
//                upvote_diff_num = Objects.requireNonNull(DbOperation.retrieveFeedbacksByFeedbackId(selectedid, connection)).get(0).diff_votes + 1;
//                DbOperation.updateUpvotes(selectedid, upvote_diff_num, upvote, connection);
//
//                upvotes_status += selectedid + ',';
//                DbOperation.updateUpvotesListForUser(userId, upvotes_status, connection);
//
//            } else {
//                System.out.println("Hello3");
//                String upvote;
//                upvote = Objects.requireNonNull(DbOperation.retrieveFeedbacksByFeedbackId(selectedid, connection)).get(0).upvotes;
//                upvote = String.valueOf(Integer.parseInt(upvote) + 1);
//                upvote = convertToEnglishDigits(upvote);
//
//                int upvote_diff_num = Objects.requireNonNull(DbOperation.retrieveFeedbacksByFeedbackId(selectedid, connection)).get(0).diff_votes + 1;
//                DbOperation.updateUpvotes(selectedid, upvote_diff_num, upvote, connection);
//
//                upvotes_status += selectedid + ',';
//                System.out.println("################");
//                System.out.println(userId);
//                System.out.println(upvotes_status);
//                DbOperation.updateUpvotesListForUser(userId, upvotes_status, connection);
//            }
//
//            if (updateCommand.startsWith("upvote_comment_teacher")) {
//                CommentEntity commentEntity = new CommentEntity();
//
//                commentEntity.feedbacks = DbOperation.retrieveFeedbacksByTeacher(current_teacher.lesson_name, current_teacher.teacher_name, connection);
//                commentEntity.lesson_name = current_teacher.lesson_name;
//                commentEntity.teacher_name = current_teacher.teacher_name;
//
//                View view = new TeacherComment();
//                view.setMustacheModel(commentEntity);
//                return view;
//
//            } else if (updateCommand.startsWith("upvote_comment_student")) {
//
//                student.feedbacks = DbOperation.retrieveFeedbacksBySelf(userId, connection);
//                int score = 0;
//                for (Feedback f : student.feedbacks) {
//                    score += (Integer.parseInt(f.upvotes) - Integer.parseInt(f.downvotes));
//                }
//
//                if (score > 0) {
//                    student.user_karma = "+";
//                    student.user_karma += convertToEnglishDigits(String.valueOf(score));
//                } else {
//                    student.user_karma = convertToEnglishDigits(String.valueOf(score));
//                }
//                View view = new ProfileCommentHistory();
//                view.setMustacheModel(student);
//                return view;
//            } else if (updateCommand.startsWith("upvote_comment_home")) {
//                View view = new Home();
//                HomePageEntity homePageEntity = new HomePageEntity();
//                homePageEntity.teachers = new ArrayList<>(Objects.requireNonNull(DbOperation.retrieveTheMostFamousTeachers(connection)).subList(0, 5));
//                try {
//                    homePageEntity.feedbacks = new ArrayList<>(Objects.requireNonNull(DbOperation.retrieveFeedbacksMostVoted(connection)).subList(0, 3));
//                } catch (Exception e) {
//                    try {
//                        homePageEntity.feedbacks = new ArrayList<>(Objects.requireNonNull(DbOperation.retrieveFeedbacksMostVoted(connection)).subList(0, 2));
//                    } catch (Exception e1) {
//                        try {
//                            homePageEntity.feedbacks = new ArrayList<>(Objects.requireNonNull(DbOperation.retrieveFeedbacksMostVoted(connection)).subList(0, 1));
//                        } catch (Exception e2) {
//
//                        }
//                    }
//                }
//                view.setMustacheModel(homePageEntity);  // query here
//                return view;
//            }
//
//        } else if (updateCommand.startsWith("downvote_comment")) {
//
//
//            String upvotes_status;
//            String downvotes_status;
//            boolean already_upvoted = false;
//            boolean already_downvoted = false;
//            upvotes_status = Objects.requireNonNull(DbOperation.retrieveVoteStatus(userId, connection)).get(0).student_upvotes;
//            downvotes_status = Objects.requireNonNull(DbOperation.retrieveVoteStatus(userId, connection)).get(0).student_downvotes;
//            if (upvotes_status == null) {
//                upvotes_status = "";
//            }
//            if (downvotes_status == null) {
//                downvotes_status = "";
//            }
//
////            System.out.println(upvotes_status);
////            System.out.println(downvotes_status);
//
//            String[] upvote_list = upvotes_status.split(",");
//            String[] downvote_list = downvotes_status.split(",");
//
////            System.out.println(Arrays.toString(upvote_list));
////            System.out.println(Arrays.toString(downvote_list);
//
//
//            String selectedid = updateCommand.substring(updateCommand.indexOf("+") + 1);
//
//            for (String str : upvote_list) {
//                if (str.equals(selectedid)) {
//                    already_upvoted = true;
//                    break;
//                }
//            }
//
//            for (String str : downvote_list) {
//                if (str.equals(selectedid)) {
//                    already_downvoted = true;
//                    break;
//                }
//            }
//
//            if (already_downvoted) {
//
//                String upvote;
//                upvote = Objects.requireNonNull(DbOperation.retrieveFeedbacksByFeedbackId(selectedid, connection)).get(0).downvotes;
//                upvote = String.valueOf(Integer.parseInt(upvote) - 1);
//                upvote = convertToEnglishDigits(upvote);
//                int upvote_diff_num = Objects.requireNonNull(DbOperation.retrieveFeedbacksByFeedbackId(selectedid, connection)).get(0).diff_votes + 1;
//                DbOperation.updateDownvotes(selectedid, upvote_diff_num, upvote, connection);
//
//                downvotes_status = downvotes_status.replace(selectedid + ",", "");
//                DbOperation.updateDownvotesListForUser(userId, downvotes_status, connection);
//
//
//            } else if (already_upvoted) {
//
//                String upvote;
//                upvote = Objects.requireNonNull(DbOperation.retrieveFeedbacksByFeedbackId(selectedid, connection)).get(0).upvotes;
//                upvote = String.valueOf(Integer.parseInt(upvote) - 1);
//                upvote = convertToEnglishDigits(upvote);
//                int upvote_diff_num = Objects.requireNonNull(DbOperation.retrieveFeedbacksByFeedbackId(selectedid, connection)).get(0).diff_votes - 1;
//                DbOperation.updateUpvotes(selectedid, upvote_diff_num, upvote, connection);
//
//                upvotes_status = upvotes_status.replace(selectedid + ",", "");
//                DbOperation.updateUpvotesListForUser(userId, upvotes_status, connection);
//
//                upvote = Objects.requireNonNull(DbOperation.retrieveFeedbacksByFeedbackId(selectedid, connection)).get(0).downvotes;
//                upvote = String.valueOf(Integer.parseInt(upvote) + 1);
//                upvote = convertToEnglishDigits(upvote);
//                upvote_diff_num = Objects.requireNonNull(DbOperation.retrieveFeedbacksByFeedbackId(selectedid, connection)).get(0).diff_votes - 1;
//                DbOperation.updateDownvotes(selectedid, upvote_diff_num, upvote, connection);
//
//                downvotes_status += selectedid + ',';
//                DbOperation.updateDownvotesListForUser(userId, downvotes_status, connection);
//
//            } else {
//
//
//                String upvote;
//                upvote = Objects.requireNonNull(DbOperation.retrieveFeedbacksByFeedbackId(selectedid, connection)).get(0).downvotes;
//                upvote = String.valueOf(Integer.parseInt(upvote) + 1);
//                upvote = convertToEnglishDigits(upvote);
////            System.out.println("upvote: " + upvote  +"    feedback" + selectedid);
//                int upvote_diff_num = Objects.requireNonNull(DbOperation.retrieveFeedbacksByFeedbackId(selectedid, connection)).get(0).diff_votes - 1;
//                DbOperation.updateDownvotes(selectedid, upvote_diff_num, upvote, connection);
//
//                downvotes_status += selectedid + ',';
//                DbOperation.updateDownvotesListForUser(userId, downvotes_status, connection);
//            }
//            if (updateCommand.startsWith("downvote_comment_teacher")) {
//                CommentEntity commentEntity = new CommentEntity();
//
//                commentEntity.feedbacks = DbOperation.retrieveFeedbacksByTeacher(current_teacher.lesson_name, current_teacher.teacher_name, connection);
//                commentEntity.lesson_name = current_teacher.lesson_name;
//                commentEntity.teacher_name = current_teacher.teacher_name;
//
//                View view = new TeacherComment();
//                view.setMustacheModel(commentEntity);
//
//                return view;
//
//            } else if (updateCommand.startsWith("downvote_comment_student")) {
//
//                student.feedbacks = DbOperation.retrieveFeedbacksBySelf(userId, connection);
//                int score = 0;
//                for (Feedback f : student.feedbacks) {
//                    score += (Integer.parseInt(f.upvotes) - Integer.parseInt(f.downvotes));
//                }
//
//                if (score > 0) {
//                    student.user_karma = "+";
//                    student.user_karma += convertToEnglishDigits(String.valueOf(score));
//                } else {
//                    student.user_karma = convertToEnglishDigits(String.valueOf(score));
//                }
//                View view = new ProfileCommentHistory();
//                view.setMustacheModel(student);
//                return view;
//            } else if (updateCommand.startsWith("downvote_comment_home")) {
//                View view = new Home();
//                HomePageEntity homePageEntity = new HomePageEntity();
//                homePageEntity.teachers = new ArrayList<>(Objects.requireNonNull(DbOperation.retrieveTheMostFamousTeachers(connection)).subList(0, 5));
//                try {
//                    homePageEntity.feedbacks = new ArrayList<>(Objects.requireNonNull(DbOperation.retrieveFeedbacksMostVoted(connection)).subList(0, 3));
//                } catch (Exception e) {
//                    try {
//                        homePageEntity.feedbacks = new ArrayList<>(Objects.requireNonNull(DbOperation.retrieveFeedbacksMostVoted(connection)).subList(0, 2));
//                    } catch (Exception e1) {
//                        try {
//                            homePageEntity.feedbacks = new ArrayList<>(Objects.requireNonNull(DbOperation.retrieveFeedbacksMostVoted(connection)).subList(0, 1));
//                        } catch (Exception e2) {
//
//                        }
//                    }
//                }
//                view.setMustacheModel(homePageEntity);  // query here
//                return view;
//            }
//
//        } else if (updateCommand.startsWith("deleteComment")) {
//            String selectedid = updateCommand.substring(updateCommand.indexOf("+") + 1);
//            DbOperation.deleteExtendedVote(selectedid, connection);
//
//
//            student.feedbacks = DbOperation.retrieveFeedbacksBySelf(userId, connection);
//
//            int score = 0;
//            for (Feedback f : student.feedbacks) {
//                score += (Integer.parseInt(f.upvotes) - Integer.parseInt(f.downvotes));
//            }
//
//            if (score > 0) {
//                student.user_karma = "+";
//                student.user_karma += convertToEnglishDigits(String.valueOf(score));
//            } else {
//                student.user_karma = convertToEnglishDigits(String.valueOf(score));
//            }
//            View view = new ProfileCommentHistory();
//            view.setMustacheModel(student);
//            return view;
//
//        } else if (updateCommand.startsWith("teacherViewAllLessons")) {
//            String selectedid = updateCommand.substring(updateCommand.indexOf("+") + 1);
//            temp.teachers = DbOperation.retrieveLessonsByTeacher(selectedid, connection);
//            View view = new FullList();
//            view.setMustacheModel(temp);
//            return view;
//        } else if (updateCommand.equals("search_button")) {
//            String search_input = pageData.get("search_input").toString();
//            System.out.println(search_input);
//            temp.teachers = DbOperation.search(search_input, connection);
//            View view = new SearchResults();
//            view.setMustacheModel(temp);
//            return view;
//        } else if (updateCommand.startsWith("big_comment_to_teacher")) {
//            String selectedid = updateCommand.substring(updateCommand.indexOf("+") + 1, updateCommand.indexOf("%"));
//            String selectedid_2 = updateCommand.substring(updateCommand.indexOf("%") + 1);
//
//            System.out.println(selectedid);
//            System.out.println(selectedid_2);
//
//            CommentEntity commentEntity = new CommentEntity();
//            temp.teachers = Objects.requireNonNull(DbOperation.retrieveTeacherByNameAndLesson(selectedid, selectedid_2, connection));
//            current_teacher = Objects.requireNonNull(DbOperation.retrieveTeacherByNameAndLesson(selectedid, selectedid_2, connection)).get(0);
//            temp.otherLessons = DbOperation.retrieveLessonsByTeacherNotRepetitive(current_teacher.teacher_name, current_teacher.lesson_name, connection);
//            temp.current_lesson = current_teacher.lesson_name;
//
//            commentEntity.feedbacks = DbOperation.retrieveFeedbacksByTeacher(current_teacher.lesson_name, current_teacher.teacher_name, connection);
//            commentEntity.lesson_name = current_teacher.lesson_name;
//            commentEntity.teacher_name = current_teacher.teacher_name;
//
//
//            View view = new TeacherComment();
//            view.setMustacheModel(commentEntity);
//
//            return view;
//
////        } else if (updateCommand.equals("register_new_user")) {
////            Student student = new Student();
////
////            student.student_name = pageData.get("first_name").toString().trim() + " " + pageData.get("last_name").toString().trim();
////            student.student_id = pageData.get("student_number").toString();
////            student.student_faculty = "مهندسی کامپیوتر";
////
////
////            student.student_gender = pageData.get("selected_gender").toString();
////            if (student.student_gender.equals("مرد")) {
////                student.student_photo = "https://s4.uupload.ir/files/cfee5087-8773-4fb3-ac5e-63372d889b1f_ks1c.png";
////            } else if (student.student_gender.equals("زن")) {
////                student.student_photo = "https://s4.uupload.ir/files/3b786101-e336-4e3d-96bb-a73d2227b8d2_n9a3.png";
////            } else {
////                student.student_photo = "https://s4.uupload.ir/files/9446101f-27b4-4f8f-9761-0397d7ea932e_mcg1.png";
////            }
////            student.user_id = userId;
////            DbOperation.sendUserInfo(student, connection);
////
////            students = DbOperation.retrieveVoteStatus(userId, connection);
////            tempStudent.students = students;
////
////
////            View view = new Home();
////            HomePageEntity homePageEntity = new HomePageEntity();
////            homePageEntity.teachers = new ArrayList<>(Objects.requireNonNull(DbOperation.retrieveTheMostFamousTeachers(connection)).subList(0, 5));
////            try {
////                homePageEntity.feedbacks = new ArrayList<>(Objects.requireNonNull(DbOperation.retrieveFeedbacksMostVoted(connection)).subList(0, 3));
////            } catch (Exception e) {
////                try {
////                    homePageEntity.feedbacks = new ArrayList<>(Objects.requireNonNull(DbOperation.retrieveFeedbacksMostVoted(connection)).subList(0, 2));
////                } catch (Exception e1) {
////                    try {
////                        homePageEntity.feedbacks = new ArrayList<>(Objects.requireNonNull(DbOperation.retrieveFeedbacksMostVoted(connection)).subList(0, 1));
////                    } catch (Exception e2) {
////
////                    }
////                }
////            }
////            view.setMustacheModel(homePageEntity);  // query here
////            return view;
//        } else if (updateCommand.equals("logout")) {
//            View view = new SignoutDialog();
////            View view = new SignInUp();
////            tempStudent.students = students;
////            view.setMustacheModel(tempStudent);
//            return view;
//        } else if (updateCommand.equals("help_register")) {
//            return new HelpRegister();
////        } else if (updateCommand.equals("help_sign_in")) {
////            return new HelpSignIn();
////        } else if (updateCommand.equals("sign_in")) {
////            students = DbOperation.retrieveVoteStatus(userId, connection);
////            View view = new SignIn();
////            tempStudent.students = students;
////            view.setMustacheModel(tempStudent);
////            return view;
//
//
//        } else if (updateCommand.equals("register")) {
//            return new Register();
//
//        } else if (updateCommand.equals("bestTAs")) {
//            View view = new FullListWithNumber();
//            teachers = DbOperation.retrieveByTaTeam(connection);
//            temp.teachers = teachers;
//            temp.numberTitle = "میانگین امتیاز: ";
//            System.out.println("sag: " + temp.teachers.get(0).teacher_name);
//            view.setMustacheModel(temp);
//            return view;
//        } else if (updateCommand.equals("popularLessons")) {
//            View view = new FullListWithNumber();
//            teachers = DbOperation.retrieveTheMostFamousTeachersLessons(connection);
//            temp.numberTitle = "میانگین امتیاز: ";
//            temp.teachers = teachers;
//            System.out.println("sag: " + temp.teachers.get(0).teacher_name);
//            view.setMustacheModel(temp);
//            return view;
//        } else if (updateCommand.equals("MostCommentedTeachers")) {
//            View view = new FullListWithNumber();
//            teachers = DbOperation.retrieveMostCommentedTeachers(connection);
//            temp.teachers = teachers;
//            temp.numberTitle = "تعداد نظرات: ";
//            System.out.println("sag: " + temp.teachers.get(0).teacher_name);
//            view.setMustacheModel(temp);
//            return view;
//        } else if (updateCommand.equals("LeastCommentedTeachers")) {
//            View view = new FullListWithNumber();
//            teachers = DbOperation.retrieveLeastCommentedTeachers(connection);
//            temp.teachers = teachers;
//            temp.numberTitle = "تعداد نظرات: ";
//            System.out.println("sag: " + temp.teachers.get(0).teacher_name);
//            view.setMustacheModel(temp);
//            return view;
//
//        } else if (updateCommand.equals("ViewTeacherCircular")) {
//
//            View view = new CircularTeacherList();
//            CircularEntity circularEntity = new CircularEntity();
//            ArrayList<Teacher> teachers = DbOperation.retrieveTheMostFamousTeachers2(connection);
//            circularEntity.teachers1 = new ArrayList<>(Objects.requireNonNull(teachers).subList(0, 5));
//            circularEntity.teachers2 = new ArrayList<>(Objects.requireNonNull(teachers).subList(5, 10));
//            circularEntity.teachers3 = new ArrayList<>(Objects.requireNonNull(teachers).subList(10, 15));
////            circularEntity.teachers4 = new ArrayList<>(Objects.requireNonNull(teachers).subList(15, 20));
//
//            view.setMustacheModel(circularEntity);
//            return view;
//
//
//        } else if (updateCommand.equals("ViewFullBestComments")) {
//            View view = new FullCommentListView();
//            FullCommentEntity fullCommentEntity = new FullCommentEntity();
//            fullCommentEntity.feedbacks = DbOperation.retrieveFeedbacksMostVoted(connection);
//            view.setMustacheModel(fullCommentEntity);
//            return view;
//        } else if (updateCommand.equals("ViewFullWorstComments")) {
//            View view = new FullCommentListView();
//            FullCommentEntity fullCommentEntity = new FullCommentEntity();
//            fullCommentEntity.feedbacks = DbOperation.retrieveFeedbacksLeastVoted(connection);
//            view.setMustacheModel(fullCommentEntity);
//            return view;
//
//        } else if (updateCommand.equals("SubmitDialogShow")) {
//            return new SubmitFeedbackDialog();
//        } else if (updateCommand.equals("DeleteDialogShow")) {
//            return new DeleteCommentDialog();
//        } else if (updateCommand.equals("ScoreValueDialogShow")) {
//            return new ScoreValueErrorDialog();
//        } else if (updateCommand.equals("StudentScoreValueDialogShow")) {
//            return new StudentScoreValueErrorDialog();
//        } else if (updateCommand.equals("SignOutDialogShow")) {
//            return new SignoutDialog();
//        } else if (updateCommand.equals("WelcomeDialogShow")) {
//            return new WelcomeDialog();
//        }


        return update;
    }


    public static String convertToEnglishDigits(String value) {
        return value.replace("1", "۱")
                .replace("2", "۲")
                .replace("3", "۳")
                .replace("4", "۴")
                .replace("5", "۵")
                .replace("6", "۶")
                .replace("7", "۷")
                .replace("8", "۸")
                .replace("9", "۹")
                .replace("0", "۰");
    }


    public void serializeUser(Student student) {
        try {
            FileOutputStream fileOut = new FileOutputStream("/main/java/ir/sample/app/Beheshtray");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(student);
            out.close();
            fileOut.close();
            System.out.printf("Serialized data is saved in /tmp/employee.ser");
        } catch (IOException i) {
            i.printStackTrace();
        }
    }

    public Student deserializeUser() {
        Student student = null;
        try {
            FileInputStream fileIn = new FileInputStream("/main\\java\\ir\\sample\\app\\BeheshtRay\\database\\current_user.ser");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            student = (Student) in.readObject();
            in.close();
            fileIn.close();
            return student;
        } catch (IOException i) {
            i.printStackTrace();
            return null;
        } catch (ClassNotFoundException c) {
            System.out.println("Employee class not found");
            c.printStackTrace();
            return null;
        }

    }

    public View showHome() {
        View view = new Home();
        HomePageEntity homePageEntity = new HomePageEntity();
        homePageEntity.teachers = new ArrayList<>(Objects.requireNonNull(DbOperation.retrieveTheMostFamousTeachers(current_user.getStudentFacultyId(), true, connection)));
        homePageEntity.feedbacks = new ArrayList<>(Objects.requireNonNull(DbOperation.retrieveTheMostVotedFeedbacks(current_user.getStudentFacultyId(), true, connection)));
        view.setMustacheModel(homePageEntity);
        return view;
    }

    public View ShowTeacherFeedback(boolean isSortedByDate) {
        View view = isSortedByDate ? new TeacherComment() : new TeacherComment2();
        currentTeacherEntity.teacherFeedbacks = DbOperation.retrieveFeedbackByTeachingId(current_teacher.getTeachingId(), isSortedByDate, connection);
        view.setMustacheModel(currentTeacherEntity);
        return view;
    }

}

