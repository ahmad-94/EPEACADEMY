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

# Diagnostic step
RUN echo "=== Project structure ===" && \
    ls -la /project/ && \
    echo "=== Site module structure ===" && \
    ls -la /project/site/ || echo "Site directory not found"

# Build the site - this MUST succeed
RUN ./gradlew :site:kobwebExport --no-daemon --stacktrace && \
    echo "=== Verifying export output ===" && \
    ls -la /project/${KOBWEB_APP_ROOT}/.kobweb/ && \
    ls -la /project/${KOBWEB_APP_ROOT}/.kobweb/server/ && \
    echo "=== Site content (should contain exported HTML/CSS/JS) ===" && \
    ls -la /project/${KOBWEB_APP_ROOT}/.kobweb/site/ || \
    (echo "ERROR: Site directory not found!" && exit 1)

FROM java as run

ARG KOBWEB_APP_ROOT

# Copy the .kobweb directory
COPY --from=export /project/${KOBWEB_APP_ROOT}/.kobweb .kobweb

RUN echo "=== Final stage verification ===" && \
    ls -la .kobweb/ && \
    ls -la .kobweb/server/ && \
    ls -la .kobweb/site/ || echo "Warning: site directory not found in final stage"

WORKDIR /app
COPY --from=export /project/${KOBWEB_APP_ROOT}/.kobweb /app/.kobweb

# Verify the JAR file exists before running
RUN test -f .kobweb/server/server.jar && echo "server.jar found!" || (echo "server.jar NOT found!" && exit 1)

ENTRYPOINT ["java", "-jar", ".kobweb/server/server.jar"]