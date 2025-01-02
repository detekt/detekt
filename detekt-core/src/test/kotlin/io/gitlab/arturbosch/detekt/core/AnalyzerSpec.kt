package io.gitlab.arturbosch.detekt.core

import io.github.detekt.test.utils.StringPrintStream
import io.github.detekt.test.utils.compileContentForTest
import io.github.detekt.test.utils.compileForTest
import io.github.detekt.test.utils.resourceAsPath
import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Configuration
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.FileProcessListener
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.RequiresFullAnalysis
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.RuleInstance
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.RuleSetProvider
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.SourceLocation
import io.gitlab.arturbosch.detekt.api.TextLocation
import io.gitlab.arturbosch.detekt.api.config
import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import io.gitlab.arturbosch.detekt.test.createBindingContext
import io.gitlab.arturbosch.detekt.test.yamlConfigFromContent
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.com.intellij.openapi.util.TextRange
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.psiUtil.elementsInRange
import org.jetbrains.kotlin.psi.psiUtil.getNonStrictParentOfType
import org.jetbrains.kotlin.resolve.BindingContext
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.net.URI
import java.util.concurrent.CompletionException
import kotlin.io.path.Path

@KotlinCoreEnvironmentTest
class AnalyzerSpec(val env: KotlinCoreEnvironment) {

    @Nested
    inner class `exceptions during analyze()` {
        @Test
        fun `throw error explicitly when config has wrong value type in config`() {
            val testFile = path.resolve("Test.kt")
            val settings = createProcessingSettings(
                config = yamlConfigFromContent(
                    """
                        custom:
                          MaxLineLength:
                            active: true
                            maxLineLength: 'abc'
                    """.trimIndent()
                ),
            )
            val analyzer = Analyzer(settings, CustomRuleSetProvider())

            assertThatThrownBy { settings.use { analyzer.run(listOf(compileForTest(testFile))) } }
                .isInstanceOf(IllegalStateException::class.java)
        }

        @Test
        fun `throw error explicitly in parallel when config has wrong value in config`() {
            val testFile = path.resolve("Test.kt")
            val settings = createProcessingSettings(
                config = yamlConfigFromContent(
                    """
                        custom:
                          MaxLineLength:
                            active: true
                            maxLineLength: 'abc'
                    """.trimIndent()
                ),
            ) {
                execution {
                    parallelParsing = true
                    parallelAnalysis = true
                }
            }
            val analyzer = Analyzer(settings, CustomRuleSetProvider())

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
                config = yamlConfigFromContent(
                    """
                        custom:
                          MaxLineLength:
                            active: true
                            maxLineLength: 120
                          RequiresFullAnalysisMaxLineLength/foo:
                            active: true
                            maxLineLength: 120
                    """.trimIndent()
                ),
                outputChannel = output,
            )
            val analyzer = Analyzer(settings, CustomRuleSetProvider())

            assertThat(settings.use { analyzer.run(listOf(compileForTest(testFile))) }).isEmpty()
            assertThat(output.toString()).isEqualTo(
                "The rule 'RequiresFullAnalysisMaxLineLength/foo' requires type resolution but it was run without it.\n"
            )
        }

