# Server.Game Module

The server.game module implements the game server for OpenMMO.

## Development
- Run with: `./gradlew :server.game:run`
- Database setup: `./gradlew cleanMigrateAndGenerate`
- Code generation: `./gradlew jooqCodegen`
- Docker build: `docker build -t openmmo-game server.game/`
