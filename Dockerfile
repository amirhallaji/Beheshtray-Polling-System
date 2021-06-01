FROM openjdk:8-jdk-alpine
RUN apk add --no-cache tzdata
ENV TZ=Asia/Tehran
WORKDIR /app
COPY target/*-jar-with-dependencies.jar java_app.jar
ENTRYPOINT ["java", "-jar", "java_app.jar"]
