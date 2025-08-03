plugins {
  `java-library`
  id("buildsrc.convention.kotlin-jvm")
  alias(libs.plugins.kotlin.serialization)
  alias(libs.plugins.ksp)
}

group = "de.fiereu.openmmo"

version = "0.1.0"

dependencies {
  api(libs.typesafe.config)
  api(libs.slf4j)
  api(libs.kotlin.logging)
  api(libs.kotlinx.serialization.core)
  api(libs.kotlinx.coroutines)
  api(libs.dagger)
  api(project(":protocol"))
  api(project(":protocol.tls"))

  ksp(libs.dagger.compiler)

  implementation(libs.kotlin.reflect)
  implementation(libs.netty)
  implementation(libs.kotlinx.serialization.json)
  implementation(libs.bundles.crypto)

  testImplementation(libs.bundles.kotest)
  testImplementation(libs.kotlinx.coroutines.test)
  testImplementation(libs.mockk)
}
