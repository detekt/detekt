package io.gitlab.arturbosch.detekt.core

import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.RuleSetProvider
import java.net.URLClassLoader
import java.nio.file.Files
import java.nio.file.Path
import java.util.ServiceLoader

/**
 * @author Artur Bosch
 */
class Detekt(project: Path,
			 val ruleSets: List<Path> = listOf(),
			 pathFilters: List<PathFilter> = listOf(),
			 parallelCompilation: Boolean = false) {

	private val compiler: KtTreeCompiler

	init {
		require(Files.exists(project)) { "Given project path does not exist!" }
		compiler = KtTreeCompiler(project, pathFilters, parallelCompilation)
	}

	fun run(): Map<String, List<Finding>> {
		val ktFiles = compiler.compile()
		val providers = loadProviders()
		val futures = providers.map {
			task { it.instance().acceptAll(ktFiles) }
		}
		return awaitAll(futures).toMap()
	}

	private fun loadProviders(): ServiceLoader<RuleSetProvider> {
		val urls = ruleSets.map { it.toUri().toURL() }.toTypedArray()
		val detektLoader = URLClassLoader(urls)
		return ServiceLoader.load(RuleSetProvider::class.java, detektLoader)
	}

}
