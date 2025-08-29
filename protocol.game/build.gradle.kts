plugins {
  id("buildsrc.convention.kotlin-jvm")
  id("buildsrc.convention.spotless")
}

dependencies {
  api(project(":protocol"))
  api(project(":common"))

  testImplementation(libs.bundles.kotest)
}