        @Test
        fun `with findings`() {
            val testFile = path.resolve("Test.kt")
            val output = StringPrintStream()
            val settings = createProcessingSettings(
                config = yamlConfigFromContent(
                    """
                        custom:
                          MaxLineLength/foo:
                            active: true
                            maxLineLength: 30
                          RequiresFullAnalysisMaxLineLength:
                            active: true
                            maxLineLength: 30
                    """.trimIndent()
                ),
                outputChannel = output,
            )
            val analyzer = Analyzer(settings, CustomRuleSetProvider())

            assertThat(settings.use { analyzer.run(listOf(compileForTest(testFile))) })
                .singleElement()
                .isEqualTo(
                    Issue(
                        ruleInstance = RuleInstance(
                            id = "MaxLineLength/foo",
                            ruleSetId = RuleSet.Id("custom"),
                            url = URI("https://detekt.dev/docs/rules/custom#maxlinelength"),
                            description = "TestDescription",
                            severity = Severity.Error,
                        ),
                        entity = Issue.Entity(
                            "AnAnnotation$@Target(AnnotationTarget.FILE, AnnotationTarget.FUNCTION)",
                            Issue.Location(
                                source = SourceLocation(8, 1),
                                endSource = SourceLocation(8, 58),
                                text = TextLocation(67, 124),
                                path = Path("build/resources/test/cases/Test.kt"),
                            ),
                        ),
                        references = emptyList(),
                        message = "TestDescription",
                        severity = Severity.Error,
                        suppressReasons = emptyList(),
                    )
                )
            assertThat(output.toString()).isEqualTo(
                "The rule 'RequiresFullAnalysisMaxLineLength' requires type resolution but it was run without it.\n"
            )
        }

        @Test
        fun `with multiple instances of same rule`() {
            val testFile = path.resolve("Test.kt")
            val output = StringPrintStream()
            val settings = createProcessingSettings(
                config = yamlConfigFromContent(
                    """
                        custom:
                          MaxLineLength/foo:
                            active: true
                            maxLineLength: 30
                          MaxLineLength/bar:
                            active: true
                            maxLineLength: 30
                    """.trimIndent()
                ),
                outputChannel = output,
            )
            val analyzer = Analyzer(settings, CustomRuleSetProvider())

            assertThat(settings.use { analyzer.run(listOf(compileForTest(testFile))) }).hasSize(2)
        }

        @Test
        fun `with findings and context binding`() {
            val testFile = path.resolve("Test.kt")
            val output = StringPrintStream()
            val settings = createProcessingSettings(
                config = yamlConfigFromContent(
                    """
                        custom:
                          MaxLineLength:
                            active: true
                            maxLineLength: 30
                          RequiresFullAnalysisMaxLineLength:
                            active: true
                            maxLineLength: 30
                    """.trimIndent()
                ),
                outputChannel = output,
            )
            val ktFile = compileForTest(testFile)
            val bindingContext = env.createBindingContext(listOf(ktFile))

            val analyzer = Analyzer(settings, CustomRuleSetProvider(), bindingContext = bindingContext)
            assertThat(settings.use { analyzer.run(listOf(ktFile)) }).hasSize(2)
            assertThat(output.toString()).isEmpty()
        }

        @Test
        fun `with findings but ignored`() {
            val testFile = path.resolve("Test.kt")
            val output = StringPrintStream()
            val settings = createProcessingSettings(
                config = yamlConfigFromContent(
                    """
                        custom:
                          MaxLineLength:
                            active: true
                            maxLineLength: 30
                            ignoreAnnotated:
                              - "AnAnnotation"
                    """.trimIndent()
                ),
                outputChannel = output,
            )
            val analyzer = Analyzer(settings, CustomRuleSetProvider())

            assertThat(settings.use { analyzer.run(listOf(compileForTest(testFile))) }).isEmpty()
            assertThat(output.toString()).isEmpty()
        }

        @Test
        fun `with faulty rule`() {
            val testFile = path.resolve("Test.kt")
            val output = StringPrintStream()
            val settings = createProcessingSettings(
                config = yamlConfigFromContent(
                    """
                      custom:
                        FaultyRule:
                          active: true
                    """.trimIndent()
                ),
                outputChannel = output,
            )
            val analyzer = Analyzer(settings, CustomRuleSetProvider())

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
                config = yamlConfigFromContent(
                    """
                        custom:
                          FaultyRuleNoStackTrace:
                            active: true
                    """.trimIndent()
                ),
                outputChannel = output,
            )
            val analyzer = Analyzer(settings, CustomRuleSetProvider())

