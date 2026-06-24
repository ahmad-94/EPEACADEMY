FROM eclipse-temurin:17-jre

# Copy the pre-exported Kobweb server output (run `kobweb export` in site/ before building)
COPY site/.kobweb /app/.kobweb

WORKDIR /app

ENV JAVA_TOOL_OPTIONS="-Xmx512m"

EXPOSE 8080

# Render injects env vars at runtime, not at image build time.
# MONGODB_URI must be set in Render -> Environment.
# Set PORT=8080 in Render to match conf.yaml, or Render's default (10000) won't reach the app.
ENTRYPOINT ["/bin/sh", "-c", "if [ -z \"$MONGODB_URI\" ]; then echo 'ERROR: MONGODB_URI is not set. Add it in Render -> Environment -> MONGODB_URI'; exit 1; fi; exec java -Dkobweb.server.environment=PROD -Dkobweb.site.layout=FULLSTACK -Dio.ktor.development=false -jar .kobweb/server/server.jar"]
