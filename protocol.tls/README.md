# Protocol.TLS Module

The protocol.tls module implements a custom TLS-like protocol for secure client-server communication in OpenMMO.

## Contents

### Core Classes
- **TlsProtocol**: Abstract base protocol for TLS handshake implementation
- **TlsServerProtocol**: Server-side TLS protocol implementation
- **TlsClientProtocol**: Client-side TLS protocol implementation

### Packet Types
- **ClientHelloPacket**: Initial handshake packet sent by client
- **ServerHelloPacket**: Server response containing server's public key
- **ClientReadyPacket**: Client confirmation with client's public key

### Cryptographic Features
- **ECKeyExtensions**: Extensions for elliptic curve key operations
- ECDH key exchange for shared secret derivation
- Secure communication setup using ECDSA key pairs

## Protocol Flow
1. Client sends `ClientHello` to initiate handshake
2. Server responds with `ServerHello` containing its public key
3. Client derives shared secret using ECDH and switches to secure mode
4. Client sends `ClientReady` with its public key
5. Server derives shared secret and switches to next protocol (e.g., LoginProtocol)

## Dependencies
- Protocol module for base framework
- BouncyCastle crypto libraries for cryptographic operations
- Kotest for testing

## Security
This module implements a custom TLS-like protocol specifically designed for PokeMMO server compatibility, using ECDSA with secp256r1 curve for key exchange and authentication.
