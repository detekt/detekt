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
open class Detekt(project: Path,
				  val ruleSets: List<Path> = listOf(),
				  pathFilters: List<String> = listOf(),
				  parallelCompilation: Boolean = false) {

	private val compiler: KtTreeCompiler

	init {
		require(Files.exists(project)) { "Given project path does not exist!" }
		val filters = pathFilters.map(::PathFilter)
		compiler = KtTreeCompiler(project, filters, parallelCompilation)
	}

	fun run(): Map<String, List<Finding>> {
		val ktFiles = compiler.compile()
		val providers = loadProviders()
		return providers.map { it.instance().acceptAll(ktFiles) }.toMap()
	}

	private fun loadProviders(): ServiceLoader<RuleSetProvider> {
		val urls = ruleSets.map { it.toUri().toURL() }.toTypedArray()
		val detektLoader = URLClassLoader(urls)
		return ServiceLoader.load(RuleSetProvider::class.java, detektLoader)
	}

}