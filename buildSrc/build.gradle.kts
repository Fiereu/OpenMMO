plugins {
  `kotlin-dsl`
}

kotlin {
  jvmToolchain(24)
}

dependencies {
  implementation(libs.kotlinGradlePlugin)
  implementation(libs.spotlessGradlePlugin)
  implementation(libs.junit5)
}
