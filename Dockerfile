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

# 👇 UPDATED to match your project version (0.23.3)
ENV KOBWEB_CLI_VERSION=0.23.3
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

# Fetch the Kobweb CLI matching your project version
RUN wget https://github.com/varabyte/kobweb-cli/releases/download/v${KOBWEB_CLI_VERSION}/kobweb-${KOBWEB_CLI_VERSION}.zip \
    && unzip kobweb-${KOBWEB_CLI_VERSION}.zip \
    && rm kobweb-${KOBWEB_CLI_VERSION}.zip

ENV PATH="/kobweb-${KOBWEB_CLI_VERSION}/bin:${PATH}"

WORKDIR /project/${KOBWEB_APP_ROOT}

# Decrease Gradle memory usage to avoid OOM situations in tight environments
# (many free Cloud tiers only give you 512M of RAM). The following amount
# should be more than enough to build and export our site.
RUN mkdir ~/.gradle && \
    echo "org.gradle.jvmargs=-Xmx512m" >> ~/.gradle/gradle.properties

# Set MongoDB URI for the build (use a dummy value if your cluster is offline)
# If your cluster is online, set this to your real connection string
ENV MONGODB_URI="mongodb://localhost:27017"

# Run the export
RUN kobweb export --notty

# Verify the export succeeded
RUN test -d .kobweb && \
    test -f .kobweb/server/server.jar && \
    echo "Export successful!" || \
    (echo "Export failed!" && exit 1)

#-----------------------------------------------------------------------------
# Create the final stage, which contains just enough bits to run the Kobweb
# server.
FROM java as run

ARG KOBWEB_APP_ROOT

# Copy the .kobweb directory from the export stage
COPY --from=export /project/${KOBWEB_APP_ROOT}/.kobweb .kobweb

# Debug: Verify the contents
RUN ls -la .kobweb/ && \
    ls -la .kobweb/server/ && \
    ls -la .kobweb/site/ 2>/dev/null || echo "site directory not found"

# Run the server
ENTRYPOINT ["/bin/sh", "-c", "if [ -f .kobweb/server/start.sh ]; then .kobweb/server/start.sh; else java -jar .kobweb/server/server.jar; fi"]