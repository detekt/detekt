package io.gitlab.arturbosch.detekt.test

import io.gitlab.arturbosch.detekt.core.KtCompiler
import org.jetbrains.kotlin.com.intellij.openapi.util.text.StringUtilRt
import org.jetbrains.kotlin.idea.KotlinLanguage
import org.jetbrains.kotlin.psi.KtFile
import java.nio.file.Paths

/**
 * Test compiler extends kt compiler and adds ability to compile from text content.
 *
 * @author Artur Bosch
 */
internal object KtTestCompiler : KtCompiler(Paths.get(resource("/"))) {

	fun compileFromContent(content: String): KtFile
			= psiFileFactory.createFileFromText(KotlinLanguage.INSTANCE, StringUtilRt.convertLineSeparators(content)) as KtFile

}