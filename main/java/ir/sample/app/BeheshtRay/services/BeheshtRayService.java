package ir.sample.app.BeheshtRay.services;

import ir.appsan.sdk.APSService;
import ir.appsan.sdk.View;
import ir.appsan.sdk.ViewUpdate;
import ir.appsan.sdk.Response;
import ir.sample.app.BeheshtRay.database.DatabaseManager;
import ir.sample.app.BeheshtRay.database.DbOperation;
import ir.sample.app.BeheshtRay.models.Comment;
import ir.sample.app.BeheshtRay.models.Student;
import ir.sample.app.BeheshtRay.models.Teacher;
import ir.sample.app.BeheshtRay.views.*;
import org.json.simple.JSONObject;

import java.sql.Connection;

public class BeheshtRayService extends APSService {

    String selectedharf = "";
    String selectedtype = "";
    String selectedid = "";



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
                view = new Search();
                break;

            default:
                System.out.println("default case");
                view = new SignInUp();
        }

        return view;
    }


    @Override
    public Response onUpdate(ViewUpdate update, String updateCommand, JSONObject pageData, String userId) {
        return update;
    }
}
