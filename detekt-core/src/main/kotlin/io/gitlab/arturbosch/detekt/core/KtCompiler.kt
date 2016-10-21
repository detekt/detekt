package io.gitlab.arturbosch.detekt.core

import com.intellij.openapi.util.Disposer
import com.intellij.psi.PsiFileFactory
import org.jetbrains.kotlin.cli.jvm.compiler.EnvironmentConfigFiles
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.idea.KotlinLanguage
import org.jetbrains.kotlin.psi.KtFile
import java.nio.file.Files
import java.nio.file.Path

/**
 * @author Artur Bosch
 */
class KtCompiler {

	private val psiFileFactory: PsiFileFactory

	init {
		val project = KotlinCoreEnvironment.createForProduction(Disposer.newDisposable(),
				CompilerConfiguration(), EnvironmentConfigFiles.JVM_CONFIG_FILES).project
		psiFileFactory = PsiFileFactory.getInstance(project)
	}

	fun compile(path: Path): KtFile {
		require(path.isFile()) { "Given path should be a regular file!" }
		val file = path.normalize()
		val content = String(Files.readAllBytes(file))
		return psiFileFactory.createFileFromText(file.fileName.toString(), KotlinLanguage.INSTANCE, content) as KtFile
	}

	fun compileFromText(content: String): KtFile {
		return psiFileFactory.createFileFromText(KotlinLanguage.INSTANCE, content) as KtFile
	}
}