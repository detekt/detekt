package io.gitlab.arturbosch.detekt.core

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.RuleSetProvider
import org.jetbrains.kotlin.psi.KtFile
import java.net.URLClassLoader
import java.nio.file.Files
import java.nio.file.Path
import java.util.ServiceLoader

/**
 * @author Artur Bosch
 */
class Detekt(val project: Path,
			 val config: Config = Config.empty,
			 val ruleSets: List<Path> = listOf(),
			 pathFilters: List<PathFilter> = listOf(),
			 parallelCompilation: Boolean = false) {

	private val notifications: MutableList<Notification> = mutableListOf()
	private val compiler: KtTreeCompiler = KtTreeCompiler(project, pathFilters, parallelCompilation)

	init {
		require(Files.exists(project)) { "Given project path does not exist!" }
		ruleSets.forEach {
			require(Files.exists(it) && it.toString().endsWith("jar")) {
				"Given ruleset $it does not exist or has no jar ending!"
			}
		}
	}

	fun run(): Detektion {
		val ktFiles = compiler.compile()
		val providers = loadProviders().asIterable().toList()
		return withExecutor {

			val futures = ktFiles.map { file -> runAsync { file.detekt(providers) } }
			val findings = awaitAll(futures).flatMap { it }.toMergedMap()

			if (config.valueOrDefault("autoCorrect") { false }) {
				ktFiles.saveModifiedFiles(project) {
					notifications.add(it)
				}
			}

			DetektResult(findings.toSortedMap(), notifications)
		}
	}

	private fun loadProviders(): ServiceLoader<RuleSetProvider> {
		val urls = ruleSets.map { it.toUri().toURL() }.toTypedArray()
		val detektLoader = URLClassLoader(urls, javaClass.classLoader)
		return ServiceLoader.load(RuleSetProvider::class.java, detektLoader)
	}

	private fun KtFile.detekt(providers: List<RuleSetProvider>): List<Pair<String, List<Finding>>> {
		return providers.map { it.buildRuleset(config) }
				.filterNotNull()
				.sortedBy { it.id }
				.distinctBy { it.id }
				.map { rule -> rule.id to rule.accept(this) }
	}

}


