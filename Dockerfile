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

# First, build the site to generate static files
RUN ./gradlew :site:build --no-daemon --stacktrace

# Then, run the export to create the server JAR
RUN ./gradlew :site:kobwebExport --no-daemon --stacktrace

# Debug: Check what was actually generated
RUN echo "=== Checking .kobweb contents ===" && \
    ls -la /project/${KOBWEB_APP_ROOT}/.kobweb/ && \
    echo "=== Checking site folder ===" && \
    ls -la /project/${KOBWEB_APP_ROOT}/.kobweb/site/ 2>/dev/null || echo "site folder is empty or missing!" && \
    echo "=== Checking build output ===" && \
    ls -la /project/${KOBWEB_APP_ROOT}/build/dist/js/productionExecutable/ 2>/dev/null || echo "productionExecutable not found"

# Verify the server JAR exists
RUN test -f /project/${KOBWEB_APP_ROOT}/.kobweb/server/server.jar && \
    echo "server.jar found!" || \
    (echo "server.jar NOT found!" && exit 1)

FROM java as run

ARG KOBWEB_APP_ROOT

# Copy the entire .kobweb directory
COPY --from=export /project/${KOBWEB_APP_ROOT}/.kobweb /app/.kobweb

# Also copy the static files as a fallback
COPY --from=export /project/${KOBWEB_APP_ROOT}/build/dist/js/productionExecutable /app/site

WORKDIR /app

# Debug: Check what we have
RUN echo "=== Final stage contents ===" && \
    ls -la /app/.kobweb/ && \
    echo "=== Site folder ===" && \
    ls -la /app/.kobweb/site/ 2>/dev/null || echo "site folder is empty!" && \
    echo "=== Static files ===" && \
    ls -la /app/site/ 2>/dev/null || echo "static files not found"

# Set MongoDB URI for runtime
ENV MONGODB_URI=""

# Try multiple ways to start the server
ENTRYPOINT ["/bin/sh", "-c", "if [ -f .kobweb/server/start.sh ]; then .kobweb/server/start.sh; elif [ -f /app/site/index.html ]; then python3 -m http.server 8080 --directory /app/site; else java -jar .kobweb/server/server.jar; fi"]