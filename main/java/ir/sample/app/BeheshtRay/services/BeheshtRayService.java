package ir.sample.app.BeheshtRay.services;

import com.github.mfathi91.time.PersianDate;
import ir.appsan.sdk.*;
import ir.sample.app.BeheshtRay.database.DatabaseManager;
import ir.sample.app.BeheshtRay.database.DbOperation;
import ir.sample.app.BeheshtRay.models.*;
import ir.sample.app.BeheshtRay.views.*;
import org.json.simple.JSONObject;

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
    ArrayList<Teacher> teachers = new ArrayList<>();
    Temp<Teacher> temp = new Temp<>();
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
                view = new SignInUp();
        }

        return view;
    }


    @Override
    public Response onUpdate(ViewUpdate update, String updateCommand, JSONObject pageData, String userId) {
        if ("nextPage".equals(updateCommand)) {
            System.out.println("json: " + pageData);
            feedback.score1 = pageData.get("right_legals").toString();
            feedback.score2 = pageData.get("transfer_content").toString();
            feedback.score3 = pageData.get("ta_team").toString();
            feedback.score4 = pageData.get("suitable_exercise").toString();
            System.out.println("techer name: " + current_teacher.teacher_name);
            System.out.println("lesson name: " + current_teacher.lesson_name);
            feedback.teacher_name = current_teacher.teacher_name;
            feedback.lesson_name = current_teacher.lesson_name;
            System.out.println("thing: " + feedback.score1);
            View view = new Score2();
            view.setMustacheModel(temp);
            return view;
        } else if ("createPoll".equals(updateCommand)) {
            feedback.student_score = convertToEnglishDigits(pageData.get("studentScore").toString());
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

            DbOperation.sendFeedBack(feedback, connection);

            View view = new TeacherComment();
            feedback_id = userId;
            student.feedbacks = DbOperation.retrieveFeedbacksByTeacher(current_teacher.lesson_name, current_teacher.teacher_name, connection);

            view.setMustacheModel(student);
            return view;
        } else if ("polling".equals(updateCommand)) {
            return new Search();
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
            String selectedid = updateCommand.substring(updateCommand.indexOf("+") + 1);
            teachers = DbOperation.retrieveTeacherByKey(selectedid, connection);
            current_teacher = teachers.get(0);
            temp.teachers = teachers;
            view.setMustacheModel(temp);
            return view;
        } else if ("makePoll".equals(updateCommand)) {
            View view = new Score1();
            view.setMustacheModel(temp);
            return view;
        } else if ("teacherCommentsTab".equals(updateCommand)) {
            View view = new TeacherComment();
            System.out.println("currentTeacher: " + current_teacher.teacher_name);
            student.feedbacks = DbOperation.retrieveFeedbacksByTeacher(current_teacher.lesson_name, current_teacher.teacher_name, connection);
//            System.out.println("MAN" + student.feedbacks.get(0).feedback_id);
//            student.image_url = DbOperation.retrieveTeacherURLImage(current_teacher.lesson_name, connection);
            view.setMustacheModel(student);
            return view;
        } else if ("teacherInformationTab".equals(updateCommand)) {
            View view = new TeacherInfo();
            view.setMustacheModel(temp);
            return view;
        } else if ("teacherScoresTab".equals(updateCommand)) {
            View view = new TeacherScores();
            view.setMustacheModel(temp);
            return view;
        } else if ("home".equals(updateCommand) || "acceptConditions".equals(updateCommand)) {
            View view = new Home();
            teachers = DbOperation.retrieveTeachers(connection);
            if (teachers != null) {
                teachers = new ArrayList<>(teachers.subList(0, 5));
            }
            temp.teachers = teachers;
            view.setMustacheModel(temp);  // query here

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
            return view;
        } else if ("studentSettingsTab".equals(updateCommand)) {
            View view = new ProfileSettings();
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

                // already upvoted

            } else if (already_downvoted) {
                System.out.println("Hello2");

                String upvote;
                upvote = Objects.requireNonNull(DbOperation.retrieveFeedbacksByFeedbackId(selectedid, connection)).get(0).downvotes;
                upvote = String.valueOf(Integer.parseInt(upvote) - 1);
                upvote = convertToEnglishDigits(upvote);
                DbOperation.updateDownvotes(selectedid, upvote, connection);

                downvotes_status = downvotes_status.replace(selectedid + ",", "");
                DbOperation.updateDownvotesListForUser(userId, downvotes_status, connection);

                upvote = Objects.requireNonNull(DbOperation.retrieveFeedbacksByFeedbackId(selectedid, connection)).get(0).upvotes;
                upvote = String.valueOf(Integer.parseInt(upvote) + 1);
                upvote = convertToEnglishDigits(upvote);
                DbOperation.updateUpvotes(selectedid, upvote, connection);

                upvotes_status += selectedid + ',';
                DbOperation.updateUpvotesListForUser(userId, upvotes_status, connection);

            } else {
                System.out.println("Hello3");
                String upvote;
                upvote = Objects.requireNonNull(DbOperation.retrieveFeedbacksByFeedbackId(selectedid, connection)).get(0).upvotes;
                upvote = String.valueOf(Integer.parseInt(upvote) + 1);
                upvote = convertToEnglishDigits(upvote);
                DbOperation.updateUpvotes(selectedid, upvote, connection);

                upvotes_status += selectedid + ',';
                System.out.println("################");
                System.out.println(userId);
                System.out.println(upvotes_status);
                DbOperation.updateUpvotesListForUser(userId, upvotes_status, connection);
            }

            if (updateCommand.startsWith("upvote_comment_teacher")) {
                student.feedbacks = DbOperation.retrieveFeedbacksByTeacher(current_teacher.lesson_name, current_teacher.teacher_name, connection);
                View view = new TeacherComment();
                view.setMustacheModel(student);
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

            } else if (already_upvoted) {

                String upvote;
                upvote = Objects.requireNonNull(DbOperation.retrieveFeedbacksByFeedbackId(selectedid, connection)).get(0).upvotes;
                upvote = String.valueOf(Integer.parseInt(upvote) - 1);
                upvote = convertToEnglishDigits(upvote);
                DbOperation.updateUpvotes(selectedid, upvote, connection);

                upvotes_status = upvotes_status.replace(selectedid + ",", "");
                DbOperation.updateUpvotesListForUser(userId, upvotes_status, connection);

                upvote = Objects.requireNonNull(DbOperation.retrieveFeedbacksByFeedbackId(selectedid, connection)).get(0).downvotes;
                upvote = String.valueOf(Integer.parseInt(upvote) + 1);
                upvote = convertToEnglishDigits(upvote);
                DbOperation.updateDownvotes(selectedid, upvote, connection);

                downvotes_status += selectedid + ',';
                DbOperation.updateDownvotesListForUser(userId, downvotes_status, connection);

            } else {


                String upvote;
                upvote = Objects.requireNonNull(DbOperation.retrieveFeedbacksByFeedbackId(selectedid, connection)).get(0).downvotes;
                upvote = String.valueOf(Integer.parseInt(upvote) + 1);
                upvote = convertToEnglishDigits(upvote);
//            System.out.println("upvote: " + upvote  +"    feedback" + selectedid);
                DbOperation.updateDownvotes(selectedid, upvote, connection);

                downvotes_status += selectedid + ',';
                DbOperation.updateDownvotesListForUser(userId, downvotes_status, connection);
            }
            if (updateCommand.startsWith("downvote_comment_teacher")) {
                student.feedbacks = DbOperation.retrieveFeedbacksByTeacher(current_teacher.lesson_name, current_teacher.teacher_name, connection);
                View view = new TeacherComment();
                view.setMustacheModel(student);
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
            }
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

