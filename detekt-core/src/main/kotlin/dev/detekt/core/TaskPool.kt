package dev.detekt.core

import java.io.Closeable
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

typealias Task<T> = CompletableFuture<T>
typealias TaskList<T> = List<CompletableFuture<T>>

fun <T> TaskPool.task(block: () -> T): Task<T> = CompletableFuture.supplyAsync({ block() }, this)

fun <T> Task<T>.recover(block: (Throwable) -> T?): Task<T?> = this.exceptionally(block)

fun <T> awaitAll(tasks: TaskList<T>) = tasks.map { it.join() }

/**
 * An [ExecutorService] with auto close capabilities for non user managed thread pools.
 */
class TaskPool private constructor(private val service: ExecutorService, private val shouldClose: Boolean) :
    ExecutorService by service,
    AutoCloseable,
    Closeable {

    constructor(executorService: ExecutorService?) : this(
        executorService ?: Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()),
        executorService == null
    )

    override fun close() {
        if (shouldClose) {
            service.shutdown()
        }
    }
}
