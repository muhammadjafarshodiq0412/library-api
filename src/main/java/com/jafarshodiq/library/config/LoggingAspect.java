package com.jafarshodiq.library.config;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class LoggingAspect {

    @Around("""
            execution(* com.jafarshodiq.library.controller..*(..))
            || execution(* com.jafarshodiq.library.service..*(..))
            """)
    public Object logExecution(ProceedingJoinPoint joinPoint)
            throws Throwable {

        String className = joinPoint
                .getSignature()
                .getDeclaringType()
                .getSimpleName();

        String methodName = joinPoint
                .getSignature()
                .getName();

        long startTime = System.currentTimeMillis();

        log.info(
                "Started: {}.{}()",
                className,
                methodName
        );

        try {

            Object result = joinPoint.proceed();

            long duration =
                    System.currentTimeMillis() - startTime;

            log.info(
                    "Completed: {}.{}() - duration={}ms",
                    className,
                    methodName,
                    duration
            );

            return result;

        } catch (Exception ex) {

            long duration =
                    System.currentTimeMillis() - startTime;

            log.error(
                    "Failed: {}.{}() - duration={}ms - error={}",
                    className,
                    methodName,
                    duration,
                    ex.getMessage(),
                    ex
            );

            throw ex;
        }
    }
}