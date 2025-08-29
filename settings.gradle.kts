dependencyResolutionManagement {
  @Suppress("UnstableApiUsage")
  repositories {
    mavenCentral()
  }
}

plugins {
  id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

rootProject.name = "openmmo"

include(":keys")
include(":patcher")
include(":common")
include(":protocol")
include(":protocol.tls")
include(":protocol.login")
include(":protocol.game")
include(":server")
include(":server.login")
include(":server.game")