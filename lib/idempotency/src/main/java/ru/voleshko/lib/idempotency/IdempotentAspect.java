package ru.voleshko.lib.idempotency;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.lang.reflect.Method;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.UUID;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class IdempotentAspect {

    private final EntityManager entityManager;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Around("@annotation(ru.voleshko.lib.idempotency.Idempotent)")
    public Object idempotentReceive(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Idempotent annotation = method.getAnnotation(Idempotent.class);
        String operation = annotation.operation();
        String idempotencyKeyParamName = annotation.idempotencyKeyName();

        Object[] args = joinPoint.getArgs();
        String[] parametersName = signature.getParameterNames();
        int idx = Arrays.asList(parametersName).indexOf(idempotencyKeyParamName);

        if(idx == -1) {
            throw new IllegalArgumentException("Parameter is not specified: " + idempotencyKeyParamName);
        }
        Object idempotencyKey = args[idx];
        if (idempotencyKey == null) {
            return joinPoint.proceed();
        }
        boolean isDuplicate =
                (boolean) entityManager
                        .createNativeQuery("SELECT COUNT(*) > 0 FROM idempotency_key WHERE key=:key AND operation=:operation")
                        .setParameter("key", idempotencyKey)
                        .setParameter("operation", operation)
                        .getSingleResult();
        if (isDuplicate) {
            return null;
        } else {
            entityManager.persist(new IdempotencyKey((UUID) idempotencyKey, operation, ZonedDateTime.now()));
            return joinPoint.proceed();
        }
    }
}
