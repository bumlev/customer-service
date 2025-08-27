# select parent image
FROM maven:3.8.3-openjdk-17 AS build
WORKDIR /home

# copy the source tree and the pom.xml to our new container
COPY src /home/src
COPY pom.xml /home/pom.xml


# package our application code
RUN mvn clean  package -Dmaven.test.skip=true # Build jar file

# Stage 2: Create the final Docker image
FROM openjdk:17-jdk

COPY --from=build /home/target/*.jar Customer-Service-0.0.1-SNAPSHOT.jar

EXPOSE 8091
# set the startup command to execute the jar
ENTRYPOINT ["java", "-jar", "Customer-Service-0.0.1-SNAPSHOT.jar"]