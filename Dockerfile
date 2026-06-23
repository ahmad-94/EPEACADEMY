ARG KOBWEB_APP_ROOT="site"

FROM eclipse-temurin:17 as java

FROM java as export

ARG KOBWEB_APP_ROOT
ENV NODE_MAJOR=20

# No need to set MONGODB_URI here - it will be read from the environment
# Or you can set it here if you want to hardcode it (not recommended for security)

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

WORKDIR /project
RUN chmod +x gradlew

RUN mkdir ~/.gradle && \
    echo "org.gradle.jvmargs=-Xmx512m" >> ~/.gradle/gradle.properties

# Build the site - this will connect to your remote MongoDB
# Make sure your cluster is ONLINE before building
RUN ./gradlew :site:kobwebExport --no-daemon --stacktrace

# Verify the export
RUN test -d /project/${KOBWEB_APP_ROOT}/.kobweb && \
    test -f /project/${KOBWEB_APP_ROOT}/.kobweb/server/server.jar && \
    echo "Export successful!" || \
    (echo "Export failed!" && exit 1)

FROM java as run

ARG KOBWEB_APP_ROOT

# At runtime, the MONGODB_URI must be set as an environment variable
# This will be passed from Render's environment variables

COPY --from=export /project/${KOBWEB_APP_ROOT}/.kobweb .kobweb

WORKDIR /app
COPY --from=export /project/${KOBWEB_APP_ROOT}/.kobweb /app/.kobweb

ENTRYPOINT ["java", "-jar", ".kobweb/server/server.jar"]