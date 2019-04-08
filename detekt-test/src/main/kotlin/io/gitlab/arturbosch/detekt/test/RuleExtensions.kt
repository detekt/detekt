package io.gitlab.arturbosch.detekt.test

import io.gitlab.arturbosch.detekt.api.BaseRule
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.test.KotlinScriptEngine.compile
import org.intellij.lang.annotations.Language
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingContext
import java.nio.file.Path

fun BaseRule.compileAndLint(@Language("kotlin") content: String): List<Finding> {
    compile(content)
    return lint(content)
}

fun BaseRule.lint(content: String): List<Finding> {
    val ktFile = KtTestCompiler.compileFromContent(content.trimIndent())
    return findingsAfterVisit(ktFile)
}

fun BaseRule.lint(path: Path): List<Finding> {
    val ktFile = KtTestCompiler.compile(path)
    return findingsAfterVisit(ktFile)
}

fun BaseRule.lintWithContext(environment: KotlinCoreEnvironment, content: String): List<Finding> {
    val ktFile = KtTestCompiler.compileFromContent(content.trimIndent())
    val bindingContext = KtTestCompiler.getContextForPaths(environment, listOf(ktFile))
    return findingsAfterVisit(ktFile, bindingContext)
}

fun BaseRule.lint(ktFile: KtFile) = findingsAfterVisit(ktFile)

private fun BaseRule.findingsAfterVisit(
    ktFile: KtFile,
    bindingContext: BindingContext = BindingContext.EMPTY
): List<Finding> {
    this.visitFile(ktFile, bindingContext)
    return this.findings
}

fun Rule.format(content: String): String {
    val ktFile = KtTestCompiler.compileFromContent(content.trimIndent())
    return contentAfterVisit(ktFile)
}

fun Rule.format(path: Path): String {
    val ktFile = KtTestCompiler.compile(path)
    return contentAfterVisit(ktFile)
}

private fun Rule.contentAfterVisit(ktFile: KtFile): String {
    this.visit(ktFile)
    return ktFile.text
}
