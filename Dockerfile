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

#FROM maven:3.6-jdk-11 as maven_build
#WORKDIR /app
#
##copy pom
#COPY pom.xml .
#
##copy source
#COPY src ./src

# build the app and download dependencies only when these are new otherwise pick from cache
#RUN --mount=type=cache,target=/root/.m2  mvn clean package -Dmaven.test.skip
#
## split the built app into multiple layers to improve layer rebuild
#RUN mkdir -p target/docker-packaging && cd target/docker-packaging && jar -xf ../log-processor-service.jar

########JRE run stage########
#FROM openjdk:11.0-jre
#
##copy built app layer by layer
#ARG DOCKER_DIR=/app/target
#COPY --from=maven_build ${DOCKER_DIR}/docker-packaging/BOOT-INF/lib /app/lib
#COPY --from=maven_build ${DOCKER_DIR}/docker-packaging/BOOT-INF/classes /app/classes
#COPY --from=maven_build ${DOCKER_DIR}/docker-packaging/META-INF /app/META-INF
#COPY --from=maven_build ${DOCKER_DIR}/log-processor-service.jar /log-processor-service.jar
#
##run the app
#CMD java -cp .:classes:lib/* \
#         -Djava.security.egd=file:/dev/./urandom \
#         com.tb.logprocessorservice.LogProcessorServiceApplication
#
#VOLUME /tmp
#EXPOSE 18080
#ENTRYPOINT ["java", "-Xmx500m", "-jar", "/log-processor-service.jar"]