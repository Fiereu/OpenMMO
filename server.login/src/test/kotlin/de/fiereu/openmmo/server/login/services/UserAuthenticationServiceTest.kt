package de.fiereu.openmmo.server.login.services

import de.fiereu.openmmo.server.login.jooq.tables.records.UserRecord
import de.fiereu.openmmo.server.login.jooq.tables.records.UserTokenRecord
import de.fiereu.openmmo.server.login.repositories.UserRepository
import de.fiereu.openmmo.server.login.repositories.UserTokenRepository
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.Optional

class UserAuthenticationServiceTest :
    ShouldSpec({
      context("UserAuthenticationService") {
        val userRepository = mockk<UserRepository>()
        val userTokenRepository = mockk<UserTokenRepository>()
        val service = UserAuthenticationService(userRepository, userTokenRepository)

        beforeTest { clearAllMocks() }

        val testUsername = "testuser"
        val testPasswordHash =
            "5baa61e4c9b93f3f0682250b6cf8331b7ee68fd8" // SHA-1 hash of "password"
        val testPasswordBytes =
            byteArrayOf(
                0x5b.toByte(),
                0xaa.toByte(),
                0x61.toByte(),
                0xe4.toByte(),
                0xc9.toByte(),
                0xb9.toByte(),
                0x3f.toByte(),
                0x3f.toByte(),
                0x06.toByte(),
                0x82.toByte(),
                0x25.toByte(),
                0x0b.toByte(),
                0x6c.toByte(),
                0xf8.toByte(),
                0x33.toByte(),
                0x1b.toByte(),
                0x7e.toByte(),
                0xe6.toByte(),
                0x8f.toByte(),
                0xd8.toByte())
        val wrongPasswordBytes = byteArrayOf(0x00, 0x01, 0x02)
        val testToken = byteArrayOf(0x01, 0x02, 0x03, 0x04)
        val testTokenRecord = UserTokenRecord(token = testToken)
        val wrongTokenRecord = UserTokenRecord(token = byteArrayOf(0x05, 0x06, 0x07, 0x08))

        context("loginPassword") {
          should("should return true when username and password match") {
            val userRecord = mockk<UserRecord> { every { password } returns testPasswordBytes }
            every { userRepository.getUser(testUsername) } returns Optional.of(userRecord)

            val result = service.loginPassword(testUsername, testPasswordHash)

            result shouldBe true
            verify { userRepository.getUser(testUsername) }
          }

          should("should return false when user does not exist") {
            every { userRepository.getUser(testUsername) } returns Optional.empty()

            val result = service.loginPassword(testUsername, testPasswordHash)

            result shouldBe false
            verify { userRepository.getUser(testUsername) }
          }

          should("should return false when password does not match") {
            val userRecord = mockk<UserRecord> { every { password } returns wrongPasswordBytes }
            every { userRepository.getUser(testUsername) } returns Optional.of(userRecord)

            val result = service.loginPassword(testUsername, testPasswordHash)

            result shouldBe false
            verify { userRepository.getUser(testUsername) }
          }
        }

        context("loginToken") {
          should("should return true when username and token match") {
            val userRecord = mockk<UserRecord>()
            every { userRepository.getUser(testUsername) } returns Optional.of(userRecord)
            every { userTokenRepository.getToken(userRecord) } returns Optional.of(testTokenRecord)

            val result = service.loginToken(testUsername, testToken)

            result shouldBe true
            verify { userRepository.getUser(testUsername) }
            verify { userTokenRepository.getToken(userRecord) }
          }

          should("should return false when user does not exist") {
            every { userRepository.getUser(testUsername) } returns Optional.empty()

            val result = service.loginToken(testUsername, testToken)

            result shouldBe false
            verify { userRepository.getUser(testUsername) }
            verify(exactly = 0) { userTokenRepository.getToken(any()) }
          }

          should("should return false when token does not exist for user") {
            val userRecord = mockk<UserRecord>()
            every { userRepository.getUser(testUsername) } returns Optional.of(userRecord)
            every { userTokenRepository.getToken(userRecord) } returns Optional.empty()

            val result = service.loginToken(testUsername, testToken)

            result shouldBe false
            verify { userRepository.getUser(testUsername) }
            verify { userTokenRepository.getToken(userRecord) }
          }

          should("should return false when token does not match") {
            val userRecord = mockk<UserRecord>()
            every { userRepository.getUser(testUsername) } returns Optional.of(userRecord)
            every { userTokenRepository.getToken(userRecord) } returns Optional.of(wrongTokenRecord)

            val result = service.loginToken(testUsername, testToken)

            result shouldBe false
            verify { userRepository.getUser(testUsername) }
            verify { userTokenRepository.getToken(userRecord) }
          }
        }

        context("createToken") {
          should("should return success with token when user exists") {
            val userRecord = mockk<UserRecord>()
            val tokenRecord = mockk<UserTokenRecord>()
            every { userRepository.getUser(testUsername) } returns Optional.of(userRecord)
            every { userTokenRepository.createToken(userRecord) } returns tokenRecord

            val result = service.createToken(testUsername)

            result.isSuccess shouldBe true
            result.getOrNull() shouldBe tokenRecord
            verify { userRepository.getUser(testUsername) }
            verify { userTokenRepository.createToken(userRecord) }
          }

          should("should return failure when user does not exist") {
            every { userRepository.getUser(testUsername) } returns Optional.empty()

            val result = service.createToken(testUsername)

            result.isFailure shouldBe true
            result.exceptionOrNull().shouldBeInstanceOf<IllegalStateException>()
            result.exceptionOrNull()?.message shouldBe "User $testUsername doesn't exist."
            verify { userRepository.getUser(testUsername) }
            verify(exactly = 0) { userTokenRepository.createToken(any()) }
          }
        }
      }
    })
