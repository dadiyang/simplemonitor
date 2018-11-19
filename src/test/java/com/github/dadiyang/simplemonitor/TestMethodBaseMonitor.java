package com.github.dadiyang.simplemonitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class TestMethodBaseMonitor {
    private static final Logger log = LoggerFactory.getLogger(TestMethodBaseMonitor.class);

    @TimeConsuming
    public String testTimeConsuming(String a, String b) {
        log.info("I'm method annotated, a:" + a + ", b:" + b);
        return "OK2";
    }

    @TimeConsuming(logLevel = 1, useSourceClassLog = true)
    public String testDebugLevel(String a, String b) {
        log.info("debug level and useSourceClassLog:" + a + ", b:" + b);
        return "haha";
    }
}