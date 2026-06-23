#-----------------------------------------------------------------------------
# Variables shared across multiple stages
ARG KOBWEB_APP_ROOT="site"

FROM eclipse-temurin:17 as java

FROM java as export

ARG KOBWEB_APP_ROOT
ENV NODE_MAJOR=20

# Use the CLI version that works with your project
ENV KOBWEB_CLI_VERSION=0.9.21

COPY . /project

# Install base packages and Node.js
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

# Download Kobweb CLI
RUN wget https://github.com/varabyte/kobweb-cli/releases/download/v${KOBWEB_CLI_VERSION}/kobweb-${KOBWEB_CLI_VERSION}.zip \
    && unzip kobweb-${KOBWEB_CLI_VERSION}.zip \
    && rm kobweb-${KOBWEB_CLI_VERSION}.zip

ENV PATH="/kobweb-${KOBWEB_CLI_VERSION}/bin:${PATH}"

# Install Playwright (minimal - just the browser)
RUN npx playwright install chromium --with-deps

WORKDIR /project/${KOBWEB_APP_ROOT}

RUN mkdir ~/.gradle && \
    echo "org.gradle.jvmargs=-Xmx512m" >> ~/.gradle/gradle.properties

# Set MongoDB URI
ENV MONGODB_URI="mongodb://localhost:27017"

# Export with STATIC layout (no browser required)
RUN kobweb export --notty --layout static

# Verify the export
RUN test -d .kobweb && \
    test -f .kobweb/server/server.jar && \
    echo "Export successful!" || \
    (echo "Export failed!" && exit 1)

# Check site content
RUN ls -la .kobweb/site/ && \
    find .kobweb/site -type f | head -10

#-----------------------------------------------------------------------------
# Create the final stage
FROM java as run

ARG KOBWEB_APP_ROOT

COPY --from=export /project/${KOBWEB_APP_ROOT}/.kobweb .kobweb

WORKDIR /app
COPY --from=export /project/${KOBWEB_APP_ROOT}/.kobweb /app/.kobweb

# Run the server
ENTRYPOINT ["java", "-jar", ".kobweb/server/server.jar"]