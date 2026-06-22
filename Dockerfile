#-----------------------------------------------------------------------------
# Variables shared across multiple stages (they need to be explicitly opted
# into each stage by being declaring there too, but their values need only be
# specified once).
ARG KOBWEB_APP_ROOT="site"

FROM eclipse-temurin:17 as java

FROM java as export

#-----------------------------------------------------------------------------
# Create an intermediate stage which builds and exports our site. In the
# final stage, we'll only extract what we need from this stage, saving a lot
# of space.

ENV KOBWEB_CLI_VERSION=0.9.13
ARG KOBWEB_APP_ROOT

ENV NODE_MAJOR=20

# Copy the project code to an arbitrary subdir so we can install stuff in the
# Docker container root without worrying about clobbering project files.
COPY . /project

# Update and install required OS packages to continue
# Note: Node install instructions from: https://github.com/nodesource/distributions#installation-instructions
# Note: Playwright is a system for running browsers, and here we use it to
# install Chromium.
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

# Fetch the latest version of the Kobweb CLI
RUN wget https://github.com/varabyte/kobweb-cli/releases/download/v${KOBWEB_CLI_VERSION}/kobweb-${KOBWEB_CLI_VERSION}.zip \
    && unzip kobweb-${KOBWEB_CLI_VERSION}.zip \
    && rm kobweb-${KOBWEB_CLI_VERSION}.zip

ENV PATH="/kobweb-${KOBWEB_CLI_VERSION}/bin:${PATH}"

WORKDIR /project/${KOBWEB_APP_ROOT}

# Decrease Gradle memory usage to avoid OOM situations in tight environments
# (many free Cloud tiers only give you 512M of RAM). The following amount
# should be more than enough to build and export our site.
RUN mkdir ~/.gradle && \
    echo "org.gradle.jvmargs=-Xmx256m" >> ~/.gradle/gradle.properties

# Run the export and verify the output
RUN kobweb export --notty && \
    echo "=== Verifying export output ===" && \
    ls -la .kobweb/ && \
    echo "=== Server directory contents ===" && \
    ls -la .kobweb/server/ || echo "Warning: server directory not found"

#-----------------------------------------------------------------------------
# Create the final stage, which contains just enough bits to run the Kobweb
# server.
FROM java as run

ARG KOBWEB_APP_ROOT

# Copy the exported site
COPY --from=export /project/${KOBWEB_APP_ROOT}/.kobweb .kobweb

# Debug: Verify the contents in the final stage
RUN echo "=== Final stage verification ===" && \
    ls -la .kobweb/ && \
    ls -la .kobweb/server/ || echo "Warning: server directory not found"

# Use a robust entrypoint that tries multiple methods to start the server
ENTRYPOINT ["/bin/sh", "-c", "if [ -f '.kobweb/server/start.sh' ]; then .kobweb/server/start.sh; elif [ -f '.kobweb/server/server.jar' ]; then java -jar .kobweb/server/server.jar; else echo 'Error: No server start script or JAR found'; ls -la .kobweb/; ls -la .kobweb/server/ || echo 'server directory missing'; exit 1; fi"]