package ir.sample.app;

import ir.appsan.sdk.APSConfig;
import ir.appsan.sdk.AppsanApplication;
import ir.sample.app.BeheshtRay.HighwayChannel;

import javax.naming.ConfigurationException;


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
