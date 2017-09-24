package io.gitlab.arturbosch.detekt.watchservice

import java.nio.file.Path
import java.nio.file.WatchService
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

/**
 * @author Artur Bosch
 */
fun startWatching(watchService: WatchService, detektService: DetektService) {
	while (true) {
		val watchKey = watchService.poll(1, TimeUnit.SECONDS)
		if (watchKey != null) {

			val events = watchKey.pollEvents()
					.map { PathEvent(it.context() as Path, it.kind()) }
			val watchedPath = watchKey.watchable() as Path
			val watchedDir = WatchedDir(watchKey.isValid, watchedPath, events)

			CompletableFuture.supplyAsync { detektService.check(watchedDir) }
			watchKey.reset()
		}
	}
}
