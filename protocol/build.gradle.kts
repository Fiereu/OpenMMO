plugins {
  id("buildsrc.convention.kotlin-jvm")
  id("buildsrc.convention.spotless")
}

dependencies {
  api(libs.kotlinx.serialization.core)
  api(libs.netty)
  api(libs.ineter)

  implementation(libs.slf4j)
  implementation(libs.kotlin.logging)
  implementation(libs.kotlin.reflect)

  testImplementation(libs.bundles.kotest)
}
