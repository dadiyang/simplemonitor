package com.github.dadiyang.simplemonitor;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@ComponentScan(basePackages = "com.github.dadiyang.simplemonitor")
@EnableAspectJAutoProxy
public class TestApplication {
}
