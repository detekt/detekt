package io.gitlab.arturbosch.detekt.formatting

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.formatting.wrappers.EnumEntryNameCase
import io.gitlab.arturbosch.detekt.formatting.wrappers.Filename
import io.gitlab.arturbosch.detekt.formatting.wrappers.ImportOrdering
import io.gitlab.arturbosch.detekt.formatting.wrappers.Indentation
import io.gitlab.arturbosch.detekt.formatting.wrappers.MaximumLineLength
import io.gitlab.arturbosch.detekt.formatting.wrappers.NoWildcardImports
import io.gitlab.arturbosch.detekt.formatting.wrappers.PackageName
import io.gitlab.arturbosch.detekt.test.assertThat
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory

class RulesWhichCantBeCorrectedSpec {

    @TestFactory
    fun `verify findings of these rules are not correctable`(): Iterable<DynamicTest> {
        val commonCode = """
            package under_score
            import xyz.wrong_order
            /*comment in between*/
            import java.io.*
            class NotTheFilename
            class MaximumLeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeth
            enum class Enum {
                violation_triggering_name
            } 
        """.trimIndent()

        val multilineQuote = "${'"'}${'"'}${'"'}"
        val indentationCode = """
                val foo = $multilineQuote
                      line1
                ${'\t'}line2
                    $multilineQuote.trimIndent()
        """.trimIndent()

        return listOf(
            Filename(Config.empty) to commonCode,
            PackageName(Config.empty) to commonCode,
            NoWildcardImports(Config.empty) to commonCode,
            MaximumLineLength(Config.empty) to commonCode,
            EnumEntryNameCase(Config.empty) to commonCode,
            ImportOrdering(Config.empty) to commonCode,
            Indentation(Config.empty) to indentationCode,
        ).map { (rule, code) ->
            DynamicTest.dynamicTest("${rule.ruleId} should not return correctable code smell") {
                assertThat(rule.lint(code, "non_pascal_case.kt"))
                    .isNotEmpty
                    .hasExactlyElementsOfTypes(CodeSmell::class.java)
            }
        }
    }
}
