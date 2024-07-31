FROM corretto:17

ARG JAR_FILE=build/libs/*.jar

COPY build/libs/* application.jar

CMD ["java", "-Dfile.encoding=UTF-8", "-jar", "application.jar"]
