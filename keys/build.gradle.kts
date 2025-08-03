import org.bouncycastle.util.io.pem.PemObject
import org.bouncycastle.util.io.pem.PemWriter
import java.security.KeyPairGenerator
import java.security.spec.ECGenParameterSpec
import java.io.StringWriter

buildscript {
  repositories {
    mavenCentral()
  }

  dependencies {
    classpath(libs.bundles.crypto)
  }
}

plugins {
  id("buildsrc.convention.spotless")
}

/**
 * Abstract task for generating a certificate.
 */
abstract class GenerateCertificateTask : DefaultTask() {

  @get:OutputFile
  val privateKeyOutput: RegularFileProperty = project.objects.fileProperty()

  @get:OutputFile
  val publicKeyOutput: RegularFileProperty = project.objects.fileProperty()

  @get:Input
  val overwriteCertificates: Property<Boolean> = project.objects.property(Boolean::class.java)
    .convention(false)

  init {
    group = "openmmo"
    outputs.upToDateWhen { !overwriteCertificates.get() }
  }

  @TaskAction
  open fun generate() {
    val publicKeyFile = publicKeyOutput.get().asFile
    val privateKeyFile = privateKeyOutput.get().asFile
    if (!overwriteCertificates.get() && (publicKeyFile.exists() || privateKeyFile.exists())) {
      logger.warn("Keys already exist. Use 'overwriteCertificates' to overwrite them.")
      return
    }

    // Generate ECDSA key pair using secp256r1 curve
    val keyPairGenerator = KeyPairGenerator.getInstance("EC")
    keyPairGenerator.initialize(ECGenParameterSpec("secp256r1"))
    val keyPair = keyPairGenerator.generateKeyPair()

    // Write private key as PEM
    val privateKeyPem = StringWriter().use { writer ->
      PemWriter(writer).use { pemWriter ->
        pemWriter.writeObject(PemObject("EC PRIVATE KEY", keyPair.private.encoded))
      }
      writer.toString()
    }
    privateKeyFile.writeText(privateKeyPem)

    // Write public key as PEM
    val publicKeyPem = StringWriter().use { writer ->
      PemWriter(writer).use { pemWriter ->
        pemWriter.writeObject(PemObject("PUBLIC KEY", keyPair.public.encoded))
      }
      writer.toString()
    }
    publicKeyFile.writeText(publicKeyPem)
  }
}

tasks.register<GenerateCertificateTask>("generateGame") {
  description = "Generates a key pair for the login and game servers."
  privateKeyOutput.set(layout.buildDirectory.file("game.private.pem"))
  publicKeyOutput.set(layout.buildDirectory.file("game.public.pem"))
}

tasks.register<GenerateCertificateTask>("generateChat") {
  description = "Generates a key pair for the chat server."
  privateKeyOutput.set(layout.buildDirectory.file("chat.private.pem"))
  publicKeyOutput.set(layout.buildDirectory.file("chat.public.pem"))
}

tasks.register("generate") {
  group = "openmmo"
  dependsOn("generateGame", "generateChat")
}