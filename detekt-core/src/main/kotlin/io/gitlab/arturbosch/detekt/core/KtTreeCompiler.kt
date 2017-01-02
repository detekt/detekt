package io.gitlab.arturbosch.detekt.core

import org.jetbrains.kotlin.psi.KtFile
import java.nio.file.Files
import java.nio.file.Path
import java.util.stream.Stream

/**
 * @author Artur Bosch
 */
class KtTreeCompiler(val project: Path,
					 val filters: List<PathFilter> = listOf(),
					 val parallel: Boolean = false) {

	private val compiler = KtCompiler(project)

	fun compile(): List<KtFile> {
		return if (project.isFile()) {
			listOf(compiler.compile(project))
		} else if (project.isDirectory()) {
			compileInternal(
					if (parallel) {
						Files.walk(project).parallel()
					} else {
						Files.walk(project)
					}
			)
		} else {
			throw IllegalArgumentException("Provided project path $project is not a file/dir." +
					" Detekt cannot work with it!")
		}
	}

	private fun compileInternal(stream: Stream<Path>): List<KtFile> {
		return stream.filter(Path::isFile)
				.filter { it.isKotlinFile() }
				.filter { notIgnored(it) }
				.map { compiler.compile(it) }
				.toList()
	}

	private fun Path.isKotlinFile(): Boolean {
		val fullPath = this.toAbsolutePath().toString()
		val kotlinEnding = fullPath.substring(fullPath.lastIndexOf('.') + 1)
		return kotlinEnding.length == 2 && kotlinEnding.endsWith("kt")
	}

	private fun notIgnored(path: Path) = !filters.any { it.matches(path) }
}
