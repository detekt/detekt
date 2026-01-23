package dev.detekt.rules.ktlintwrapper

import dev.detekt.api.Config
import dev.detekt.rules.ktlintwrapper.wrappers.CommentWrapping
import dev.detekt.rules.ktlintwrapper.wrappers.NoSingleLineBlockComment
import dev.detekt.test.assertj.assertThat
import org.junit.jupiter.api.Test

class NoSingleLineBlockCommentSpec {
    @Test
    fun `Given a single documentation comment that start starts and end on a same line`() {
        val code = """
            /** Some comment */
        """.trimIndent()
        assertThat(NoSingleLineBlockComment(Config.Empty).lint(code)).isEmpty()
    }

    @Test
    fun `Given a single block comment that start starts and end on a same line`() {
        val code = """
            /* Some comment */
        """.trimIndent()
        assertThat(NoSingleLineBlockComment(Config.Empty).lint(code)).hasSize(1)
    }

    @Test
    fun `Given a block comment followed by a code element on the same line as the block`() {
        val code = """
            /* Some comment 1 */ val foo1 = "foo1"
            /* Some comment 2 */val foo2 = "foo2"
            /* Some comment 3 */ fun foo3() = "foo3"
            /* Some comment 4 */fun foo4() = "foo4"
        """.trimIndent()

        assertThat(CommentWrapping(Config.Empty).lint(code)).hasSize(4)
    }
}
