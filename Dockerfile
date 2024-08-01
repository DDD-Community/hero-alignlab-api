# jdk setup
FROM amazoncorretto:17

# JAR 파일 경로와 파일명
ARG JAR_FILE=build/libs/*.jar

# JAR 파일 이미지 내부로 복사
COPY ${JAR_FILE} app.jar

# 어플리케이션 실행 및 로그 출력
#CMD nohup java -jar /app.jar > stdout.log 2> stderr.log &
ENTRYPOINT ["java","-Dspring.profiles.active=prod","-jar","/app.jar"]
