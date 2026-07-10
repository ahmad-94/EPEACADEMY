# STAGE 1: Build
FROM eclipse-temurin:17-jdk AS build

# Install necessary tools
RUN apt-get update && apt-get install -y unzip

WORKDIR /app
COPY . .

# Run the Kobweb build and export
# This generates the static pages for SEO and the server jar
RUN ./gradlew site:kobwebExport site:kobwebAssemble

# STAGE 2: Runtime
FROM eclipse-temurin:17-jre

WORKDIR /app

# Copy the exported site and server from the build stage
COPY --from=build /app/site/.kobweb /app/.kobweb

ENV JAVA_TOOL_OPTIONS="-Xmx512m"
EXPOSE 8080

# Ensure MONGODB_URI is provided
ENTRYPOINT ["/bin/sh", "-c", "if [ -z \"$MONGODB_URI\" ]; then echo 'ERROR: MONGODB_URI is not set. Add it in Render -> Environment -> MONGODB_URI'; exit 1; fi; exec java -Dkobweb.server.environment=PROD -Dkobweb.site.layout=FULLSTACK -Dio.ktor.development=false -jar .kobweb/server/server.jar"]
