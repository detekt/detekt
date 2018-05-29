package io.gitlab.arturbosch.detekt.test

import io.gitlab.arturbosch.detekt.core.KtCompiler
import org.jetbrains.kotlin.com.intellij.openapi.util.text.StringUtilRt
import org.jetbrains.kotlin.idea.KotlinLanguage
import org.jetbrains.kotlin.psi.KtFile
import java.nio.file.Path
import java.nio.file.Paths

/**
 * Test compiler extends kt compiler and adds ability to compile from text content.
 *
 * @author Artur Bosch
 */
object KtTestCompiler : KtCompiler() {

	private val root = Paths.get(resource("/"))

	fun compile(path: Path) = compile(root, path)

	fun compileFromContent(content: String): KtFile {
		val psiFile = psiFileFactory.createFileFromText(
				KotlinLanguage.INSTANCE,
				StringUtilRt.convertLineSeparators(content))
		return psiFile as? KtFile ?: throw IllegalStateException("kotlin file expected")
	}
}
