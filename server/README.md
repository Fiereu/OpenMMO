# Server Module

The server module provides the core server framework for OpenMMO using Netty for networking and protocol handling.

## Contents

### Core Classes
- **Server**: Main server class with lifecycle management (start/stop)
- **ServerBuilder**: Builder pattern for server configuration and setup
- **NettyServer**: Netty-based server implementation for async I/O

### Configuration
- **ServerConfig**: Main server configuration settings
- **TlsConfig**: TLS-specific configuration for secure communication
- **ConfigLoader**: Configuration loading utilities

### Networking Components
- **ChannelHandlerProvider**: Interface for providing Netty channel handlers
- **DefaultChannelHandlerProvider**: Default implementation of channel handler provider

### Netty Handlers
- **ProtocolHandler**: Main protocol routing and packet handling
- **PacketFrameDecoder/Encoder**: Packet framing with length prefixing
- **ChecksumFrameDecoder/Encoder**: Packet integrity validation (CRC16 and HMAC-SHA256)
- **TlsDecryptionHandler/TlsEncryptionHandler**: TLS-like encryption/decryption

### Protocol System
- **TlsContext**: TLS session context management
- **TlsProtocolHandler**: TLS protocol implementation
- **PacketEvent**: Event system for packet processing

### Cryptography
- **KeyLoader**: Utility for loading ECDSA keys from PEM files
- **Checksum**: Abstraction for packet integrity validation
  - **Crc16Checksum**: CRC16 implementation
  - **HmacSha256Checksum**: HMAC-SHA256 implementation

### Dependency Injection
- **ServerComponent**: Dagger component for dependency injection
- **ServerModule**: Dagger module providing server dependencies

## Features
- Asynchronous networking using Netty
- Custom TLS-like protocol for secure communication
- Packet framing and integrity validation
- Protocol switching (TLS handshake → application protocols)
- ECDSA key management for packet signing/verification
- Configurable coroutine scopes for async operations

## Dependencies
- Netty for networking
- Dagger 2 for dependency injection
- BouncyCastle for cryptography
- Kotlinx.coroutines for async operations
- Kotlinx.serialization for configuration
- TypeSafe Config for configuration management
- Protocol and Protocol.TLS modules

## Testing
- Comprehensive unit tests using Kotest
- Integration tests for Netty pipeline components
- TLS handler tests with helper utilities
- MockK for mocking dependencies