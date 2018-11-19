package com.github.dadiyang.simplemonitor;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = TestApplication.class)
public class TimeConsumingMonitorTest {
    @Autowired
    private TestClassBaseMonitor testClassBaseMonitor;

    @Autowired
    private TestMethodBaseMonitor testMethodBaseMonitor;
    @Test
    public void testClass() throws InterruptedException {
        testClassBaseMonitor.testTimeConsuming("run");
        testClassBaseMonitor.testTimeConsumingWithAnno("haha", "heyhey");
        testClassBaseMonitor.testPackagePrivateMethod("you know");
    }

    @Test
    public void testMethod(){
        testMethodBaseMonitor.testTimeConsuming("run run run！", "here we go");
        testMethodBaseMonitor.testDebugLevel("debug test", "sourceClass");
    }
}