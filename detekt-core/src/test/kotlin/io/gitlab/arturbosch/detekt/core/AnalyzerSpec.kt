package io.gitlab.arturbosch.detekt.core

import io.github.detekt.test.utils.StringPrintStream
import io.github.detekt.test.utils.compileForTest
import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.RuleSetProvider
import io.gitlab.arturbosch.detekt.api.internal.RequiresTypeResolution
import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import io.gitlab.arturbosch.detekt.test.getContextForPaths
import io.gitlab.arturbosch.detekt.test.yamlConfig
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.psi.KtFile
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.concurrent.CompletionException

@KotlinCoreEnvironmentTest
class AnalyzerSpec(val env: KotlinCoreEnvironment) {

    @Nested
    inner class `exceptions during analyze()` {
        @Test
        fun `throw error explicitly when config has wrong value type in config`() {
            val testFile = path.resolve("Test.kt")
            val settings = createProcessingSettings(testFile, yamlConfig("configs/config-value-type-wrong.yml"))
            val analyzer = Analyzer(settings, listOf(StyleRuleSetProvider()), emptyList())

            assertThatThrownBy {
                settings.use { analyzer.run(listOf(compileForTest(testFile))) }
            }.isInstanceOf(IllegalStateException::class.java)
        }

        @Test
        fun `throw error explicitly in parallel when config has wrong value in config`() {
            val testFile = path.resolve("Test.kt")
            val settings = createProcessingSettings(
                inputPath = testFile,
                config = yamlConfig("configs/config-value-type-wrong.yml"),
            ) {
                execution {
                    parallelParsing = true
                    parallelAnalysis = true
                }
            }
            val analyzer = Analyzer(settings, listOf(StyleRuleSetProvider()), emptyList())

            assertThatThrownBy { settings.use { analyzer.run(listOf(compileForTest(testFile))) } }
                .isInstanceOf(CompletionException::class.java)
                .hasCauseInstanceOf(IllegalStateException::class.java)
        }
    }

    @Nested
    inner class `analyze successfully when config has correct value type in config` {

        @Test
        fun `no findings`() {
            val testFile = path.resolve("Test.kt")
            val output = StringPrintStream()
            val settings = createProcessingSettings(
                testFile,
                yamlConfig("configs/config-value-type-correct.yml"),
                outputChannel = output,
            )
            val analyzer = Analyzer(settings, listOf(StyleRuleSetProvider()), emptyList())

            assertThat(settings.use { analyzer.run(listOf(compileForTest(testFile))) }.values.flatten()).isEmpty()
            assertThat(output.toString()).isEqualTo(
                "The rule 'RequiresTypeResolutionMaxLineLength' requires type resolution but it was run without it.\n"
            )
        }

        @Test
        fun `with findings`() {
            val testFile = path.resolve("Test.kt")
            val output = StringPrintStream()
            val settings = createProcessingSettings(
                testFile,
                yamlConfig("configs/config-value-type-correct.yml"),
                outputChannel = output,
            )
            val analyzer = Analyzer(settings, listOf(StyleRuleSetProvider(30)), emptyList())

            assertThat(settings.use { analyzer.run(listOf(compileForTest(testFile))) }.values.flatten()).hasSize(1)
            assertThat(output.toString()).isEqualTo(
                "The rule 'RequiresTypeResolutionMaxLineLength' requires type resolution but it was run without it.\n"
            )
        }

        @Test
        fun `with findings and context binding`() {
            val testFile = path.resolve("Test.kt")
            val output = StringPrintStream()
            val settings = createProcessingSettings(
                testFile,
                yamlConfig("configs/config-value-type-correct.yml"),
                outputChannel = output,
            )
            val analyzer = Analyzer(settings, listOf(StyleRuleSetProvider(30)), emptyList())
            val ktFile = compileForTest(testFile)
            val bindingContext = env.getContextForPaths(listOf(ktFile))

            assertThat(settings.use { analyzer.run(listOf(ktFile), bindingContext) }.values.flatten()).hasSize(2)
            assertThat(output.toString()).isEmpty()
        }

        @Test
        fun `with findings but ignored`() {
            val testFile = path.resolve("Test.kt")
            val output = StringPrintStream()
            val settings = createProcessingSettings(
                testFile,
                yamlConfig("configs/config-value-type-correct-ignore-annotated.yml"),
                outputChannel = output,
            )
            val analyzer = Analyzer(settings, listOf(StyleRuleSetProvider(18)), emptyList())

            assertThat(settings.use { analyzer.run(listOf(compileForTest(testFile))) }.values.flatten()).isEmpty()
            assertThat(output.toString()).isEmpty()
        }

        @Test
        fun `with faulty rule`() {
            val testFile = path.resolve("Test.kt")
            val output = StringPrintStream()
            val settings = createProcessingSettings(
                testFile,
                yamlConfig("configs/config-value-type-correct.yml"),
                outputChannel = output,
            )
            val analyzer = Analyzer(settings, listOf(FaultyRuleSetProvider()), emptyList())

            assertThatThrownBy { settings.use { analyzer.run(listOf(compileForTest(testFile))) } }
                .hasCauseInstanceOf(IllegalStateException::class.java)
                .hasMessageContaining("Location: ${FaultyRule::class.java.name}")
            assertThat(output.toString()).isEmpty()
        }

        @Test
        fun `with faulty rule without stack trace`() {
            val testFile = path.resolve("Test.kt")
            val output = StringPrintStream()
            val settings = createProcessingSettings(
                testFile,
                yamlConfig("configs/config-value-type-correct.yml"),
                outputChannel = output,
            )
            val analyzer = Analyzer(settings, listOf(FaultyRuleNoStackTraceSetProvider()), emptyList())

            assertThatThrownBy { settings.use { analyzer.run(listOf(compileForTest(testFile))) } }
                .hasCauseInstanceOf(IllegalStateException::class.java)
                .hasMessageContaining("Location: ${null}")
            assertThat(output.toString()).isEmpty()
        }
    }
}

