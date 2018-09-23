package io.gitlab.arturbosch.detekt.watcher.service

import io.gitlab.arturbosch.detekt.cli.console.FindingsReport
import io.gitlab.arturbosch.detekt.core.DetektFacade
import io.gitlab.arturbosch.detekt.core.KtCompiler
import io.gitlab.arturbosch.detekt.core.KtTreeCompiler
import io.gitlab.arturbosch.detekt.core.RuleSetLocator
import io.gitlab.arturbosch.detekt.core.isDirectory
import io.gitlab.arturbosch.detekt.core.isFile
import io.gitlab.arturbosch.detekt.watcher.config.DetektHome
import io.gitlab.arturbosch.detekt.watcher.config.Injekt
import io.gitlab.arturbosch.detekt.watcher.config.WATCHER_CHANGE_NOTIFICATION
import io.gitlab.arturbosch.detekt.watcher.state.State
import io.gitlab.arturbosch.kutils.get
import java.nio.file.Path

/**
 * @author Artur Bosch
 */
class DetektService(
		private val state: State = Injekt.get(),
		home: DetektHome = Injekt.get()
) {

	private val compiler by lazy { KtCompiler() }
	private val reporter = FindingsReport()

	private val printChangesNotification = home.property(WATCHER_CHANGE_NOTIFICATION)
			?.toBoolean() ?: true

	fun run(subPath: Path) {
		val settings = state.settings()
		val ktFiles = when {
			subPath.isFile() -> listOf(compiler.compile(subPath, subPath))
			subPath.isDirectory() -> with(settings) {
				KtTreeCompiler(compiler, pathFilters, parallelCompilation, debug = true)
						.compile(subPath)
			}
			else -> emptyList()
		}

		val detektor = DetektFacade.create(
				settings, RuleSetLocator(settings).load(), emptyList()
		)

		val result = detektor.run(state.project(), ktFiles)
		reporter.render(result)?.let { println(it) }
	}

	fun check(dir: WatchedDir) {
		val settings = state.settings()
		val detektor = DetektFacade.create(
				settings, RuleSetLocator(settings).load(), emptyList()
		)

		val watchedDir = dir.dir
		val paths = dir.events
				.asSequence()
				.filter { it.kind.name() != "ENTRY_DELETE" }
				.filter { it.path.toString().endsWith(".kt") }
				.map { watchedDir.resolve(it.path) }
				.distinct()
				.toList()

		val ktFiles = paths
				.map { compiler.compile(watchedDir, it) }

		if (ktFiles.isNotEmpty()) {
			if (printChangesNotification) {
				paths.forEach { println("Change detected for $it") }
			}
			detektor.run(watchedDir, ktFiles)
					.findings
					.values
					.asSequence()
					.flatMap { it.asSequence() }
					.forEach { println(it.compact()) }
		}
	}
}
