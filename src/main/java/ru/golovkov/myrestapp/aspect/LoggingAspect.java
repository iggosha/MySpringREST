package ru.golovkov.myrestapp.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.stream.Collectors;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Before("@within(ru.golovkov.myrestapp.aspect.LoggingMark) && execution(* *(..))")
    public void logBeforeAccountOperations(JoinPoint joinPoint) {
        String methodArgs = Arrays.stream(joinPoint.getArgs())
                .map(Object::toString)
                .collect(Collectors.joining(", "));
        log.info("Account operation: {}, {}", joinPoint.getSignature().getName(), methodArgs);
    }
}