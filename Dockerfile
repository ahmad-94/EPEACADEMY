#-----------------------------------------------------------------------------
# Variables shared across multiple stages
ARG KOBWEB_APP_ROOT="site"

FROM eclipse-temurin:17 as java

FROM java as export

ARG KOBWEB_APP_ROOT
ENV NODE_MAJOR=20

# Copy the project code
COPY . /project

# Update and install required OS packages
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

WORKDIR /project/${KOBWEB_APP_ROOT}

# Decrease Gradle memory usage
RUN mkdir ~/.gradle && \
    echo "org.gradle.jvmargs=-Xmx256m" >> ~/.gradle/gradle.properties

# Use Gradle directly (uses the plugin version from your project)
RUN chmod +x /project/gradlew && \
    /project/gradlew :site:kobwebExport --no-daemon && \
    echo "=== Verifying export output ===" && \
    ls -la .kobweb/ && \
    ls -la .kobweb/server/ && \
    echo "=== Site content (should contain exported HTML/CSS/JS) ===" && \
    ls -la .kobweb/site/ || echo "Warning: site directory not found"

#-----------------------------------------------------------------------------
# Create the final stage
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

ENTRYPOINT ["java", "-jar", ".kobweb/server/server.jar"]