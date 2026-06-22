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

# Decrease Gradle memory usage
RUN mkdir ~/.gradle && \
    echo "org.gradle.jvmargs=-Xmx256m" >> ~/.gradle/gradle.properties

# Run the export - this creates the .kobweb folder with server.jar
RUN kobweb export --notty && \
    echo "=== Verifying export output ===" && \
    ls -la .kobweb/ && \
    ls -la .kobweb/server/ && \
    echo "=== Site content (should contain exported HTML/CSS/JS) ===" && \
    ls -la .kobweb/site/ || echo "Warning: site directory not found"

#-----------------------------------------------------------------------------
# Create the final stage, which contains just enough bits to run the Kobweb
# server.
FROM java as run

ARG KOBWEB_APP_ROOT

# Copy the ENTIRE .kobweb directory from the export stage
COPY --from=export /project/${KOBWEB_APP_ROOT}/.kobweb .kobweb

# Debug: Verify the contents in the final stage
RUN echo "=== Final stage verification ===" && \
    ls -la .kobweb/ && \
    ls -la .kobweb/server/ && \
    ls -la .kobweb/site/ || echo "Warning: site directory not found in final stage"

# Set working directory to where the .kobweb folder is
WORKDIR /app

# Copy the .kobweb folder to the working directory
COPY --from=export /project/${KOBWEB_APP_ROOT}/.kobweb /app/.kobweb

# Run the server JAR directly with the correct classpath
ENTRYPOINT ["java", "-jar", ".kobweb/server/server.jar"]