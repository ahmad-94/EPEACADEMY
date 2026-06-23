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
    find /project/${KOBWEB_APP_ROOT}/build -name "*.html" 2>/dev/null

FROM java as run

ARG KOBWEB_APP_ROOT

# Copy the entire build output
COPY --from=export /project/${KOBWEB_APP_ROOT}/build /app/build

# Also copy the production executable directly
COPY --from=export /project/${KOBWEB_APP_ROOT}/build/dist/js/productionExecutable /app/site

# Debug: Find the HTML file
RUN echo "=== Finding HTML files in /app ===" && \
    find /app -name "*.html" 2>/dev/null && \
    echo "=== Checking kobweb directory ===" && \
    ls -la /app/site/kobweb/ 2>/dev/null || echo "kobweb directory not found" && \
    echo "=== Checking public directory ===" && \
    ls -la /app/site/public/ 2>/dev/null || echo "public directory not found"

WORKDIR /app

# Install Python for HTTP server
RUN apt-get update && apt-get install -y python3

# Serve from the directory that actually contains the HTML
ENTRYPOINT ["/bin/sh", "-c", "if [ -f /app/site/kobweb/index.html ]; then echo 'Serving from /app/site/kobweb'; python3 -m http.server 8080 --directory /app/site/kobweb; elif [ -f /app/site/public/index.html ]; then echo 'Serving from /app/site/public'; python3 -m http.server 8080 --directory /app/site/public; elif [ -f /app/site/index.html ]; then echo 'Serving from /app/site'; python3 -m http.server 8080 --directory /app/site; else echo 'No HTML found!'; find /app -name '*.html' 2>/dev/null || echo 'No HTML files found at all'; exit 1; fi"]