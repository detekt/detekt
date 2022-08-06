package io.gitlab.arturbosch.detekt.formatting

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.formatting.wrappers.CommentWrapping
import io.gitlab.arturbosch.detekt.test.assertThat
import org.junit.jupiter.api.Test

/**
 * Some test cases were used directly from KtLint to verify the [CommentWrapping] rule:
 *
 * https://github.com/pinterest/ktlint/blob/master/ktlint-ruleset-experimental/src/test/kotlin/com/pinterest/ktlint/ruleset/experimental/CommentWrappingRuleTest.kt
 */
class CommentWrappingSpec {
    @Test
    fun `Given a single line block comment that start starts and end on a separate line then report no error`() {
        val code = """
            /* Some comment */
        """.trimIndent()
        assertThat(CommentWrapping(Config.empty).lint(code)).isEmpty()
    }

    @Test
    fun `Given a block comment followed by a code element on the same line as the block comment ended then report four errors`() {
        val code = """
            /* Some comment 1 */ val foo1 = "foo1"
            /* Some comment 2 */val foo2 = "foo2"
            /* Some comment 3 */ fun foo3() = "foo3"
            /* Some comment 4 */fun foo4() = "foo4"
        """.trimIndent()

        assertThat(CommentWrapping(Config.empty).lint(code)).hasSize(4)
    }
}
