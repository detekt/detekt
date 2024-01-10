package io.gitlab.arturbosch.detekt.test

import io.github.detekt.test.utils.KotlinScriptEngine
import io.github.detekt.test.utils.compileContentForTest
import io.github.detekt.test.utils.compileForTest
import io.gitlab.arturbosch.detekt.api.CompilerResources
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.Rule
import org.intellij.lang.annotations.Language
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.config.languageVersionSettings
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.calls.smartcasts.DataFlowValueFactoryImpl
import java.nio.file.Path

private val shouldCompileTestSnippets: Boolean =
    System.getProperty("compile-test-snippets", "false")!!.toBoolean()

fun Rule.compileAndLint(@Language("kotlin") content: String): List<Finding> {
    if (shouldCompileTestSnippets) {
        KotlinScriptEngine.compile(content)
    }
    return lint(content)
}

fun Rule.lint(@Language("kotlin") content: String): List<Finding> {
    val ktFile = compileContentForTest(content)
    return findingsAfterVisit(ktFile)
}

fun Rule.lint(path: Path): List<Finding> {
    val ktFile = compileForTest(path)
    return findingsAfterVisit(ktFile)
}

fun Rule.lintWithContext(
    environment: KotlinCoreEnvironment,
    @Language("kotlin") content: String,
    @Language("kotlin") vararg additionalContents: String,
): List<Finding> {
    val ktFile = compileContentForTest(content)
    val additionalKtFiles = additionalContents.mapIndexed { index, additionalContent ->
        compileContentForTest(additionalContent, "AdditionalTest$index.kt")
    }
    val bindingContext = environment.getContextForPaths(listOf(ktFile) + additionalKtFiles)
    val languageVersionSettings = environment.configuration.languageVersionSettings

    val dataFlowValueFactory = DataFlowValueFactoryImpl(languageVersionSettings)
    val compilerResources = CompilerResources(languageVersionSettings, dataFlowValueFactory)
    return findingsAfterVisit(ktFile, bindingContext, compilerResources)
}

fun Rule.compileAndLintWithContext(
    environment: KotlinCoreEnvironment,
    @Language("kotlin") content: String
): List<Finding> {
    if (shouldCompileTestSnippets) {
        KotlinScriptEngine.compile(content)
    }
    return lintWithContext(environment, content)
}

fun Rule.lint(ktFile: KtFile): List<Finding> = findingsAfterVisit(ktFile)

private fun Rule.findingsAfterVisit(
    ktFile: KtFile,
    bindingContext: BindingContext = BindingContext.EMPTY,
    compilerResources: CompilerResources? = null
): List<Finding> {
    this.visitFile(ktFile, bindingContext, compilerResources)
    return this.findings
}