            assertThatThrownBy { settings.use { analyzer.run(listOf(compileForTest(testFile))) } }
                .hasCauseInstanceOf(IllegalStateException::class.java)
                .hasMessageContaining("Location: ${null}")
            assertThat(output.toString()).isEmpty()
        }
    }

    @Nested
    inner class IncludeExclude {

        @Nested
        inner class Rule {
            @Test
            fun `A file excluded and included is not checked`() {
                assertThat(isPathChecked("foo/Test.kt", excludes = listOf("**/*.kt"), includes = listOf("**/*.kt")))
                    .isFalse()
            }

            @Test
            fun `A file included is checked`() {
                assertThat(isPathChecked("foo/Test.kt", includes = listOf("**/foo/*.kt")))
                    .isTrue()
            }

            @Test
            fun `A file not included is not checked`() {
                assertThat(isPathChecked("foo/Test.kt", includes = listOf("**/bar/*.kt")))
                    .isFalse()
            }

            @Test
            fun `A file excluded is not checked`() {
                assertThat(isPathChecked("foo/Test.kt", excludes = listOf("**/foo/*.kt")))
                    .isFalse()
            }

            @Test
            fun `A file not excluded is checked`() {
                assertThat(isPathChecked("foo/Test.kt", excludes = listOf("**/bar/*.kt")))
                    .isTrue()
            }

            private fun isPathChecked(
                path: String,
                excludes: List<String>? = null,
                includes: List<String>? = null
            ): Boolean {
                fun List<String>.toYaml() = joinToString(", ", "[", "]") { "\"$it\"" }

                return isPathChecked(
                    path,
                    yamlConfigFromContent(
                        """
                            custom:
                              NoEmptyFile:
                                active: true
                                ${if (excludes != null) "excludes: ${excludes.toYaml()}" else ""}
                                ${if (includes != null) "includes: ${includes.toYaml()}" else ""}
                        """.trimIndent()
                    ),
                )
            }
        }

        @Nested
        inner class RuleSet {
            @Test
            fun `A file excluded and included is not checked`() {
                assertThat(isPathChecked("foo/Test.kt", excludes = listOf("**/*.kt"), includes = listOf("**/*.kt")))
                    .isFalse()
            }

            @Test
            fun `A file included is checked`() {
                assertThat(isPathChecked("foo/Test.kt", includes = listOf("**/foo/*.kt")))
                    .isTrue()
            }

            @Test
            fun `A file not included is not checked`() {
                assertThat(isPathChecked("foo/Test.kt", includes = listOf("**/bar/*.kt")))
                    .isFalse()
            }

            @Test
            fun `A file excluded is not checked`() {
                assertThat(isPathChecked("foo/Test.kt", excludes = listOf("**/foo/*.kt")))
                    .isFalse()
            }

            @Test
            fun `A file not excluded is checked`() {
                assertThat(isPathChecked("foo/Test.kt", excludes = listOf("**/bar/*.kt")))
                    .isTrue()
            }

            private fun isPathChecked(
                path: String,
                excludes: List<String>? = null,
                includes: List<String>? = null
            ): Boolean {
                fun List<String>.toYaml() = joinToString(", ", "[", "]") { "\"$it\"" }

                return isPathChecked(
                    path,
                    yamlConfigFromContent(
                        """
                            custom:
                              ${if (excludes != null) "excludes: ${excludes.toYaml()}" else ""}
                              ${if (includes != null) "includes: ${includes.toYaml()}" else ""}
                              NoEmptyFile:
                                active: true
                        """.trimIndent()
                    ),
                )
            }
        }

        private fun isPathChecked(
            path: String,
            config: Config,
        ): Boolean {
            val root = resourceAsPath("include_exclude")
            val pathToCheck = resourceAsPath("include_exclude").resolve(path)

            return createProcessingSettings(config = config) { project { basePath = root } }
                .use { settings ->
                    Analyzer(settings, CustomRuleSetProvider())
                        .run(listOf(compileForTest(pathToCheck)))
                        .isNotEmpty()
                }
        }
    }

    @Nested
    inner class Suppress {
        @ParameterizedTest
        @ValueSource(strings = ["MaxLineLength", "detekt.MaxLineLength", "MLL", "custom", "all"])
        fun `if suppressed the rule is not executed`(suppress: String) {
            val config = yamlConfigFromContent(
                """
                    custom:
                      MaxLineLength:
                        active: true
                        maxLineLength: 10
                        aliases: ["MLL"]
                """.trimIndent()
            )
            val code = """
                @file:Suppress("$suppress")
                
                fun foo() = Unit
            """.trimIndent()
            val findings = createProcessingSettings(config = config).use { settings ->
                Analyzer(settings, CustomRuleSetProvider())
                    .run(listOf(compileContentForTest(code)))
            }
            assertThat(findings).isEmpty()
        }

        @ParameterizedTest
        @ValueSource(strings = ["MaxLineLength", "detekt.MaxLineLength", "MLL", "custom", "all"])
        fun `when the ktElement is suppressed the issue is not raised`(suppress: String) {
            val config = yamlConfigFromContent(
                """
                    custom:
                      MaxLineLength:
                        active: true
                        maxLineLength: 10
                        aliases: ["MLL"]
                """.trimIndent()
            )
            val code = """
                @Suppress("$suppress")
                fun foo() = Unit
            """.trimIndent()
            val findings = createProcessingSettings(config = config).use { settings ->
                Analyzer(settings, CustomRuleSetProvider())
                    .run(listOf(compileContentForTest(code)))
            }
            assertThat(findings).isEmpty()
        }
    }
}

