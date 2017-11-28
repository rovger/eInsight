package com.eInsight.common;

import com.eInsight.resources.ConfigService;

public class BaseInitializer {
    public void init() {
        System.out.println("===================start initilizing===============");
        ConfigService config = ConfigService.getInstance();
        config.competeForLeader();
        config.checkFailureTask();
        System.out.println("===================finish initilizing===============");
    }
}
