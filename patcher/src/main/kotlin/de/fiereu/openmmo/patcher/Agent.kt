package de.fiereu.openmmo.patcher

import java.io.StringReader
import kotlin.io.encoding.Base64
import org.bouncycastle.util.io.pem.PemReader

const val POKEMMO_PUBKEY_GAME =
    "MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEtqx2myJz3ftlYWgd7cbNqf2t208itQMY7ouPNBDpQetbi7eXbEDxDDZy4Q9fMnI6mF5/D0qMdRd40SRXf0OS7Q=="
const val POKEMMO_PUBKEY_CHAT =
    "MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEh4Vqgnd+8Fqebu0H40v+FgwhE6RwgAYxJMihb8mJmcHDy8r/rPz3kLHH1oabyKIRUa5Y2cK0TsxZky+mp7DKWA=="

object Agent {

  /**
   * Simple logging function that prints messages to the console. We use this instead of a logging
   * framework to not collide with the game's logging system. And to keep it simple and lightweight.
   *
   * @param message A lambda that returns the message to log.
   */
  fun log(message: () -> String) {
    println("[OpenMMO Patcher] ${message()}")
  }

  @JvmStatic
  @JvmName("premain")
  fun premain(agentArgs: String?, inst: java.lang.instrument.Instrumentation) {
    log { "Agent started with args: $agentArgs" }
    val patches =
        listOf(
            StringPatcher.Patch(
                "GamePubKeyPatch", POKEMMO_PUBKEY_GAME, loadPublicKey("/game.public.pem")),
            StringPatcher.Patch(
                "ChatPubKeyPatch", POKEMMO_PUBKEY_CHAT, loadPublicKey("/chat.public.pem")))

    patches.forEach { patch -> log { "Loaded patch: ${patch.name} - for '${patch.original}'" } }

    inst.addTransformer(PatchingClassFileTransformer(patches))
  }

  private fun loadPublicKey(path: String): String {
    return Agent::class.java.getResourceAsStream(path)?.use { inputStream ->
      val pemContent = inputStream.readBytes().decodeToString()
      val pemReader = PemReader(StringReader(pemContent))
      val pemObject = pemReader.readPemObject()
      Base64.Default.encode(pemObject.content)
    } ?: throw IllegalArgumentException("Public key not found at path: $path")
  }
}
