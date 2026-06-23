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

# Fetch the Kobweb CLI
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

# Try to export, but if it fails, we'll fall back to running the server directly
RUN ./gradlew :site:kobwebExport --no-daemon --stacktrace || echo "Export failed, will run server directly"

FROM java as run

ARG KOBWEB_APP_ROOT

# Copy the Kobweb CLI and the project
COPY --from=export /kobweb-${KOBWEB_CLI_VERSION} /kobweb
COPY --from=export /project /project

ENV PATH="/kobweb/bin:${PATH}"
ENV MONGODB_URI=""

WORKDIR /project/${KOBWEB_APP_ROOT}

# Run the server directly (like you do locally)
ENTRYPOINT ["kobweb", "run", "--env", "prod"]