# JDK image for building the application
FROM eclipse-temurin:17-jdk AS build 

WORKDIR /app
# Copy contents to build the application. Test are skipped as they are verified in the Jenkins pipeline
COPY . .
RUN ./mvnw clean package -DskipTests


# Application runtime
FROM eclipse-temurin:17-jre
WORKDIR /app

# Non root user for better security
RUN groupadd spring && useradd -m -r -g spring --shell /bin/bash spring
USER spring

# Copy the built jar file
COPY --from=build /app/target/products-api-0.0.1-SNAPSHOT.jar /app/products-api.jar

EXPOSE 8080
# Run the application
CMD ["java", "-jar", "products-api.jar"]