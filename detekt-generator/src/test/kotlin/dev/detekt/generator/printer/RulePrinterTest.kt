package dev.detekt.generator.printer

import dev.detekt.generator.collection.Active
import dev.detekt.generator.collection.Inactive
import dev.detekt.generator.printer.RulePrinter
import dev.detekt.generator.collection.Rule
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class RulePrinterTest {
    val ruleTemplate = Rule(
        name = "RuleName",
        description = "rule description",
        nonCompliantCodeExample = "",
        compliantCodeExample = "",
        defaultActivationStatus = Inactive,
        aliases = listOf("alias1", "alias2"),
        parent = "",
    )

    @Test
    fun `rule name`() {
        val rule = ruleTemplate.copy(name = "RuleName")
        val actual = RulePrinter.print(rule)
        assertThat(actual).contains("""### RuleName""")
    }

    @Nested
    inner class Description {
        @Test
        fun `empty description`() {
            val rule = ruleTemplate.copy(description = "")
            val actual = RulePrinter.print(rule)
            assertThat(actual).contains("TODO: Specify description")
        }

        @Test
        fun `with description`() {
            val description = "This is the description"
            val rule = ruleTemplate.copy(description = description)
            val actual = RulePrinter.print(rule)
            assertThat(actual).contains(description)
        }

        @Test
        fun `with html tags does not escape them`() {
            val description = "The return type is `Array<String>`"
            val rule = ruleTemplate.copy(description = description)
            val actual = RulePrinter.print(rule)
            assertThat(actual).contains("The return type is `Array<String>`")
        }
    }

    @Nested
    inner class ActiveByDefault {
        @Test
        fun inactive() {
            val rule = ruleTemplate.copy(defaultActivationStatus = Inactive)
            val actual = RulePrinter.print(rule)
            assertThat(actual).contains("""**Active by default**: No""")
        }

        @Test
        fun active() {
            val rule = ruleTemplate.copy(defaultActivationStatus = Active("1.2.3"))
            val actual = RulePrinter.print(rule)
            assertThat(actual).contains("""**Active by default**: Yes - Since v1.2.3""")
        }
    }

    @Nested
    inner class Aliases {
        @Test
        fun `no alias`() {
            val rule = ruleTemplate.copy(aliases = emptyList())
            val actual = RulePrinter.print(rule)
            assertThat(actual).doesNotContainIgnoringCase("aliases")
        }

        @Test
        fun `with alias`() {
            val rule = ruleTemplate.copy(aliases = listOf("alias1", "alias2"))
            val actual = RulePrinter.print(rule)
            assertThat(actual).contains("""**Aliases**: alias1, alias2""")
        }
    }

    @Nested
    inner class TypeResolution {
        @Test
        fun `no type resolution`() {
            val rule = ruleTemplate.copy(requiresFullAnalysis = false)
            val actual = RulePrinter.print(rule)
            assertThat(actual).doesNotContainIgnoringCase("type resolution")
        }

        @Test
        fun `with type resolution`() {
            val rule = ruleTemplate.copy(requiresFullAnalysis = true)
            val actual = RulePrinter.print(rule)
            assertThat(actual).contains("""**Requires Type Resolution**""")
        }
    }
}
