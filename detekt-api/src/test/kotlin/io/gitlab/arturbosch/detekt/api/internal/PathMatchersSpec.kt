package io.gitlab.arturbosch.detekt.api.internal

import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.nio.file.Paths

/**
 * @author Artur Bosch
 */
class PathMatchersSpec : Spek({

    val expectedMatch = Paths.get("/detekt/api/Issue.kt")
    val nonMatchingPath = Paths.get("/detekt/cli/Issue.kt")

    describe("supports globing") {
        val libraryPattern = "**/detekt/api/**"

        it("should work as a globing path matcher when syntax not specified") {
            val matcher = pathMatcher(libraryPattern)
            assertThat(matcher.matches(expectedMatch)).isTrue()
        }

        val matcher = pathMatcher("glob:$libraryPattern")

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

    describe("supports regex") {
        val libraryPattern = ".*/detekt/api/.*"

        it("should work as a regex path matcher when syntax not specified but default overridden") {
            val matcher = pathMatcher(libraryPattern, defaultSyntax = "regex")
            assertThat(matcher.matches(expectedMatch)).isTrue()
        }

        val matcher = pathMatcher("regex:$libraryPattern")

        it("should match") {
            assertThat(matcher.matches(expectedMatch)).isTrue()
        }

        it("should not match") {
            assertThat(matcher.matches(nonMatchingPath)).isFalse()
        }
    }
})
