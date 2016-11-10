package io.gitlab.arturbosch.detekt.core

import com.intellij.testFramework.LightVirtualFile
import io.gitlab.arturbosch.detekt.api.Config
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
		val futures = providers.map { it.buildRuleset(config) }
				.filterNotNull()
				.sortedBy { it.id }
				.distinctBy { it.id }
				.map { task { it.acceptAll(ktFiles) } }
		val findings = awaitAll(futures).toMap()
		saveModifiedFilesIfAutoCorrectEnabled(ktFiles)
		return DetektResult(findings, notifications)
	}

	private fun loadProviders(): ServiceLoader<RuleSetProvider> {
		val urls = ruleSets.map { it.toUri().toURL() }.toTypedArray()
		val detektLoader = URLClassLoader(urls)
		return ServiceLoader.load(RuleSetProvider::class.java, detektLoader)
	}

	private fun saveModifiedFilesIfAutoCorrectEnabled(ktFiles: List<KtFile>) {
		if (config.valueOrDefault("autoCorrect") { false }) {
			ktFiles.filter { it.modificationStamp > 0 }
					.map { it.relativePath to it.text }
					.filter { it.first != null }
					.map { project.resolve(it.first) to it.second }
					.forEach {
						notifications.add(ModificationNotification(it.first))
						Files.write(it.first, it.second.toByteArray())
					}
		}
	}

	private val KtFile.relativePath: String?
		get() = (this.containingFile.viewProvider.virtualFile as LightVirtualFile).originalFile?.name

}

