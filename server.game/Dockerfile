# Multi-stage build for OpenMMO Game Server
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
RUN echo 'include("protocol.game")'   >> settings.gradle.kts
RUN echo 'include("server")'          >> settings.gradle.kts
RUN echo 'include("server.game")'     >> settings.gradle.kts

# Copy all module build files
COPY ./build.gradle.kts                 ./
COPY ./common/build.gradle.kts          common/
COPY ./keys/build.gradle.kts            keys/
COPY ./protocol/build.gradle.kts        protocol/
COPY ./protocol.tls/build.gradle.kts    protocol.tls/
COPY ./protocol.game/build.gradle.kts   protocol.game/
COPY ./server/build.gradle.kts          server/
COPY ./server.game/build.gradle.kts     server.game/

# Download dependencies
RUN ./gradlew dependencies --no-daemon

# Copy source code
COPY ./common/src           common/src/
COPY ./protocol/src         protocol/src/
COPY ./protocol.tls/src     protocol.tls/src/
COPY ./protocol.game/src    protocol.game/src/
COPY ./server/src           server/src/
COPY ./server.game/src      server.game/src/

# Build the application
RUN ./gradlew :server.game:distTar --no-daemon

# Extract the distribution
RUN cd server.game/build/distributions && \
    tar -xf server.game-*.tar && \
    mv server.game-*/ server.game/

# Runtime stage
FROM azul/zulu-openjdk:24-jre-latest

WORKDIR /app

# Copy the extracted distribution from builder
COPY --from=builder /app/server.game/build/distributions/server.game/ .

EXPOSE 2106

ENTRYPOINT ["./bin/server.game"]