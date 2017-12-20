package io.gitlab.arturbosch.detekt.core

import org.jetbrains.kotlin.psi.KtFile
import java.nio.file.Files
import java.nio.file.Path

/**
 * @author Artur Bosch
 */
class KtTreeCompiler(private val compiler: KtCompiler = KtCompiler(),
					 private val filters: List<PathFilter> = listOf(),
					 private val parallel: Boolean = false) {

	companion object {
		fun instance(settings: ProcessingSettings) = with(settings) {
			KtTreeCompiler(KtCompiler(), pathFilters, parallelCompilation)
		}
	}

	fun compile(project: Path): List<KtFile> {
		require(Files.exists(project)) { "Given project path does not exist!" }
		return when {
			project.isFile() && project.isKotlinFile() -> listOf(compiler.compile(project, project))
			project.isDirectory() -> compileInternal(project)
			else -> throw IllegalArgumentException("Provided project path $project is not a file/dir." +
					" Detekt cannot work with it!")
		}
	}

	private fun streamFor(project: Path) = Files.walk(project).apply { if (parallel) parallel() }

	private fun compileInternal(project: Path): List<KtFile> = streamFor(project)
			.filter(Path::isFile)
			.filter { it.isKotlinFile() }
			.filter { notIgnored(it) }
			.map { compiler.compile(project, it) }
			.toList()

	private fun Path.isKotlinFile(): Boolean {
		val fullPath = toAbsolutePath().toString()
		val kotlinEnding = fullPath.substring(fullPath.lastIndexOf('.') + 1)
		return kotlinEnding == "kt" || kotlinEnding == "kts"
	}

	private fun notIgnored(path: Path) = !filters.any { it.matches(path) }
}
