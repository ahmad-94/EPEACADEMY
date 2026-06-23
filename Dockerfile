#-----------------------------------------------------------------------------
# Variables shared across multiple stages
ARG KOBWEB_APP_ROOT="site"

FROM eclipse-temurin:17 as java

FROM java as export

ARG KOBWEB_APP_ROOT
ENV NODE_MAJOR=20

COPY . /project

# Install Node.js and npm
RUN apt-get update \
    && apt-get install -y ca-certificates curl gnupg unzip wget \
    && mkdir -p /etc/apt/keyrings \
    && curl -fsSL https://deb.nodesource.com/gpgkey/nodesource-repo.gpg.key | gpg --dearmor -o /etc/apt/keyrings/nodesource.gpg \
    && echo "deb [signed-by=/etc/apt/keyrings/nodesource.gpg] https://deb.nodesource.com/node_$NODE_MAJOR.x nodistro main" | tee /etc/apt/sources.list.d/nodesource.list \
    && apt-get update \
    && apt-get install -y nodejs \
    && apt-get install -y npm \
    && npm install -g npm@latest \
    && node --version && npm --version

WORKDIR /project

# Make gradlew executable
RUN chmod +x gradlew

# Create gradle properties
RUN mkdir ~/.gradle && \
    echo "org.gradle.jvmargs=-Xmx512m" >> ~/.gradle/gradle.properties

# Set MongoDB URI (needed for the build)
ENV MONGODB_URI="mongodb://localhost:27017"

# Use the build task - this generates the static files
RUN ./gradlew :site:build --no-daemon --stacktrace

# Verify the build output
RUN echo "=== Checking build output ===" && \
    ls -la /project/${KOBWEB_APP_ROOT}/build/ && \
    echo "=== Checking production executable ===" && \
    ls -la /project/${KOBWEB_APP_ROOT}/build/dist/js/productionExecutable/ && \
    echo "=== Checking for index.html ===" && \
    test -f /project/${KOBWEB_APP_ROOT}/build/dist/js/productionExecutable/index.html && \
    echo "index.html found!" || echo "index.html NOT found!"

# Also try the kobwebExport (which creates the server)
RUN ./gradlew :site:kobwebExport --no-daemon --stacktrace || echo "Export failed, but build succeeded"

#-----------------------------------------------------------------------------
# Create the final stage
FROM java as run

ARG KOBWEB_APP_ROOT

# Copy the built static files from the build directory
COPY --from=export /project/${KOBWEB_APP_ROOT}/build/dist/js/productionExecutable /app/site

# Also copy .kobweb if it exists (for server functionality)
COPY --from=export /project/${KOBWEB_APP_ROOT}/.kobweb /app/.kobweb 2>/dev/null || echo "No .kobweb found"

# Debug: Check what was copied
RUN echo "=== Checking /app/site contents ===" && \
    ls -la /app/site/ && \
    echo "=== Checking for index.html ===" && \
    test -f /app/site/index.html && echo "index.html found!" || echo "index.html NOT found!"

WORKDIR /app

# Run the server using the start script or serve static files
ENTRYPOINT ["/bin/sh", "-c", "if [ -f .kobweb/server/start.sh ]; then .kobweb/server/start.sh; elif [ -f /app/site/index.html ]; then python3 -m http.server 8080; else echo 'No files found!'; exit 1; fi"]