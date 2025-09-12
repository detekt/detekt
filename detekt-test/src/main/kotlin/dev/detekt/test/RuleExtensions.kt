package dev.detekt.test

import dev.detekt.api.Config
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.RequiresFullAnalysis
import dev.detekt.api.Rule
import dev.detekt.api.RuleSet
import dev.detekt.core.suppressors.isSuppressedBy
import dev.detekt.test.utils.KotlinAnalysisApiEngine
import dev.detekt.test.utils.KotlinEnvironmentContainer
import dev.detekt.test.utils.compileContentForTest
import org.intellij.lang.annotations.Language
import org.jetbrains.kotlin.cli.jvm.config.javaSourceRoots
import org.jetbrains.kotlin.config.LanguageVersionSettings
import org.jetbrains.kotlin.config.languageVersionSettings
import org.jetbrains.kotlin.psi.KtFile
import kotlin.io.path.Path

private val shouldCompileTestSnippets: Boolean =
    System.getProperty("compile-test-snippets", "false")!!.toBoolean()

fun Rule.lint(
    @Language("kotlin") content: String,
    languageVersionSettings: LanguageVersionSettings = FakeLanguageVersionSettings(),
    compile: Boolean = true,
): List<Finding> {
    require(this !is RequiresFullAnalysis) {
        "${this.ruleName} requires full analysis so you should use lintWithContext instead of lint"
    }
    require(this !is RequiresAnalysisApi) {
        "${this.ruleName} requires Analysis API so you should use lintWithContext instead of lint"
    }
    if (compile && shouldCompileTestSnippets) {
        try {
            KotlinAnalysisApiEngine.compile(content)
        } catch (ex: RuntimeException) {
            if (!ex.isNoMatchingOutputFiles()) throw ex
        }
    }
    val ktFile = compileContentForTest(content)
    return visitFile(ktFile, languageVersionSettings = languageVersionSettings).filterSuppressed(this)
}

fun <T> T.lintWithContext(
    environment: KotlinEnvironmentContainer,
    @Language("kotlin") content: String,
    @Language("kotlin") vararg dependencyContents: String,
    allowCompilationErrors: Boolean = false,
    languageVersionSettings: LanguageVersionSettings = environment.configuration.languageVersionSettings,
): List<Finding> where T : Rule, T : RequiresAnalysisApi {
    val ktFile = KotlinAnalysisApiEngine.compile(
        code = content,
        dependencyCodes = dependencyContents.toList(),
        javaSourceRoots = environment.configuration.javaSourceRoots.map(::Path),
        allowCompilationErrors = allowCompilationErrors
    )
    return visitFile(ktFile, languageVersionSettings).filterSuppressed(this)
}

fun Rule.lint(
    ktFile: KtFile,
    languageVersionSettings: LanguageVersionSettings = FakeLanguageVersionSettings(),
): List<Finding> {
    require(this !is RequiresFullAnalysis) {
        "${this.ruleName} requires full analysis so you should use lintWithContext instead of lint"
    }
    require(this !is RequiresAnalysisApi) {
        "${this.ruleName} requires Analysis Api so you should use lintWithContext instead of lint"
    }
    return visitFile(ktFile, languageVersionSettings = languageVersionSettings).filterSuppressed(this)
}

private fun List<Finding>.filterSuppressed(rule: Rule): List<Finding> =
    filterNot {
        it.entity.ktElement.isSuppressedBy(rule.ruleName.value, rule.aliases, RuleSet.Id("NoARuleSetId"))
    }

private val Rule.aliases: Set<String> get() = config.valueOrDefault(Config.ALIASES_KEY, emptyList<String>()).toSet()

private fun RuntimeException.isNoMatchingOutputFiles() =
    message?.contains("Compilation produced no matching output files") == true
