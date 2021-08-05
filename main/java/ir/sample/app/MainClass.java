    package ir.sample.app;

import com.github.mfathi91.time.PersianDate;
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
import java.util.Date;


    public class MainClass {

    public static void main(String[] args) throws ConfigurationException {

        System.out.println("****** BEHESHTRAY ORG® ******");

        String host = "connectorsbu.appsan.ir";
        String port = "443";

        APSConfig config = new APSConfig(host, Integer.parseInt(port), "4283BF786996E133D9F9A0B37BC067C3");
        AppsanApplication.setDebug(true);
        AppsanApplication.run(MainClass.class, args, config);
        AppsanApplication.registerChannel(new HighwayChannel());

    }
}
