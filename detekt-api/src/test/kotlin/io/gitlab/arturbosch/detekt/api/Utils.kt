package io.gitlab.arturbosch.detekt.api

import org.jetbrains.kotlin.com.intellij.psi.PsiFileFactory
import org.jetbrains.kotlin.idea.KotlinLanguage
import org.jetbrains.kotlin.psi.KtFile
import java.io.File

/**
 * @author Artur Bosch
 */
internal object Compiler {

	private val psiFileFactory: PsiFileFactory = PsiFileFactory.getInstance(PROJECT)

	fun compileFromContent(content: String): KtFile {
		return psiFileFactory.createFileFromText(KotlinLanguage.INSTANCE, content) as KtFile
	}
}

fun compilerFor(resource: String) = Compiler.compileFromContent(
		File(Compiler.javaClass.getResource("/$resource").path).readText())

fun yamlConfig(resource: String) = YamlConfig.loadResource(Compiler.javaClass.getResource("/$resource"))
