plugins {
  id("buildsrc.convention.kotlin-jvm")
  id("buildsrc.convention.spotless")
}

dependencies {
  api(project(":protocol"))
  api(libs.bundles.crypto)

  testImplementation(libs.bundles.kotest)
}
