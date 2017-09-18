package io.gitlab.arturbosch.detekt.watchservice

import io.gitlab.arturbosch.detekt.cli.console.FindingsReport
import io.gitlab.arturbosch.detekt.core.DetektFacade
import io.gitlab.arturbosch.detekt.core.KtCompiler
import io.gitlab.arturbosch.detekt.core.PathFilter
import io.gitlab.arturbosch.detekt.core.ProcessingSettings
import io.gitlab.arturbosch.detekt.core.RuleSetLocator
import org.jetbrains.kotlin.psi.KtFile
import java.nio.file.Path

/**
 * @author Artur Bosch
 */
class DetektService(parameters: Parameters) {

	private val settings = with(parameters) {
		ProcessingSettings(
				extractWatchDirectory(),
				extractConfig(),
				listOf(PathFilter(".*/test/.*"),
						PathFilter(".*/resources/.*"),
						PathFilter(".*/build/.*"))
		)
	}

	private val detektor = DetektFacade.instance(settings, RuleSetLocator(settings).load(), emptyList())
	private val reporter = FindingsReport()

	fun check(dir: WatchedDir) {
		val watchedDir = dir.dir
		val paths = dir.events
				.filter { it.kind.name() != "ENTRY_DELETE" }
				.filter { it.path.toString().endsWith(".kt") }
				.map { watchedDir.resolve(it.path) }
				.distinct()
				.fold(mutableListOf<Path>()) { acc, path -> acc.add(path); acc }

		val compiler = KtCompiler(watchedDir)
		val ktFiles = paths
				.map { compiler.compile(it) }
				.fold(mutableListOf<KtFile>()) { acc, ktFile -> acc.add(ktFile); acc }

		if (ktFiles.isNotEmpty()) {
			paths.forEach { println("Change detected for $it") }
			val detektion = detektor.run(ktFiles)
			println(reporter.render(detektion))
		}
	}
}
