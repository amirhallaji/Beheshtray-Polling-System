package ir.sample.app;

import ir.appsan.sdk.APSConfig;
import ir.appsan.sdk.AppsanApplication;
import ir.sample.app.BeheshtRay.HighwayChannel;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.ConfigurationException;


public class MainClass {

    public static void main(String[] args) throws ConfigurationException {

        System.out.println("****** BEHESHTRAY ORG® ******");
        
        PropertiesConfiguration propertiesConfiguration = new PropertiesConfiguration();
        propertiesConfiguration.load("application.properties");
        String port = System.getenv("CONNECTOR_PORT");
        String host = System.getenv("CONNECTOR_HOST");
        if(port == null)
            port = propertiesConfiguration.getString("ms.connector.port");
        if(host == null)
            host = propertiesConfiguration.getString("ms.connector.host");


        APSConfig config = new APSConfig(host, Integer.parseInt(port), "4283BF786996E133D9F9A0B37BC067C3");
        AppsanApplication.setDebug(true);
        AppsanApplication.run(MainClass.class, args, config);
        AppsanApplication.registerChannel(new HighwayChannel());

    }
}
