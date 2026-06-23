FROM eclipse-temurin:17

# Copy the entire .kobweb folder (generated locally)
COPY site/.kobweb /app/.kobweb

WORKDIR /app

# MongoDB URI will be set at runtime
ENV MONGODB_URI=""

EXPOSE 8080

# Run the server
ENTRYPOINT ["java", "-jar", ".kobweb/server/server.jar"]