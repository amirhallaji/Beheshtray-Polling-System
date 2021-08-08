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
            searchPageEntity.number = BeheshtRayService.convertToEnglishDigits(String.valueOf(searchPageEntity.teachers_list != null ? searchPageEntity.teachers_list.size() : 0));

            view.setMustacheModel(searchPageEntity);
        } else if (command.startsWith("comment_click_clk")) {
            String teachingId = command.substring(command.indexOf("+") + 1);

            view = new TeacherComment();
            currentTeacherEntity.currentTeacher = DbOperation.retrieveTeacherInfoByTeachingId(Integer.parseInt(teachingId), connection);

            current_teacher = Objects.requireNonNull(currentTeacherEntity.currentTeacher).get(0);
            currentTeacherEntity.otherLessons = DbOperation.retrieveOtherLessonsByTeacherInfo(current_teacher.getLessonName(), current_teacher.getTeacherName(), current_teacher.getFacultyId(), connection);
            currentTeacherEntity.teacherFeedbacks = DbOperation.retrieveFeedbackByTeachingId(current_teacher.getTeachingId(), true, connection);
            currentTeacherEntity.number = BeheshtRayService.convertToEnglishDigits(String.valueOf(currentTeacherEntity.teacherFeedbacks != null ? currentTeacherEntity.teacherFeedbacks.size() : 0));

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
                    feedbackEntity.number = BeheshtRayService.convertToEnglishDigits(String.valueOf(feedbackEntity.feedbacks.size()));
                    view.setMustacheModel(feedbackEntity);
                    break;

                case "profile_comment_history_tab_non_extended_sec":
                    view = new ProfileCommentHistory2();
                    feedbackEntity = new FeedbackEntity();
                    feedbackEntity.feedbacks = DbOperation.retrieveMyFeedbacks(current_user.getUserId(), false, connection);
                    feedbackEntity.studentKarma = DbOperation.retrieveMyKarma(current_user.getUserId(), connection);
                    feedbackEntity.number = BeheshtRayService.convertToEnglishDigits(String.valueOf(feedbackEntity.feedbacks.size()));
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
                    searchPageEntity.number = BeheshtRayService.convertToEnglishDigits(String.valueOf(searchPageEntity.teachers_list.size()));
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
                        feedback.setScore1Persian("-");
                        feedback.setScore2Persian("-");
                        feedback.setScore3Persian("-");
                        feedback.setScore4Persian("-");
                        feedback.setAverageScorePersian("-");
                        currentTeacherEntity.number = "۰";
                        feedbacks.add(feedback);
                        currentTeacherEntity.teacherFeedbacks = feedbacks;
//                        System.out.println("here");
                    } else {
                        currentTeacherEntity.teacherFeedbacks = feedbacks;
                        currentTeacherEntity.number = currentTeacherEntity.teacherFeedbacks.get(0).getNumberOfParticipants();
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
                    circularEntity.number = BeheshtRayService.convertToEnglishDigits(String.valueOf(teachers != null ? teachers.size() : 0));


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
                    homePageEntity.number = BeheshtRayService.convertToEnglishDigits(String.valueOf(homePageEntity.feedbacks != null ? homePageEntity.feedbacks.size() : 0));
                    view.setMustacheModel(homePageEntity);
                    break;

                case "view_best_TAs":
                    view = new FullListWithNumber();
                    searchPageEntity = new SearchPageEntity();
                    searchPageEntity.teachers_list = DbOperation.retrieveBestTAs(current_user.getStudentFacultyId(),connection);
                    searchPageEntity.number = BeheshtRayService.convertToEnglishDigits(String.valueOf(searchPageEntity.teachers_list != null ? searchPageEntity.teachers_list.size() : 0));
                    view.setMustacheModel(searchPageEntity);
                    break;

                case "view_popular_lessons":
                    view = new FullListWithNumber();
                    searchPageEntity = new SearchPageEntity();
                    searchPageEntity.teachers_list = DbOperation.retrieveBestLessons(current_user.getStudentFacultyId(),connection);
                    searchPageEntity.number = BeheshtRayService.convertToEnglishDigits(String.valueOf(searchPageEntity.teachers_list != null ? searchPageEntity.teachers_list.size() : 0));
                    view.setMustacheModel(searchPageEntity);
                    break;

                case "view_most_commented":
                    view = new FullListWithNumber();
                    searchPageEntity = new SearchPageEntity();
                    searchPageEntity.teachers_list = DbOperation.retrieveTheMostLeastCommentedLessons(current_user.getStudentFacultyId(), true,connection);
                    searchPageEntity.number = BeheshtRayService.convertToEnglishDigits(String.valueOf(searchPageEntity.teachers_list != null ? searchPageEntity.teachers_list.size() : 0));
                    view.setMustacheModel(searchPageEntity);
                    break;

                case "view_least_commented":
                    view = new FullListWithNumber();
                    searchPageEntity = new SearchPageEntity();
                    searchPageEntity.teachers_list = DbOperation.retrieveTheMostLeastCommentedLessons(current_user.getStudentFacultyId(), false,connection);
                    searchPageEntity.number = BeheshtRayService.convertToEnglishDigits(String.valueOf(searchPageEntity.teachers_list != null ? searchPageEntity.teachers_list.size() : 0));
                    view.setMustacheModel(searchPageEntity);
                    break;

                case "view_general_lessons":
                    view = new FullList();
                    searchPageEntity = new SearchPageEntity();
                    searchPageEntity.teachers_list = DbOperation.retrieveTheGeneralLessons(connection);
                    searchPageEntity.number = BeheshtRayService.convertToEnglishDigits(String.valueOf(searchPageEntity.teachers_list != null ? searchPageEntity.teachers_list.size() : 0));
                    view.setMustacheModel(searchPageEntity);
                    break;

                case "view_worst_comments":
                    view = new FullCommentListView();
                    homePageEntity = new HomePageEntity();
                    homePageEntity.feedbacks = DbOperation.retrieveTheLeastVotedFeedbacks(current_user.getStudentFacultyId(), connection);
                    homePageEntity.number = BeheshtRayService.convertToEnglishDigits(String.valueOf(homePageEntity.feedbacks != null ? homePageEntity.feedbacks.size() : 0));
                    view.setMustacheModel(homePageEntity);
                    break;

                case "sign_out_btn":


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
                        current_user = student;

                        if (updateCommand.equals("change_user_info")) {
                            DbOperation.updateUserInfo(student, connection);
                            students.set(0, current_user);
                            currentStudentEntity.currentStudent = students;
                        } else {
                            student.setUserKarma("0");
                            DbOperation.sendUserInfo(student, connection);
                            students = new ArrayList<>();
                            students.add(current_user);
                            currentStudentEntity.currentStudent = students;
                        }

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

//                    return new SubmitFeedbackDialog();
                    }

