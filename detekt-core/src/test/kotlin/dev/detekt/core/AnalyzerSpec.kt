package dev.detekt.core

import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import dev.detekt.api.Config
import dev.detekt.api.Configuration
import dev.detekt.api.Entity
import dev.detekt.api.FileProcessListener
import dev.detekt.api.Finding
import dev.detekt.api.Issue
import dev.detekt.api.Rule
import dev.detekt.api.RuleInstance
import dev.detekt.api.RuleSetId
import dev.detekt.api.Severity
import dev.detekt.api.SourceLocation
import dev.detekt.api.TextLocation
import dev.detekt.api.config
import dev.detekt.test.utils.compileContentForTest
import dev.detekt.test.utils.compileForTest
import dev.detekt.test.utils.resourceAsPath
import dev.detekt.tooling.api.AnalysisMode
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.intellij.lang.annotations.Language
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.psiUtil.elementsInRange
import org.jetbrains.kotlin.psi.psiUtil.getNonStrictParentOfType
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.net.URI
import java.util.concurrent.CompletionException
import kotlin.io.path.Path

class AnalyzerSpec {

    @Nested
    inner class `exceptions during analyze()` {
        @Test
        fun `throw error explicitly when config has wrong value type in config`() {
            val testFile = path.resolve("Test.kt")
            val settings = createProcessingSettings()
            val analyzer = Analyzer(
                settings,
                createRuleDescriptor(
                    ::MaxLineLength,
                    """
                        custom:
                          MaxLineLength:
                            active: true
                            maxLineLength: 'abc'
                    """.trimIndent()
                        .toConfig("custom", "MaxLineLength"),
                )
            )

            assertThatThrownBy { settings.use { analyzer.run(listOf(compileForTest(testFile))) } }
                .isInstanceOf(IllegalStateException::class.java)
        }

        @Test
        fun `throw error explicitly in parallel when config has wrong value in config`() {
            val testFile = path.resolve("Test.kt")
            val settings = createProcessingSettings {
                execution {
                    parallelParsing = true
                    parallelAnalysis = true
                }
            }
            val analyzer = Analyzer(
                settings,
                createRuleDescriptor(
                    ::MaxLineLength,
                    """
                        custom:
                          MaxLineLength:
                            active: true
                            maxLineLength: 'abc'
                    """.trimIndent()
                        .toConfig("custom", "MaxLineLength"),
                )
            )

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
            val settings = createProcessingSettings()
            val analyzer = Analyzer(
                settings,
                createRuleDescriptor(
                    ::MaxLineLength,
                    """
                        custom:
                          MaxLineLength:
                            active: true
                            maxLineLength: 120
                    """.trimIndent()
                        .toConfig("custom", "MaxLineLength"),
                ),
            )

            assertThat(settings.use { analyzer.run(listOf(compileForTest(testFile))) })
                .isEmpty()
        }

        @Test
        fun `with findings`() {
            val testFile = path.resolve("Test.kt")
            val settings = createProcessingSettings()
            val analyzer = Analyzer(
                settings,
                createRuleDescriptor(
                    ::MaxLineLength,
                    """
                        custom:
                          MaxLineLength:
                            active: true
                            maxLineLength: 30
                    """.trimIndent()
                        .toConfig("custom", "MaxLineLength"),
                ),
            )

            assertThat(settings.use { analyzer.run(listOf(compileForTest(testFile))) }).singleElement()
                .isEqualTo(
                    Issue(
                        ruleInstance = RuleInstance(
                            id = "MaxLineLength",
                            ruleSetId = RuleSetId("custom"),
                            url = URI("https://example.org/"),
                            description = "TestDescription",
                            severity = Severity.Error,
                            active = true,
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
        }

        @Test
        fun `with findings but ignored`() {
            val testFile = path.resolve("Test.kt")
            val settings = createProcessingSettings()
            val analyzer = Analyzer(
                settings,
                createRuleDescriptor(
                    ::MaxLineLength,
                    """
                        custom:
                          MaxLineLength:
                            active: true
                            maxLineLength: 30
                            ignoreAnnotated:
                              - "AnAnnotation"
                    """.trimIndent()
                        .toConfig("custom", "MaxLineLength"),
                ),
            )

            assertThat(settings.use { analyzer.run(listOf(compileForTest(testFile))) }).isEmpty()
        }

        @Test
        fun `with faulty rule`() {
            val testFile = path.resolve("Test.kt")
            val settings = createProcessingSettings()
            val analyzer = Analyzer(
                settings,
                createRuleDescriptor(
                    ::FaultyRule,
                    """
                        custom:
                          FaultyRule:
                            active: true
                    """.trimIndent()
                        .toConfig("custom", "FaultyRule")
                )
            )

            assertThatThrownBy { settings.use { analyzer.run(listOf(compileForTest(testFile))) } }
                .hasCauseInstanceOf(IllegalStateException::class.java)
                .hasMessageContaining("Location: ${FaultyRule::class.java.name}")
        }

        @Test
        fun `with faulty rule without stack trace`() {
            val testFile = path.resolve("Test.kt")
            val settings = createProcessingSettings()
            val analyzer = Analyzer(
                settings,
                createRuleDescriptor(
                    ::FaultyRuleNoStackTrace,
                    """
                        custom:
                          FaultyRuleNoStackTrace:
                            active: true
                    """.trimIndent()
                        .toConfig("custom", "FaultyRuleNoStackTrace")
                )
            )

            assertThatThrownBy { settings.use { analyzer.run(listOf(compileForTest(testFile))) } }
                .hasCauseInstanceOf(IllegalStateException::class.java)
                .hasMessageContaining("Location: ${null}")
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
                includes: List<String>? = null,
            ): Boolean {
                fun List<String>.toYaml() = joinToString(", ", "[", "]") { "\"$it\"" }

                return isPathChecked(
                    path,
                    """
                        custom:
                          NoEmptyFile:
                            active: true
                            ${if (excludes != null) "excludes: ${excludes.toYaml()}" else ""}
                            ${if (includes != null) "includes: ${includes.toYaml()}" else ""}
                    """.trimIndent()
                        .toConfig("custom", "NoEmptyFile"),
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
                includes: List<String>? = null,
            ): Boolean {
                fun List<String>.toYaml() = joinToString(", ", "[", "]") { "\"$it\"" }

                return isPathChecked(
                    path,
                    """
                        custom:
                          ${if (excludes != null) "excludes: ${excludes.toYaml()}" else ""}
                          ${if (includes != null) "includes: ${includes.toYaml()}" else ""}
                          NoEmptyFile:
                            active: true
                    """.trimIndent()
                        .toConfig("custom", "NoEmptyFile"),
                )
            }
        }

        private fun isPathChecked(path: String, config: Config): Boolean {
            val root = resourceAsPath("include_exclude")
            val pathToCheck = resourceAsPath("include_exclude").resolve(path)

            return createProcessingSettings(config = config) { project { basePath = root } }
                .use { settings ->
                    Analyzer(settings, createRuleDescriptor(::NoEmptyFile, config))
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
            val code = """
                @file:Suppress("$suppress")
                
                fun foo() = Unit
            """.trimIndent()

            val findings = createProcessingSettings().use { settings ->
                Analyzer(
                    settings,
                    createRuleDescriptor(
                        ::MaxLineLength,
                        """
                            custom:
                              MaxLineLength:
                                active: true
                                maxLineLength: 10
                                aliases: ["MLL"]
                        """.trimIndent()
                            .toConfig("custom", "MaxLineLength")
                    )
                )
                    .run(listOf(compileContentForTest(code)))
            }
            assertThat(findings).isEmpty()
        }

        @ParameterizedTest
        @ValueSource(strings = ["MaxLineLength", "detekt.MaxLineLength", "MLL", "custom", "all"])
        fun `when the ktElement is suppressed the issue is not raised`(suppress: String) {
            val code = """
                @Suppress("$suppress")
                fun foo() = Unit
            """.trimIndent()

            val findings = createProcessingSettings().use { settings ->
                Analyzer(
                    settings,
                    createRuleDescriptor(
                        ::MaxLineLength,
                        """
                            custom:
                              MaxLineLength:
                                active: true
                                maxLineLength: 10
                                aliases: ["MLL"]
                        """.trimIndent()
                            .toConfig("custom", "MaxLineLength")
                    )
                )
                    .run(listOf(compileContentForTest(code)))
            }
            assertThat(findings).isEmpty()
        }
    }
}

private class NoEmptyFile(config: Config) : Rule(config, "TestDescription") {
    override fun visitKtFile(file: KtFile) {
        if (file.text.isEmpty()) {
            report(Finding(Entity.from(file), "This file is empty"))
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
                report(Finding(Entity.from(ktElement), description))
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
    vararg ruleDescriptors: RuleDescriptor,
    processors: List<FileProcessListener> = emptyList(),
    analysisMode: AnalysisMode = AnalysisMode.light,
): Analyzer =
    Analyzer(
        settings,
        ruleDescriptors.toList(),
        processors,
        analysisMode,
    )

internal fun createRuleDescriptor(provider: (Config) -> Rule, config: Config) =
    RuleDescriptor(
        provider,
        config,
        RuleInstance(
            id = provider(Config.empty).javaClass.simpleName,
            ruleSetId = RuleSetId("custom"),
            url = URI("https://example.org/"),
            description = "TestDescription",
            severity = Severity.Error,
            active = true,
        ),
    )

// The @receiver:Language("yaml") does nothing because of this bug on IntelliJ
// https://youtrack.jetbrains.com/issue/KTIJ-5643/Language-injection-does-not-work-for-extension-receivers
private fun @receiver:Language("yaml") String.toConfig(vararg subConfigs: String): Config =
    subConfigs.fold(yamlConfigFromContent(this)) { acc, key -> acc.subConfig(key) }
