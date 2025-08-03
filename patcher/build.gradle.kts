plugins {
  id("buildsrc.convention.kotlin-jvm")
  id("buildsrc.convention.spotless")
  id("buildsrc.common.keys")
  alias(libs.plugins.shadow)
}

group = "de.fiereu.openmmo"

version = "1.0.0"

dependencies {
  implementation(libs.asm)
  implementation(libs.bundles.crypto)
}

tasks.shadowJar {
  archiveBaseName.set("patcher")
  manifest { attributes("Premain-Class" to "de.fiereu.openmmo.patcher.Agent") }
}

fun String.evalEnvVars(): String {
  return this.replace(Regex("\\$\\{([^}]+)\\}")) { matchResult ->
    System.getenv(matchResult.groupValues[1]) ?: matchResult.value
  }
}

tasks.register("setLocalServer") {
  description = "Creates a configuration file for PokeMMO that sets the login server to 127.0.0.1."

  val pokemmoWorkingDir = (project.findProperty("pokemmo.workingDir") as String).evalEnvVars()
  val configFile = File("$pokemmoWorkingDir/config/openmmo.properties")

  doFirst {
    if (!configFile.exists()) {
      configFile.parentFile.mkdirs()
      configFile.createNewFile()
    }
  }

  doLast {
    var content = configFile.readText()
    fun createOrReplace(key: String, value: String): String {
      return if (content.contains(key)) {
        content.replace("$key=.*", "$key=$value")
      } else {
        "$content\n$key=$value"
      }
    }
    content = createOrReplace("client.misc.ignore_feed", "true")
    content = createOrReplace("loginserver.network.client.host", "127.0.0.1")

    configFile.writeText(content)
  }
}

tasks.register<JavaExec>("run") {
  group = "application"
  description = "Runs the Patcher agent"
  dependsOn("shadowJar", "setLocalServer", "copyPublicKeys")
  tasks.processResources.get().mustRunAfter("copyPublicKeys")

  val pokemmoMainClass = project.findProperty("pokemmo.mainClass") as String
  val pokemmoExecutable = (project.findProperty("pokemmo.executable") as String).evalEnvVars()
  val pokemmoWorkingDir = (project.findProperty("pokemmo.workingDir") as String).evalEnvVars()
  val agentJar = tasks.shadowJar.get().archiveFile.get().asFile.absolutePath

  mainClass.set(pokemmoMainClass)
  classpath(pokemmoExecutable)
  jvmArgs =
      listOf(
          "-javaagent:$agentJar", // Attach Agent
      )
  workingDir = File(pokemmoWorkingDir)
}
