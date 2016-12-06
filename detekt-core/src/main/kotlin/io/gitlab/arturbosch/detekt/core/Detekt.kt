package io.gitlab.arturbosch.detekt.core

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.RuleSetProvider
import org.jetbrains.kotlin.fileClasses.javaFileFacadeFqName
import org.jetbrains.kotlin.psi.KtFile
import java.net.URLClassLoader
import java.nio.file.Files
import java.nio.file.Path
import java.util.HashMap
import java.util.ServiceLoader
import java.util.concurrent.CompletableFuture

/**
 * @author Artur Bosch
 */
class Detekt(val project: Path,
			 val config: Config = Config.empty,
			 val ruleSets: List<Path> = listOf(),
			 pathFilters: List<PathFilter> = listOf(),
			 parallelCompilation: Boolean = false) {

	private val notifications: MutableList<Notification> = mutableListOf()
	private val compiler: KtTreeCompiler

	init {
		require(Files.exists(project)) { "Given project path does not exist!" }
		ruleSets.forEach {
			require(Files.exists(it) && it.toString().endsWith("jar")) {
				"Given ruleset $it does not exist or has no jar ending!"
			}
		}
		compiler = KtTreeCompiler(project, pathFilters, parallelCompilation)
	}

	fun run(): Detektion {
		val ktFiles = compiler.compile()
		val providers = loadProviders()
		return withExecutor {

			val futures = mutableListOf<CompletableFuture<Pair<String, List<Finding>>>>()

			ktFiles.forEach {
				futures.addAll(providers.map { it.buildRuleset(config) }
						.filterNotNull()
						.sortedBy { it.id }
						.distinctBy { it.id }
						.map { rule -> task(this) { rule.acceptAll(listOf(it)) } })
			}

			val findings = HashMap<String, MutableList<Finding>>()
			awaitAll(futures).forEach {
				findings.merge(it.first, it.second.toMutableList(), { l1, l2 -> l1.apply { addAll(l2) } })
			}

			saveModifiedFilesIfAutoCorrectEnabled(ktFiles)
			DetektResult(findings.toSortedMap(), notifications)
		}
	}

	private fun loadProviders(): ServiceLoader<RuleSetProvider> {
		val urls = ruleSets.map { it.toUri().toURL() }.toTypedArray()
		val detektLoader = URLClassLoader(urls)
		return ServiceLoader.load(RuleSetProvider::class.java, detektLoader)
	}

	private fun saveModifiedFilesIfAutoCorrectEnabled(ktFiles: List<KtFile>) {
		if (config.valueOrDefault("autoCorrect") { false }) {
			ktFiles.filter { it.modificationStamp > 0 }
					.map { it.relativePath to it.unnormalizedContent() }
					.filter { it.first != null }
					.map { project.resolve(it.first) to it.second }
					.forEach {
						notifications.add(ModificationNotification(it.first))
						Files.write(it.first, it.second.toByteArray())
					}
		}
	}

	private fun  KtFile.unnormalizedContent(): String {
		val lineSeparator = this.getUserData(KtCompiler.LINE_SEPARATOR)
		require(lineSeparator != null) { "No line separator entry for ktFile ${this.javaFileFacadeFqName.asString()}" }
		return this.text.replace("\n", lineSeparator!!)
	}

	private val KtFile.relativePath: String?
		get() = this.getUserData(KtCompiler.RELATIVE_PATH)

}


