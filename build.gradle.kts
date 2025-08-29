allprojects {
  beforeEvaluate {
    loadEnvFile("${rootDir.absolutePath}/.env")
  }
}

fun loadEnvFile(fileName: String) {
  val file = rootProject.file(fileName)
  if (file.exists()) {
    file.forEachLine { line ->
      if (line.isNotBlank() && !line.startsWith("#")) {
        val parts = line.split("=", limit = 2)
        if (parts.size == 2) {
          System.setProperty(parts[0].trim(), parts[1].trim())
        } else {
          throw IllegalArgumentException("Invalid line in env file: $line")
        }
      }
    }
  } else {
    throw IllegalArgumentException("Env file '$fileName' does not exist.")
  }
}

tasks.register("runAll") {
  group = "application"
  description = "Runs all servers and the patcher"
  listOf(
    ":server.login:run",
    ":server.game:run",
    ":patcher:run"
  ).forEach { dependsOn(it) }
}