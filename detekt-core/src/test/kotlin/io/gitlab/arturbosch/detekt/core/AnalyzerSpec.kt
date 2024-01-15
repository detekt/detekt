package io.gitlab.arturbosch.detekt.core

import io.github.detekt.test.utils.StringPrintStream
import io.github.detekt.test.utils.compileContentForTest
import io.github.detekt.test.utils.compileForTest
import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.RequiresTypeResolution
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.RuleSetProvider
import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import io.gitlab.arturbosch.detekt.test.getContextForPaths
import io.gitlab.arturbosch.detekt.test.yamlConfigFromContent
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.psi.KtFile
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
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
                inputPath = testFile,
                config = yamlConfigFromContent(
                    """
                        custom:
                          MaxLineLength:
                            active: true
                            maxLineLength: 'abc'
                    """.trimIndent()
                ),
            )
            val analyzer = Analyzer(settings, listOf(CustomRuleSetProvider()), emptyList())

            assertThatThrownBy {
                settings.use { analyzer.run(listOf(compileForTest(testFile))) }
            }.isInstanceOf(IllegalStateException::class.java)
        }

        @Test
        fun `throw error explicitly in parallel when config has wrong value in config`() {
            val testFile = path.resolve("Test.kt")
            val settings = createProcessingSettings(
                inputPath = testFile,
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
            val analyzer = Analyzer(settings, listOf(CustomRuleSetProvider()), emptyList())

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
                yamlConfigFromContent(
                    """
                        custom:
                          MaxLineLength:
                            active: true
                            maxLineLength: 120
                          RequiresTypeResolutionMaxLineLength:
                            active: true
                            maxLineLength: 120
                    """.trimIndent()
                ),
                outputChannel = output,
            )
            val analyzer = Analyzer(settings, listOf(CustomRuleSetProvider()), emptyList())

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
                yamlConfigFromContent(
                    """
                        custom:
                          MaxLineLength:
                            active: true
                            maxLineLength: 30
                          RequiresTypeResolutionMaxLineLength:
                            active: true
                            maxLineLength: 30
                    """.trimIndent()
                ),
                outputChannel = output,
            )
            val analyzer = Analyzer(settings, listOf(CustomRuleSetProvider()), emptyList())

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
                yamlConfigFromContent(
                    """
                        custom:
                          MaxLineLength:
                            active: true
                            maxLineLength: 30
                          RequiresTypeResolutionMaxLineLength:
                            active: true
                            maxLineLength: 30
                    """.trimIndent()
                ),
                outputChannel = output,
            )
            val analyzer = Analyzer(settings, listOf(CustomRuleSetProvider()), emptyList())
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
                yamlConfigFromContent(
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
            val analyzer = Analyzer(settings, listOf(CustomRuleSetProvider()), emptyList())

            assertThat(settings.use { analyzer.run(listOf(compileForTest(testFile))) }.values.flatten()).isEmpty()
            assertThat(output.toString()).isEmpty()
        }

        @Test
        fun `with faulty rule`() {
            val testFile = path.resolve("Test.kt")
            val output = StringPrintStream()
            val settings = createProcessingSettings(
                testFile,
                yamlConfigFromContent(
                    """
                      custom:
                        FaultyRule:
                          active: true
                    """.trimIndent()
                ),
                outputChannel = output,
            )
            val analyzer = Analyzer(settings, listOf(CustomRuleSetProvider()), emptyList())

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
                yamlConfigFromContent(
                    """
                        custom:
                          FaultyRuleNoStackTrace:
                            active: true
                    """.trimIndent()
                ),
                outputChannel = output,
            )
            val analyzer = Analyzer(settings, listOf(CustomRuleSetProvider()), emptyList())

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
            val root = Path("/foo/bar/sample/")

            return createProcessingSettings(config = config) { project { basePath = root } }
                .use { settings ->
                    Analyzer(settings, listOf(CustomRuleSetProvider()), emptyList())
                        .run(listOf(compileContentForTest("", root, Path(path))))
                        .values
                        .flatten()
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
                Analyzer(settings, listOf(CustomRuleSetProvider()), emptyList())
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
            ::RequiresTypeResolutionMaxLineLength,
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

private class MaxLineLength(config: Config) : Rule(config, "TestDescription") {
    private val lengthThreshold: Int = config.valueOrDefault("maxLineLength", 10)
    override fun visitKtFile(file: KtFile) {
        super.visitKtFile(file)
        for (line in file.text.lineSequence()) {
            if (line.length > lengthThreshold) {
                report(CodeSmell(Entity.atPackageOrFirstDecl(file), description))
            }
        }
    }
}

@RequiresTypeResolution
private class RequiresTypeResolutionMaxLineLength(config: Config) : Rule(config, "TestDescription") {
    private val lengthThreshold: Int = config.valueOrDefault("maxLineLength", 10)
    override fun visitKtFile(file: KtFile) {
        super.visitKtFile(file)
        for (line in file.text.lineSequence()) {
            if (line.length > lengthThreshold) {
                report(CodeSmell(Entity.atPackageOrFirstDecl(file), description))
            }
        }
    }
}

private class FaultyRule(config: Config) : Rule(config, "") {
    override fun visitKtFile(file: KtFile) {
        throw object : IllegalStateException("Deliberately triggered error.") {}
    }
}

private class FaultyRuleNoStackTrace(config: Config) : Rule(config, "") {
    override fun visitKtFile(file: KtFile) {
        throw object : IllegalStateException("Deliberately triggered error without stack trace.") {
            init {
                stackTrace = emptyArray()
            }
        }
    }
}
