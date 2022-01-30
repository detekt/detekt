package io.gitlab.arturbosch.detekt.api

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class SplitPatternSpec {

    @Nested
    inner class `an excludes rule with a single exclude` {

        private val excludes = SplitPattern("test")

        @Test
        fun `contains the _test_ parameter`() {
            val parameter = "test"
            assertThat(excludes.contains(parameter)).isTrue()
            assertThat(excludes.none(parameter)).isFalse()
        }

        @Test
        fun `contains an extension of the _test_ parameter`() {
            val parameter = "test.com"
            assertThat(excludes.contains(parameter)).isTrue()
            assertThat(excludes.none(parameter)).isFalse()
        }

        @Test
        fun `does not contain a different parameter`() {
            val parameter = "detekt"
            assertThat(excludes.contains(parameter)).isFalse()
            assertThat(excludes.none(parameter)).isTrue()
        }

        @Test
        fun `returns all matches`() {
            val parameter = "test.com"
            val matches = excludes.matches(parameter)
            assertThat(matches).hasSize(1)
            assertThat(matches).contains("test")
        }

        @Test
        fun `does not contain null value`() {
            val parameter = null
            assertThat(excludes.contains(parameter)).isFalse()
        }
    }

    @Nested
    inner class `an excludes rule with multiple excludes` {

        private val excludes = SplitPattern("here.there.io, test.com")

        @Test
        fun `contains the _test_ parameter`() {
            val parameter = "test.com"
            assertThat(excludes.contains(parameter)).isTrue()
            assertThat(excludes.none(parameter)).isFalse()
        }

        @Test
        fun `contains the _here_there_io_ parameter`() {
            val parameter = "here.there.io"
            assertThat(excludes.contains(parameter)).isTrue()
            assertThat(excludes.none(parameter)).isFalse()
        }

        @Test
        fun `does not contain a parameter spanning over the excludes`() {
            val parameter = "io.test.com"
            assertThat(excludes.contains(parameter)).isTrue()
            assertThat(excludes.none(parameter)).isFalse()
        }

        @Test
        fun `does not contain a different`() {
            val parameter = "detekt"
            assertThat(excludes.contains(parameter)).isFalse()
            assertThat(excludes.none(parameter)).isTrue()
        }

        @Test
        fun `starts with test`() {
            val parameter = "test.com"
            assertThat(excludes.startWith(parameter)).isTrue()
        }

        @Test
        fun `does not start with a different`() {
            val parameter = "detekt"
            assertThat(excludes.startWith(parameter)).isFalse()
        }

        @Test
        fun `does not start with null`() {
            val parameter = null
            assertThat(excludes.startWith(parameter)).isFalse()
        }

        @Test
        fun `has a test`() {
            val parameter = "test.com"
            assertThat(excludes.any(parameter)).isTrue()
        }

        @Test
        fun `has no different`() {
            val parameter = "detekt"
            assertThat(excludes.any(parameter)).isFalse()
        }

        @Test
        fun `has no null value`() {
            val parameter = null
            assertThat(excludes.any(parameter)).isFalse()
        }
    }

    @Nested
    inner class `an excludes rule with lots of whitespace and an empty parameter` {

        private val excludes = SplitPattern("    test,  ,       here.there       ")

        @Test
        fun `contains the _test_ parameter`() {
            val parameter = "test"
            assertThat(excludes.contains(parameter)).isTrue()
            assertThat(excludes.none(parameter)).isFalse()
        }

        @Test
        fun `contains the _here_there_ parameter`() {
            val parameter = "here.there"
            assertThat(excludes.contains(parameter)).isTrue()
            assertThat(excludes.none(parameter)).isFalse()
        }

        @Test
        fun `does not contain a different parameter`() {
            val parameter = "detekt"
            assertThat(excludes.contains(parameter)).isFalse()
            assertThat(excludes.none(parameter)).isTrue()
        }

        @Test
        fun `does not match empty strings`() {
            val parameter = "  "
            assertThat(excludes.contains(parameter)).isFalse()
            assertThat(excludes.none(parameter)).isTrue()
        }
    }
}
