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

# Decrease Gradle memory usage
RUN mkdir ~/.gradle && \
    echo "org.gradle.jvmargs=-Xmx512m" >> ~/.gradle/gradle.properties

# Diagnostic: Show project structure
RUN echo "=== Project structure ===" && \
    ls -la /project/ && \
    echo "=== Site module structure ===" && \
    ls -la /project/site/ || echo "Site directory not found"

# Run the export and capture the full output
RUN ./gradlew :site:kobwebExport --no-daemon --stacktrace --info 2>&1 | tee /build.log || true

# Show the last 200 lines of the build log (where the error usually is)
RUN echo "=== Last 200 lines of build log ===" && \
    tail -n 200 /build.log

# Check if .kobweb was created at all
RUN echo "=== Checking .kobweb directory ===" && \
    ls -la /project/${KOBWEB_APP_ROOT}/.kobweb/ || echo ".kobweb not found"

# If .kobweb exists, check its contents
RUN if [ -d "/project/${KOBWEB_APP_ROOT}/.kobweb" ]; then \
        echo "=== .kobweb/server contents ===" && \
        ls -la /project/${KOBWEB_APP_ROOT}/.kobweb/server/ || echo "server not found"; \
        echo "=== .kobweb/site contents (if exists) ===" && \
        ls -la /project/${KOBWEB_APP_ROOT}/.kobweb/site/ || echo "site not found"; \
    fi

FROM java as run

ARG KOBWEB_APP_ROOT

COPY --from=export /project/${KOBWEB_APP_ROOT}/.kobweb .kobweb || echo "No .kobweb to copy"

WORKDIR /app
COPY --from=export /project/${KOBWEB_APP_ROOT}/.kobweb /app/.kobweb || echo "No .kobweb to copy"

RUN if [ -f .kobweb/server/server.jar ]; then \
        echo "server.jar found!"; \
    else \
        echo "server.jar NOT found!"; \
        ls -la .kobweb/server/ || echo "server directory missing"; \
        exit 1; \
    fi

ENTRYPOINT ["java", "-jar", ".kobweb/server/server.jar"]