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

WORKDIR /project

RUN chmod +x gradlew

RUN mkdir ~/.gradle && \
    echo "org.gradle.jvmargs=-Xmx512m" >> ~/.gradle/gradle.properties

ENV MONGODB_URI="mongodb://localhost:27017"

# Build the site
RUN ./gradlew :site:build --no-daemon --stacktrace

# Also run kobwebExport to generate the server JAR
RUN ./gradlew :site:kobwebExport --no-daemon --stacktrace || echo "Export had issues, but continuing"

# Verify the server JAR exists
RUN test -f /project/${KOBWEB_APP_ROOT}/.kobweb/server/server.jar && \
    echo "server.jar found!" || \
    (echo "server.jar NOT found!" && exit 1)

FROM java as run

ARG KOBWEB_APP_ROOT

# Copy the .kobweb directory with the server JAR
COPY --from=export /project/${KOBWEB_APP_ROOT}/.kobweb /app/.kobweb

# Also copy the static files (in case they're needed)
COPY --from=export /project/${KOBWEB_APP_ROOT}/build/dist/js/productionExecutable /app/site 2>/dev/null || true

WORKDIR /app

# Debug: Verify the server JAR exists
RUN test -f /app/.kobweb/server/server.jar && \
    echo "server.jar found in final stage!" || \
    (echo "server.jar NOT found in final stage!" && exit 1)

# Set MongoDB URI for runtime (will be overridden by Render env var)
ENV MONGODB_URI=""

# Use the Kobweb server JAR
ENTRYPOINT ["java", "-jar", ".kobweb/server/server.jar"]