package io.gitlab.arturbosch.detekt.api

import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class SplitPatternSpec : Spek({

    describe("an excludes rule with a single exclude") {

        val excludes by memoized { SplitPattern("test") }

        it("contains the `test` parameter") {
            val parameter = "test"
            assertThat(excludes.contains(parameter)).isTrue()
            assertThat(excludes.none(parameter)).isFalse()
        }

        it("contains an extension of the `test` parameter") {
            val parameter = "test.com"
            assertThat(excludes.contains(parameter)).isTrue()
            assertThat(excludes.none(parameter)).isFalse()
        }

        it("does not contain a different parameter") {
            val parameter = "detekt"
            assertThat(excludes.contains(parameter)).isFalse()
            assertThat(excludes.none(parameter)).isTrue()
        }

        it("returns all matches") {
            val parameter = "test.com"
            val matches = excludes.matches(parameter)
            assertThat(matches).hasSize(1)
            assertThat(matches).contains("test")
        }

        it("does not contain null value") {
            val parameter = null
            assertThat(excludes.contains(parameter)).isFalse()
        }
    }

    describe("an excludes rule with multiple excludes") {

        val excludes by memoized { SplitPattern("here.there.io, test.com") }

        it("contains the `test` parameter") {
            val parameter = "test.com"
            assertThat(excludes.contains(parameter)).isTrue()
            assertThat(excludes.none(parameter)).isFalse()
        }

        it("contains the `here.there.io` parameter") {
            val parameter = "here.there.io"
            assertThat(excludes.contains(parameter)).isTrue()
            assertThat(excludes.none(parameter)).isFalse()
        }

        it("does not contain a parameter spanning over the excludes") {
            val parameter = "io.test.com"
            assertThat(excludes.contains(parameter)).isTrue()
            assertThat(excludes.none(parameter)).isFalse()
        }

        it("does not contain a different") {
            val parameter = "detekt"
            assertThat(excludes.contains(parameter)).isFalse()
            assertThat(excludes.none(parameter)).isTrue()
        }

        it("starts with test") {
            val parameter = "test.com"
            assertThat(excludes.startWith(parameter)).isTrue()
        }

        it("does not start with a different") {
            val parameter = "detekt"
            assertThat(excludes.startWith(parameter)).isFalse()
        }

        it("does not start with null") {
            val parameter = null
            assertThat(excludes.startWith(parameter)).isFalse()
        }

        it("has a test") {
            val parameter = "test.com"
            assertThat(excludes.any(parameter)).isTrue()
        }

        it("has no different") {
            val parameter = "detekt"
            assertThat(excludes.any(parameter)).isFalse()
        }

        it("has no null value") {
            val parameter = null
            assertThat(excludes.any(parameter)).isFalse()
        }
    }

    describe("an excludes rule with lots of whitespace and an empty parameter") {

        val excludes by memoized { SplitPattern("    test,  ,       here.there       ") }

        it("contains the `test` parameter") {
            val parameter = "test"
            assertThat(excludes.contains(parameter)).isTrue()
            assertThat(excludes.none(parameter)).isFalse()
        }

        it("contains the `here.there` parameter") {
            val parameter = "here.there"
            assertThat(excludes.contains(parameter)).isTrue()
            assertThat(excludes.none(parameter)).isFalse()
        }

        it("does not contain a different parameter") {
            val parameter = "detekt"
            assertThat(excludes.contains(parameter)).isFalse()
            assertThat(excludes.none(parameter)).isTrue()
        }

        it("does not match empty strings") {
            val parameter = "  "
            assertThat(excludes.contains(parameter)).isFalse()
            assertThat(excludes.none(parameter)).isTrue()
        }
    }
})
