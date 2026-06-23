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

# Create gradle properties
RUN mkdir ~/.gradle && \
    echo "org.gradle.jvmargs=-Xmx512m" >> ~/.gradle/gradle.properties

# Install Playwright in the project directory
RUN npm init -y \
    && npx playwright install --with-deps chromium \
    && npx playwright install-deps

# Set MongoDB URI
ENV MONGODB_URI="mongodb://localhost:27017"

# Run the export using Gradle
RUN ./gradlew :site:kobwebExport --no-daemon --stacktrace -Pkobweb.headless=true

# Verify
RUN test -d /project/${KOBWEB_APP_ROOT}/.kobweb && \
    test -f /project/${KOBWEB_APP_ROOT}/.kobweb/server/server.jar && \
    echo "Export successful!" || \
    (echo "Export failed!" && exit 1)

FROM java as run

ARG KOBWEB_APP_ROOT

COPY --from=export /project/${KOBWEB_APP_ROOT}/.kobweb .kobweb

WORKDIR /app
COPY --from=export /project/${KOBWEB_APP_ROOT}/.kobweb /app/.kobweb

ENTRYPOINT ["java", "-jar", ".kobweb/server/server.jar"]