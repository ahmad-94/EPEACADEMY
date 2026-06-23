ARG KOBWEB_APP_ROOT="site"

FROM eclipse-temurin:17 as java

FROM java as export

ARG KOBWEB_APP_ROOT
ENV NODE_MAJOR=20

# Use the correct CLI version
ENV KOBWEB_CLI_VERSION=0.9.21

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
    && node --version && npm --version \
    && npm init -y \
    && npx playwright install --with-deps chromium

WORKDIR /project

# Make gradlew executable
RUN chmod +x gradlew

RUN mkdir ~/.gradle && \
    echo "org.gradle.jvmargs=-Xmx512m" >> ~/.gradle/gradle.properties

ENV MONGODB_URI="mongodb://localhost:27017"

# Use Gradle instead of CLI - this ensures compatibility
RUN ./gradlew :site:kobwebExport --no-daemon --stacktrace

# Verify the export
RUN test -d /project/${KOBWEB_APP_ROOT}/.kobweb && \
    test -f /project/${KOBWEB_APP_ROOT}/.kobweb/server/server.jar && \
    echo "Export successful!" || \
    (echo "Export failed!" && exit 1)

# Check site content
RUN ls -la /project/${KOBWEB_APP_ROOT}/.kobweb/site/ && \
    find /project/${KOBWEB_APP_ROOT}/.kobweb/site -type f | head -10

FROM java as run

ARG KOBWEB_APP_ROOT

COPY --from=export /project/${KOBWEB_APP_ROOT}/.kobweb .kobweb

WORKDIR /app
COPY --from=export /project/${KOBWEB_APP_ROOT}/.kobweb /app/.kobweb

# Try the start script first, fallback to JAR
ENTRYPOINT ["/bin/sh", "-c", "if [ -f .kobweb/server/start.sh ]; then .kobweb/server/start.sh; else java -jar .kobweb/server/server.jar; fi"]