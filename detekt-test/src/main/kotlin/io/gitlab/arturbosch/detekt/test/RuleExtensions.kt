package io.gitlab.arturbosch.detekt.test

import io.gitlab.arturbosch.detekt.api.BaseRule
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.Rule
import org.intellij.lang.annotations.Language
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingContext
import java.nio.file.Path

val shouldCompileTestSnippets: Boolean =
    System.getProperty("compile-snippet-tests", "false")!!.toBoolean()

fun BaseRule.compileAndLint(@Language("kotlin") content: String): List<Finding> {
    if (shouldCompileTestSnippets) {
        KotlinScriptEngine.compile(content)
    }
    return lint(content)
}

fun BaseRule.lint(@Language("kotlin") content: String): List<Finding> {
    val ktFile = KtTestCompiler.compileFromContent(content.trimIndent())
    return findingsAfterVisit(ktFile)
}

fun BaseRule.lint(path: Path): List<Finding> {
    val ktFile = KtTestCompiler.compile(path)
    return findingsAfterVisit(ktFile)
}

fun BaseRule.compileAndLintWithContext(
    environment: KotlinCoreEnvironment,
    @Language("kotlin") content: String
): List<Finding> {
    if (shouldCompileTestSnippets) {
        KotlinScriptEngine.compile(content)
    }
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

fun Rule.format(@Language("kotlin") content: String): String {
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
