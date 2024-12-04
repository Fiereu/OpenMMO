plugins {
    id("java")
    id("application")
}

group = "de.fiereu.openmmo"
version = "unspecified"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    compileOnly("org.projectlombok:lombok:1.18.34")
    annotationProcessor("org.projectlombok:lombok:1.18.34")

    implementation("org.slf4j:slf4j-api:2.0.16")
    implementation("ch.qos.logback:logback-classic:1.5.7")

    implementation(project(":server"))
    implementation("io.netty:netty-all:4.1.112.Final")
    implementation("com.github.maltalex:ineter:0.3.1")
    implementation("com.google.inject:guice:7.0.0")

    implementation(project(":db"))
    implementation("org.jooq:jooq:3.19.13")
    implementation("org.jooq:jooq-postgres-extensions:3.19.13")
    implementation("org.postgresql:postgresql:42.7.3")
}

tasks.test {
    useJUnitPlatform()
}

application {
    mainClass.set("de.fiereu.openmmo.gameserver.Main")
}

tasks.named<JavaExec>("run") {
    workingDir = rootDir
}