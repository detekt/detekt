package io.gitlab.arturbosch.detekt.core

import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.function.Supplier

/**
 * @author Artur Bosch
 */

fun <T> withExecutor(block: Executor.() -> T): T {
	val executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())
	return block.invoke(executor).apply {
		executor.shutdown()
	}
}

fun <T> task(executor: Executor, task: () -> T): CompletableFuture<T> {
	return CompletableFuture.supplyAsync(Supplier { task() }, executor)
}

fun <T> awaitAll(futures: List<CompletableFuture<T>>): List<T> {
	CompletableFuture.allOf(*futures.toTypedArray()).join()
	return futures.map { it.get() }
}