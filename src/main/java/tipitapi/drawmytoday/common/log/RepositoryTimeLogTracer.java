package tipitapi.drawmytoday.common.log;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class RepositoryTimeLogTracer {

    @Around("execution(* tipitapi.drawmytoday.domain.*.repository.*Repository.*(..)) "
        + "&& !execution(* tipitapi.drawmytoday.domain.*.repository.*Impl.*(..))")
    public Object doLog(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Object proceed = joinPoint.proceed();
        long finish = System.currentTimeMillis();
        String[] typeName = joinPoint.getSignature().getDeclaringTypeName().split("\\.");
        log.info("[{}] [{}] [{}]", typeName[typeName.length - 1],
            joinPoint.getSignature().getName(), finish - start + "ms");
        return proceed;
    }
}
