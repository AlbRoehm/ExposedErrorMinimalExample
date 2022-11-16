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
class DatabaseTestConfigCase3 {

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

    // CASE 3 Setting schema only in hikari and using different transaction_level for creating table and writing to it.
    // Outcome: schema of hikari config is ignored, table is created in public schema, error is thrown after timeout

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
                transaction {
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
            const val DATABASE_URL = "jdbc:postgresql://localhost:5432/hg_kotlin_test"
        }
    }
}

/*
11:28:57.442 [Coroutine thread @coroutine#2] INFO com.zaxxer.hikari.HikariDataSource - HikariPool-1 - Starting...
11:28:57.671 [Coroutine thread @coroutine#2] INFO com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Added connection org.postgresql.jdbc.PgConnection@4cdf776c
11:28:57.674 [Coroutine thread @coroutine#2] INFO com.zaxxer.hikari.HikariDataSource - HikariPool-1 - Start completed.
11:28:57.768 [Coroutine thread @coroutine#2] DEBUG com.zaxxer.hikari.pool.ProxyConnection - HikariPool-1 - Executed rollback on connection org.postgresql.jdbc.PgConnection@4cdf776c due to dirty commit state on close().
11:28:57.776 [HikariPool-1 housekeeper] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Pool stats (total=1, active=0, idle=1, waiting=0)
11:28:57.793 [HikariPool-1 connection adder] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Added connection org.postgresql.jdbc.PgConnection@24f1e522
11:28:57.817 [HikariPool-1 connection adder] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Added connection org.postgresql.jdbc.PgConnection@2a361fcc
11:28:57.843 [HikariPool-1 connection adder] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Added connection org.postgresql.jdbc.PgConnection@2e098e52
11:28:57.868 [HikariPool-1 connection adder] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Added connection org.postgresql.jdbc.PgConnection@6b0b15f7
11:28:57.878 [HikariPool-1 connection adder] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - After adding stats (total=5, active=0, idle=5, waiting=0)
11:28:58.122 [Coroutine thread @coroutine#2] DEBUG com.zaxxer.hikari.pool.PoolBase - HikariPool-1 - Reset (isolation) on connection org.postgresql.jdbc.PgConnection@4cdf776c
11:28:58.176 [DefaultDispatcher-worker-1 @coroutine#3] DEBUG Exposed - SELECT VERSION();
11:28:58.861 [DefaultDispatcher-worker-1 @coroutine#4] DEBUG Exposed - INSERT INTO random ("text") VALUES ('c4ee5630-a465-42ac-8644-28c72ae394e2')
11:29:26.987 [HikariPool-1 connection closer] DEBUG com.zaxxer.hikari.pool.PoolBase - HikariPool-1 - Closing connection org.postgresql.jdbc.PgConnection@4cdf776c: (connection has passed maxLifetime)
11:29:27.001 [HikariPool-1 connection adder] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Added connection org.postgresql.jdbc.PgConnection@66741d86
11:29:27.087 [HikariPool-1 connection closer] DEBUG com.zaxxer.hikari.pool.PoolBase - HikariPool-1 - Closing connection org.postgresql.jdbc.PgConnection@24f1e522: (connection has passed maxLifetime)
11:29:27.101 [HikariPool-1 connection adder] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Added connection org.postgresql.jdbc.PgConnection@68b3ff38
11:29:27.779 [HikariPool-1 housekeeper] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Pool stats (total=5, active=0, idle=5, waiting=0)
11:29:27.779 [HikariPool-1 housekeeper] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Fill pool skipped, pool has sufficient level or currently being filled (queueDepth=0).
11:29:27.820 [HikariPool-1 connection closer] DEBUG com.zaxxer.hikari.pool.PoolBase - HikariPool-1 - Closing connection org.postgresql.jdbc.PgConnection@2e098e52: (connection has passed maxLifetime)
11:29:27.835 [HikariPool-1 connection adder] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Added connection org.postgresql.jdbc.PgConnection@6c8c76f6
11:29:27.837 [HikariPool-1 housekeeper] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Add connection elided, waiting=0, adders pending/running=2
11:29:27.837 [HikariPool-1 connection closer] DEBUG com.zaxxer.hikari.pool.PoolBase - HikariPool-1 - Closing connection org.postgresql.jdbc.PgConnection@2a361fcc: (connection has passed maxLifetime)
11:29:27.846 [HikariPool-1 connection closer] DEBUG com.zaxxer.hikari.pool.PoolBase - HikariPool-1 - Closing connection org.postgresql.jdbc.PgConnection@6b0b15f7: (connection has passed maxLifetime)
11:29:27.846 [HikariPool-1 housekeeper] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Add connection elided, waiting=0, adders pending/running=3
11:29:27.858 [HikariPool-1 connection adder] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Added connection org.postgresql.jdbc.PgConnection@298ef503
11:29:27.881 [HikariPool-1 connection adder] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Added connection org.postgresql.jdbc.PgConnection@6f504599
11:29:29.890 [DefaultDispatcher-worker-1 @coroutine#5] DEBUG Exposed - SELECT VERSION();
11:29:29.890 [DefaultDispatcher-worker-1 @coroutine#5] WARN Exposed - Transaction attempt #1 failed: org.postgresql.util.PSQLException: Cannot change transaction isolation level in the middle of a transaction.. Statement(s): SELECT VERSION();
org.jetbrains.exposed.exceptions.ExposedSQLException: org.postgresql.util.PSQLException: Cannot change transaction isolation level in the middle of a transaction.
	at org.jetbrains.exposed.sql.statements.Statement.executeIn$exposed_core(Statement.kt:49)
	at org.jetbrains.exposed.sql.Transaction.exec(Transaction.kt:141)
	at org.jetbrains.exposed.sql.Transaction.exec(Transaction.kt:127)
	at org.jetbrains.exposed.sql.Transaction.exec(Transaction.kt:109)
	at org.jetbrains.exposed.sql.Transaction.exec$default(Transaction.kt:97)
	at com.example.exposed.transaction.configCase.DatabaseTestConfigCase3$getPSQLVersion$2.invokeSuspend(DatabaseTestConfigCase3.kt:62)
	at com.example.exposed.transaction.configCase.DatabaseTestConfigCase3$getPSQLVersion$2.invoke(DatabaseTestConfigCase3.kt)
	at com.example.exposed.transaction.configCase.DatabaseTestConfigCase3$getPSQLVersion$2.invoke(DatabaseTestConfigCase3.kt)
	at com.example.exposed.transaction.configCase.DatabaseTestConfigCase3$dbQuery$2.invokeSuspend(DatabaseTestConfigCase3.kt:74)
	at com.example.exposed.transaction.configCase.DatabaseTestConfigCase3$dbQuery$2.invoke(DatabaseTestConfigCase3.kt)
	at com.example.exposed.transaction.configCase.DatabaseTestConfigCase3$dbQuery$2.invoke(DatabaseTestConfigCase3.kt)
	at org.jetbrains.exposed.sql.transactions.experimental.SuspendedKt$suspendedTransactionAsyncInternal$1.invokeSuspend(Suspended.kt:129)
	at kotlin.coroutines.jvm.internal.BaseContinuationImpl.resumeWith(ContinuationImpl.kt:33)
	at kotlinx.coroutines.DispatchedTask.run(DispatchedTask.kt:106)
	at kotlinx.coroutines.internal.LimitedDispatcher.run(LimitedDispatcher.kt:42)
	at kotlinx.coroutines.scheduling.TaskImpl.run(Tasks.kt:95)
	at kotlinx.coroutines.scheduling.CoroutineScheduler.runSafely(CoroutineScheduler.kt:570)
	at kotlinx.coroutines.scheduling.CoroutineScheduler$Worker.executeTask(CoroutineScheduler.kt:750)
	at kotlinx.coroutines.scheduling.CoroutineScheduler$Worker.runWorker(CoroutineScheduler.kt:677)
	at kotlinx.coroutines.scheduling.CoroutineScheduler$Worker.run(CoroutineScheduler.kt:664)
Caused by: org.postgresql.util.PSQLException: Cannot change transaction isolation level in the middle of a transaction.
	at org.postgresql.jdbc.PgConnection.setTransactionIsolation(PgConnection.java:970)
	at com.zaxxer.hikari.pool.ProxyConnection.setTransactionIsolation(ProxyConnection.java:420)
	at com.zaxxer.hikari.pool.HikariProxyConnection.setTransactionIsolation(HikariProxyConnection.java)
	at org.jetbrains.exposed.sql.statements.jdbc.JdbcConnectionImpl.setTransactionIsolation(JdbcConnectionImpl.kt:60)
	at org.jetbrains.exposed.sql.transactions.ThreadLocalTransactionManager$ThreadLocalTransaction$connectionLazy$1.invoke(ThreadLocalTransactionManager.kt:82)
	at org.jetbrains.exposed.sql.transactions.ThreadLocalTransactionManager$ThreadLocalTransaction$connectionLazy$1.invoke(ThreadLocalTransactionManager.kt:75)
	at kotlin.UnsafeLazyImpl.getValue(Lazy.kt:81)
	at org.jetbrains.exposed.sql.transactions.ThreadLocalTransactionManager$ThreadLocalTransaction.getConnection(ThreadLocalTransactionManager.kt:89)
	at org.jetbrains.exposed.sql.Transaction.getConnection(Transaction.kt)
	at org.jetbrains.exposed.sql.statements.Statement.prepared(Statement.kt:24)
	at org.jetbrains.exposed.sql.statements.Statement.executeIn$exposed_core(Statement.kt:47)
	... 19 common frames omitted

org.postgresql.util.PSQLException: Cannot change transaction isolation level in the middle of a transaction.
org.jetbrains.exposed.exceptions.ExposedSQLException: org.postgresql.util.PSQLException: Cannot change transaction isolation level in the middle of a transaction.
SQL: [SELECT VERSION();]
	at app//org.jetbrains.exposed.sql.statements.Statement.executeIn$exposed_core(Statement.kt:49)
	at app//org.jetbrains.exposed.sql.Transaction.exec(Transaction.kt:141)
	at app//org.jetbrains.exposed.sql.Transaction.exec(Transaction.kt:127)
	at app//org.jetbrains.exposed.sql.Transaction.exec(Transaction.kt:109)
	at app//org.jetbrains.exposed.sql.Transaction.exec$default(Transaction.kt:97)
	at app//com.example.exposed.transaction.configCase.DatabaseTestConfigCase3$getPSQLVersion$2.invokeSuspend(DatabaseTestConfigCase3.kt:62)
	at app//com.example.exposed.transaction.configCase.DatabaseTestConfigCase3$getPSQLVersion$2.invoke(DatabaseTestConfigCase3.kt)
	at app//com.example.exposed.transaction.configCase.DatabaseTestConfigCase3$getPSQLVersion$2.invoke(DatabaseTestConfigCase3.kt)
	at app//com.example.exposed.transaction.configCase.DatabaseTestConfigCase3$dbQuery$2.invokeSuspend(DatabaseTestConfigCase3.kt:74)
	at app//com.example.exposed.transaction.configCase.DatabaseTestConfigCase3$dbQuery$2.invoke(DatabaseTestConfigCase3.kt)
	at app//com.example.exposed.transaction.configCase.DatabaseTestConfigCase3$dbQuery$2.invoke(DatabaseTestConfigCase3.kt)
	at app//org.jetbrains.exposed.sql.transactions.experimental.SuspendedKt$suspendedTransactionAsyncInternal$1.invokeSuspend(Suspended.kt:129)
	at app//kotlin.coroutines.jvm.internal.BaseContinuationImpl.resumeWith(ContinuationImpl.kt:33)
	at app//kotlinx.coroutines.DispatchedTask.run(DispatchedTask.kt:106)
	at app//kotlinx.coroutines.internal.LimitedDispatcher.run(LimitedDispatcher.kt:42)
	at app//kotlinx.coroutines.scheduling.TaskImpl.run(Tasks.kt:95)
	at app//kotlinx.coroutines.scheduling.CoroutineScheduler.runSafely(CoroutineScheduler.kt:570)
	at app//kotlinx.coroutines.scheduling.CoroutineScheduler$Worker.executeTask(CoroutineScheduler.kt:750)
	at app//kotlinx.coroutines.scheduling.CoroutineScheduler$Worker.runWorker(CoroutineScheduler.kt:677)
	at app//kotlinx.coroutines.scheduling.CoroutineScheduler$Worker.run(CoroutineScheduler.kt:664)
Caused by: org.postgresql.util.PSQLException: Cannot change transaction isolation level in the middle of a transaction.
	at app//org.postgresql.jdbc.PgConnection.setTransactionIsolation(PgConnection.java:970)
	at app//com.zaxxer.hikari.pool.ProxyConnection.setTransactionIsolation(ProxyConnection.java:420)
	at app//com.zaxxer.hikari.pool.HikariProxyConnection.setTransactionIsolation(HikariProxyConnection.java)
	at app//org.jetbrains.exposed.sql.statements.jdbc.JdbcConnectionImpl.setTransactionIsolation(JdbcConnectionImpl.kt:60)
	at app//org.jetbrains.exposed.sql.transactions.ThreadLocalTransactionManager$ThreadLocalTransaction$connectionLazy$1.invoke(ThreadLocalTransactionManager.kt:82)
	at app//org.jetbrains.exposed.sql.transactions.ThreadLocalTransactionManager$ThreadLocalTransaction$connectionLazy$1.invoke(ThreadLocalTransactionManager.kt:75)
	at app//kotlin.UnsafeLazyImpl.getValue(Lazy.kt:81)
	at app//org.jetbrains.exposed.sql.transactions.ThreadLocalTransactionManager$ThreadLocalTransaction.getConnection(ThreadLocalTransactionManager.kt:89)
	at app//org.jetbrains.exposed.sql.Transaction.getConnection(Transaction.kt)
	at app//org.jetbrains.exposed.sql.statements.Statement.prepared(Statement.kt:24)
	at app//org.jetbrains.exposed.sql.statements.Statement.executeIn$exposed_core(Statement.kt:47)
	... 19 more


 */
