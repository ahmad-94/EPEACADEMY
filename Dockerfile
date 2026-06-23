FROM eclipse-temurin:17

# Copy the .kobweb folder
COPY site/.kobweb /app/.kobweb

WORKDIR /app

# 👇 Debug: Show environment variables
RUN echo "=== All environment variables ===" && \
    env && \
    echo "=== MONGODB_URI specifically ===" && \
    echo "MONGODB_URI=${MONGODB_URI}"

EXPOSE 8080

ENTRYPOINT ["/bin/sh", "-c", "java -jar .kobweb/server/server.jar"]