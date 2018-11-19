package com.github.dadiyang.simplemonitor;

import java.lang.annotation.*;

/**
 * 监控方法的耗时信息
 *
 * @author dadiyang
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.METHOD, ElementType.TYPE})
public @interface TimeConsuming {
    /**
     * 是否打印完整的方法名、方法参数和返回值
     *
     * @return 是否打印完整的方法名、方法参数和返回值
     */
    boolean fullMsg() default false;

    /**
     * 是否打印完整的参数
     *
     * @return 是否打印完整的参数
     */
    boolean fullArg() default false;

    /**
     * 是否打印完整的返回值
     *
     * @return 是否打印完整的返回值
     */
    boolean fullReturnVal() default false;

    /**
     * 是否打印完整的方法名称（包括全类名和方法的全类名）
     *
     * @return 是否打印完整的方法名称
     */
    boolean fullMethodName() default false;

    /**
     * 是否监控方法抛出的异常，打印异常信息
     * <p>
     * 注意：监控方法抛出的异常只会以logLevel指定的日志级别打印方法相关信息和 e.getMessage()，最后把异常重新抛出
     *
     * @return 是否监控方法抛出的异常，默认 true
     */
    boolean monitorException() default true;

    /**
     * 日志级别(0: TRACE, 1: DEBUG, 2: INFO, 3: WARN, 4: ERROR)，默认 2，即INFO级别
     *
     * @return 日志级别
     */
    int logLevel() default 2;

    /**
     * 是否使用被注解方法所属的类对应的日志类进行日志输出
     * <p>
     * 即 LoggerFactory.getLogger(方法所属类);
     * <p>
     * 默认 false
     *
     * @return 是否使用被注解方法所属的类对应的日志类进行日志输出
     */
    boolean useSourceClassLog() default false;
}
