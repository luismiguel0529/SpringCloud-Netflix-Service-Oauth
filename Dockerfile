FROM openjdk:14
VOLUME /tmp
EXPOSE 8761
ADD ./target/springboot-servicio-oauth-0.0.1-SNAPSHOT.jar oauth-server.jar
ENTRYPOINT ["java","-jar","/oauth-server.jar"]