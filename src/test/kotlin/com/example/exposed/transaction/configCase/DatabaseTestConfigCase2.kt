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
class DatabaseTestConfigCase2 {

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

    // CASE 2 Setting schema only via HikariConfig
    // Outcome: if autoCommit is enabled there is no error but connection is reset everytime.

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
                SchemaUtils.create(RandomTable)
            }
        }

        private fun hikari(): HikariDataSource {
            val hikariConfig = HikariConfig().apply {
                driverClassName = driverName
                jdbcUrl = DATABASE_URL
                schema = DATABASE_SCHEMA
                username = DATABASE_USER
                password = DATABASE_PASSWORD
                maximumPoolSize = 5
                // isAutoCommit = false // if isAutoCommit = false the test will fail directly
                isAutoCommit = true // if isAutoCommit = true the test will pass but resetting connection after every access by exposed
                maxLifetime = 30020
                transactionIsolation = isolationLevel
            }
            hikariConfig.validate()

            return HikariDataSource(hikariConfig)
        }

        companion object {
            const val DATABASE_USER = "hg_kotlin"
            const val DATABASE_SCHEMA = "test_schema"
            const val DATABASE_PASSWORD = "super_secret"
            const val DATABASE_URL = "jdbc:postgresql://localhost:5432/hg_kotlin_test"
        }
    }
}

/*
11:14:47.467 [Coroutine thread @coroutine#2] INFO com.zaxxer.hikari.HikariDataSource - HikariPool-1 - Starting...
11:14:47.688 [Coroutine thread @coroutine#2] INFO com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Added connection org.postgresql.jdbc.PgConnection@9661d0
11:14:47.690 [Coroutine thread @coroutine#2] INFO com.zaxxer.hikari.HikariDataSource - HikariPool-1 - Start completed.
11:14:47.792 [HikariPool-1 housekeeper] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Pool stats (total=1, active=0, idle=1, waiting=0)
11:14:47.807 [HikariPool-1 connection adder] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Added connection org.postgresql.jdbc.PgConnection@4012d307
11:14:47.842 [HikariPool-1 connection adder] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Added connection org.postgresql.jdbc.PgConnection@4ed52575
11:14:47.870 [HikariPool-1 connection adder] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Added connection org.postgresql.jdbc.PgConnection@4aeab877
11:14:47.897 [HikariPool-1 connection adder] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Added connection org.postgresql.jdbc.PgConnection@fe82f4e
11:14:47.908 [HikariPool-1 connection adder] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - After adding stats (total=5, active=0, idle=5, waiting=0)
11:14:48.128 [Coroutine thread @coroutine#2] DEBUG com.zaxxer.hikari.pool.PoolBase - HikariPool-1 - Reset (autoCommit) on connection org.postgresql.jdbc.PgConnection@9661d0
11:14:48.181 [DefaultDispatcher-worker-1 @coroutine#3] DEBUG Exposed - SELECT VERSION();
11:14:48.184 [DefaultDispatcher-worker-1 @coroutine#3] DEBUG com.zaxxer.hikari.pool.PoolBase - HikariPool-1 - Reset (autoCommit) on connection org.postgresql.jdbc.PgConnection@9661d0
11:14:48.854 [DefaultDispatcher-worker-1 @coroutine#4] DEBUG Exposed - INSERT INTO random ("text") VALUES ('604d88d9-190f-4132-b08c-20d68c4cc7b0')
11:14:48.862 [DefaultDispatcher-worker-1 @coroutine#4] DEBUG com.zaxxer.hikari.pool.PoolBase - HikariPool-1 - Reset (autoCommit) on connection org.postgresql.jdbc.PgConnection@9661d0
11:15:17.258 [HikariPool-1 connection closer] DEBUG com.zaxxer.hikari.pool.PoolBase - HikariPool-1 - Closing connection org.postgresql.jdbc.PgConnection@4012d307: (connection has passed maxLifetime)
11:15:17.272 [HikariPool-1 connection adder] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Added connection org.postgresql.jdbc.PgConnection@3c258b8e
11:15:17.488 [HikariPool-1 connection closer] DEBUG com.zaxxer.hikari.pool.PoolBase - HikariPool-1 - Closing connection org.postgresql.jdbc.PgConnection@4ed52575: (connection has passed maxLifetime)
11:15:17.501 [HikariPool-1 connection adder] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Added connection org.postgresql.jdbc.PgConnection@32e2627a
11:15:17.509 [HikariPool-1 housekeeper] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Add connection elided, waiting=0, adders pending/running=2
11:15:17.509 [HikariPool-1 connection closer] DEBUG com.zaxxer.hikari.pool.PoolBase - HikariPool-1 - Closing connection org.postgresql.jdbc.PgConnection@9661d0: (connection has passed maxLifetime)
11:15:17.526 [HikariPool-1 connection adder] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Added connection org.postgresql.jdbc.PgConnection@36dde0fe
11:15:17.696 [HikariPool-1 connection closer] DEBUG com.zaxxer.hikari.pool.PoolBase - HikariPool-1 - Closing connection org.postgresql.jdbc.PgConnection@4aeab877: (connection has passed maxLifetime)
11:15:17.710 [HikariPool-1 connection adder] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Added connection org.postgresql.jdbc.PgConnection@73df6841
11:15:17.794 [HikariPool-1 housekeeper] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Pool stats (total=5, active=0, idle=5, waiting=0)
11:15:17.794 [HikariPool-1 housekeeper] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Fill pool skipped, pool has sufficient level or currently being filled (queueDepth=0).
11:15:17.849 [HikariPool-1 connection closer] DEBUG com.zaxxer.hikari.pool.PoolBase - HikariPool-1 - Closing connection org.postgresql.jdbc.PgConnection@fe82f4e: (connection has passed maxLifetime)
11:15:17.863 [HikariPool-1 connection adder] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Added connection org.postgresql.jdbc.PgConnection@6336e44f
11:15:19.875 [DefaultDispatcher-worker-1 @coroutine#5] DEBUG Exposed - SELECT VERSION();
11:15:19.877 [DefaultDispatcher-worker-1 @coroutine#5] DEBUG com.zaxxer.hikari.pool.PoolBase - HikariPool-1 - Reset (autoCommit) on connection org.postgresql.jdbc.PgConnection@3c258b8e
11:15:19.881 [DefaultDispatcher-worker-1 @coroutine#6] DEBUG Exposed - INSERT INTO random ("text") VALUES ('d306055b-4dc3-4de5-9a8a-6fb380b1479b')
11:15:19.884 [DefaultDispatcher-worker-1 @coroutine#6] DEBUG com.zaxxer.hikari.pool.PoolBase - HikariPool-1 - Reset (autoCommit) on connection org.postgresql.jdbc.PgConnection@3c258b8e
[0K[1mcom.example.exposed.transaction.configCase.DatabaseTestConfigCase2[m
[0K[1m  Test [22mCreate a new Connection after it was closed, PSQLException()[32m PASSED[31m (32.6s)[m
[0K[1;32mSUCCESS: [39mExecuted 1 tests in 34.1s[m
BUILD SUCCESSFUL in 34s
5 actionable tasks: 2 executed, 3 up-to-date
11:15:20: Execution finished ':test --tests "com.example.exposed.transaction.configCase.DatabaseTestConfigCase2.Create a new Connection after it was closed, PSQLException"'.
 */
