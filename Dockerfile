FROM eclipse-temurin:17

# Copy the .kobweb folder
COPY site/.kobweb /app/.kobweb

WORKDIR /app

# Debug: Verify the files were copied correctly
RUN echo "=== Verifying .kobweb contents ===" && \
    ls -la .kobweb/ && \
    echo "=== Verifying server directory ===" && \
    ls -la .kobweb/server/ && \
    echo "=== Checking for server.jar ===" && \
    test -f .kobweb/server/server.jar && echo "server.jar found!" || echo "server.jar NOT found!"

# MongoDB URI will be set at runtime via Render environment variables
ENV MONGODB_URI=""

# Expose the port
EXPOSE 8080

# Run the JAR directly (no permission issues)
ENTRYPOINT ["java", "-jar", ".kobweb/server/server.jar"]