package com.example.exposed.transaction.configCase

import com.example.exposed.transaction.RandomEntity
import com.example.exposed.transaction.RandomTable
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.*
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.*
import java.sql.Connection
import java.util.*
import java.util.concurrent.*

@ExperimentalCoroutinesApi
@DelicateCoroutinesApi
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DatabaseTestConfigCase1 {

    private val mainThreadSurrogate = newSingleThreadContext("Coroutine thread")

    private fun testCoroutine(testFunction: suspend () -> Unit) {
        runBlocking {
            launch(Dispatchers.Main) {
                testFunction()
            }
        }
    }

    @BeforeAll
    fun setUp() {
        Dispatchers.setMain(mainThreadSurrogate)
    }

    @AfterAll
    fun tearDown() {
        Dispatchers.resetMain() // reset the main dispatcher to the original Main dispatcher
        mainThreadSurrogate.close()
    }

    // Case 1 setting schema only in exposed transaction
    // Outcome: Did not result in an error! But schema is set at the beginning of every transaction.

    @Test
    fun `Create a new Connection after it was closed, PSQLException`() = testCoroutine {

        val db = DatabaseMock()

        getPSQLVersion()
        createEntry()

        delay(TimeUnit.SECONDS.toMillis(31))

        getPSQLVersion()
        createEntry()
    }

    private suspend fun getPSQLVersion() = dbQuery {
        exec("SELECT VERSION();") { it.next(); it.getString(1) }
    }

    private suspend fun createEntry() = dbQuery {
        RandomEntity.new { test = UUID.randomUUID().toString() }
    }

    suspend fun <T> dbQuery(dispatcher: CoroutineDispatcher = Dispatchers.IO, block: suspend Transaction.() -> T): T =
        newSuspendedTransaction(
            dispatcher,
            transactionIsolation = Connection.TRANSACTION_SERIALIZABLE
        ) {
            SchemaUtils.setSchema(Schema(DATABASE_SCHEMA, DATABASE_USER))
            block()
        }

    class DatabaseMock {

        private val isolationLevel = "TRANSACTION_SERIALIZABLE"
        private val driverName = "org.postgresql.Driver"

        init {
            setupDatasource()
        }

        private fun setupDatasource() {
            val dataSource = hikari()

            Database.connect(dataSource)
            transaction(Connection.TRANSACTION_SERIALIZABLE, 1) {
                // transaction {
                val schema = Schema(DATABASE_SCHEMA, DATABASE_USER)
                SchemaUtils.createSchema(schema)
                SchemaUtils.setSchema(schema)
                SchemaUtils.create(RandomTable)
            }
        }

        private fun hikari(): HikariDataSource {
            val hikariConfig = HikariConfig().apply {
                driverClassName = driverName
                jdbcUrl = DATABASE_URL
                // schema = DATABASE_SCHEMA
                username = DATABASE_USER
                password = DATABASE_PASSWORD
                maximumPoolSize = 5
                isAutoCommit = false
                maxLifetime = 30020
                transactionIsolation = isolationLevel
            }
            hikariConfig.validate()

            return HikariDataSource(hikariConfig)
        }
    }

    companion object {
        const val DATABASE_USER = "hg_kotlin"
        const val DATABASE_SCHEMA = "test_schema"
        const val DATABASE_PASSWORD = "super_secret"
        const val DATABASE_URL = "jdbc:postgresql://localhost:5432/hg_kotlin_test"
    }
}


