FROM eclipse-temurin:17

# Copy the .kobweb folder from your local export
COPY site/.kobweb /app/.kobweb

# Set working directory
WORKDIR /app

# Debug: Verify the files were copied correctly
RUN echo "=== Verifying .kobweb contents ===" && \
    ls -la .kobweb/ && \
    echo "=== Verifying server directory ===" && \
    ls -la .kobweb/server/ && \
    echo "=== Verifying site directory ===" && \
    ls -la .kobweb/site/ && \
    echo "=== Checking for server.jar ===" && \
    test -f .kobweb/server/server.jar && echo "server.jar found!" || echo "server.jar NOT found!"

# MongoDB URI will be set at runtime via Render environment variables
ENV MONGODB_URI=""

# Expose the port
EXPOSE 8080

# Start the server - try start.sh first, fallback to server.jar
ENTRYPOINT ["/bin/sh", "-c", "if [ -f .kobweb/server/start.sh ]; then .kobweb/server/start.sh; else java -jar .kobweb/server/server.jar; fi"]