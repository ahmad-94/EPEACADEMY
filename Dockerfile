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

ENV MONGODB_URI=""

# Build the site
RUN ./gradlew :site:build --no-daemon --stacktrace

# Run the export to create the server JAR
RUN ./gradlew :site:kobwebExport --no-daemon --stacktrace || echo "Export had issues, but continuing"

FROM java as run

ARG KOBWEB_APP_ROOT

# Copy the entire site folder (which contains .kobweb)
COPY --from=export /project/${KOBWEB_APP_ROOT} /app/site

WORKDIR /app/site

ENV MONGODB_URI=""

# Debug
RUN echo "=== Checking .kobweb/server ===" && \
    ls -la .kobweb/server/ && \
    echo "=== Checking .kobweb/site ===" && \
    ls -la .kobweb/site/ 2>/dev/null || echo "site subfolder not found"

# Run the server JAR directly (no terminal needed)
ENTRYPOINT ["java", "-jar", ".kobweb/server/server.jar"]