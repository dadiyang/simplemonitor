package com.github.dadiyang.simplemonitor;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@ComponentScan(basePackages = "com.bj58.crm.simplemonitor")
@EnableAspectJAutoProxy
public class TestApplication {
}
