package io.gitlab.arturbosch.detekt.core

import java.util.concurrent.CompletableFuture

/**
 * @author Artur Bosch
 */

fun <T> task(task: () -> T): CompletableFuture<T> {
	return CompletableFuture.supplyAsync { task() }
}

fun <T> awaitAll(futures: List<CompletableFuture<T>>): List<T> {
	CompletableFuture.allOf(*futures.toTypedArray()).join()
	return futures.map { it.get() }
}