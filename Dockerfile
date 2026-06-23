ARG KOBWEB_APP_ROOT="site"

FROM eclipse-temurin:17 as export

ARG KOBWEB_APP_ROOT
ENV NODE_MAJOR=20
ENV MONGODB_URI="mongodb://localhost:27017"

COPY . /project

RUN apt-get update \
    && apt-get install -y ca-certificates curl gnupg unzip wget \
    && mkdir -p /etc/apt/keyrings \
    && curl -fsSL https://deb.nodesource.com/gpgkey/nodesource-repo.gpg.key | gpg --dearmor -o /etc/apt/keyrings/nodesource.gpg \
    && echo "deb [signed-by=/etc/apt/keyrings/nodesource.gpg] https://deb.nodesource.com/node_$NODE_MAJOR.x nodistro main" | tee /etc/apt/sources.list.d/nodesource.list \
    && apt-get update \
    && apt-get install -y nodejs \
    && apt-get install -y npm \
    && npm install -g npm@latest

WORKDIR /project
RUN chmod +x gradlew

# First, check what tasks are available
RUN ./gradlew tasks --no-daemon | grep -i kobweb

# Try to build the JS first
RUN ./gradlew :site:compileKotlinJs --no-daemon --stacktrace

# Then try the export
RUN ./gradlew :site:kobwebExport --no-daemon --stacktrace || \
    (echo "=== Export failed, checking what was built ===" && \
     ls -la /project/site/build/ || echo "No build directory" && \
     ls -la /project/site/.kobweb/ || echo "No .kobweb directory")

FROM eclipse-temurin:17 as run

ARG KOBWEB_APP_ROOT

COPY --from=export /project/${KOBWEB_APP_ROOT}/.kobweb .kobweb || echo "No .kobweb to copy"

WORKDIR /app
COPY --from=export /project/${KOBWEB_APP_ROOT}/.kobweb /app/.kobweb || echo "No .kobweb to copy"

ENTRYPOINT ["java", "-jar", ".kobweb/server/server.jar"]