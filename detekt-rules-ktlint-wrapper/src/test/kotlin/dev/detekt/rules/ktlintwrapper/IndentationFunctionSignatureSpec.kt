package dev.detekt.rules.ktlintwrapper

import dev.detekt.api.modifiedText
import dev.detekt.rules.ktlintwrapper.wrappers.FunctionSignature
import dev.detekt.rules.ktlintwrapper.wrappers.Indentation
import dev.detekt.test.TestConfig
import dev.detekt.test.assertj.assertThat
import dev.detekt.test.lint
import dev.detekt.test.utils.compileContentForTest
import org.junit.jupiter.api.Test
import org.assertj.core.api.Assertions.assertThat as assertThatJ

/**
 * Regression test for https://github.com/detekt/detekt/issues/9295.
 *
 * `indent_style` used to only reach the `Indentation` rule, so setting it to `tab` made `Indentation`
 * and `FunctionSignature` disagree on how to indent the parameters of a multi-line signature; with
 * autocorrect on, they'd undo each other's changes. The config below mirrors how detekt resolves it:
 * the value sits on the `ktlint` rule set, with the rules nested under it.
 */
class IndentationFunctionSignatureSpec {

    @Test
    fun `Indentation and FunctionSignature agree on tab-indented multiline signature`() {
        val ktlintConfig = TestConfig(
            "ktlint" to mapOf(
                "indent_style" to "tab",
                "autoCorrect" to true,
                "Indentation" to mapOf("active" to true),
                "FunctionSignature" to mapOf("active" to true),
            ),
        ).subConfig("ktlint")
        val file = compileContentForTest(TAB_INDENTED_SNIPPET)

        val findings = Indentation(ktlintConfig.subConfig("Indentation")).lint(file) +
            FunctionSignature(ktlintConfig.subConfig("FunctionSignature")).lint(file)

        assertThat(findings).isEmpty()
        assertThatJ(file.modifiedText).isNull()
    }

    companion object {
        private val TAB_INDENTED_SNIPPET = listOf(
            "class A {",
            "\tfun func(",
            "\t\targ1: SomeLongType,",
            "\t\targ2: SomeOtherLongType,",
            "\t\targ3: AnotherLongTypeNameBecauseWeLoveJavaNamingSchemeFactory,",
            "\t\targ4: RunningOutOfTypeNameIdeas,",
            "\t) {",
            "\t}",
            "}",
        ).joinToString(separator = "\n", postfix = "\n")
    }
}
