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

# Find the HTML file
RUN echo "=== Searching for HTML files ===" && \
    find /project/${KOBWEB_APP_ROOT}/build -name "*.html" 2>/dev/null && \
    echo "=== Searching for index files ===" && \
    find /project/${KOBWEB_APP_ROOT}/build -name "index.*" 2>/dev/null

# Also try the kobwebExport
RUN ./gradlew :site:kobwebExport --no-daemon --stacktrace || echo "Export failed, but build succeeded"

FROM java as run

ARG KOBWEB_APP_ROOT

# Copy the entire build output
COPY --from=export /project/${KOBWEB_APP_ROOT}/build /app/build

# Also copy the production executable directly
COPY --from=export /project/${KOBWEB_APP_ROOT}/build/dist/js/productionExecutable /app/site

# Copy .kobweb if it exists
RUN if [ -d "/project/${KOBWEB_APP_ROOT}/.kobweb" ]; then \
        mkdir -p /app && \
        cp -r /project/${KOBWEB_APP_ROOT}/.kobweb /app/.kobweb; \
    fi

# Debug: List all HTML files
RUN echo "=== Finding HTML files in /app ===" && \
    find /app -name "*.html" 2>/dev/null && \
    echo "=== Listing /app/site/kobweb ===" && \
    ls -la /app/site/kobweb/ 2>/dev/null || echo "kobweb directory not found" && \
    echo "=== Listing /app/site/public ===" && \
    ls -la /app/site/public/ 2>/dev/null || echo "public directory not found"

WORKDIR /app/site

# Try multiple possible entry points
ENTRYPOINT ["/bin/sh", "-c", "if [ -f /app/site/index.html ]; then python3 -m http.server 8080 --directory /app/site; elif [ -f /app/site/kobweb/index.html ]; then python3 -m http.server 8080 --directory /app/site/kobweb; elif [ -f /app/site/public/index.html ]; then python3 -m http.server 8080 --directory /app/site/public; elif [ -f /app/.kobweb/server/start.sh ]; then /app/.kobweb/server/start.sh; else echo 'No files found!'; find /app -name '*.html' 2>/dev/null || echo 'No HTML files found at all'; exit 1; fi"]