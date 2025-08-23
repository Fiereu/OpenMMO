# Multi-stage build for OpenMMO Login Server
FROM azul/zulu-openjdk:24-latest AS builder

WORKDIR /app

# Copy gradle files first for better caching
COPY ./gradle       gradle/
COPY ./gradlew gradlew.bat gradle.properties settings.gradle.kts gradle.env ./
COPY ./buildSrc     buildSrc/

# remove all includ("...") from settings.gradle.kts
RUN sed -i '/include(/d' settings.gradle.kts
# manually include only the required modules
RUN echo 'include("common")'          >> settings.gradle.kts
RUN echo 'include("keys")'            >> settings.gradle.kts
RUN echo 'include("protocol")'        >> settings.gradle.kts
RUN echo 'include("protocol.tls")'    >> settings.gradle.kts
RUN echo 'include("protocol.login")'  >> settings.gradle.kts
RUN echo 'include("server")'          >> settings.gradle.kts
RUN echo 'include("server.login")'    >> settings.gradle.kts

# Copy all module build files
COPY ./build.gradle.kts                 ./
COPY ./common/build.gradle.kts          common/
COPY ./keys/build.gradle.kts            keys/
COPY ./protocol/build.gradle.kts        protocol/
COPY ./protocol.tls/build.gradle.kts    protocol.tls/
COPY ./protocol.login/build.gradle.kts  protocol.login/
COPY ./server/build.gradle.kts          server/
COPY ./server.login/build.gradle.kts    server.login/

# Download dependencies
RUN ./gradlew dependencies --no-daemon

# Copy source code
COPY ./common/src           common/src/
COPY ./protocol/src         protocol/src/
COPY ./protocol.tls/src     protocol.tls/src/
COPY ./protocol.login/src   protocol.login/src/
COPY ./server/src           server/src/
COPY ./server.login/src     server.login/src/

# Build the application
RUN ./gradlew :server.login:distTar --no-daemon

# Extract the distribution
RUN cd server.login/build/distributions && \
    tar -xf server.login-*.tar && \
    mv server.login-*/ server.login/

# Runtime stage
FROM azul/zulu-openjdk:24-jre-latest

WORKDIR /app

# Copy the extracted distribution from builder
COPY --from=builder /app/server.login/build/distributions/server.login/ .

EXPOSE 2106

ENTRYPOINT ["./bin/server.login"]