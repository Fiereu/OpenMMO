package de.fiereu.openmmo.server.protocol.tls

import java.security.MessageDigest
import java.security.PrivateKey
import java.security.PublicKey
import javax.crypto.Cipher
import javax.crypto.KeyAgreement
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

interface TlsContext {
    fun  encrypt(data: ByteArray): ByteArray
    fun  decrypt(data: ByteArray): ByteArray
}

class NoOpTlsContext : TlsContext {
    override fun encrypt(data: ByteArray): ByteArray = data
    override fun decrypt(data: ByteArray): ByteArray = data
}

class DefaultTlsContext(
    privateKey: PrivateKey,
    publicKey: PublicKey
) : TlsContext {
    companion object {
        private val COMMON_IV = "IVDERIV".toByteArray()
        private val CLIENT_KEY_SALT = "KeySalt".toByteArray() + byteArrayOf(1)
        private val SERVER_KEY_SALT = "KeySalt".toByteArray() + byteArrayOf(2)
    }

    private var clientSeed = byteArrayOf(63, 24, -15, 98, 114, 7, 68, 24, -12, 109, -111, -105, 66, -96, -2, -55)
    private var serverSeed = byteArrayOf(31, -102, -128, 60, -103, 38, 10, -117, -105, -50, 2, 116, -83, 57, 39, -76)

    private val encryptionCipher: Cipher // Server to client
    private val decryptionCipher: Cipher // Client to server
    
    fun getClientSeed(): ByteArray = clientSeed.copyOf()
    fun getServerSeed(): ByteArray = serverSeed.copyOf()

    init {
        val sharedSecret = performKeyAgreement(privateKey, publicKey)
        
        if (sharedSecret.size * 8 >= 128) {
            clientSeed = tripleHash(sharedSecret, CLIENT_KEY_SALT)
            serverSeed = tripleHash(sharedSecret, SERVER_KEY_SALT)
        }
        
        encryptionCipher = createCipher(Cipher.ENCRYPT_MODE, serverSeed) // Server encrypts outgoing
        decryptionCipher = createCipher(Cipher.DECRYPT_MODE, clientSeed) // Server decrypts incoming
    }

    private fun performKeyAgreement(privateKey: PrivateKey, publicKey: PublicKey): ByteArray {
        val keyAgreement = KeyAgreement.getInstance("ECDH")
        keyAgreement.init(privateKey)
        keyAgreement.doPhase(publicKey, true)
        return keyAgreement.generateSecret()
    }

    private fun tripleHash(data1: ByteArray, data2: ByteArray): ByteArray {
        val sha256 = MessageDigest.getInstance("SHA-256")
        sha256.update(data2)
        sha256.update(data1)
        sha256.update(data2)
        val digest = sha256.digest()
        return digest.copyOfRange(0, 16)
    }

    private fun createCipher(mode: Int, seed: ByteArray): Cipher {
        val cipher = Cipher.getInstance("AES/CTR/NoPadding")
        val key = SecretKeySpec(seed, "AES")
        val iv = IvParameterSpec(tripleHash(seed, COMMON_IV))
        cipher.init(mode, key, iv)
        return cipher
    }

    override fun encrypt(data: ByteArray): ByteArray =
        encryptionCipher.update(data)

    override fun decrypt(data: ByteArray): ByteArray =
        decryptionCipher.update(data)
}