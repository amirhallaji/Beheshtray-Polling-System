package ir.sample.app.BeheshtRay.services;

import ir.appsan.sdk.APSService;
import ir.appsan.sdk.View;
import ir.appsan.sdk.ViewUpdate;
import ir.appsan.sdk.Response;
import ir.sample.app.BeheshtRay.database.DatabaseManager;
import ir.sample.app.BeheshtRay.models.Student;
import ir.sample.app.BeheshtRay.models.Teacher;
import org.json.simple.JSONObject;

import java.sql.Connection;

    public class BeheshtRayService extends APSService {

    String selectedharf = "";
    String selectedtype = "";
    String selectedid = "";

    Student student = new Student();
    Teacher teacher = new Teacher();

    Connection connection = DatabaseManager.getConnection();
    boolean allowmake = true;

    public BeheshtRayService(String channelName) {
        super(channelName);
    }

    @Override
    public String getServiceName() {
        return "app:admin:highway";
    }

    @Override
    public View onCreateView(String command, JSONObject pageData, String userId) {
        View view = new ir.sample.app.beheshtray.views.SignInUp();
        return view;
    }

    @Override
    public Response onUpdate(ViewUpdate update, String updateCommand, JSONObject pageData, String userId) {
        return update;
    }
}
