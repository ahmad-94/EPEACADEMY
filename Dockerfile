#-----------------------------------------------------------------------------
# Variables shared across multiple stages
ARG KOBWEB_APP_ROOT="site"

FROM eclipse-temurin:17 as java

FROM java as export

ARG KOBWEB_APP_ROOT
ENV NODE_MAJOR=20

# Copy the project code
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

# Decrease Gradle memory usage
RUN mkdir ~/.gradle && \
    echo "org.gradle.jvmargs=-Xmx512m" >> ~/.gradle/gradle.properties

# Set MongoDB URI for the build
ENV MONGODB_URI="mongodb://localhost:27017"

# ONLY build the site - skip the export entirely
RUN ./gradlew :site:build --no-daemon --stacktrace

# Verify the build produced the static files
RUN echo "=== Checking build output ===" && \
    ls -la /project/${KOBWEB_APP_ROOT}/build/dist/js/productionExecutable/ && \
    echo "=== Looking for HTML files ===" && \
    find /project/${KOBWEB_APP_ROOT}/build -name "*.html" -type f 2>/dev/null | head -10

#-----------------------------------------------------------------------------
# Create the final stage, which serves the static files
FROM java as run

ARG KOBWEB_APP_ROOT

# Install Python for HTTP server
RUN apt-get update && apt-get install -y python3 -y

# Copy the built static files
COPY --from=export /project/${KOBWEB_APP_ROOT}/build/dist/js/productionExecutable /app/site

WORKDIR /app

# Debug: Check what was copied
RUN echo "=== Files in /app/site ===" && \
    ls -la /app/site/ && \
    echo "=== Looking for HTML files ===" && \
    find /app/site -name "*.html" 2>/dev/null || echo "No HTML files found"

# Set MongoDB URI for runtime (will be overridden by Render env var)
ENV MONGODB_URI=""

# Try multiple locations for serving
ENTRYPOINT ["/bin/sh", "-c", "if [ -f /app/site/index.html ]; then echo 'Serving from /app/site'; python3 -m http.server 8080 --directory /app/site; elif [ -f /app/site/kobweb/index.html ]; then echo 'Serving from /app/site/kobweb'; python3 -m http.server 8080 --directory /app/site/kobweb; elif [ -f /app/site/public/index.html ]; then echo 'Serving from /app/site/public'; python3 -m http.server 8080 --directory /app/site/public; else echo 'No HTML found!'; find /app -name '*.html' 2>/dev/null || echo 'No HTML files at all'; exit 1; fi"]