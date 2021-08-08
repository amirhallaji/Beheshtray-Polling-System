package ir.sample.app.BeheshtRay.services;

import com.github.mfathi91.time.PersianDate;
import ir.appsan.sdk.*;
import ir.sample.app.BeheshtRay.database.DatabaseManager;
import ir.sample.app.BeheshtRay.database.DbOperation;
import ir.sample.app.BeheshtRay.models.*;
import ir.sample.app.BeheshtRay.views.*;
import org.json.simple.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Array;
import java.sql.Connection;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class BeheshtRayService extends APSService {

    public static ArrayList<String> commentIds = new ArrayList<>();
    Teacher teacher = new Teacher();
    Student student = new Student();
    Comment comment = new Comment("34");
    Feedback feedback = new Feedback();
    ArrayList<Feedback> feedbacks = new ArrayList<>();
    ArrayList<Student> students = new ArrayList<>();
    ArrayList<Teacher> teachers = new ArrayList<>();
    Temp<Teacher> temp = new Temp<>();
    TempStudent<Student> tempStudent = new TempStudent<>();
    Teacher current_teacher;

    String feedback_id = "";

    Connection connection = DatabaseManager.getConnection();
    boolean allowmake = true;
    private String userId;

    public BeheshtRayService(String channelName) {
        super(channelName);
    }

    @Override
    public String getServiceName() {
        return "app:09384158428:Beheshti Poll Service";
    }

    @Override
    public View onCreateView(String command, JSONObject pageData, String userId) {
        String rightLegals = "", transferContent = "", taTeam = "", suitableExercise = "";
        View view;

        switch (command) {
            case "acceptConditions":
                System.out.println("case acceptedConditions");
                view = new Home();
                break;

            case "home":
                System.out.println("case home");
                view = new Home();
                break;

            case "enter":
                System.out.println("case enter");
                view = new TeacherInfo();
                break;

            case "profile":
                System.out.println("case profile");
                view = new ProfileInfo();
                break;

            case "polling":
                System.out.println("case polling");

                view = new Score1(); // changed here
                break;

            case "makePoll":
                System.out.println("create poll");
                view = new Score1();

//                rightLegals = pageData.get("right_legals").toString();
//                transferContent = pageData.get("transfer_content").toString();
//                taTeam = pageData.get("ta_team").toString();
//                suitableExercise = pageData.get("suitable_exercise").toString();
//
//                System.out.println("contents received: " + rightLegals);
//                System.out.println("contents received: " + transferContent);
//                System.out.println("contents received: " + taTeam);
//                System.out.println("contents received: " + suitableExercise);

                break;
            case "createPoll":
                System.out.println("contents received: " + rightLegals);
                System.out.println("contents received: " + transferContent);
                System.out.println("contents received: " + taTeam);
                System.out.println("contents received: " + suitableExercise);
                view = new SignInUp();

                break;

            case "nextPage":
                System.out.println("case nextPage");
                view = new Score2();
                break;

            default:
                System.out.println("default case");
                students = DbOperation.retrieveVoteStatus(userId, connection);
                if (Objects.requireNonNull(students).size() == 0) {
                    view = new Register();
                } else {
                    view = new SignInUp();
                    tempStudent.students = students;
                    view.setMustacheModel(tempStudent);
                }

        }

        return view;
    }


    @Override
    public Response onUpdate(ViewUpdate update, String updateCommand, JSONObject pageData, String userId) {
        if ("nextPage".equals(updateCommand)) {
            System.out.println("json: " + pageData);
            System.out.println((pageData.get("right_legals").toString()));
            if (pageData.get("right_legals").toString().trim().equals("") || pageData.get("transfer_content").toString().trim().equals("") || pageData.get("ta_team").toString().trim().equals("") || pageData.get("suitable_exercise").toString().trim().equals("")){
                return new ScoreValueErrorDialog();
            }

            feedback.score1 = Double.parseDouble(pageData.get("right_legals").toString());
            feedback.score2 = Double.parseDouble(pageData.get("transfer_content").toString());
            feedback.score3 = Double.parseDouble(pageData.get("ta_team").toString());
            feedback.score4 = Double.parseDouble(pageData.get("suitable_exercise").toString());

            if (!((feedback.score1 >= 0 && feedback.score1 <= 100) && (feedback.score2 >= 0 && feedback.score2 <= 100) && (feedback.score3 >= 0 && feedback.score3 <= 100) && (feedback.score4 >= 0 && feedback.score4 <= 100))) {

                return new ScoreValueErrorDialog();

            }


            feedback.score_avg = convertToEnglishDigits(String.valueOf((feedback.score1 + feedback.score2 + feedback.score3 + feedback.score4) / 4.0));
            System.out.println("techer name: " + current_teacher.teacher_name);
            System.out.println("lesson name: " + current_teacher.lesson_name);
            feedback.teacher_name = current_teacher.teacher_name;
            feedback.lesson_name = current_teacher.lesson_name;
            System.out.println("thing: " + feedback.score1);
            View view = new Score2();
            view.setMustacheModel(temp);
            return view;
        } else if ("createPoll".equals(updateCommand)) {
            System.out.println("json: " + pageData);

//            System.out.println(pageData.get("studentScore").toString());

            if (pageData.get("student_score").toString().trim().equals("")){
                return new StudentScoreValueErrorDialog();
            }

            if (!(Double.parseDouble(pageData.get("student_score").toString())>=0 && Double.parseDouble(pageData.get("student_score").toString())<=20)){
                return new StudentScoreValueErrorDialog();
            }

            feedback.student_score = convertToEnglishDigits(pageData.get("student_score").toString());
            feedback.extended_feedback = pageData.get("extendedFeedback").toString();
            if (feedback.extended_feedback.trim().isEmpty()) {
                feedback.extended_feedback = null;
            }
//            feedback.feedback_key = Integer.parseInt(userId);
            feedback.upvotes = "۰";
            feedback.downvotes = "۰";
            feedback.user_id = userId;
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
            feedback.date_number = convertToEnglishDigits(dtf.format(PersianDate.now()));
            int number = 0;
            number = Objects.requireNonNull(DbOperation.retrieveFeedbacksBySelf2(userId, connection)).size();
            feedback.feedback_id = userId + "_" + number;
            Date date = new Date();
            feedback.created_time = String.valueOf(date.getTime());
            feedback.diff_votes = 0;

            DbOperation.sendFeedBack(feedback, connection);

            View view = new TeacherComment();
            feedback_id = userId;

            CommentEntity commentEntity = new CommentEntity();

            commentEntity.feedbacks = DbOperation.retrieveFeedbacksByTeacher(current_teacher.lesson_name, current_teacher.teacher_name, connection);
            commentEntity.lesson_name = current_teacher.lesson_name;
            commentEntity.teacher_name = current_teacher.teacher_name;

            view.setMustacheModel(commentEntity);
            return view;
        } else if ("polling".equals(updateCommand)) {

            View view = new Search();
            SearchPageEntity searchPageEntity = new SearchPageEntity();
//            searchPageEntity.teachers_recent = new ArrayList<>(Objects.requireNonNull(DbOperation.retrieveTeachers(connection)).subList(0, 3));
            searchPageEntity.teachers_list = new ArrayList<>(Objects.requireNonNull(DbOperation.retrieveTeachers(connection)).subList(0, 3));
            view.setMustacheModel(searchPageEntity);
            return view;

        } else if ("seeAll".equals(updateCommand)) {
            View view = new FullList();
            teachers = DbOperation.retrieveTeachers(connection);
            temp.teachers = teachers;
            System.out.println(teachers);
            System.out.println("get: " + teachers.get(0).teacher_key);
            view.setMustacheModel(temp);
            return view;
        } else if (updateCommand.startsWith("teacher_profile_info")) {
            View view = new TeacherInfo();
            System.out.println("uooo"+updateCommand);
            String selectedid = updateCommand.substring(updateCommand.indexOf("+") + 1);
            System.out.println("jhjhjh "+selectedid);
            teachers = DbOperation.retrieveTeacherByKey(selectedid, connection);
            current_teacher = teachers.get(0);
            temp.teachers = teachers;
            temp.otherLessons = DbOperation.retrieveLessonsByTeacherNotRepetitive(current_teacher.teacher_name, current_teacher.lesson_name, connection);
            temp.current_lesson = current_teacher.lesson_name;
            view.setMustacheModel(temp);
            return view;
        } else if ("makePoll".equals(updateCommand)) {
            View view = new Score1();
            view.setMustacheModel(temp);
            return view;
        } else if ("teacherCommentsTab".equals(updateCommand) || "last_comments_teacher".equals(updateCommand)) {
            View view = new TeacherComment();
//            System.out.println("currentTeacher: " + current_teacher.teacher_name);
            CommentEntity commentEntity = new CommentEntity();
            commentEntity.feedbacks = DbOperation.retrieveFeedbacksByTeacher(current_teacher.lesson_name, current_teacher.teacher_name, connection);
            commentEntity.lesson_name = current_teacher.lesson_name;
            commentEntity.teacher_name = current_teacher.teacher_name;

            view.setMustacheModel(commentEntity);
//            System.out.println("MAN" + student.feedbacks.get(0).feedback_id);
//            student.image_url = DbOperation.retrieveTeacherURLImage(current_teacher.lesson_name, connection);
            return view;
        } else if ("best_comments_teacher".equals(updateCommand)) {
            View view = new TeacherComment2();
            CommentEntity commentEntity = new CommentEntity();
            commentEntity.feedbacks = DbOperation.retrieveFeedbacksByTeacherMostVoted(current_teacher.lesson_name, current_teacher.teacher_name, connection);
            commentEntity.lesson_name = current_teacher.lesson_name;
            commentEntity.teacher_name = current_teacher.teacher_name;

            view.setMustacheModel(commentEntity);
            return view;
        } else if ("teacherInformationTab".equals(updateCommand)) {
            View view = new TeacherInfo();
            view.setMustacheModel(temp);
            return view;
        } else if ("teacherScoresTab".equals(updateCommand)) {

            ArrayList<Double> scores = DbOperation.retrieveScoreMagic(current_teacher.teacher_name, current_teacher.lesson_name, connection);
            temp.score_overall = convertToEnglishDigits(BigDecimal.valueOf(Objects.requireNonNull(scores).get(0)).setScale(2, RoundingMode.HALF_UP).toString()) + "/۱۰۰";
            temp.score1 = convertToEnglishDigits(BigDecimal.valueOf(scores.get(1)).setScale(2, RoundingMode.HALF_UP).toString()) + "/۱۰۰";
            temp.score2 = convertToEnglishDigits(BigDecimal.valueOf(scores.get(2)).setScale(2, RoundingMode.HALF_UP).toString()) + "/۱۰۰";
            temp.score3 = convertToEnglishDigits(BigDecimal.valueOf(scores.get(3)).setScale(2, RoundingMode.HALF_UP).toString()) + "/۱۰۰";
            temp.score4 = convertToEnglishDigits(BigDecimal.valueOf(scores.get(4)).setScale(2, RoundingMode.HALF_UP).toString()) + "/۱۰۰";

            View view = new TeacherScores();
            view.setMustacheModel(temp);
            return view;
        } else if ("home".equals(updateCommand) || "acceptConditions".equals(updateCommand)) {
            View view = new Home();
            //DbOperation.retrieveTheMostFamousTeachers(connection);
//            System.out.println(DbOperation.retrieveTeacherURLImage("حامد ملک", connection));
            HomePageEntity homePageEntity = new HomePageEntity();
            homePageEntity.teachers = new ArrayList<>(Objects.requireNonNull(DbOperation.retrieveTheMostFamousTeachers(connection)).subList(0, 5));
            try {
                homePageEntity.feedbacks = new ArrayList<>(Objects.requireNonNull(DbOperation.retrieveFeedbacksMostVoted(connection)).subList(0, 3));
            } catch (Exception e) {
                try {
                    homePageEntity.feedbacks = new ArrayList<>(Objects.requireNonNull(DbOperation.retrieveFeedbacksMostVoted(connection)).subList(0, 2));
                } catch (Exception e1) {
                    try {
                        homePageEntity.feedbacks = new ArrayList<>(Objects.requireNonNull(DbOperation.retrieveFeedbacksMostVoted(connection)).subList(0, 1));
                    } catch (Exception e2) {

                    }
                }
            }
            view.setMustacheModel(homePageEntity);  // query here
            return view;
        } else if ("studentCommentHistoryTab".equals(updateCommand)) {
            View view = new ProfileCommentHistory();

            student.feedbacks = DbOperation.retrieveFeedbacksBySelf(userId, connection);
            int score = 0;
            for (Feedback f : student.feedbacks) {
                score += (Integer.parseInt(f.upvotes) - Integer.parseInt(f.downvotes));
            }

            if (score > 0) {
                student.user_karma = "+";
                student.user_karma += convertToEnglishDigits(String.valueOf(score));
            } else {
                student.user_karma = convertToEnglishDigits(String.valueOf(score));
            }
            view.setMustacheModel(student);
            return view;
        } else if ("studentInformationTab".equals(updateCommand) || "profile_info".equals(updateCommand)) {
            View view = new ProfileInfo();
//            students = DbOperation.retrieveVoteStatus(userId, connection); //
//            System.out.println("sggg: " + students.get(0).student_name);
            students.get(0).student_id = convertToEnglishDigits(students.get(0).student_id);
//            tempStudent.students = students;
            view.setMustacheModel(tempStudent);
            return view;
        } else if ("studentSettingsTab".equals(updateCommand)) {
            View view = new ProfileSettings();
            view.setMustacheModel(tempStudent);
            return view;
        } else if (updateCommand.startsWith("upvote_comment")) {

            String upvotes_status;
            String downvotes_status;
            boolean already_upvoted = false;
            boolean already_downvoted = false;
            upvotes_status = Objects.requireNonNull(DbOperation.retrieveVoteStatus(userId, connection)).get(0).student_upvotes;
            downvotes_status = Objects.requireNonNull(DbOperation.retrieveVoteStatus(userId, connection)).get(0).student_downvotes;
            if (upvotes_status == null) {
                upvotes_status = "";
            }
            if (downvotes_status == null) {
                downvotes_status = "";
            }

//            System.out.println(upvotes_status);
//            System.out.println(downvotes_status);

            String[] upvote_list = upvotes_status.split(",");
            String[] downvote_list = downvotes_status.split(",");

//            System.out.println(Arrays.toString(upvote_list));
//            System.out.println(Arrays.toString(downvote_list);


            String selectedid = updateCommand.substring(updateCommand.indexOf("+") + 1);

            for (String str : upvote_list) {
                if (str.equals(selectedid)) {
                    already_upvoted = true;
                    break;
                }
            }

            for (String str : downvote_list) {
                if (str.equals(selectedid)) {
                    already_downvoted = true;
                    break;
                }
            }

            if (already_upvoted) {
                System.out.println("Hello1");

                String upvote;
                upvote = Objects.requireNonNull(DbOperation.retrieveFeedbacksByFeedbackId(selectedid, connection)).get(0).upvotes;
                upvote = String.valueOf(Integer.parseInt(upvote) - 1);
                upvote = convertToEnglishDigits(upvote);

                int upvote_diff_num = Objects.requireNonNull(DbOperation.retrieveFeedbacksByFeedbackId(selectedid, connection)).get(0).diff_votes - 1;
                DbOperation.updateUpvotes(selectedid, upvote_diff_num, upvote, connection);

                upvotes_status = upvotes_status.replace(selectedid + ",", "");
                DbOperation.updateUpvotesListForUser(userId, upvotes_status, connection);
                // already upvoted

            } else if (already_downvoted) {
                System.out.println("Hello2");

                String upvote;
                upvote = Objects.requireNonNull(DbOperation.retrieveFeedbacksByFeedbackId(selectedid, connection)).get(0).downvotes;
                upvote = String.valueOf(Integer.parseInt(upvote) - 1);
                upvote = convertToEnglishDigits(upvote);
                int upvote_diff_num = Objects.requireNonNull(DbOperation.retrieveFeedbacksByFeedbackId(selectedid, connection)).get(0).diff_votes + 1;
                DbOperation.updateDownvotes(selectedid, upvote_diff_num, upvote, connection);

                downvotes_status = downvotes_status.replace(selectedid + ",", "");
                DbOperation.updateDownvotesListForUser(userId, downvotes_status, connection);

                upvote = Objects.requireNonNull(DbOperation.retrieveFeedbacksByFeedbackId(selectedid, connection)).get(0).upvotes;
                upvote = String.valueOf(Integer.parseInt(upvote) + 1);
                upvote = convertToEnglishDigits(upvote);

                upvote_diff_num = Objects.requireNonNull(DbOperation.retrieveFeedbacksByFeedbackId(selectedid, connection)).get(0).diff_votes + 1;
                DbOperation.updateUpvotes(selectedid, upvote_diff_num, upvote, connection);

                upvotes_status += selectedid + ',';
                DbOperation.updateUpvotesListForUser(userId, upvotes_status, connection);

            } else {
                System.out.println("Hello3");
                String upvote;
                upvote = Objects.requireNonNull(DbOperation.retrieveFeedbacksByFeedbackId(selectedid, connection)).get(0).upvotes;
                upvote = String.valueOf(Integer.parseInt(upvote) + 1);
                upvote = convertToEnglishDigits(upvote);

                int upvote_diff_num = Objects.requireNonNull(DbOperation.retrieveFeedbacksByFeedbackId(selectedid, connection)).get(0).diff_votes + 1;
                DbOperation.updateUpvotes(selectedid, upvote_diff_num, upvote, connection);

                upvotes_status += selectedid + ',';
                System.out.println("################");
                System.out.println(userId);
                System.out.println(upvotes_status);
                DbOperation.updateUpvotesListForUser(userId, upvotes_status, connection);
            }

            if (updateCommand.startsWith("upvote_comment_teacher")) {
                CommentEntity commentEntity = new CommentEntity();

                commentEntity.feedbacks = DbOperation.retrieveFeedbacksByTeacher(current_teacher.lesson_name, current_teacher.teacher_name, connection);
                commentEntity.lesson_name = current_teacher.lesson_name;
                commentEntity.teacher_name = current_teacher.teacher_name;

                View view = new TeacherComment();
                view.setMustacheModel(commentEntity);
                return view;

            } else if (updateCommand.startsWith("upvote_comment_student")) {

                student.feedbacks = DbOperation.retrieveFeedbacksBySelf(userId, connection);
                int score = 0;
                for (Feedback f : student.feedbacks) {
                    score += (Integer.parseInt(f.upvotes) - Integer.parseInt(f.downvotes));
                }

                if (score > 0) {
                    student.user_karma = "+";
                    student.user_karma += convertToEnglishDigits(String.valueOf(score));
                } else {
                    student.user_karma = convertToEnglishDigits(String.valueOf(score));
                }
                View view = new ProfileCommentHistory();
                view.setMustacheModel(student);
                return view;
            } else if (updateCommand.startsWith("upvote_comment_home")) {
                View view = new Home();
                HomePageEntity homePageEntity = new HomePageEntity();
                homePageEntity.teachers = new ArrayList<>(Objects.requireNonNull(DbOperation.retrieveTheMostFamousTeachers(connection)).subList(0, 5));
                try {
                    homePageEntity.feedbacks = new ArrayList<>(Objects.requireNonNull(DbOperation.retrieveFeedbacksMostVoted(connection)).subList(0, 3));
                } catch (Exception e) {
                    try {
                        homePageEntity.feedbacks = new ArrayList<>(Objects.requireNonNull(DbOperation.retrieveFeedbacksMostVoted(connection)).subList(0, 2));
                    } catch (Exception e1) {
                        try {
                            homePageEntity.feedbacks = new ArrayList<>(Objects.requireNonNull(DbOperation.retrieveFeedbacksMostVoted(connection)).subList(0, 1));
                        } catch (Exception e2) {

                        }
                    }
                }
                view.setMustacheModel(homePageEntity);  // query here
                return view;
            }

        } else if (updateCommand.startsWith("downvote_comment")) {


            String upvotes_status;
            String downvotes_status;
            boolean already_upvoted = false;
            boolean already_downvoted = false;
            upvotes_status = Objects.requireNonNull(DbOperation.retrieveVoteStatus(userId, connection)).get(0).student_upvotes;
            downvotes_status = Objects.requireNonNull(DbOperation.retrieveVoteStatus(userId, connection)).get(0).student_downvotes;
            if (upvotes_status == null) {
                upvotes_status = "";
            }
            if (downvotes_status == null) {
                downvotes_status = "";
            }

//            System.out.println(upvotes_status);
//            System.out.println(downvotes_status);

            String[] upvote_list = upvotes_status.split(",");
            String[] downvote_list = downvotes_status.split(",");

//            System.out.println(Arrays.toString(upvote_list));
//            System.out.println(Arrays.toString(downvote_list);


            String selectedid = updateCommand.substring(updateCommand.indexOf("+") + 1);

            for (String str : upvote_list) {
                if (str.equals(selectedid)) {
                    already_upvoted = true;
                    break;
                }
            }

            for (String str : downvote_list) {
                if (str.equals(selectedid)) {
                    already_downvoted = true;
                    break;
                }
            }

            if (already_downvoted) {

                String upvote;
                upvote = Objects.requireNonNull(DbOperation.retrieveFeedbacksByFeedbackId(selectedid, connection)).get(0).downvotes;
                upvote = String.valueOf(Integer.parseInt(upvote) - 1);
                upvote = convertToEnglishDigits(upvote);
                int upvote_diff_num = Objects.requireNonNull(DbOperation.retrieveFeedbacksByFeedbackId(selectedid, connection)).get(0).diff_votes + 1;
                DbOperation.updateDownvotes(selectedid, upvote_diff_num, upvote, connection);

                downvotes_status = downvotes_status.replace(selectedid + ",", "");
                DbOperation.updateDownvotesListForUser(userId, downvotes_status, connection);


            } else if (already_upvoted) {

                String upvote;
                upvote = Objects.requireNonNull(DbOperation.retrieveFeedbacksByFeedbackId(selectedid, connection)).get(0).upvotes;
                upvote = String.valueOf(Integer.parseInt(upvote) - 1);
                upvote = convertToEnglishDigits(upvote);
                int upvote_diff_num = Objects.requireNonNull(DbOperation.retrieveFeedbacksByFeedbackId(selectedid, connection)).get(0).diff_votes - 1;
                DbOperation.updateUpvotes(selectedid, upvote_diff_num, upvote, connection);

                upvotes_status = upvotes_status.replace(selectedid + ",", "");
                DbOperation.updateUpvotesListForUser(userId, upvotes_status, connection);

                upvote = Objects.requireNonNull(DbOperation.retrieveFeedbacksByFeedbackId(selectedid, connection)).get(0).downvotes;
                upvote = String.valueOf(Integer.parseInt(upvote) + 1);
                upvote = convertToEnglishDigits(upvote);
                upvote_diff_num = Objects.requireNonNull(DbOperation.retrieveFeedbacksByFeedbackId(selectedid, connection)).get(0).diff_votes - 1;
                DbOperation.updateDownvotes(selectedid, upvote_diff_num, upvote, connection);

                downvotes_status += selectedid + ',';
                DbOperation.updateDownvotesListForUser(userId, downvotes_status, connection);

            } else {


                String upvote;
                upvote = Objects.requireNonNull(DbOperation.retrieveFeedbacksByFeedbackId(selectedid, connection)).get(0).downvotes;
                upvote = String.valueOf(Integer.parseInt(upvote) + 1);
                upvote = convertToEnglishDigits(upvote);
//            System.out.println("upvote: " + upvote  +"    feedback" + selectedid);
                int upvote_diff_num = Objects.requireNonNull(DbOperation.retrieveFeedbacksByFeedbackId(selectedid, connection)).get(0).diff_votes - 1;
                DbOperation.updateDownvotes(selectedid, upvote_diff_num, upvote, connection);

                downvotes_status += selectedid + ',';
                DbOperation.updateDownvotesListForUser(userId, downvotes_status, connection);
            }
            if (updateCommand.startsWith("downvote_comment_teacher")) {
                CommentEntity commentEntity = new CommentEntity();

                commentEntity.feedbacks = DbOperation.retrieveFeedbacksByTeacher(current_teacher.lesson_name, current_teacher.teacher_name, connection);
                commentEntity.lesson_name = current_teacher.lesson_name;
                commentEntity.teacher_name = current_teacher.teacher_name;

                View view = new TeacherComment();
                view.setMustacheModel(commentEntity);

                return view;

            } else if (updateCommand.startsWith("downvote_comment_student")) {

                student.feedbacks = DbOperation.retrieveFeedbacksBySelf(userId, connection);
                int score = 0;
                for (Feedback f : student.feedbacks) {
                    score += (Integer.parseInt(f.upvotes) - Integer.parseInt(f.downvotes));
                }

                if (score > 0) {
                    student.user_karma = "+";
                    student.user_karma += convertToEnglishDigits(String.valueOf(score));
                } else {
                    student.user_karma = convertToEnglishDigits(String.valueOf(score));
                }
                View view = new ProfileCommentHistory();
                view.setMustacheModel(student);
                return view;
            } else if (updateCommand.startsWith("downvote_comment_home")) {
                View view = new Home();
                HomePageEntity homePageEntity = new HomePageEntity();
                homePageEntity.teachers = new ArrayList<>(Objects.requireNonNull(DbOperation.retrieveTheMostFamousTeachers(connection)).subList(0, 5));
                try {
                    homePageEntity.feedbacks = new ArrayList<>(Objects.requireNonNull(DbOperation.retrieveFeedbacksMostVoted(connection)).subList(0, 3));
                } catch (Exception e) {
                    try {
                        homePageEntity.feedbacks = new ArrayList<>(Objects.requireNonNull(DbOperation.retrieveFeedbacksMostVoted(connection)).subList(0, 2));
                    } catch (Exception e1) {
                        try {
                            homePageEntity.feedbacks = new ArrayList<>(Objects.requireNonNull(DbOperation.retrieveFeedbacksMostVoted(connection)).subList(0, 1));
                        } catch (Exception e2) {

                        }
                    }
                }
                view.setMustacheModel(homePageEntity);  // query here
                return view;
            }

        } else if (updateCommand.startsWith("deleteComment")) {
            String selectedid = updateCommand.substring(updateCommand.indexOf("+") + 1);
            DbOperation.deleteExtendedVote(selectedid, connection);


            student.feedbacks = DbOperation.retrieveFeedbacksBySelf(userId, connection);

            int score = 0;
            for (Feedback f : student.feedbacks) {
                score += (Integer.parseInt(f.upvotes) - Integer.parseInt(f.downvotes));
            }

            if (score > 0) {
                student.user_karma = "+";
                student.user_karma += convertToEnglishDigits(String.valueOf(score));
            } else {
                student.user_karma = convertToEnglishDigits(String.valueOf(score));
            }
            View view = new ProfileCommentHistory();
            view.setMustacheModel(student);
            return view;

        } else if (updateCommand.startsWith("teacherViewAllLessons")) {
            String selectedid = updateCommand.substring(updateCommand.indexOf("+") + 1);
            temp.teachers = DbOperation.retrieveLessonsByTeacher(selectedid, connection);
            View view = new FullList();
            view.setMustacheModel(temp);
            return view;
        } else if (updateCommand.equals("search_button")) {
            String search_input = pageData.get("search_input").toString();
            System.out.println(search_input);
            temp.teachers = DbOperation.search(search_input, connection);
            View view = new SearchResults();
            view.setMustacheModel(temp);
            return view;
        } else if (updateCommand.startsWith("big_comment_to_teacher")) {
            String selectedid = updateCommand.substring(updateCommand.indexOf("+") + 1, updateCommand.indexOf("%"));
            String selectedid_2 = updateCommand.substring(updateCommand.indexOf("%") + 1);

            System.out.println(selectedid);
            System.out.println(selectedid_2);

            CommentEntity commentEntity = new CommentEntity();
            temp.teachers = Objects.requireNonNull(DbOperation.retrieveTeacherByNameAndLesson(selectedid, selectedid_2, connection));
            current_teacher = Objects.requireNonNull(DbOperation.retrieveTeacherByNameAndLesson(selectedid, selectedid_2, connection)).get(0);
            temp.otherLessons = DbOperation.retrieveLessonsByTeacherNotRepetitive(current_teacher.teacher_name, current_teacher.lesson_name, connection);
            temp.current_lesson = current_teacher.lesson_name;

            commentEntity.feedbacks = DbOperation.retrieveFeedbacksByTeacher(current_teacher.lesson_name, current_teacher.teacher_name, connection);
            commentEntity.lesson_name = current_teacher.lesson_name;
            commentEntity.teacher_name = current_teacher.teacher_name;


            View view = new TeacherComment();
            view.setMustacheModel(commentEntity);

            return view;

        } else if (updateCommand.equals("register_new_user")) {
            Student student = new Student();

            student.student_name = pageData.get("first_name").toString().trim() + " " + pageData.get("last_name").toString().trim();
            student.student_id = pageData.get("student_number").toString();
            student.student_faculty = "مهندسی کامپیوتر";



            student.student_gender = pageData.get("selected_gender").toString();
            if (student.student_gender.equals("مرد")) {
                student.student_photo = "https://s4.uupload.ir/files/cfee5087-8773-4fb3-ac5e-63372d889b1f_ks1c.png";
            } else if (student.student_gender.equals("زن")) {
                student.student_photo = "https://s4.uupload.ir/files/3b786101-e336-4e3d-96bb-a73d2227b8d2_n9a3.png";
            } else {
                student.student_photo = "https://s4.uupload.ir/files/9446101f-27b4-4f8f-9761-0397d7ea932e_mcg1.png";
            }
            student.user_id = userId;
            DbOperation.sendUserInfo(student, connection);

            students = DbOperation.retrieveVoteStatus(userId, connection);
            tempStudent.students = students;


            View view = new Home();
            HomePageEntity homePageEntity = new HomePageEntity();
            homePageEntity.teachers = new ArrayList<>(Objects.requireNonNull(DbOperation.retrieveTheMostFamousTeachers(connection)).subList(0, 5));
            try {
                homePageEntity.feedbacks = new ArrayList<>(Objects.requireNonNull(DbOperation.retrieveFeedbacksMostVoted(connection)).subList(0, 3));
            } catch (Exception e) {
                try {
                    homePageEntity.feedbacks = new ArrayList<>(Objects.requireNonNull(DbOperation.retrieveFeedbacksMostVoted(connection)).subList(0, 2));
                } catch (Exception e1) {
                    try {
                        homePageEntity.feedbacks = new ArrayList<>(Objects.requireNonNull(DbOperation.retrieveFeedbacksMostVoted(connection)).subList(0, 1));
                    } catch (Exception e2) {

                    }
                }
            }
            view.setMustacheModel(homePageEntity);  // query here
            return view;
        } else if (updateCommand.equals("logout")) {
            View view = new SignoutDialog();
//            View view = new SignInUp();
//            tempStudent.students = students;
//            view.setMustacheModel(tempStudent);
            return view;
        } else if (updateCommand.equals("help_register")) {
            return new HelpRegister();
        } else if (updateCommand.equals("help_sign_in")) {
            return new HelpSignInUp();
        } else if (updateCommand.equals("sign_in")) {
            students = DbOperation.retrieveVoteStatus(userId, connection);
            View view = new SignInUp();
            tempStudent.students = students;
            view.setMustacheModel(tempStudent);
            return view;


        } else if (updateCommand.equals("register")) {
            return new Register();

        }

        else if(updateCommand.equals("bestTAs")) {
            View view = new FullListWithNumber();
            teachers = DbOperation.retrieveByTaTeam(connection);
            temp.teachers = teachers;
            temp.numberTitle = "میانگین امتیاز: ";
            System.out.println("sag: "  + temp.teachers.get(0).teacher_name);
            view.setMustacheModel(temp);
            return view;
        }

        else if(updateCommand.equals("popularLessons")){
            View view = new FullListWithNumber();
            teachers = DbOperation.retrieveTheMostFamousTeachersLessons(connection);
            temp.numberTitle = "میانگین امتیاز: ";
            temp.teachers = teachers;
            System.out.println("sag: "  + temp.teachers.get(0).teacher_name);
            view.setMustacheModel(temp);
            return view;
        }
        else if (updateCommand.equals("MostCommentedTeachers")){
            View view = new FullListWithNumber();
            teachers = DbOperation.retrieveMostCommentedTeachers(connection);
            temp.teachers = teachers;
            temp.numberTitle = "تعداد نظرات: ";
            System.out.println("sag: "  + temp.teachers.get(0).teacher_name);
            view.setMustacheModel(temp);
            return view;
        }
        else if (updateCommand.equals("LeastCommentedTeachers")){
            View view = new FullListWithNumber();
            teachers = DbOperation.retrieveLeastCommentedTeachers(connection);
            temp.teachers = teachers;
            temp.numberTitle = "تعداد نظرات: ";
            System.out.println("sag: "  + temp.teachers.get(0).teacher_name);
            view.setMustacheModel(temp);
            return view;

        } else if (updateCommand.equals("ViewTeacherCircular")){

            View view = new CircularTeacherList();
            CircularEntity circularEntity = new CircularEntity();
            ArrayList<Teacher> teachers =  DbOperation.retrieveTheMostFamousTeachers2(connection);
            circularEntity.teachers1 = new ArrayList<>(Objects.requireNonNull(teachers).subList(0, 5));
            circularEntity.teachers2 = new ArrayList<>(Objects.requireNonNull(teachers).subList(5, 10));
            circularEntity.teachers3 = new ArrayList<>(Objects.requireNonNull(teachers).subList(10, 15));
//            circularEntity.teachers4 = new ArrayList<>(Objects.requireNonNull(teachers).subList(15, 20));

            view.setMustacheModel(circularEntity);
            return view;


        } else if(updateCommand.equals("ViewFullBestComments")){
            View view = new FullCommentListView();
            FullCommentEntity fullCommentEntity = new FullCommentEntity();
            fullCommentEntity.feedbacks = DbOperation.retrieveFeedbacksMostVoted(connection);
            view.setMustacheModel(fullCommentEntity);
            return view;
        } else if (updateCommand.equals("ViewFullWorstComments")){
            View view = new FullCommentListView();
            FullCommentEntity fullCommentEntity = new FullCommentEntity();
            fullCommentEntity.feedbacks = DbOperation.retrieveFeedbacksLeastVoted(connection);
            view.setMustacheModel(fullCommentEntity);
            return view;

        } else if (updateCommand.equals("SubmitDialogShow")){
            return new SubmitFeedbackDialog();
        } else if(updateCommand.equals("DeleteDialogShow")){
            return new DeleteCommentDialog();
        } else if(updateCommand.equals("ScoreValueDialogShow")){
            return new ScoreValueErrorDialog();
        } else if(updateCommand.equals("StudentScoreValueDialogShow")){
            return new StudentScoreValueErrorDialog();
        } else if(updateCommand.equals("SignOutDialogShow")){
            return new SignoutDialog();
        } else if(updateCommand.equals("WelcomeDialogShow")){
            return new WelcomeDialog();
        }


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


}

