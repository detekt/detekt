package io.gitlab.arturbosch.detekt.api.internal

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.nio.file.Paths

class PathMatchersSpec : Spek({

    val expectedMatch = Paths.get("/detekt/api/Issue.kt")
    val nonMatchingPath = Paths.get("/detekt/cli/Issue.kt")

    describe("supports globing") {

        val libraryPattern = "**/detekt/api/**"
        val matcher by memoized { pathMatcher("glob:$libraryPattern") }

        it("should match") {
            assertThat(matcher.matches(expectedMatch)).isTrue()
        }

        it("should not match") {
            assertThat(matcher.matches(nonMatchingPath)).isFalse()
        }

        it("should work with windows like paths") {
            assertThat(matcher.matches(Paths.get("C:/detekt/api/Issue.kt"))).isTrue()
        }
    }

    describe("does not support regex") {

        it("should work as a regex path matcher when syntax not specified") {
            assertThatThrownBy { pathMatcher("regex:.*/detekt/api/.*") }
                .isInstanceOf(IllegalArgumentException::class.java)
                .hasMessage("Only globbing patterns are supported as they are treated os-independently by the PathMatcher api.")
        }
    }
})
