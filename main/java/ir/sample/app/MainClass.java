    package ir.sample.app;

import ir.appsan.sdk.APSConfig;
import ir.appsan.sdk.AppsanApplication;
import ir.sample.app.BeheshtRay.HighwayChannel;
import ir.sample.app.BeheshtRay.database.DatabaseManager;
import ir.sample.app.BeheshtRay.database.DbOperation;
import ir.sample.app.BeheshtRay.models.Comment;
import ir.sample.app.BeheshtRay.models.Student;
import ir.sample.app.BeheshtRay.models.Teacher;
import org.apache.commons.configuration.PropertiesConfiguration;


import javax.naming.ConfigurationException;


    public class MainClass {

    public static void main(String[] args) throws ConfigurationException {

        System.out.println("start main");

        PropertiesConfiguration propertiesConfiguration = new PropertiesConfiguration();

//        try {
//            propertiesConfiguration.load("application.properties");
//        } catch (org.apache.commons.configuration.ConfigurationException e) {
//            e.printStackTrace();
//        }
        //String port = System.getenv("CONNECTOR_PORT");
        //String host = System.getenv("CONNECTOR_HOST");
        String host = "sbu.appsan.ir";
        String port = "9092";
//        if(port == null)
//            port = propertiesConfiguration.getString("ms.connector.port");
//        if(host == null)
//            host = propertiesConfiguration.getString("ms.connector.host");

        APSConfig config = new APSConfig(host, Integer.parseInt(port), "4283BF786996E133D9F9A0B37BC067C3");
        AppsanApplication.setDebug(true);
        AppsanApplication.run(MainClass.class, args, config);
        AppsanApplication.registerChannel(new HighwayChannel());

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


        //commentId,teacherName,teacherAcademicGroup,teacherEmail,teacherLessons,studentName,studentFaculty,studentGender,studentId
    }
}
