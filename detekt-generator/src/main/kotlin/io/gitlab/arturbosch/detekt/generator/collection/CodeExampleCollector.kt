package io.gitlab.arturbosch.detekt.generator.collection

import java.io.File

class CodeExampleCollector {

	fun collect(): Set<CodeExample> {
		return emptySet()
	}

	private fun collectExamples(): Set<CodeExample> {
		return File(DIRECTORY).walk().map {
			val parts = it.readText().split(SEPARATOR_COMMENT)
			CodeExample(it.nameWithoutExtension, parts[0].trim(), parts[1].trim())
		}.toSet()
	}

	companion object {
		private const val SEPARATOR_COMMENT = "// ##"
		private const val DIRECTORY = ""
	}
}
