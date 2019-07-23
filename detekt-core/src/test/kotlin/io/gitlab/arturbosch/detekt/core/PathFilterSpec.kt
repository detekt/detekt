package io.gitlab.arturbosch.detekt.core

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatIllegalArgumentException
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.nio.file.Paths

internal class PathFilterSpec : Spek({

    describe("an invalid regex pattern") {
        it("throws an IllegalArgumentException") {
            assertThatIllegalArgumentException().isThrownBy { PathFilter("*.") }
        }
    }

    describe("an empty pattern") {
        it("throws an IllegalArgumentException") {
            assertThatIllegalArgumentException().isThrownBy { PathFilter("") }
        }
    }

    describe("a blank pattern") {
        it("throws an IllegalArgumentException") {
            assertThatIllegalArgumentException().isThrownBy { PathFilter("    ") }
        }
    }

    describe("a single regex pattern on Unix systems") {
        val filter = ".*/build/.*"
        val defaultRoot = Paths.get("").toAbsolutePath()

        it("matches a corresponding relative path") {
            val path = defaultRoot.resolve("some/build/path/should/match")

            assertThat(PathFilter(filter).matches(path)).isTrue()
        }

        it("matches a corresponding relative path with the filter in the beginning") {
            val path = defaultRoot.resolve("build/path/should/match")

            assertThat(PathFilter(filter).matches(path)).isTrue()
        }

        it("does not match an unrelated path") {
            val path = defaultRoot.resolve("this/should/NOT/match")

            assertThat(PathFilter(filter).matches(path)).isFalse()
        }

        it("does not match the pattern in the absolute path") {
            val root = Paths.get("/tmp/build/detekt").toAbsolutePath()
            val path = root.resolve("should/not/match")

            assertThat(PathFilter(filter, root).matches(path)).isFalse()
        }

        it("does not match the pattern in the absolute path but the relative path") {
            val root = Paths.get("/tmp/detekt").toAbsolutePath()
            val path = root.resolve("should/match/build/path")

            assertThat(PathFilter(filter, root).matches(path)).isTrue()
        }
    }
})