private class StyleRuleSetProvider(private val threshold: Int? = null) : RuleSetProvider {
    override val ruleSetId: String = "style"
    override fun instance(config: Config) = RuleSet(
        ruleSetId,
        listOf(
            MaxLineLength(config, threshold),
            RequiresTypeResolutionMaxLineLength(config, threshold),
        )
    )
}

private class MaxLineLength(config: Config, threshold: Int?) : Rule(config) {
    override val issue = Issue(this::class.java.simpleName, "", Debt.FIVE_MINS)
    private val lengthThreshold: Int = threshold ?: valueOrDefault("maxLineLength", 100)
    override fun visitKtFile(file: KtFile) {
        super.visitKtFile(file)
        for (line in file.text.lineSequence()) {
            if (line.length > lengthThreshold) {
                report(CodeSmell(issue, Entity.atPackageOrFirstDecl(file), issue.description))
            }
        }
    }
}

@RequiresTypeResolution
private class RequiresTypeResolutionMaxLineLength(config: Config, threshold: Int?) : Rule(config) {
    override val issue = Issue(this::class.java.simpleName, "", Debt.FIVE_MINS)
    private val lengthThreshold: Int = threshold ?: valueOrDefault("maxLineLength", 100)
    override fun visitKtFile(file: KtFile) {
        super.visitKtFile(file)
        for (line in file.text.lineSequence()) {
            if (line.length > lengthThreshold) {
                report(CodeSmell(issue, Entity.atPackageOrFirstDecl(file), issue.description))
            }
        }
    }
}

private class FaultyRuleSetProvider : RuleSetProvider {
    override val ruleSetId: String = "style"
    override fun instance(config: Config) = RuleSet(ruleSetId, listOf(FaultyRule(config)))
}

private class FaultyRule(config: Config) : Rule(config) {
    override val issue = Issue(this::class.java.simpleName, "", Debt.FIVE_MINS)
    override fun visitKtFile(file: KtFile) {
        throw object : IllegalStateException("Deliberately triggered error.") {}
    }
}

private class FaultyRuleNoStackTraceSetProvider : RuleSetProvider {
    override val ruleSetId: String = "style"
    override fun instance(config: Config) = RuleSet(ruleSetId, listOf(FaultyRuleNoStackTrace(config)))
}

private class FaultyRuleNoStackTrace(config: Config) : Rule(config) {
    override val issue = Issue(this::class.java.simpleName, "", Debt.FIVE_MINS)
    override fun visitKtFile(file: KtFile) {
        throw object : IllegalStateException("Deliberately triggered error without stack trace.") {
            init {
                stackTrace = emptyArray()
            }
        }
    }
}
