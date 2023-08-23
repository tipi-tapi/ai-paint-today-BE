# 개발 컨벤션 & 협업 전략

## 커밋 메시지

커밋 메시지 형식은 아래와 같습니다. `subject`까지만 작성해도 괜찮습니다.

```
<type>(<scope>): <subject>
<BLANK LINE>
<body>
<BLANK LINE>
<footer>
```

## 커밋 타입(`<Type>`)

- `feat` : (feature)
- `fix` : (bug fix)
- `docs`: (documentation)
- `style` : (formatting, missing semi colons, …)
- `refactor`
- `test` : (when adding missing tests)
- `chore` : (maintain)

## 브랜치 전략

- main
- develop
- feature
- release
- hotfix

## 로그 전략

src/main/resources/logback-spring.xml에서 logback 설정 파일을 확인할 수 있습니다.

```xml

<configuration>

    <property name="CONSOLE_LOG_PATTERN"
              value="[%d{HH:mm:ss.SSS}] [%blue(%X{request_id})] %green([%thread]) %highlight(%-5level) %boldWhite([%C.%M:%yellow(%L)]) - %msg%n" />
    <!--로그 패턴을 property로 등록하여 관리한다. %d는 날짜, %X는 MDC에 저장된 값을 불러올 수 있다.-->

    <property name="FILE_LOG_PATTERN"
              value="[%d{HH:mm:ss.SSS}] [%blue(%X{request_id})] %green([%thread]) %highlight(%-5level) %boldWhite([%C.%M:%yellow(%L)]) - %msg%n" />

    <property name="LOG_PATH" value="./logs" />

    <appender class="ch.qos.logback.core.rolling.RollingFileAppender" name="FILE_INFO">
        <encoder>
            <pattern>${FILE_LOG_PATTERN}</pattern>
        </encoder>
        <filter class="ch.qos.logback.classic.filter.LevelFilter">
            <level>INFO</level>
            <onMatch>ACCEPT</onMatch>
            <onMismatch>DENY</onMismatch>
            <!-- INFO를 제외한 다른 레벨의 로그는 제외하는 필터 -->
        </filter>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>${LOG_PATH}/%d{yyyy-MM}/info-%d{yyyy-MM}.log</fileNamePattern>
            <!-- LOG_PATH 속성값 경로 / 연월 / info-연월.log 경로에 저장된다 -->
            <maxHistory>12</maxHistory>
            <!-- info 레벨의 로그 파일이 12개가 넘어가면 오래된 로그 파일을 삭제한다(1년) -->
            <totalSizeCap>100MB</totalSizeCap>
            <!-- 만약 하나의 로그 파일이 100MB가 넘어가면 롤링한다. -->
        </rollingPolicy>
    </appender>

    <appender class="ch.qos.logback.core.rolling.RollingFileAppender" name="FILE_WARN">
        <encoder>
            <pattern>${FILE_LOG_PATTERN}</pattern>
        </encoder>
        <filter class="ch.qos.logback.classic.filter.LevelFilter">
            <level>WARN</level>
            <onMatch>ACCEPT</onMatch>
            <onMismatch>DENY</onMismatch>
        </filter>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>${LOG_PATH}/%d{yyyy-MM}/warn-%d{yyyy-MM}.log</fileNamePattern>
            <maxHistory>12</maxHistory>
            <totalSizeCap>100MB</totalSizeCap>
        </rollingPolicy>
    </appender>

    <appender class="ch.qos.logback.core.rolling.RollingFileAppender" name="FILE_ERROR">
        <encoder>
            <pattern>${FILE_LOG_PATTERN}</pattern>
        </encoder>
        <filter class="ch.qos.logback.classic.filter.LevelFilter">
            <level>ERROR</level>
            <onMatch>ACCEPT</onMatch>
            <onMismatch>DENY</onMismatch>
        </filter>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>${LOG_PATH}/%d{yyyy-MM}/error-%d{yyyy-MM}.log</fileNamePattern>
            <maxHistory>12</maxHistory>
            <totalSizeCap>100MB</totalSizeCap>
        </rollingPolicy>
    </appender>

    <springProfile name="develop">
        <!-- profile이 develop(local 환경)일 경우 콘솔에 로그를 출력한다 -->
        <appender class="ch.qos.logback.core.ConsoleAppender" name="STDOUT">
            <encoder>
                <pattern>${CONSOLE_LOG_PATTERN}</pattern>
            </encoder>
        </appender>

        <root level="INFO">
            <appender-ref ref="STDOUT" />
        </root>
        <!-- INFO 이상의 레벨에 대해 name이 STDOUT인 appender의 이벤트를 발행한다 -->
    </springProfile>

    <springProfile name="test">
        <property name="LOG_PATH" value="~/TipiTapiTestServer/log" />
        <!-- 로그 파일 경로를 ~/TipiTapiTestServer/log로 설정한다.-->
        <!--  따라서 FILE_INFO 등의 appender가 해당 경로로 로그를 저장한다 -->
        <root level="INFO">
            <appender-ref ref="FILE_INFO" />
            <appender-ref ref="FILE_WARN" />
            <appender-ref ref="FILE_ERROR" />
        </root>
    </springProfile>

    <springProfile name="prod">
        <property name="LOG_PATH" value="~/SpringServer/log" />
        <root level="INFO">
            <appender-ref ref="FILE_INFO" />
            <appender-ref ref="FILE_WARN" />
            <appender-ref ref="FILE_ERROR" />
        </root>
    </springProfile>
</configuration>
```