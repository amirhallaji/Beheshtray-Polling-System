package ir.sample.app;

import ir.appsan.sdk.APSConfig;
import ir.appsan.sdk.AppsanApplication;
import ir.sample.app.BeheshtRay.HighwayChannel;

import javax.naming.ConfigurationException;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;


public class MainClass {

    public static void main(String[] args) throws ConfigurationException {

        System.out.println("****** BEHESHTRAY ORG® ******");

        try {
            FileOutputStream fileOut = new FileOutputStream("/main/java/ir/sample/app/BeheshtRay/database/current_user.ser");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


        String host = "connectorsbu.appsan.ir";
        String port = "443";

        APSConfig config = new APSConfig(host, Integer.parseInt(port), "4283BF786996E133D9F9A0B37BC067C3");
        AppsanApplication.setDebug(true);
        AppsanApplication.run(MainClass.class, args, config);
        AppsanApplication.registerChannel(new HighwayChannel());

    }
}
