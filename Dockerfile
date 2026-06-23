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

RUN ./gradlew :site:build --no-daemon --stacktrace

FROM java as run

ARG KOBWEB_APP_ROOT

RUN apt-get update && apt-get install -y python3 -y

# Copy the production executable
COPY --from=export /project/${KOBWEB_APP_ROOT}/build/dist/js/productionExecutable /app/site

WORKDIR /app

# Flatten the directory structure - move all files to root
RUN echo "=== Before flattening ===" && \
    ls -la /app/site/ && \
    echo "=== Flattening directories ===" && \
    if [ -d /app/site/kobweb ]; then \
        echo "Moving kobweb contents to root"; \
        cp -r /app/site/kobweb/* /app/site/ && \
        rm -rf /app/site/kobweb; \
    fi && \
    if [ -d /app/site/public ]; then \
        echo "Moving public contents to root"; \
        cp -r /app/site/public/* /app/site/ && \
        rm -rf /app/site/public; \
    fi && \
    echo "=== After flattening ===" && \
    ls -la /app/site/

ENV MONGODB_URI=""

# Serve from the flattened directory
EXPOSE 8080
ENTRYPOINT ["/bin/sh", "-c", "if [ -f /app/site/index.html ]; then python3 -m http.server 8080 --directory /app/site; else echo 'No index.html found!'; find /app/site -name '*.html'; exit 1; fi"]