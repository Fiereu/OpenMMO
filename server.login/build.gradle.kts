import org.flywaydb.gradle.task.AbstractFlywayTask

buildscript { dependencies { classpath(libs.flyway.postgresql) } }

plugins {
  application
  distribution
  id("buildsrc.convention.kotlin-jvm")
  id("buildsrc.convention.spotless")
  id("buildsrc.common.keys")
  alias(libs.plugins.kotlin.serialization)
  alias(libs.plugins.jooq)
  alias(libs.plugins.flyway)
}

group = "de.fiereu.openmmo"

version = "0.1.0"

dependencies {
  implementation(project(":server"))
  implementation(project(":protocol.login"))
  implementation(libs.logback)
  implementation(libs.kotlinx.coroutines)
  implementation(libs.guava)

  runtimeOnly(libs.postgresql)

  implementation(libs.bundles.jooq)
  jooqCodegen(libs.postgresql)

  implementation(libs.flyway.core)
  implementation(libs.flyway.postgresql)

  testImplementation(libs.bundles.kotest)
  testImplementation(libs.kotlinx.coroutines.test)
  testImplementation(libs.mockk)
  testImplementation(libs.testcontainers.core)
  testImplementation(libs.testcontainers.postgresql)
}

application { mainClass.set("de.fiereu.openmmo.server.login.LoginServerKt") }

sourceSets { main { java { srcDir("src/main/jooq") } } }

fun env(key: String): Any {
  val value =
      property(key)
          ?: System.getenv(key)
          ?: throw IllegalArgumentException(
              "Environment variable or system property '$key' is not set.")
  fun interpolate(value: String): Any {
    val PATTERN_WORD = Regex("\\$([a-zA-Z0-9_]+)")
    val PATTERN_MULTI = Regex("\\$\\{([a-zA-Z0-9_]+)\\}")
    return (PATTERN_WORD.findAll(value) + PATTERN_MULTI.findAll(value)).distinct().fold(value) {
        acc,
        match ->
      val envName = match.groupValues[1]
      val envValue = System.getProperty(envName) ?: System.getProperty(envName)
      if (envValue == null) {
        throw IllegalArgumentException(
            "Environment variable or system property '$envName' is not set.")
      }
      acc.replace(match.value, envValue)
    }
  }
  return if (value is String && (value.contains('$') || value.contains('{'))) {
    interpolate(value)
  } else value
}

val dbPort = env("openmmo.db.login.port") as String
val dbName = env("openmmo.db.login.name") as String
val dbUser = env("openmmo.db.login.user") as String
val dbPassword = env("openmmo.db.login.password") as String

jooq {
  configuration {
    jdbc {
      driver = "org.postgresql.Driver"
      url = "jdbc:postgresql://localhost:$dbPort/$dbName"
      user = dbUser
      password = dbPassword
    }
    generator {
      database {
        name = "org.jooq.meta.postgres.PostgresDatabase"
        includes = ".*"
        excludes =
            """
          flyway_schema_history |
          pgp_armor_headers
        """
        inputSchema = "public"
      }

      generate { name = "org.jooq.codegen.KotlinGenerator" }
      target {
        packageName = "de.fiereu.openmmo.server.login.jooq"
        directory = "src/main/jooq"
      }
    }
  }
}

flyway {
  driver = "org.postgresql.Driver"
  url = "jdbc:postgresql://localhost:$dbPort/$dbName"
  user = dbUser
  password = dbPassword
  schemas = arrayOf("public")
  locations = arrayOf("filesystem:src/main/resources/db/migration")
  cleanDisabled = false
}

// Fuck this shit uhh we are not gonna update our gradle plugin bc all our enterprise customers are
// stuck on Java 1.6 and gradle 5 anyway
tasks.withType<AbstractFlywayTask>().configureEach {
  notCompatibleWithConfigurationCache("https://github.com/flyway/flyway/issues/3550")
}

tasks.register("cleanMigrateAndGenerate") {
  group = "openmmo"
  description = "Cleans the database, migrates it and generates the jOOQ classes."
  dependsOn("flywayClean", "flywayMigrate", "jooqCodegen")
}

spotless { kotlin { targetExclude("src/main/jooq/**") } }

// If someone knows a better way then this please please please help me :(
listOf("classes", "processResources", "spotlessKotlin", "spotlessKotlinGradle", "spotlessMisc")
    .forEach { taskName ->
      tasks.named(taskName) {
        listOf("copyPublicKeyGame", "copyPrivateKeyGame").forEach { dependsOn(it) }
      }
    }
