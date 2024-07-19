package io.gitlab.arturbosch.detekt.test

import io.github.detekt.test.utils.KotlinScriptEngine
import io.github.detekt.test.utils.compileContentForTest
import io.gitlab.arturbosch.detekt.api.CompilerResources
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.core.suppressors.isSuppressedBy
import org.intellij.lang.annotations.Language
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.config.languageVersionSettings
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.calls.smartcasts.DataFlowValueFactoryImpl

private val shouldCompileTestSnippets: Boolean =
    System.getProperty("compile-test-snippets", "false")!!.toBoolean()

fun Rule.compileAndLint(
    @Language("kotlin") content: String,
    compilerResources: CompilerResources = FakeCompilerResources(),
): List<Finding> {
    if (shouldCompileTestSnippets) {
        KotlinScriptEngine.compile(content)
    }
    return lint(content, compilerResources)
}

fun Rule.lint(
    @Language("kotlin") content: String,
    compilerResources: CompilerResources = FakeCompilerResources()
): List<Finding> {
    val ktFile = compileContentForTest(content)
    return visitFile(ktFile, compilerResources = compilerResources).filterSuppressed(this)
}

fun Rule.lintWithContext(
    environment: KotlinCoreEnvironment,
    @Language("kotlin") content: String,
    @Language("kotlin") vararg additionalContents: String,
    compilerResources: CompilerResources = CompilerResources(
        environment.configuration.languageVersionSettings,
        DataFlowValueFactoryImpl(environment.configuration.languageVersionSettings)
    )
): List<Finding> {
    val ktFile = compileContentForTest(content)
    val additionalKtFiles = additionalContents.mapIndexed { index, additionalContent ->
        compileContentForTest(additionalContent, "AdditionalTest$index.kt")
    }
    val bindingContext = environment.createBindingContext(listOf(ktFile) + additionalKtFiles)

    return visitFile(ktFile, bindingContext, compilerResources).filterSuppressed(this)
}

fun Rule.compileAndLintWithContext(
    environment: KotlinCoreEnvironment,
    @Language("kotlin") content: String,
    compilerResources: CompilerResources = CompilerResources(
        environment.configuration.languageVersionSettings,
        DataFlowValueFactoryImpl(environment.configuration.languageVersionSettings)
    )
): List<Finding> {
    if (shouldCompileTestSnippets) {
        KotlinScriptEngine.compile(content)
    }
    return lintWithContext(environment, content, compilerResources = compilerResources)
}

fun Rule.lint(ktFile: KtFile, compilerResources: CompilerResources = FakeCompilerResources()): List<Finding> =
    visitFile(ktFile, compilerResources = compilerResources).filterSuppressed(this)

private fun List<Finding>.filterSuppressed(rule: Rule): List<Finding> =
    filterNot {
        it.entity.ktElement.isSuppressedBy(Rule.Id(rule.ruleName.value), rule.aliases, RuleSet.Id("NoARuleSetId"))
    }

private val Rule.aliases: Set<String> get() = config.valueOrDefault(Config.ALIASES_KEY, emptyList<String>()).toSet()
