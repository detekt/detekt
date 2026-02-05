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
 * Before the fix, indent style was a per-rule setting on `Indentation` only, so configuring `tab`
 * left `Indentation` and `FunctionSignature` disagreeing on how to indent the parameters of a
 * multi-line signature; with autocorrect on, they'd undo each other's changes. The config below sets
 * it at the `ktlint` rule set level (the way detekt resolves it now), with the rules nested under it.
 */
class IndentationFunctionSignatureSpec {

    @Test
    fun `Indentation and FunctionSignature agree on tab-indented multiline signature`() {
        val ktlintConfig = TestConfig(
            "ktlint" to mapOf(
                "indentStyle" to "tab",
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
