package io.gitlab.arturbosch.detekt.api

import org.jetbrains.kotlin.com.intellij.openapi.util.text.StringUtilRt
import org.jetbrains.kotlin.com.intellij.psi.PsiFileFactory
import org.jetbrains.kotlin.idea.KotlinLanguage
import org.jetbrains.kotlin.psi.KtFile
import java.io.File
import java.net.URI

/**
 * @author Artur Bosch
 */
internal object Compiler {

	private val psiFileFactory: PsiFileFactory = PsiFileFactory.getInstance(psiProject)

	fun compileFromContent(content: String): KtFile {
		val psiFile = psiFileFactory.createFileFromText(
				KotlinLanguage.INSTANCE,
				StringUtilRt.convertLineSeparators(content))
		return psiFile as? KtFile ?: throw IllegalStateException("kotlin file expected")
	}
}

fun compilerFor(resource: String) = Compiler.compileFromContent(
		File(Compiler.javaClass.getResource("/$resource").path).readText())

fun yamlConfig(resource: String) = YamlConfig.loadResource(Compiler.javaClass.getResource("/$resource"))

fun resource(name: String): URI = Compiler::class.java.getResource(if (name.startsWith("/")) name else "/$name").toURI()
