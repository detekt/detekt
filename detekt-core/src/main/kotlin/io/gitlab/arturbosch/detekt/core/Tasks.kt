package io.gitlab.arturbosch.detekt.core

import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor
import java.util.concurrent.ForkJoinPool
import java.util.function.Supplier

/**
 * @author Artur Bosch
 */

fun <T> withExecutor(executor: Executor? = null, block: Executor.() -> T): T {
	if (executor == null) {
		val defaultExecutor = ForkJoinPool.commonPool()
		return block.invoke(defaultExecutor).apply {
			defaultExecutor.shutdown()
		}
	}
	return block.invoke(executor)
}

fun <T> Executor.runAsync(block: () -> T): CompletableFuture<T> {
	return task(this) { block() }
}

fun <T> task(executor: Executor, task: () -> T): CompletableFuture<T> {
	return CompletableFuture.supplyAsync(Supplier { task() }, executor)
}

fun <T> awaitAll(futures: List<CompletableFuture<T>>) = futures.map { it.join() }
