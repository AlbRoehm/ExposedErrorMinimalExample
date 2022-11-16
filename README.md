Minimal example for better discussion of this issue https://github.com/JetBrains/Exposed/issues/1575

Java: `temurin-11.0.17`

DBMS: PostgreSQL (ver. 13.3 (Debian 13.3-1.pgdg100+1))


Output of DatabaseTest.`Create a new Connection after it was closed, PSQLException`()

```
> Task :wrapper
BUILD SUCCESSFUL in 1s
1 actionable task: 1 executed
> Task :processResources NO-SOURCE
> Task :processTestResources NO-SOURCE
> Task :compileKotlin:;
> Task :compileJava NO-SOURCE
> Task :classes UP-TO-DATE
> Task :jar UP-TO-DATE
> Task :inspectClassesForKotlinIC UP-TO-DATE
> Task :compileTestKotlin
> Task :compileTestJava NO-SOURCE
> Task :testClasses UP-TO-DATE
> Task :test
09:42:54.483 [Coroutine thread @coroutine#2] DEBUG com.zaxxer.hikari.HikariConfig - Driver class org.postgresql.Driver found in Thread context class loader jdk.internal.loader.ClassLoaders$AppClassLoader@7ad041f3
09:42:54.499 [Coroutine thread @coroutine#2] DEBUG com.zaxxer.hikari.HikariConfig - HikariPool-1 - configuration:
09:42:54.509 [Coroutine thread @coroutine#2] DEBUG com.zaxxer.hikari.HikariConfig - allowPoolSuspension.............false
09:42:54.510 [Coroutine thread @coroutine#2] DEBUG com.zaxxer.hikari.HikariConfig - autoCommit......................false
09:42:54.510 [Coroutine thread @coroutine#2] DEBUG com.zaxxer.hikari.HikariConfig - catalog.........................none
09:42:54.510 [Coroutine thread @coroutine#2] DEBUG com.zaxxer.hikari.HikariConfig - connectionInitSql...............none
09:42:54.511 [Coroutine thread @coroutine#2] DEBUG com.zaxxer.hikari.HikariConfig - connectionTestQuery.............none
09:42:54.511 [Coroutine thread @coroutine#2] DEBUG com.zaxxer.hikari.HikariConfig - connectionTimeout...............30000
09:42:54.511 [Coroutine thread @coroutine#2] DEBUG com.zaxxer.hikari.HikariConfig - dataSource......................none
09:42:54.511 [Coroutine thread @coroutine#2] DEBUG com.zaxxer.hikari.HikariConfig - dataSourceClassName.............none
09:42:54.511 [Coroutine thread @coroutine#2] DEBUG com.zaxxer.hikari.HikariConfig - dataSourceJNDI..................none
09:42:54.512 [Coroutine thread @coroutine#2] DEBUG com.zaxxer.hikari.HikariConfig - dataSourceProperties............{password=<masked>}
09:42:54.512 [Coroutine thread @coroutine#2] DEBUG com.zaxxer.hikari.HikariConfig - driverClassName................."org.postgresql.Driver"
09:42:54.512 [Coroutine thread @coroutine#2] DEBUG com.zaxxer.hikari.HikariConfig - exceptionOverrideClassName......none
09:42:54.513 [Coroutine thread @coroutine#2] DEBUG com.zaxxer.hikari.HikariConfig - healthCheckProperties...........{}
09:42:54.513 [Coroutine thread @coroutine#2] DEBUG com.zaxxer.hikari.HikariConfig - healthCheckRegistry.............none
09:42:54.513 [Coroutine thread @coroutine#2] DEBUG com.zaxxer.hikari.HikariConfig - idleTimeout.....................600000
09:42:54.513 [Coroutine thread @coroutine#2] DEBUG com.zaxxer.hikari.HikariConfig - initializationFailTimeout.......1
09:42:54.513 [Coroutine thread @coroutine#2] DEBUG com.zaxxer.hikari.HikariConfig - isolateInternalQueries..........false
09:42:54.513 [Coroutine thread @coroutine#2] DEBUG com.zaxxer.hikari.HikariConfig - jdbcUrl.........................jdbc:postgresql://localhost:5432/hg_kotlin_test
09:42:54.514 [Coroutine thread @coroutine#2] DEBUG com.zaxxer.hikari.HikariConfig - keepaliveTime...................0
09:42:54.514 [Coroutine thread @coroutine#2] DEBUG com.zaxxer.hikari.HikariConfig - leakDetectionThreshold..........0
09:42:54.514 [Coroutine thread @coroutine#2] DEBUG com.zaxxer.hikari.HikariConfig - maxLifetime.....................30020
09:42:54.514 [Coroutine thread @coroutine#2] DEBUG com.zaxxer.hikari.HikariConfig - maximumPoolSize.................5
09:42:54.515 [Coroutine thread @coroutine#2] DEBUG com.zaxxer.hikari.HikariConfig - metricRegistry..................none
09:42:54.515 [Coroutine thread @coroutine#2] DEBUG com.zaxxer.hikari.HikariConfig - metricsTrackerFactory...........none
09:42:54.515 [Coroutine thread @coroutine#2] DEBUG com.zaxxer.hikari.HikariConfig - minimumIdle.....................5
09:42:54.515 [Coroutine thread @coroutine#2] DEBUG com.zaxxer.hikari.HikariConfig - password........................<masked>
09:42:54.515 [Coroutine thread @coroutine#2] DEBUG com.zaxxer.hikari.HikariConfig - poolName........................"HikariPool-1"
09:42:54.516 [Coroutine thread @coroutine#2] DEBUG com.zaxxer.hikari.HikariConfig - readOnly........................false
09:42:54.516 [Coroutine thread @coroutine#2] DEBUG com.zaxxer.hikari.HikariConfig - registerMbeans..................false
09:42:54.516 [Coroutine thread @coroutine#2] DEBUG com.zaxxer.hikari.HikariConfig - scheduledExecutor...............none
09:42:54.516 [Coroutine thread @coroutine#2] DEBUG com.zaxxer.hikari.HikariConfig - schema.........................."test_schema"
09:42:54.516 [Coroutine thread @coroutine#2] DEBUG com.zaxxer.hikari.HikariConfig - threadFactory...................internal
09:42:54.516 [Coroutine thread @coroutine#2] DEBUG com.zaxxer.hikari.HikariConfig - transactionIsolation............"TRANSACTION_SERIALIZABLE"
09:42:54.516 [Coroutine thread @coroutine#2] DEBUG com.zaxxer.hikari.HikariConfig - username........................"hg_kotlin"
09:42:54.516 [Coroutine thread @coroutine#2] DEBUG com.zaxxer.hikari.HikariConfig - validationTimeout...............5000
09:42:54.519 [Coroutine thread @coroutine#2] DEBUG com.zaxxer.hikari.HikariConfig - HikariPool-1 - configuration:
09:42:54.521 [Coroutine thread @coroutine#2] DEBUG com.zaxxer.hikari.HikariConfig - allowPoolSuspension.............false
09:42:54.521 [Coroutine thread @coroutine#2] DEBUG com.zaxxer.hikari.HikariConfig - autoCommit......................false
09:42:54.521 [Coroutine thread @coroutine#2] DEBUG com.zaxxer.hikari.HikariConfig - catalog.........................none
09:42:54.521 [Coroutine thread @coroutine#2] DEBUG com.zaxxer.hikari.HikariConfig - connectionInitSql...............none
09:42:54.521 [Coroutine thread @coroutine#2] DEBUG com.zaxxer.hikari.HikariConfig - connectionTestQuery.............none
09:42:54.521 [Coroutine thread @coroutine#2] DEBUG com.zaxxer.hikari.HikariConfig - connectionTimeout...............30000
09:42:54.522 [Coroutine thread @coroutine#2] DEBUG com.zaxxer.hikari.HikariConfig - dataSource......................none
09:42:54.522 [Coroutine thread @coroutine#2] DEBUG com.zaxxer.hikari.HikariConfig - dataSourceClassName.............none
09:42:54.522 [Coroutine thread @coroutine#2] DEBUG com.zaxxer.hikari.HikariConfig - dataSourceJNDI..................none
09:42:54.522 [Coroutine thread @coroutine#2] DEBUG com.zaxxer.hikari.HikariConfig - dataSourceProperties............{password=<masked>}
09:42:54.523 [Coroutine thread @coroutine#2] DEBUG com.zaxxer.hikari.HikariConfig - driverClassName................."org.postgresql.Driver"
09:42:54.523 [Coroutine thread @coroutine#2] DEBUG com.zaxxer.hikari.HikariConfig - exceptionOverrideClassName......none
09:42:54.523 [Coroutine thread @coroutine#2] DEBUG com.zaxxer.hikari.HikariConfig - healthCheckProperties...........{}
09:42:54.523 [Coroutine thread @coroutine#2] DEBUG com.zaxxer.hikari.HikariConfig - healthCheckRegistry.............none
09:42:54.524 [Coroutine thread @coroutine#2] DEBUG com.zaxxer.hikari.HikariConfig - idleTimeout.....................600000
09:42:54.524 [Coroutine thread @coroutine#2] DEBUG com.zaxxer.hikari.HikariConfig - initializationFailTimeout.......1
09:42:54.524 [Coroutine thread @coroutine#2] DEBUG com.zaxxer.hikari.HikariConfig - isolateInternalQueries..........false
09:42:54.524 [Coroutine thread @coroutine#2] DEBUG com.zaxxer.hikari.HikariConfig - jdbcUrl.........................jdbc:postgresql://localhost:5432/hg_kotlin_test
09:42:54.525 [Coroutine thread @coroutine#2] DEBUG com.zaxxer.hikari.HikariConfig - keepaliveTime...................0
09:42:54.525 [Coroutine thread @coroutine#2] DEBUG com.zaxxer.hikari.HikariConfig - leakDetectionThreshold..........0
09:42:54.525 [Coroutine thread @coroutine#2] DEBUG com.zaxxer.hikari.HikariConfig - maxLifetime.....................30020
09:42:54.525 [Coroutine thread @coroutine#2] DEBUG com.zaxxer.hikari.HikariConfig - maximumPoolSize.................5
09:42:54.525 [Coroutine thread @coroutine#2] DEBUG com.zaxxer.hikari.HikariConfig - metricRegistry..................none
09:42:54.526 [Coroutine thread @coroutine#2] DEBUG com.zaxxer.hikari.HikariConfig - metricsTrackerFactory...........none
09:42:54.526 [Coroutine thread @coroutine#2] DEBUG com.zaxxer.hikari.HikariConfig - minimumIdle.....................5
09:42:54.526 [Coroutine thread @coroutine#2] DEBUG com.zaxxer.hikari.HikariConfig - password........................<masked>
09:42:54.526 [Coroutine thread @coroutine#2] DEBUG com.zaxxer.hikari.HikariConfig - poolName........................"HikariPool-1"
09:42:54.526 [Coroutine thread @coroutine#2] DEBUG com.zaxxer.hikari.HikariConfig - readOnly........................false
09:42:54.526 [Coroutine thread @coroutine#2] DEBUG com.zaxxer.hikari.HikariConfig - registerMbeans..................false
09:42:54.527 [Coroutine thread @coroutine#2] DEBUG com.zaxxer.hikari.HikariConfig - scheduledExecutor...............none
09:42:54.527 [Coroutine thread @coroutine#2] DEBUG com.zaxxer.hikari.HikariConfig - schema.........................."test_schema"
09:42:54.527 [Coroutine thread @coroutine#2] DEBUG com.zaxxer.hikari.HikariConfig - threadFactory...................internal
09:42:54.527 [Coroutine thread @coroutine#2] DEBUG com.zaxxer.hikari.HikariConfig - transactionIsolation............"TRANSACTION_SERIALIZABLE"
09:42:54.527 [Coroutine thread @coroutine#2] DEBUG com.zaxxer.hikari.HikariConfig - username........................"hg_kotlin"
09:42:54.527 [Coroutine thread @coroutine#2] DEBUG com.zaxxer.hikari.HikariConfig - validationTimeout...............5000
09:42:54.529 [Coroutine thread @coroutine#2] INFO com.zaxxer.hikari.HikariDataSource - HikariPool-1 - Starting...
09:42:54.806 [Coroutine thread @coroutine#2] INFO com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Added connection org.postgresql.jdbc.PgConnection@65d3c064
09:42:54.810 [Coroutine thread @coroutine#2] INFO com.zaxxer.hikari.HikariDataSource - HikariPool-1 - Start completed.
09:42:54.916 [HikariPool-1 housekeeper] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Pool stats (total=1, active=1, idle=0, waiting=0)
09:42:54.919 [Coroutine thread @coroutine#2] DEBUG com.zaxxer.hikari.pool.ProxyConnection - HikariPool-1 - Executed rollback on connection org.postgresql.jdbc.PgConnection@65d3c064 due to dirty commit state on close().
09:42:54.934 [HikariPool-1 connection adder] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Added connection org.postgresql.jdbc.PgConnection@1a31af1a
09:42:54.960 [HikariPool-1 connection adder] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Added connection org.postgresql.jdbc.PgConnection@14f6422c
09:42:54.986 [HikariPool-1 connection adder] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Added connection org.postgresql.jdbc.PgConnection@10c9c46a
09:42:55.010 [HikariPool-1 connection adder] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Added connection org.postgresql.jdbc.PgConnection@55cb4817
09:42:55.022 [HikariPool-1 connection adder] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - After adding stats (total=5, active=0, idle=5, waiting=0)
09:42:55.206 [Coroutine thread @coroutine#2] DEBUG Exposed - SET search_path TO test_schema
09:42:55.253 [Coroutine thread @coroutine#2] DEBUG com.zaxxer.hikari.pool.PoolBase - HikariPool-1 - Reset (isolation) on connection org.postgresql.jdbc.PgConnection@65d3c064
09:42:55.979 [DefaultDispatcher-worker-1 @coroutine#3] DEBUG Exposed - INSERT INTO random ("text") VALUES ('3b0dd146-2ce4-4ab5-a85d-ef2439a7a445')
09:43:24.113 [HikariPool-1 connection closer] DEBUG com.zaxxer.hikari.pool.PoolBase - HikariPool-1 - Closing connection org.postgresql.jdbc.PgConnection@65d3c064: (connection has passed maxLifetime)
09:43:24.125 [HikariPool-1 connection adder] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Added connection org.postgresql.jdbc.PgConnection@5114e510
09:43:24.589 [HikariPool-1 connection closer] DEBUG com.zaxxer.hikari.pool.PoolBase - HikariPool-1 - Closing connection org.postgresql.jdbc.PgConnection@14f6422c: (connection has passed maxLifetime)
09:43:24.594 [HikariPool-1 housekeeper] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Add connection elided, waiting=0, adders pending/running=2
09:43:24.594 [HikariPool-1 connection closer] DEBUG com.zaxxer.hikari.pool.PoolBase - HikariPool-1 - Closing connection org.postgresql.jdbc.PgConnection@55cb4817: (connection has passed maxLifetime)
09:43:24.605 [HikariPool-1 connection adder] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Added connection org.postgresql.jdbc.PgConnection@5fd30142
09:43:24.630 [HikariPool-1 connection adder] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Added connection org.postgresql.jdbc.PgConnection@5bec8e60
09:43:24.644 [HikariPool-1 connection closer] DEBUG com.zaxxer.hikari.pool.PoolBase - HikariPool-1 - Closing connection org.postgresql.jdbc.PgConnection@10c9c46a: (connection has passed maxLifetime)
09:43:24.656 [HikariPool-1 connection adder] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Added connection org.postgresql.jdbc.PgConnection@1eee0b79
09:43:24.664 [HikariPool-1 housekeeper] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Add connection elided, waiting=0, adders pending/running=2
09:43:24.664 [HikariPool-1 connection closer] DEBUG com.zaxxer.hikari.pool.PoolBase - HikariPool-1 - Closing connection org.postgresql.jdbc.PgConnection@1a31af1a: (connection has passed maxLifetime)
09:43:24.680 [HikariPool-1 connection adder] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Added connection org.postgresql.jdbc.PgConnection@68a451d1
09:43:24.921 [HikariPool-1 housekeeper] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Pool stats (total=5, active=0, idle=5, waiting=0)
09:43:24.921 [HikariPool-1 housekeeper] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Fill pool skipped, pool has sufficient level or currently being filled (queueDepth=0).
09:43:27.001 [DefaultDispatcher-worker-1 @coroutine#4] DEBUG Exposed - INSERT INTO random ("text") VALUES ('2205f9c0-0bc7-4e96-954b-ccb9dc99899e')
09:43:27.001 [DefaultDispatcher-worker-1 @coroutine#4] WARN Exposed - Transaction attempt #1 failed: org.postgresql.util.PSQLException: Cannot change transaction isolation level in the middle of a transaction.. Statement(s): INSERT INTO random ("text") VALUES (?)
org.jetbrains.exposed.exceptions.ExposedSQLException: org.postgresql.util.PSQLException: Cannot change transaction isolation level in the middle of a transaction.
	at org.jetbrains.exposed.sql.statements.Statement.executeIn$exposed_core(Statement.kt:49)
	at org.jetbrains.exposed.sql.Transaction.exec(Transaction.kt:141)
	at org.jetbrains.exposed.sql.Transaction.exec(Transaction.kt:127)
	at org.jetbrains.exposed.sql.statements.Statement.execute(Statement.kt:28)
	at org.jetbrains.exposed.sql.QueriesKt.executeBatch(Queries.kt:177)
	at org.jetbrains.exposed.sql.QueriesKt.batchInsert(Queries.kt:106)
	at org.jetbrains.exposed.sql.QueriesKt.batchInsert(Queries.kt:92)
	at org.jetbrains.exposed.sql.QueriesKt.batchInsert$default(Queries.kt:87)
	at org.jetbrains.exposed.dao.EntityCache$flushInserts$ids$1.invoke(EntityCache.kt:184)
	at org.jetbrains.exposed.dao.EntityCache$flushInserts$ids$1.invoke(EntityCache.kt:183)
	at org.jetbrains.exposed.dao.EntityLifecycleInterceptorKt.executeAsPartOfEntityLifecycle(EntityLifecycleInterceptor.kt:18)
	at org.jetbrains.exposed.dao.EntityCache.flushInserts$exposed_dao(EntityCache.kt:183)
	at org.jetbrains.exposed.dao.EntityCache.flush(EntityCache.kt:138)
	at org.jetbrains.exposed.dao.EntityCache.flush(EntityCache.kt:107)
	at org.jetbrains.exposed.dao.EntityCacheKt.flushCache(EntityCache.kt:243)
	at org.jetbrains.exposed.dao.EntityLifecycleInterceptor.beforeCommit(EntityLifecycleInterceptor.kt:74)
	at org.jetbrains.exposed.sql.Transaction.commit(Transaction.kt:69)
	at org.jetbrains.exposed.sql.transactions.experimental.SuspendedKt$suspendedTransactionAsyncInternal$1.invokeSuspend(Suspended.kt:130)
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
	at org.jetbrains.exposed.sql.statements.InsertStatement.prepared(InsertStatement.kt:156)
	at org.jetbrains.exposed.sql.statements.BaseBatchInsertStatement.prepared(BaseBatchInsertStatement.kt:92)
	at org.jetbrains.exposed.sql.statements.Statement.executeIn$exposed_core(Statement.kt:47)
	... 25 common frames omitted

org.postgresql.util.PSQLException: Cannot change transaction isolation level in the middle of a transaction.
org.jetbrains.exposed.exceptions.ExposedSQLException: org.postgresql.util.PSQLException: Cannot change transaction isolation level in the middle of a transaction.
SQL: [Failed on expanding args for INSERT: org.jetbrains.exposed.sql.statements.BatchInsertStatement@7711a0fc]
	at app//org.jetbrains.exposed.sql.statements.Statement.executeIn$exposed_core(Statement.kt:49)
	at app//org.jetbrains.exposed.sql.Transaction.exec(Transaction.kt:141)
	at app//org.jetbrains.exposed.sql.Transaction.exec(Transaction.kt:127)
	at app//org.jetbrains.exposed.sql.statements.Statement.execute(Statement.kt:28)
	at app//org.jetbrains.exposed.sql.QueriesKt.executeBatch(Queries.kt:177)
	at app//org.jetbrains.exposed.sql.QueriesKt.batchInsert(Queries.kt:106)
	at app//org.jetbrains.exposed.sql.QueriesKt.batchInsert(Queries.kt:92)
	at app//org.jetbrains.exposed.sql.QueriesKt.batchInsert$default(Queries.kt:87)
	at app//org.jetbrains.exposed.dao.EntityCache$flushInserts$ids$1.invoke(EntityCache.kt:184)
	at app//org.jetbrains.exposed.dao.EntityCache$flushInserts$ids$1.invoke(EntityCache.kt:183)
	at app//org.jetbrains.exposed.dao.EntityLifecycleInterceptorKt.executeAsPartOfEntityLifecycle(EntityLifecycleInterceptor.kt:18)
	at app//org.jetbrains.exposed.dao.EntityCache.flushInserts$exposed_dao(EntityCache.kt:183)
	at app//org.jetbrains.exposed.dao.EntityCache.flush(EntityCache.kt:138)
	at app//org.jetbrains.exposed.dao.EntityCache.flush(EntityCache.kt:107)
	at app//org.jetbrains.exposed.dao.EntityCacheKt.flushCache(EntityCache.kt:243)
	at app//org.jetbrains.exposed.dao.EntityLifecycleInterceptor.beforeCommit(EntityLifecycleInterceptor.kt:74)
	at app//org.jetbrains.exposed.sql.Transaction.commit(Transaction.kt:69)
	at app//org.jetbrains.exposed.sql.transactions.experimental.SuspendedKt$suspendedTransactionAsyncInternal$1.invokeSuspend(Suspended.kt:130)
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
	at app//org.jetbrains.exposed.sql.statements.InsertStatement.prepared(InsertStatement.kt:156)
	at app//org.jetbrains.exposed.sql.statements.BaseBatchInsertStatement.prepared(BaseBatchInsertStatement.kt:92)
	at app//org.jetbrains.exposed.sql.statements.Statement.executeIn$exposed_core(Statement.kt:47)
	... 25 more


[0K[1mcom.example.exposed.transaction.DatabaseTest[m
[0K[1m  Test [22mCreate a new Connection after it was closed, PSQLException()[31m FAILED[31m (32.7s)[31m
  org.jetbrains.exposed.exceptions.ExposedSQLException: org.postgresql.util.PSQLException: Cannot change transaction isolation level in the middle of a transaction.
  SQL: [Failed on expanding args for INSERT: org.jetbrains.exposed.sql.statements.BatchInsertStatement@7711a0fc]
  Caused by: org.postgresql.util.PSQLException: Cannot change transaction isolation level in the middle of a transaction.
[m
[0K[1;31mFAILURE: [39mExecuted 1 tests in 34.2s (1 failed)[m
1 test completed, 1 failed
> Task :test FAILED
FAILURE: Build failed with an exception.
* What went wrong:
Execution failed for task ':test'.
> There were failing tests. See the report at: file:///Users/albrecht/workspace/sandbox/exposedTransactionError/build/reports/tests/test/index.html
* Try:
> Run with --stacktrace option to get the stack trace.
> Run with --info or --debug option to get more log output.
> Run with --scan to get full insights.
* Get more help at https://help.gradle.org
BUILD FAILED in 34s
5 actionable tasks: 3 executed, 2 up-to-date

```
