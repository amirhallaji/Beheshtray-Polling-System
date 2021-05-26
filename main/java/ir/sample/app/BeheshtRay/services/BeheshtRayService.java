package ir.sample.app.BeheshtRay.services;

import ir.appsan.sdk.APSService;
import ir.appsan.sdk.View;
import ir.appsan.sdk.ViewUpdate;
import ir.appsan.sdk.Response;
import ir.sample.app.BeheshtRay.database.DatabaseManager;
import ir.sample.app.BeheshtRay.database.DbOperation;
import ir.sample.app.BeheshtRay.models.Comment;
import ir.sample.app.BeheshtRay.models.Feedback;
import ir.sample.app.BeheshtRay.models.Student;
import ir.sample.app.BeheshtRay.models.Teacher;
import ir.sample.app.BeheshtRay.views.*;
import org.json.simple.JSONObject;

import java.sql.Array;
import java.sql.Connection;
import java.util.ArrayList;

public class BeheshtRayService extends APSService {

    public static ArrayList<String> commentIds = new ArrayList<>();
    Teacher teacher = new Teacher();
    Student student = new Student();
    Comment comment = new Comment("34");
    Feedback feedback = new Feedback();


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
                rightLegals = pageData.get("right_legals").toString();
                transferContent = pageData.get("transfer_content").toString();
                taTeam = pageData.get("ta_team").toString();
                suitableExercise = pageData.get("suitable_exercise").toString();

                System.out.println("contents received: " + rightLegals);
                System.out.println("contents received: " + transferContent);
                System.out.println("contents received: " + taTeam);
                System.out.println("contents received: " + suitableExercise);

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
        String thing;
        if ("nextPage".equals(updateCommand)) {
            feedback.score1 = pageData.get("right_legals").toString();
            System.out.println("thing: " + feedback.score1);

            DbOperation.sendFeedBack(feedback, connection);
            return new SignInUp();
        }


        return update;
    }
}
