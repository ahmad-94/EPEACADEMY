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

FROM java as run

ARG KOBWEB_APP_ROOT

# Install Python for HTTP server
RUN apt-get update && apt-get install -y python3 -y

# Copy the built static files
COPY --from=export /project/${KOBWEB_APP_ROOT}/build/dist/js/productionExecutable /app/site

WORKDIR /app

ENV MONGODB_URI=""

# Debug: Check what was copied
RUN echo "=== Files in /app/site ===" && \
    ls -la /app/site/ && \
    echo "=== Looking for HTML files ===" && \
    find /app/site -name "*.html" 2>/dev/null || echo "No HTML files found"

# Serve the static files
EXPOSE 8080
ENTRYPOINT ["/bin/sh", "-c", "if [ -f /app/site/index.html ]; then python3 -m http.server 8080 --directory /app/site; elif [ -f /app/site/kobweb/index.html ]; then python3 -m http.server 8080 --directory /app/site/kobweb; elif [ -f /app/site/public/index.html ]; then python3 -m http.server 8080 --directory /app/site/public; else echo 'No HTML found!'; find /app/site -name '*.html'; exit 1; fi"]