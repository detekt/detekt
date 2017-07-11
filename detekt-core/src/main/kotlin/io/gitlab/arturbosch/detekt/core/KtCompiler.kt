package io.gitlab.arturbosch.detekt.core

import io.gitlab.arturbosch.detekt.api.PROJECT
import org.jetbrains.kotlin.com.intellij.openapi.util.Key
import org.jetbrains.kotlin.com.intellij.openapi.util.text.StringUtilRt
import org.jetbrains.kotlin.com.intellij.psi.PsiFileFactory
import org.jetbrains.kotlin.com.intellij.testFramework.LightVirtualFile
import org.jetbrains.kotlin.idea.KotlinLanguage
import org.jetbrains.kotlin.psi.KtFile
import java.nio.file.Path

/**
 * @author Artur Bosch
 */
open class KtCompiler(val project: Path) {

	protected val psiFileFactory: PsiFileFactory = PsiFileFactory.getInstance(PROJECT)

	fun compile(subPath: Path): KtFile {
		require(subPath.isFile()) { "Given sub path should be a regular file!" }
		val relativePath = if (project == subPath) subPath else project.relativize(subPath)
		val content = subPath.toFile().readText()
		val lineSeparator = content.determineLineSeparator()
		val normalizedContent = StringUtilRt.convertLineSeparators(content)
		val ktFile = createKtFile(normalizedContent, relativePath)
		ktFile.putExtraInformation(lineSeparator, relativePath)
		return ktFile
	}

	private fun KtFile.putExtraInformation(lineSeparator: String, relativePath: Path) {
		this.putUserData(LINE_SEPARATOR, lineSeparator)
		this.putUserData(RELATIVE_PATH, relativePath.toString())
	}

	private fun createKtFile(content: String, relativePath: Path) = psiFileFactory.createFileFromText(
			relativePath.fileName.toString(), KotlinLanguage.INSTANCE, StringUtilRt.convertLineSeparators(content),
			true, true, false, LightVirtualFile(relativePath.toString())) as KtFile

	private fun String.determineLineSeparator(): String {
		val i = this.lastIndexOf('\n')
		if (i == -1) {
			return if (this.lastIndexOf('\r') == -1) System.getProperty("line.separator") else "\r"
		}
		return if (i != 0 && this[i] == '\r') "\r\n" else "\n"
	}

	companion object {
		val LINE_SEPARATOR: Key<String> = Key("lineSeparator")
		val RELATIVE_PATH: Key<String> = Key("relativePath")
	}

}
