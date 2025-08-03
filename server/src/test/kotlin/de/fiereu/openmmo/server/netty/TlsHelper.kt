package de.fiereu.openmmo.server.netty

import de.fiereu.openmmo.server.protocol.tls.TlsContext
import io.mockk.every
import io.mockk.mockk

fun createMockTlsContext(xorKey: Int = 0x42): TlsContext {
  val mockTlsContext = mockk<TlsContext>(relaxed = true)

  every { mockTlsContext.encrypt(any<ByteArray>()) } answers {
    val input = firstArg<ByteArray>()
    input.map { (it.toInt() xor xorKey).toByte() }.toByteArray()
  }

  every { mockTlsContext.decrypt(any<ByteArray>()) } answers {
    val input = firstArg<ByteArray>()
    input.map { (it.toInt() xor xorKey).toByte() }.toByteArray()
  }

  return mockTlsContext
}