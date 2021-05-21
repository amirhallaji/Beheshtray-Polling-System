package ir.sample.app.BeheshtRay;

import ir.appsan.sdk.APSChannel;
import ir.sample.app.BeheshtRay.services.BeheshtRayService;


public class HighwayChannel extends APSChannel {

    public String getChannelName() {
        return "miniApp";
    }

    public void init() {
        registerService(new BeheshtRayService(getChannelName()));
    }
}
