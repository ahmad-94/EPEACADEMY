FROM eclipse-temurin:17

# Copy the entire site folder
COPY site /app/site

WORKDIR /app/site

# Debug: Check what was copied
RUN echo "=== Verifying .kobweb contents ===" && \
    ls -la .kobweb/ && \
    echo "=== Verifying server directory ===" && \
    ls -la .kobweb/server/ && \
    echo "=== Verifying site directory ===" && \
    ls -la .kobweb/site/ && \
    echo "=== Checking for server.jar ===" && \
    test -f .kobweb/server/server.jar && echo "server.jar found!" || echo "server.jar NOT found!"

ENV MONGODB_URI=""

EXPOSE 8080

# Run the server
ENTRYPOINT ["java", "-jar", ".kobweb/server/server.jar"]