#-----------------------------------------------------------------------------
# Variables shared across multiple stages
ARG KOBWEB_APP_ROOT="site"

# Stage 1: Build the site
FROM eclipse-temurin:17 as builder

ARG KOBWEB_APP_ROOT
ENV NODE_MAJOR=20

# Copy project
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

# Install Kobweb CLI
ENV KOBWEB_CLI_VERSION=0.9.21
RUN wget https://github.com/varabyte/kobweb-cli/releases/download/v${KOBWEB_CLI_VERSION}/kobweb-${KOBWEB_CLI_VERSION}.zip \
    && unzip kobweb-${KOBWEB_CLI_VERSION}.zip \
    && rm kobweb-${KOBWEB_CLI_VERSION}.zip
ENV PATH="/kobweb-${KOBWEB_CLI_VERSION}/bin:${PATH}"

WORKDIR /project

# Make gradlew executable
RUN chmod +x gradlew

# Create gradle properties
RUN mkdir ~/.gradle && \
    echo "org.gradle.jvmargs=-Xmx512m" >> ~/.gradle/gradle.properties

# Set dummy MongoDB URI for build (will be replaced at runtime)
ENV MONGODB_URI="mongodb://localhost:27017"

# Build and export the site
RUN ./gradlew :site:build --no-daemon --stacktrace && \
    ./gradlew :site:kobwebExport --no-daemon --stacktrace

#-----------------------------------------------------------------------------
# Stage 2: Runtime
FROM eclipse-temurin:17

# Copy the .kobweb directory from the builder stage
COPY --from=builder /project/${KOBWEB_APP_ROOT}/.kobweb /app/.kobweb

WORKDIR /app

# MongoDB URI will be set at runtime
ENV MONGODB_URI=""

EXPOSE 8080

# Run the server
ENTRYPOINT ["java", "-jar", ".kobweb/server/server.jar"]