/*
11:13:03.042 [Coroutine thread @coroutine#2] INFO com.zaxxer.hikari.HikariDataSource - HikariPool-1 - Starting...
11:13:03.265 [Coroutine thread @coroutine#2] INFO com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Added connection org.postgresql.jdbc.PgConnection@245788ff
11:13:03.268 [Coroutine thread @coroutine#2] INFO com.zaxxer.hikari.HikariDataSource - HikariPool-1 - Start completed.
11:13:03.372 [HikariPool-1 housekeeper] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Pool stats (total=1, active=0, idle=1, waiting=0)
11:13:03.387 [HikariPool-1 connection adder] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Added connection org.postgresql.jdbc.PgConnection@7ab7dde8
11:13:03.410 [HikariPool-1 connection adder] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Added connection org.postgresql.jdbc.PgConnection@74703cce
11:13:03.436 [HikariPool-1 connection adder] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Added connection org.postgresql.jdbc.PgConnection@704fb698
11:13:03.461 [HikariPool-1 connection adder] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Added connection org.postgresql.jdbc.PgConnection@354d36f5
11:13:03.478 [HikariPool-1 connection adder] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - After adding stats (total=5, active=0, idle=5, waiting=0)
11:13:03.685 [Coroutine thread @coroutine#2] DEBUG Exposed - SET search_path TO test_schema
11:13:03.767 [DefaultDispatcher-worker-1 @coroutine#3] DEBUG Exposed - SET search_path TO test_schema
11:13:03.771 [DefaultDispatcher-worker-1 @coroutine#3] DEBUG Exposed - SELECT VERSION();
11:13:03.784 [DefaultDispatcher-worker-1 @coroutine#4] DEBUG Exposed - SET search_path TO test_schema
11:13:04.474 [DefaultDispatcher-worker-1 @coroutine#4] DEBUG Exposed - INSERT INTO random ("text") VALUES ('0c64e359-299e-4ffa-aad3-c06a51d10ea1')
11:13:32.746 [HikariPool-1 connection closer] DEBUG com.zaxxer.hikari.pool.PoolBase - HikariPool-1 - Closing connection org.postgresql.jdbc.PgConnection@7ab7dde8: (connection has passed maxLifetime)
11:13:32.758 [HikariPool-1 connection adder] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Added connection org.postgresql.jdbc.PgConnection@66741d86
11:13:32.775 [HikariPool-1 connection closer] DEBUG com.zaxxer.hikari.pool.PoolBase - HikariPool-1 - Closing connection org.postgresql.jdbc.PgConnection@245788ff: (connection has passed maxLifetime)
11:13:32.788 [HikariPool-1 connection adder] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Added connection org.postgresql.jdbc.PgConnection@68b3ff38
11:13:32.988 [HikariPool-1 connection closer] DEBUG com.zaxxer.hikari.pool.PoolBase - HikariPool-1 - Closing connection org.postgresql.jdbc.PgConnection@354d36f5: (connection has passed maxLifetime)
11:13:33.001 [HikariPool-1 connection adder] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Added connection org.postgresql.jdbc.PgConnection@6c8c76f6
11:13:33.104 [HikariPool-1 connection closer] DEBUG com.zaxxer.hikari.pool.PoolBase - HikariPool-1 - Closing connection org.postgresql.jdbc.PgConnection@704fb698: (connection has passed maxLifetime)
11:13:33.117 [HikariPool-1 connection adder] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Added connection org.postgresql.jdbc.PgConnection@298ef503
11:13:33.300 [HikariPool-1 connection closer] DEBUG com.zaxxer.hikari.pool.PoolBase - HikariPool-1 - Closing connection org.postgresql.jdbc.PgConnection@74703cce: (connection has passed maxLifetime)
11:13:33.312 [HikariPool-1 connection adder] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Added connection org.postgresql.jdbc.PgConnection@6f504599
11:13:33.373 [HikariPool-1 housekeeper] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Pool stats (total=5, active=0, idle=5, waiting=0)
11:13:33.373 [HikariPool-1 housekeeper] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Fill pool skipped, pool has sufficient level or currently being filled (queueDepth=0).
11:13:35.492 [DefaultDispatcher-worker-1 @coroutine#5] DEBUG Exposed - SET search_path TO test_schema
11:13:35.493 [DefaultDispatcher-worker-1 @coroutine#5] DEBUG Exposed - SELECT VERSION();
11:13:35.498 [DefaultDispatcher-worker-1 @coroutine#6] DEBUG Exposed - SET search_path TO test_schema
11:13:35.501 [DefaultDispatcher-worker-1 @coroutine#6] DEBUG Exposed - INSERT INTO random ("text") VALUES ('791fbfa0-e460-456c-bcca-3496e66de40d')
 */
