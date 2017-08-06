package io.gitlab.arturbosch.detekt.core.processors

import io.gitlab.arturbosch.detekt.api.DetektVisitor
import org.jetbrains.kotlin.com.intellij.openapi.util.Key
import org.jetbrains.kotlin.psi.KtFile

class ProjectSLOCProcessor : AbstractProcessor() {

	override val visitor: DetektVisitor = SLOCVisitor()
	override val key: Key<Int> = SLOC_KEY
}

class SLOCVisitor : DetektVisitor() {

	override fun visitKtFile(file: KtFile) {
		val lines = file.text.split('\n')
		val sloc = SLOC().count(lines)
		file.putUserData(SLOC_KEY, sloc)
	}

	private class SLOC {

		private val comments = arrayOf("//", "/*", "*/", "*")

		fun count(lines: List<String>): Int {
			return lines
						.map { it.trim() }
						.filter { trim -> trim.isNotEmpty() && !comments.any { trim.startsWith(it) } }
						.size
		}
	}
}

val SLOC_KEY = Key<Int>("sloc")
