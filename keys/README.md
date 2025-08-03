# Keys (OpenMMO)

## ToC
- [Description](#description)
- [How to Use](#how-to-use)
- [How It Works](#how-it-works)

## Description
The keys module is responsible for generating the keys used by the servers for signing packets and by the client for verifying them.
It generates the public keys that are later used by the patcher module to replace the client's public keys.

> Generated keys are specifically excluded from git commits using `.gitignore` and should not be committed.

## How to Use
Normally, you don't need to worry about this module, as it is automatically called by all depending modules.
However, if you want to generate the keys manually, you can do so by running the `generateKeys` task in Gradle.
This will generate the keys and place them in the `build/` directory of the module.

Currently, there are only two key pairs generated:
- `game`: The key pair used for signing game packets by the login and game servers.
- `chat`: The key pair used for signing chat packets by the chat server.

## How It Works
The build script used [cryptography-kotlin](https://github.com/whyoleg/cryptography-kotlin) to generate the keypairs.
`cryptography-kotlin` is a Kotlin wrapper around libraries like OpenSSL and just provides us a simple API to generate the keys.

### Key Generation
Keys are generated using the `ECDSA` algorithm with the `secp256r1` curve. Generated keys are saved in PEM format.
