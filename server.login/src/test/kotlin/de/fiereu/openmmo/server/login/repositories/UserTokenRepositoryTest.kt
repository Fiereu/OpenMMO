package de.fiereu.openmmo.server.login.repositories

import com.google.common.hash.Hashing
import de.fiereu.openmmo.server.login.jooq.tables.references.USER
import de.fiereu.openmmo.server.login.jooq.tables.references.USER_TOKEN
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.flywaydb.core.Flyway
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName

class UserTokenRepositoryTest : FunSpec({
  lateinit var flyway: Flyway
  lateinit var postgres: PostgreSQLContainer<*>
  lateinit var ctx: DSLContext
  lateinit var repository: UserTokenRepository

  beforeSpec {
    postgres = PostgreSQLContainer(DockerImageName.parse("postgres:latest"))
      .apply { start() }

    ctx = DSL.using(
      postgres.jdbcUrl,
      postgres.username,
      postgres.password
    )

    flyway = Flyway.configure()
      .dataSource(postgres.jdbcUrl, postgres.username, postgres.password)
      .locations("filesystem:src/main/resources/db/migration")
      .load()
    flyway.migrate()

    repository = UserTokenRepository(ctx)
  }

  afterSpec {
    postgres.stop()
  }

  beforeTest {
    flyway.clean()
  }

  test("createToken should create a new token for a user") {
    val user = ctx.insertInto(USER)
      .set(USER.USERNAME, "testuser")
      .set(USER.PASSWORD, Hashing.sha256().hashString("password", Charsets.UTF_8).asBytes())
      .returning()
      .fetchOne()!!

    val token = repository.createToken(user)

    token shouldNotBe null
    token.userId shouldBe user.id
    token.token!!.size shouldBe 32
  }

  test("createToken should replace existing token on duplicate") {
    val user = ctx.insertInto(USER)
      .set(USER.USERNAME, "testuser")
      .set(USER.PASSWORD, Hashing.sha256().hashString("password", Charsets.UTF_8).asBytes())
      .returning()
      .fetchOne()!!

    val firstToken = repository.createToken(user)

    val secondToken = repository.createToken(user)

    firstToken.token shouldNotBe secondToken.token

    val allTokens = ctx.selectFrom(USER_TOKEN)
      .where(USER_TOKEN.USER_ID.eq(user.id))
      .fetch()
    allTokens shouldHaveSize 1
  }

  test("getToken should return empty when user has no token") {
    val user = ctx.insertInto(USER)
      .set(USER.USERNAME, "testuser")
      .set(USER.PASSWORD, Hashing.sha256().hashString("password", Charsets.UTF_8).asBytes())
      .returning()
      .fetchOne()!!

    val result = repository.getToken(user)

    result.isEmpty shouldBe true
  }

  test("getToken should return token when user has one") {
    val user = ctx.insertInto(USER)
      .set(USER.USERNAME, "testuser")
      .set(USER.PASSWORD, Hashing.sha256().hashString("password", Charsets.UTF_8).asBytes())
      .returning()
      .fetchOne()!!

    val createdToken = repository.createToken(user)

    val result = repository.getToken(user)

    result.isPresent shouldBe true
    result.get().token shouldBe createdToken.token
  }

  test("createToken should generate different tokens for different calls") {
    val user = ctx.insertInto(USER)
      .set(USER.USERNAME, "testuser")
      .set(USER.PASSWORD, Hashing.sha256().hashString("password", Charsets.UTF_8).asBytes())
      .returning()
      .fetchOne()!!

    val token1 = repository.createToken(user)
    val token2 = repository.createToken(user)

    token1.token shouldNotBe token2.token
  }
})