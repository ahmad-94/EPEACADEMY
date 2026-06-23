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

# Set dummy MongoDB URI for build (will be replaced at runtime)
ENV MONGODB_URI="mongodb://localhost:27017"

# Build the site
RUN ./gradlew :site:build --no-daemon --stacktrace

# Try to export - this creates the server JAR
RUN ./gradlew :site:kobwebExport --no-daemon --stacktrace || echo "Export had issues, but continuing"

# Debug: Check what was generated
RUN echo "=== Checking .kobweb ===" && \
    ls -la /project/${KOBWEB_APP_ROOT}/.kobweb/ && \
    echo "=== Checking .kobweb/server ===" && \
    ls -la /project/${KOBWEB_APP_ROOT}/.kobweb/server/ && \
    echo "=== Checking .kobweb/site ===" && \
    ls -la /project/${KOBWEB_APP_ROOT}/.kobweb/site/ 2>/dev/null || echo "site folder is empty!"

# Copy static files to .kobweb/site if it's empty
RUN if [ ! "$(ls -A /project/${KOBWEB_APP_ROOT}/.kobweb/site 2>/dev/null)" ]; then \
        echo "Site folder is empty, copying static files..."; \
        mkdir -p /project/${KOBWEB_APP_ROOT}/.kobweb/site; \
        cp -r /project/${KOBWEB_APP_ROOT}/build/dist/js/productionExecutable/* /project/${KOBWEB_APP_ROOT}/.kobweb/site/; \
        echo "Static files copied to .kobweb/site"; \
    fi

FROM java as run

ARG KOBWEB_APP_ROOT

# Copy the entire .kobweb directory
COPY --from=export /project/${KOBWEB_APP_ROOT}/.kobweb /app/.kobweb

WORKDIR /app

ENV MONGODB_URI=""

# Debug: Check what we have
RUN echo "=== Final stage .kobweb ===" && \
    ls -la /app/.kobweb/ && \
    echo "=== Final stage .kobweb/server ===" && \
    ls -la /app/.kobweb/server/ && \
    echo "=== Final stage .kobweb/site ===" && \
    ls -la /app/.kobweb/site/ 2>/dev/null || echo "site folder not found"

# Run the server JAR
ENTRYPOINT ["java", "-jar", ".kobweb/server/server.jar"]