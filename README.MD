# 🚧 OpenMMO 🚧 

---

[![](https://dcbadge.limes.pink/api/server/SQwGXyY2gz)](https://discord.gg/SQwGXyY2gz)
![Java CI with Gradle](https://github.com/Fiereu/OpenMMO/actions/workflows/gradle.yml/badge.svg)

> ❗OpenMMO is currently in development and not yet ready for any real use.

OpenMMO is an open-source implementation of the PokeMMO server. 
The goal is it to provide a free and open-source alternative to the PokeMMO server.

_[PokeMMO](https://pokemmo.eu/) is not affiliated with this project in any way.
Hosting/Using a private server might be against the [PokeMMO ToS](https://pokemmo.com/tos/)._

I have reverse-engineered the PokeMMO client for a while now and want to share my knowledge with the community.
I do not have any access to the original PokeMMO server code, and I am not affiliated with the PokeMMO team.
This project is purely educational and should not be used for any commercial purposes.

The whole project is created from reverse-engineering the PokeMMO client and inspecting the network traffic
between the client and the server. The client is 99% written in Java and uses LibGDX for rendering.
The Networking is a fully custom implementation using Java NIO.
This project on the other hand uses the Netty library for networking. 

> Why use Netty? idk, I just wanted to try it out.

## Status

The project is currently in development and not yet ready for any real use.
I was able to implement the basic login process and am able to redirect the client to the game server after successful login.
The client is also able to load the current map and the player's position. The next steps will be to implement basic movement and multiplayer features.
Since the game is under active development, Things may change. So to not waste time on fixings things after every update we will work with the client revision *26211*.

## Setup

1. Clone the repository
2. Run `docker compose up` to start the database
3. Start the server(s) via `gradlew :server.login:run` & `gradlew :server.game:run`
4. Copy the public key `./game.public` to `./patcher/src/main/resources/game.public`
5. Build the patcher via `gradlew :patcher:shadowJar`
6. Start PokeMMO with the patcher to replace the login & game server certificates via `java -javaagent:<path-to-agent.jar> -jar <path-to-patcher.jar>` from the PokeMMO directory
> Example for Windows: `java -javaagent:C:\...\patcher.jar -jar "C:\Program Files\PokeMMO\PokeMMO.exe"`
7. The default login is *admin:admin*

## Contributing

If you want to contribute to the project, feel free to create a pull request.
I'm happy about every contribution to the project. 
But im fairly that not many people are interested in this project so it will probably take a while to get it to a usable state.
