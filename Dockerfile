########Maven build stage########
FROM maven:3.6-jdk-11 as maven_build
WORKDIR /app
COPY . .
RUN mvn clean package
##run the app
CMD java -cp .:classes:lib/* \
         -Djava.security.egd=file:/dev/./urandom \
         com.tb.logprocessorservice.LogProcessorServiceApplication
VOLUME /tmp
EXPOSE 8081
ENTRYPOINT ["java", "-Xmx500m", "-jar", "target/logprocessor-docker-0.0.1-SNAPSHOT.jar"]