FROM maven:3.9.9-eclipse-temurin-8

WORKDIR /app

COPY . .

RUN mvn clean package -DskipTests

EXPOSE 8080

ENTRYPOINT ["java","-Dserver.port=${PORT}","-jar","target/recruitiq-1.0.0.jar"]