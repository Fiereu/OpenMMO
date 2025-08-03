# OpenMMO

[![](https://dcbadge.limes.pink/api/server/SQwGXyY2gz)](https://discord.gg/SQwGXyY2gz)

## ToC
- [Description](#description)
- [Modules](#modules)
- [Protocol Documentation](#protocol-documentation)
- [Docker](#docker)
- [License](LICENSE)
- [Disclaimer](#disclaimer)

## Description

> OpenMMO is currently in development and not yet ready for any real use.

OpenMMO is an open-source implementation of the PokeMMO server.
The goal is it to provide a free and open-source alternative to the PokeMMO server.

## Modules
OpenMMO is split into several modules for better organization and easier documentation.
- [common](common/README.md): Common enums and shared utilities used across most modules.
- [keys](keys/README.md): A module for generating the keys used by the servers for signing packets and by the client for verifying them.
- [patcher](patcher/README.md): A tool for applying patches to the PokeMMO client so that it can connect to OpenMMO.
- [protocol](protocol/README.md): Core packet protocol framework with serialization and deserialization.
- [protocol.tls](protocol.tls/README.md): TLS-like protocol implementation for secure client-server communication.
- [protocol.login](protocol.login/README.md): Login-specific protocol packets and serialization.
- [server](server/README.md): Core server framework using Netty for networking and protocol handling.
- [server.login](server.login/README.md): Login server implementation with authentication and database integration.

## Protocol Documentation
Detailed specifications and technical documentation for the network protocols used in OpenMMO.

- [TLS Protocol](docs/protocol/tls/index.md): Custom TLS-like protocol specification with ECDH key exchange, ECDSA authentication, and packet structure diagrams.

## Disclaimer
[PokeMMO](https://pokemmo.eu/) is not affiliated with this project in any way.
Hosting/Using a private server might be against the [PokeMMO ToS](https://pokemmo.com/tos/).