private class CustomRuleSetProvider : RuleSetProvider {
    override val ruleSetId = RuleSet.Id("custom")
    override fun instance() = RuleSet(
        ruleSetId,
        listOf(
            ::NoEmptyFile,
            ::MaxLineLength,
            ::RequiresFullAnalysisMaxLineLength,
            ::FaultyRule,
            ::FaultyRuleNoStackTrace,
        )
    )
}

private class NoEmptyFile(config: Config) : Rule(config, "TestDescription") {
    override fun visitKtFile(file: KtFile) {
        if (file.text.isEmpty()) {
            report(CodeSmell(Entity.from(file), "This file is empty"))
        }
    }
}

@Suppress("MemberNameEqualsClassName")
private open class MaxLineLength(config: Config) : Rule(config, "TestDescription") {
    @Configuration("lengthThreshold")
    private val maxLineLength by config(10)

    override fun visitKtFile(file: KtFile) {
        super.visitKtFile(file)
        var offset = 0
        for (line in file.text.lineSequence()) {
            if (line.length > maxLineLength) {
                val ktElement = file.findFirstMeaningfulKtElementInParents(offset..(offset + line.length))
                report(CodeSmell(Entity.from(ktElement), description))
            }
            offset += line.length + 1 // \n
        }
    }

    private fun KtFile.findFirstMeaningfulKtElementInParents(range: IntRange): PsiElement =
        elementsInRange(TextRange.create(range.first, range.last))
            .asSequence()
            .plus(findElementAt(range.last))
            .mapNotNull { it?.getNonStrictParentOfType() }
            .first { it.text.isNotBlank() }
}

@RequiresFullAnalysis
private class RequiresFullAnalysisMaxLineLength(config: Config) : MaxLineLength(config)

private class FaultyRule(config: Config) : Rule(config, "") {
    override fun visitKtFile(file: KtFile): Unit =
        throw object : IllegalStateException("Deliberately triggered error.") {}
}

private class FaultyRuleNoStackTrace(config: Config) : Rule(config, "") {
    override fun visitKtFile(file: KtFile): Unit =
        throw object : IllegalStateException("Deliberately triggered error without stack trace.") {
            init {
                stackTrace = emptyArray()
            }
        }
}

internal fun Analyzer(
    settings: ProcessingSettings,
    vararg ruleSetProviders: RuleSetProvider,
    processors: List<FileProcessListener> = emptyList(),
    bindingContext: BindingContext = BindingContext.EMPTY,
): Analyzer = Analyzer(settings, ruleSetProviders.toList(), processors, bindingContext)
