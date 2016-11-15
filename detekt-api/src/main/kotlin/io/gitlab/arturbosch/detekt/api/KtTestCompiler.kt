package io.gitlab.arturbosch.detekt.api

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
 * This is basically an enhanced version of the KtCompiler from the core project which allows plain text to be
 * compiled to a KtFile.
 *
 * @author Artur Bosch
 */
@Unstable(removedIn = "M4")
internal object KtTestCompiler {

	private val psiFileFactory: PsiFileFactory

	init {
		val project = KotlinCoreEnvironment.createForProduction(Disposer.newDisposable(),
				CompilerConfiguration(), EnvironmentConfigFiles.JVM_CONFIG_FILES).project
		psiFileFactory = PsiFileFactory.getInstance(project)
	}

	internal fun compileFromText(content: String): KtFile {
		return psiFileFactory.createFileFromText(KotlinLanguage.INSTANCE, content) as KtFile
	}

	internal fun compile(path: Path): KtFile {
		require(Files.isRegularFile(path)) { "Given path should be a regular file!" }
		val file = path.normalize().toAbsolutePath()
		val content = String(Files.readAllBytes(file))
		return psiFileFactory.createFileFromText(file.fileName.toString(), KotlinLanguage.INSTANCE, content) as KtFile
	}
}

/**
 * Use this method if you define a kt file/class as a plain string in your test.
 */
@Unstable(removedIn = "M4")
fun compileContentForTest(content: String) = KtTestCompiler.compileFromText(content)

/**
 * Use this method if you test a kt file/class in the test resources.
 */
@Unstable(removedIn = "M4")
fun compileForTest(path: Path) = KtTestCompiler.compile(path)
