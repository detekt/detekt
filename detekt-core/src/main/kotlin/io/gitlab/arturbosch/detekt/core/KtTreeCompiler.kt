package io.gitlab.arturbosch.detekt.core

import org.jetbrains.kotlin.psi.KtFile
import java.nio.file.Files
import java.nio.file.Path
import java.util.stream.Stream

/**
 * @author Artur Bosch
 */
class KtTreeCompiler(private val project: Path,
					 private val filters: List<PathFilter> = listOf(),
					 private val parallel: Boolean = false) {

	private val compiler = KtCompiler(project)

	init {
		require(Files.exists(project)) { "Given project path does not exist!" }
	}

	companion object {
		fun instance(settings: ProcessingSettings) = with(settings) {
			KtTreeCompiler(project, pathFilters, parallelCompilation)
		}
	}

	fun compile(): List<KtFile> = when {
		project.isFile() -> listOf(compiler.compile(project))
		project.isDirectory() -> compileInternal(createStream())
		else -> throw IllegalArgumentException("Provided project path $project is not a file/dir." +
				" Detekt cannot work with it!")
	}

	private fun createStream(): Stream<Path> = Files.walk(project).apply {
		if (parallel) parallel()
	}

	private fun compileInternal(stream: Stream<Path>): List<KtFile> = stream.filter(Path::isFile)
			.filter { it.isKotlinFile() }
			.filter { notIgnored(it) }
			.map { compiler.compile(it) }
			.toList()

	private fun Path.isKotlinFile(): Boolean {
		val fullPath = this.toAbsolutePath().toString()
		val kotlinEnding = fullPath.substring(fullPath.lastIndexOf('.') + 1)
		return kotlinEnding.length == 2 && kotlinEnding.endsWith("kt")
	}

	private fun notIgnored(path: Path) = !filters.any { it.matches(path) }
}
