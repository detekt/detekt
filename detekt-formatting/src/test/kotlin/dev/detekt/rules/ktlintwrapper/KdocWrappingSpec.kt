package dev.detekt.rules.ktlintwrapper

import dev.detekt.api.Config
import dev.detekt.rules.ktlintwrapper.wrappers.KdocWrapping
import dev.detekt.test.assertThat
import org.junit.jupiter.api.Test

/**
 * Some test cases were used directly from KtLint to verify the [KdocWrapping] rule:
 *
 * https://github.com/pinterest/ktlint/blob/0.45.0/ktlint-ruleset-experimental/src/test/kotlin/com/pinterest/ktlint/ruleset/experimental/KdocWrappingRuleTest.kt
 */
class KdocWrappingSpec {
    @Test
    fun `Given a single line KDoc comment that start starts and end on a separate line then do not reformat`() {
        val code = """
            /** Some KDoc comment */
        """.trimIndent()

        assertThat(KdocWrapping(Config.empty).lint(code)).isEmpty()
    }

    @Test
    fun `Given a KDoc comment followed by a code element on the same line as the block comment ended then report four errors`() {
        val code = """
            /** Some comment 1 */ val foo1 = "foo1"
            /** Some comment 2 */val foo2 = "foo2"
            /** Some comment 3 */ fun foo3() = "foo3"
            /** Some comment 4 */fun foo4() = "foo4"
        """.trimIndent()

        assertThat(KdocWrapping(Config.empty).lint(code)).hasSize(4)
    }
}
