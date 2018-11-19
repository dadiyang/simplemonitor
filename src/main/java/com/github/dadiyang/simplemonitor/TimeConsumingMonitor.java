package com.github.dadiyang.simplemonitor;

import com.alibaba.fastjson.JSON;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 使用切面记录每个接口调用的耗时
 *
 * @author dadiyang
 * @date 2018/10/25
 */
@Aspect
@Component
public class TimeConsumingMonitor {
    private static final Logger log = LoggerFactory.getLogger(TimeConsumingMonitor.class);
    private static final int MAX_STRING_LENGTH = 128;

    /**
     * 拦截类上的 TimeConsuming 注解
     */
    @Around(value = "@within(timeConsuming)")
    public Object cutClazz(ProceedingJoinPoint joinPoint, TimeConsuming timeConsuming) throws Throwable {
        return logging(joinPoint, timeConsuming);
    }

    /**
     * 拦截方法上的 TimeConsuming 注解
     */
    @Around(value = "@annotation(timeConsuming)")
    public Object cutMethod(ProceedingJoinPoint joinPoint, TimeConsuming timeConsuming) throws Throwable {
        if (joinPoint.getSignature().getDeclaringType().isAnnotationPresent(timeConsuming.getClass())) {
            // 此方法仅拦截方法上的 TimeConsuming 注解
            return joinPoint.proceed(joinPoint.getArgs());
        }
        return logging(joinPoint, timeConsuming);
    }

    /**
     * 记录接口耗时和方法参数简单摘要
     */
    private Object logging(ProceedingJoinPoint joinPoint, TimeConsuming timeConsuming) throws Throwable {
        Logger logger = getLogger(joinPoint, timeConsuming);
        try {
            long start = System.currentTimeMillis();
            Object result = joinPoint.proceed(joinPoint.getArgs());
            // 如果获取不到注解或者设置的日志级别与当前logger的级别不匹配，则直接返回结果
            if (timeConsuming == null || !needPrint(timeConsuming.logLevel(), logger)) {
                return result;
            }
            long time = System.currentTimeMillis() - start;
            String argsString = "[]";
            if (joinPoint.getArgs().length > 0) {
                argsString = summary(joinPoint.getArgs(), timeConsuming.fullMsg() || timeConsuming.fullArg());
            }
            String resultString = "";
            if (!(result instanceof Void)) {
                resultString = summary(result, timeConsuming.fullMsg() || timeConsuming.fullReturnVal());
            }
            String methodName = getMethodName(joinPoint, timeConsuming);
            printLog(timeConsuming.logLevel(), logger, "调用方法{}, 参数: {}, 结果: {}, 执行耗时: {}", methodName, argsString, resultString, time);
            return result;
        } catch (Throwable throwable) {
            // 如果需要监控异常信息，才打印异常日志
            if (timeConsuming.monitorException() && needPrint(timeConsuming.logLevel(), logger)) {
                String methodName = getMethodName(joinPoint, timeConsuming);
                String argString = summary(joinPoint.getArgs(), timeConsuming.fullMsg() || timeConsuming.fullArg());
                printLog(timeConsuming.logLevel(), logger, "调用方法{}, 参数:{}, 抛出异常:{}", methodName, argString, throwable.getMessage());
            }
            // 把异常抛出
            throw throwable;
        }
    }

    /**
     * 根据条件获取日志实例
     *
     * @param joinPoint     连接点
     * @param timeConsuming 注解
     * @return 日志实例
     */
    private Logger getLogger(ProceedingJoinPoint joinPoint, TimeConsuming timeConsuming) {
        return timeConsuming != null && timeConsuming.useSourceClassLog() ? LoggerFactory.getLogger(joinPoint.getSignature().getDeclaringType()) : log;
    }

    /**
     * 根据给你写的日志级别和日志类实例打印日志
     *
     * @param level  日志级别
     * @param logger 日志类
     * @param format 格式
     * @param args   日志格式使用的参数
     */
    private void printLog(int level, Logger logger, String format, Object... args) {
        switch (level) {
            case 0:
                logger.trace(format, args);
                break;
            case 1:
                logger.debug(format, args);
                break;
            case 2:
                logger.info(format, args);
                break;
            case 3:
                logger.warn(format, args);
                break;
            case 4:
                logger.error(format, args);
                break;
            default:
                logger.info(format, args);
        }
    }

    /**
     * 设置的日志级别与给定的logger级别是否一致
     *
     * @param level  注解中设置的日志级别
     * @param logger 日志类
     * @return 是否需要打印日志
     */
    private boolean needPrint(int level, Logger logger) {
        return (level == 0 && logger.isTraceEnabled())
                || (level == 1 && logger.isDebugEnabled())
                || (level == 2 && logger.isInfoEnabled())
                || (level == 3 && logger.isWarnEnabled())
                || (level == 4 && logger.isErrorEnabled());
    }

    /**
     * @param joinPoint     连接点
     * @param timeConsuming 注解
     * @return 方法名
     */
    private String getMethodName(ProceedingJoinPoint joinPoint, TimeConsuming timeConsuming) {
        Signature signature = joinPoint.getSignature();
        return timeConsuming.fullMsg() || timeConsuming.fullMethodName() ? signature.toLongString() : signature.toShortString();
    }

    /**
     * 将对象序列化后取摘要
     *
     * @param obj     需要被摘要的类
     * @param fullMsg 是否使用全信息
     * @return 摘要
     */
    private String summary(Object obj, boolean fullMsg) {
        String argsString = JSON.toJSONString(obj);
        if (fullMsg) {
            return argsString;
        }
        if (argsString.length() > MAX_STRING_LENGTH) {
            // 参数的简单摘要
            argsString = argsString.substring(0, MAX_STRING_LENGTH) + "...";
        }
        return argsString;
    }

}
