package io.gitlab.arturbosch.detekt.core

import com.intellij.mock.MockProject
import com.intellij.openapi.util.Disposer
import com.intellij.psi.PsiFileFactory
import com.intellij.testFramework.LightVirtualFile
import io.gitlab.arturbosch.detekt.api.Unstable
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
open class KtCompiler(val project: Path) {

	protected val psiFileFactory: PsiFileFactory

	init {
		val project = KotlinCoreEnvironment.createForProduction(Disposer.newDisposable(),
				CompilerConfiguration(), EnvironmentConfigFiles.JVM_CONFIG_FILES).project
		MutableAST.forProject(project as MockProject)
		psiFileFactory = PsiFileFactory.getInstance(project)
	}

	fun compile(subPath: Path): KtFile {
		require(subPath.isFile()) { "Given sub path should be a regular file!" }
		val relativePath = if (project == subPath) subPath else project.relativize(subPath)
		val content = String(Files.readAllBytes(subPath))
		return psiFileFactory.createFileFromText(relativePath.fileName.toString(), KotlinLanguage.INSTANCE,
				content, true, true, false, LightVirtualFile(relativePath.toString())) as KtFile
	}

}