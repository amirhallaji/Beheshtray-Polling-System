package ir.sample.app.highway;

import ir.appsan.sdk.APSChannel;
import ir.sample.app.highway.services.HighwayService;

public class HighwayChannel extends APSChannel {

    public String getChannelName() {
        return "miniApp";
    }

    public void init() {
        registerService(new HighwayService(getChannelName()));
    }
}
