package io.gitlab.arturbosch.detekt.core

import java.io.Closeable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * An [ExecutorService] with auto close capabilities for non user managed thread pools.
 */
class TaskPool private constructor(
    private val service: ExecutorService,
    private val shouldClose: Boolean
) : ExecutorService by service, AutoCloseable, Closeable {

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
