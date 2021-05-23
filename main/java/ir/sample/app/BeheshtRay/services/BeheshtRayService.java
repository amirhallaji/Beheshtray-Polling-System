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
import ir.sample.app.BeheshtRay.views.SignInUp;
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
        return "app:09384158428:Beheshti Poll Service";
    }

    @Override
    public View onCreateView(String command, JSONObject pageData, String userId) {
        View view = new SignInUp();

        System.out.println("after send comment");
        Comment comment = new Comment("abc");
        Student student = new Student();
        Teacher teacher = new Teacher();
        teacher.setTeacherName("Ali");
        teacher.setTeacherAcademicGroup("math");
        teacher.setTeacherEmail("mail@ali.ir");
        teacher.setTeacherLessons("physics");
        student.setStudentName("ahmad");
        student.setStudentFaculty("computer");
        student.setStudentGender("male");
        student.setStudentId("9724");
        DbOperation.sendComment(comment, DatabaseManager.getConnection());

        return view;
    }

    @Override
    public Response onUpdate(ViewUpdate update, String updateCommand, JSONObject pageData, String userId) {
        return update;
    }
}
