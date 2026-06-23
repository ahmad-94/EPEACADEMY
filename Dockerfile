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

# Make start.sh executable (optional, but we won't use it)
RUN chmod +x .kobweb/server/start.sh || true

# Expose the port
EXPOSE 8080

# 👇 Explicitly pass the environment variable to Java
ENTRYPOINT ["/bin/sh", "-c", "java -DMONGODB_URI=${MONGODB_URI} -jar .kobweb/server/server.jar"]