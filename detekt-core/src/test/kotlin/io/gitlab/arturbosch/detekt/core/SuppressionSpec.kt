package io.gitlab.arturbosch.detekt.core

import io.github.detekt.test.utils.compileContentForTest
import io.github.detekt.test.utils.compileForTest
import io.github.detekt.test.utils.resourceAsPath
import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Location
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.internal.isSuppressedBy
import io.gitlab.arturbosch.detekt.test.lint
import io.gitlab.arturbosch.detekt.test.yamlConfigFromContent
import org.assertj.core.api.Assertions.assertThat
import org.intellij.lang.annotations.Language
import org.jetbrains.kotlin.psi.KtAnnotated
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.psiUtil.lastBlockStatementOrThis
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class SuppressionSpec {

    @Nested
    inner class `detekt findings can be suppressed with @Suppress or @SuppressWarnings` {

        @Test
        fun `should not be suppressed by a @Deprecated annotation`() {
            assertThat(isSuppressedBy("Deprecated", "This should no longer be used")).isFalse()
        }

        @Test
        fun `should not be suppressed by a @Suppress annotation for another rule`() {
            assertThat(isSuppressedBy("Suppress", "NotATest")).isFalse()
        }

        @Test
        fun `should not be suppressed by a @SuppressWarnings annotation for another rule`() {
            assertThat(isSuppressedBy("SuppressWarnings", "NotATest")).isFalse()
        }

        @Test
        fun `should be suppressed by a @Suppress annotation for the rule`() {
            assertThat(isSuppressedBy("Suppress", "Test")).isTrue()
        }

        @Test
        fun `should be suppressed by a @SuppressWarnings annotation for the rule`() {
            assertThat(isSuppressedBy("SuppressWarnings", "Test")).isTrue()
        }

        @Test
        fun `should be suppressed by a @SuppressWarnings annotation for 'all' rules`() {
            assertThat(isSuppressedBy("Suppress", "all")).isTrue()
        }

        @Test
        fun `should be suppressed by a @SuppressWarnings annotation for 'ALL' rules`() {
            assertThat(isSuppressedBy("SuppressWarnings", "ALL")).isTrue()
        }

        @Test
        fun `should not be suppressed by a @Suppress annotation with a Checkstyle prefix`() {
            assertThat(isSuppressedBy("Suppress", "Checkstyle:Test")).isFalse()
        }

        @Test
        fun `should not be suppressed by a @SuppressWarnings annotation with a Checkstyle prefix`() {
            assertThat(isSuppressedBy("SuppressWarnings", "Checkstyle:Test")).isFalse()
        }

        @Test
        fun `should be suppressed by a @Suppress annotation with a 'Detekt' prefix`() {
            assertThat(isSuppressedBy("Suppress", "Detekt:Test")).isTrue()
        }

        @Test
        fun `should be suppressed by a @SuppressWarnings annotation with a 'Detekt' prefix`() {
            assertThat(isSuppressedBy("SuppressWarnings", "Detekt:Test")).isTrue()
        }

        @Test
        fun `should be suppressed by a @Suppress annotation with a 'detekt' prefix`() {
            assertThat(isSuppressedBy("Suppress", "detekt:Test")).isTrue()
        }

        @Test
        fun `should be suppressed by a @SuppressWarnings annotation with a 'detekt' prefix`() {
            assertThat(isSuppressedBy("SuppressWarnings", "detekt:Test")).isTrue()
        }

        @Test
        fun `should be suppressed by a @Suppress annotation with a 'detekt' prefix with a dot`() {
            assertThat(isSuppressedBy("Suppress", "detekt.Test")).isTrue()
        }

        @Test
        fun `should be suppressed by a @SuppressWarnings annotation with a 'detekt' prefix with a dot`() {
            assertThat(isSuppressedBy("SuppressWarnings", "detekt.Test")).isTrue()
        }

        @Test
        fun `should not be suppressed by a @Suppress annotation with a 'detekt' prefix with a wrong separator`() {
            assertThat(isSuppressedBy("Suppress", "detekt/Test")).isFalse()
        }

        @Test
        fun `should not be suppressed by a @SuppressWarnings annotation with a 'detekt' prefix with a wrong separator`() {
            assertThat(isSuppressedBy("SuppressWarnings", "detekt/Test")).isFalse()
        }

        @Test
        fun `should be suppressed by a @Suppress annotation with an alias`() {
            assertThat(isSuppressedBy("Suppress", "alias")).isTrue()
        }

        @Test
        fun `should be suppressed by a @SuppressWarnings annotation with an alias`() {
            assertThat(isSuppressedBy("SuppressWarnings", "alias")).isTrue()
        }
    }

    @Test
    fun `findings are suppressed`() {
        val ktFile = compileForTest(resourceAsPath("/suppression/SuppressedElements.kt"))
        val findings = listOf(TestLM(), TestLPL()).flatMap { it.visitFile(ktFile) }
        assertThat(findings).isEmpty()
    }

    @Nested
    inner class `suppression's via rule set id` {

        val code = """
            fun lpl(a: Int, b: Int, c: Int, d: Int, e: Int, f: Int) = Unit
        """.trimIndent()

        val config = yamlConfigFromContent(
            """
                complexity:
                  TestLPL:
                    active: true
                    threshold: 5
            """.trimIndent()
        )
            .subConfig("complexity")
            .subConfig("TestLPL")

        @Test
        fun `reports without a suppression`() {
            assertThat(TestLPL(config).lint(code)).isNotEmpty()
        }

        @Test
        fun `reports with wrong suppression`() {
            assertThat(TestLPL(config).lint("""@Suppress("wrong_name_used")$code""")).isNotEmpty()
        }

        @Test
        fun `suppresses by rule set id`() {
            assertCodeIsSuppressed("""@Suppress("complexity")$code""", config)
        }

        @Test
        fun `suppresses by rule set id and detekt prefix`() {
            assertCodeIsSuppressed("""@Suppress("detekt.complexity")$code""", config)
        }

        @Test
        fun `suppresses by rule id`() {
            assertCodeIsSuppressed("""@Suppress("TestLPL")$code""", config)
        }

        @Test
        fun `suppresses by combination of rule set and rule id`() {
            assertCodeIsSuppressed("""@Suppress("complexity.TestLPL")$code""", config)
        }

        @Test
        fun `suppresses by combination of detekt prefix, rule set and rule id`() {
            assertCodeIsSuppressed("""@Suppress("detekt:complexity:TestLPL")$code""", config)
        }

        private fun assertCodeIsSuppressed(@Language("kotlin") code: String, config: Config) {
            val findings = TestLPL(config).lint(code)
            assertThat(findings).isEmpty()
        }
    }
}

private fun isSuppressedBy(annotation: String, argument: String): Boolean {
    val annotated = """
        @$annotation("$argument")
        class Test
    """.trimIndent()
    val file = compileContentForTest(annotated)
    val annotatedClass = file.children.first { it is KtClass } as KtAnnotated
    return annotatedClass.isSuppressedBy(Rule.Id("Test"), setOf("alias"))
}

private class TestLM(config: Config = Config.empty) : Rule(config, "") {
    override fun visitNamedFunction(function: KtNamedFunction) {
        val start = Location.startLineAndColumn(function.funKeyword!!).line
        val end = Location.startLineAndColumn(function.lastBlockStatementOrThis()).line
        val offset = end - start
        if (offset > 10) report(CodeSmell(Entity.from(function), message = "TestMessage"))
    }
}

private class TestLPL(config: Config = Config.empty) : Rule(config, "") {
    override fun visitNamedFunction(function: KtNamedFunction) {
        val size = function.valueParameters.size
        if (size > 5) report(CodeSmell(Entity.from(function), message = "TestMessage"))
    }
}
