package io.gitlab.arturbosch.detekt

import io.gitlab.arturbosch.detekt.core.KtCompiler
import io.gitlab.arturbosch.detekt.default.CodeSmellProvider
import io.gitlab.arturbosch.detekt.default.StyleGuideProvider
import java.nio.file.Paths

/**
 * @author Artur Bosch
 */
fun main(args: Array<String>) {
	val compiler = KtCompiler()
	val path = Paths.get("./src/test/resources/cases/Default.kt")
	val file = compiler.compile(path)
	printFindings(CodeSmellProvider().instance().acceptAll(listOf(file)))
	printFindings(StyleGuideProvider().instance().acceptAll(listOf(file)))
}
