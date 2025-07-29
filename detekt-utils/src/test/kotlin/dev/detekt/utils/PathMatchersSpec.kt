package dev.detekt.utils

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.io.path.Path

class PathMatchersSpec {

    @Nested
    inner class `supports globbing` {
        private val matcher = pathMatcher("glob:**/detekt/api/**")

        @Test
        fun `should match`() {
            assertThat(matcher.matches(Path("/detekt/api/Issue.kt"))).isTrue()
        }

        @Test
        fun `should not match`() {
            assertThat(matcher.matches(Path("/detekt/cli/Issue.kt"))).isFalse()
        }

        @Test
        fun `should work with windows like paths`() {
            assertThat(matcher.matches(Path("C:/detekt/api/Issue.kt"))).isTrue()
        }
    }

    @Nested
    inner class `does not support regex` {

        @Test
        fun `should work as a regex path matcher when syntax not specified`() {
            assertThatThrownBy { pathMatcher("regex:.*/detekt/api/.*") }
                .isInstanceOf(IllegalArgumentException::class.java)
                .hasMessage(
                    "Only globbing patterns are supported as they are treated os-independently by the PathMatcher api."
                )
        }
    }
}
