FROM eclipse-temurin:17

# Copy the .kobweb folder
COPY site/.kobweb /app/.kobweb

WORKDIR /app

# Fix the start.sh script to use Linux paths and make it executable
RUN sed -i 's/\r$//' .kobweb/server/start.sh && \
    sed -i 's/\\/\//g' .kobweb/server/start.sh && \
    chmod +x .kobweb/server/start.sh

# Debug: Check what's in start.sh
RUN echo "=== start.sh contents ===" && \
    cat .kobweb/server/start.sh

# Expose the port
EXPOSE 8080

# Use the fixed start.sh
ENTRYPOINT ["/app/.kobweb/server/start.sh"]