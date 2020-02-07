FROM openjdk:8-alpine

COPY target/uberjar/admin-app.jar /admin-app/app.jar

EXPOSE 3000

CMD ["java", "-jar", "/admin-app/app.jar"]
