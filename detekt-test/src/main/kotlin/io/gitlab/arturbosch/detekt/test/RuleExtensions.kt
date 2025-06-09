package io.gitlab.arturbosch.detekt.test

import io.github.detekt.test.utils.KotlinAnalysisApiEngine
import io.github.detekt.test.utils.KotlinEnvironmentContainer
import io.github.detekt.test.utils.KotlinScriptEngine
import io.github.detekt.test.utils.compileContentForTest
import io.gitlab.arturbosch.detekt.api.CompilerResources
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.RequiresAnalysisApi
import io.gitlab.arturbosch.detekt.api.RequiresFullAnalysis
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.core.suppressors.isSuppressedBy
import org.intellij.lang.annotations.Language
import org.jetbrains.kotlin.config.languageVersionSettings
import org.jetbrains.kotlin.psi.KtFile

private val shouldCompileTestSnippets: Boolean =
    System.getProperty("compile-test-snippets", "false")!!.toBoolean()

private val shouldCompileTestSnippetsAa: Boolean =
    System.getProperty("compile-test-snippets-aa", "false")!!.toBoolean()

fun Rule.lint(
    @Language("kotlin") content: String,
    compilerResources: CompilerResources = FakeCompilerResources(),
    compile: Boolean = true,
): List<Finding> {
    require(this !is RequiresFullAnalysis) {
        "${this.ruleName} requires full analysis so you should use lintWithContext instead of lint"
    }
    if (compile && shouldCompileTestSnippets) {
        KotlinScriptEngine.compile(content)
    }
    if (compile && shouldCompileTestSnippetsAa) {
        KotlinAnalysisApiEngine.compile(content)
    }
    val ktFile = compileContentForTest(content)
    return visitFile(ktFile, compilerResources = compilerResources).filterSuppressed(this)
}

fun <T> T.lintWithContext(
    environment: KotlinEnvironmentContainer,
    @Language("kotlin") content: String,
    @Language("kotlin") vararg additionalContents: String,
    compilerResources: CompilerResources = CompilerResources(
        environment.configuration.languageVersionSettings
    ),
    compile: Boolean = true,
): List<Finding> where T : Rule, T : RequiresFullAnalysis {
    if (compile && shouldCompileTestSnippets) {
        KotlinScriptEngine.compile(content)
    }
    if (compile && shouldCompileTestSnippetsAa) {
        KotlinAnalysisApiEngine.compile(content)
    }
    val ktFile = compileContentForTest(content)
    val additionalKtFiles = additionalContents.mapIndexed { index, additionalContent ->
        compileContentForTest(additionalContent, "AdditionalTest$index.kt")
    }
    setBindingContext(environment.createBindingContext(listOf(ktFile) + additionalKtFiles))

    return visitFile(ktFile, compilerResources).filterSuppressed(this)
}

fun <T> T.lintWithContext(
    environment: KotlinEnvironmentContainer,
    @Language("kotlin") content: String,
): List<Finding> where T : Rule, T : RequiresAnalysisApi {
    val ktFile = KotlinAnalysisApiEngine.compile(content)

    val compilerResources = CompilerResources(
        environment.configuration.languageVersionSettings
    )

    return visitFile(ktFile, compilerResources).filterSuppressed(this)
}

fun Rule.lint(ktFile: KtFile, compilerResources: CompilerResources = FakeCompilerResources()): List<Finding> {
    require(this !is RequiresFullAnalysis) {
        "${this.ruleName} requires full analysis so you should use lintWithContext instead of lint"
    }
    return visitFile(ktFile, compilerResources = compilerResources).filterSuppressed(this)
}

private fun List<Finding>.filterSuppressed(rule: Rule): List<Finding> =
    filterNot {
        it.entity.ktElement.isSuppressedBy(rule.ruleName.value, rule.aliases, RuleSet.Id("NoARuleSetId"))
    }

private val Rule.aliases: Set<String> get() = config.valueOrDefault(Config.ALIASES_KEY, emptyList<String>()).toSet()