//                case "send_feedback_btn":
                    current_feedback.setTeachingId(current_teacher.getTeachingId());
                    current_feedback.setUserId(current_user.getUserId());
                    current_feedback.setUpVotes(0);
                    current_feedback.setDownVotes(0);
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
                    current_feedback.setPersianDate(convertToEnglishDigits(dtf.format(PersianDate.now())));
                    DbOperation.sendFeedback(current_feedback, connection);

                    if (extendedFeedback == null) {
                        View view = new TeacherScores();
                        ArrayList<Feedback> feedbacks;
                        feedbacks = DbOperation.retrieveScoreByTeachingId(current_teacher.getTeachingId(), connection);
                        currentTeacherEntity.teacherFeedbacks = feedbacks;
                        currentTeacherEntity.number = currentTeacherEntity.teacherFeedbacks != null ? currentTeacherEntity.teacherFeedbacks.get(0).getNumberOfParticipants() : "0";
                        view.setMustacheModel(currentTeacherEntity);
                        return view;
                    }

                    return ShowTeacherFeedback(true);


                case "search_btn":
                    String searchInputText = pageData.get("search_input_text").toString().trim();
                    View view = new SearchResults();
                    SearchPageEntity searchPageEntity = new SearchPageEntity();
                    searchPageEntity.teachers_list = new ArrayList<>(Objects.requireNonNull(DbOperation.retrieveSearchedTeachersList(searchInputText, current_user.getUserId(), connection)));
                    searchPageEntity.number = BeheshtRayService.convertToEnglishDigits(String.valueOf(searchPageEntity.teachers_list.size()));
                    view.setMustacheModel(searchPageEntity);
                    return view;


//
//                current_feedback.setStudentScore();


//                DbOperation.sendFeedback();


                default:
                    break;
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
        currentTeacherEntity.number = BeheshtRayService.convertToEnglishDigits(String.valueOf(currentTeacherEntity.teacherFeedbacks != null ? currentTeacherEntity.teacherFeedbacks.size() : 0));
        view.setMustacheModel(currentTeacherEntity);
        return view;
    }

}

