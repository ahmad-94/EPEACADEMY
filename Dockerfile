ARG KOBWEB_APP_ROOT="site"

FROM eclipse-temurin:17 as java

FROM java as export

ARG KOBWEB_APP_ROOT
ENV NODE_MAJOR=20

COPY . /project

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

# Install Kobweb CLI
ENV KOBWEB_CLI_VERSION=0.9.21
RUN wget https://github.com/varabyte/kobweb-cli/releases/download/v${KOBWEB_CLI_VERSION}/kobweb-${KOBWEB_CLI_VERSION}.zip \
    && unzip kobweb-${KOBWEB_CLI_VERSION}.zip \
    && rm kobweb-${KOBWEB_CLI_VERSION}.zip
ENV PATH="/kobweb-${KOBWEB_CLI_VERSION}/bin:${PATH}"

WORKDIR /project

RUN chmod +x gradlew

RUN mkdir ~/.gradle && \
    echo "org.gradle.jvmargs=-Xmx512m" >> ~/.gradle/gradle.properties

ENV MONGODB_URI="mongodb://localhost:27017"

# Build the site
RUN ./gradlew :site:build --no-daemon --stacktrace

# Try to export (this creates the .kobweb folder with server.jar)
RUN ./gradlew :site:kobwebExport --no-daemon --stacktrace || echo "Export had issues, but continuing"

# Check what we have
RUN echo "=== Checking .kobweb ===" && \
    ls -la /project/${KOBWEB_APP_ROOT}/.kobweb/ 2>/dev/null || echo ".kobweb not found" && \
    echo "=== Checking .kobweb/server ===" && \
    ls -la /project/${KOBWEB_APP_ROOT}/.kobweb/server/ 2>/dev/null || echo "server not found"

FROM java as run

ARG KOBWEB_APP_ROOT

# Copy the .kobweb directory (contains server.jar)
COPY --from=export /project/${KOBWEB_APP_ROOT}/.kobweb /app/.kobweb

# Also copy static files as fallback
COPY --from=export /project/${KOBWEB_APP_ROOT}/build/dist/js/productionExecutable /app/site

WORKDIR /app

ENV MONGODB_URI=""

# Debug
RUN echo "=== Final stage .kobweb ===" && \
    ls -la /app/.kobweb/ 2>/dev/null || echo ".kobweb not found" && \
    echo "=== Final stage .kobweb/server ===" && \
    ls -la /app/.kobweb/server/ 2>/dev/null || echo "server not found"

# Run the Kobweb server JAR directly
ENTRYPOINT ["/bin/sh", "-c", "if [ -f /app/.kobweb/server/server.jar ]; then echo 'Starting Kobweb server...'; java -jar /app/.kobweb/server/server.jar; else echo 'server.jar not found!'; exit 1; fi"]