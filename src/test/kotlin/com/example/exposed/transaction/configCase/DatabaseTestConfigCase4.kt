package com.example.exposed.transaction.configCase

import com.example.exposed.transaction.*
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
class DatabaseTestConfigCase4 {

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

    // CASE 4 Setting schema only in the url
    // Outcome: no errors, no timeout, no reset

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

        companion object {
            const val DATABASE_USER = "hg_kotlin"
            const val DATABASE_SCHEMA = "test_schema"
            const val DATABASE_PASSWORD = "super_secret"
            const val DATABASE_URL = "jdbc:postgresql://localhost:5432/hg_kotlin_test?currentSchema=$DATABASE_SCHEMA"
        }
    }
}
