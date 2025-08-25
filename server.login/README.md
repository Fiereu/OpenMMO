# Server.Login Module

The server.login module implements the login server for OpenMMO with authentication and database integration.

## Contents

### Main Application
- **LoginServer**: Main application entry point that configures and starts the login server on port 2106
- **LoginProtocolHandler**: Protocol handler for processing login-specific packets

### Configuration
- Server runs on port 2106
- Uses ECDSA game keys for packet signing/verification
- Configured with TLS checksum size of 16 bytes (partial HMAC-SHA256)

### Database Integration
- **PostgreSQL**: Primary database for user authentication
- **Flyway**: Database migration management
- **jOOQ**: Type-safe database queries with code generation

### Key Features
- User authentication & management
- Integration with protocol.login for packet handling
- TLS-secured communication
- Docker support with dedicated Dockerfile

### Configuration Files
- **application.conf**: HOCON configuration for server settings
- **logback.xml**: Logging configuration
- **db.migrations**: SQL scripts for schema management

### Docker Support
The module includes a Dockerfile for containerized deployment:
- Multi-stage build using Zulu OpenJDK 24
- Exposes port 2106
- Includes all necessary dependencies and keys

## Dependencies
- Server module for core framework
- Protocol.login module for authentication packets
- PostgreSQL driver for database connectivity
- jOOQ for database operations
- Flyway for schema management
- Logback for logging
- Kotlinx.coroutines for async processing

## Environment Variables
The application requires database configuration through environment variables:
- Database connection settings (host, port, name, user, password)
- Configured via gradle.properties or environment

## Development
- Run with: `./gradlew :server.login:run`
- Database setup: `./gradlew cleanMigrateAndGenerate`
- Code generation: `./gradlew jooqCodegen`
- Docker build: `docker build -t openmmo-login server.login/`
