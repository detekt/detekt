package io.github.detekt.tooling.api

import io.github.detekt.tooling.internal.DefaultAnalysisResult
import io.github.detekt.tooling.internal.EmptyContainer
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatCode
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class AnalysisResultSpec {

    @Nested
    inner class `returns different exit codes based on error` {

        @Test
        fun `returns zero on no error`() {
            assertThat(DefaultAnalysisResult(EmptyContainer, null).exitCode()).isEqualTo(0)
        }

        @Test
        fun `returns one on any UnexpectedError`() {
            val unexpectedError = UnexpectedError(IllegalArgumentException())
            assertThat(DefaultAnalysisResult(null, unexpectedError).exitCode()).isEqualTo(1)
        }

        @Test
        fun `returns two on MaxIssuesReached`() {
            assertThat(DefaultAnalysisResult(null, MaxIssuesReached("")).exitCode()).isEqualTo(2)
        }

        @Test
        fun `returns three on InvalidConfig`() {
            assertThat(DefaultAnalysisResult(null, InvalidConfig("")).exitCode()).isEqualTo(3)
        }
    }

    @Test
    fun `either container or error must be present`() {
        assertThatCode { DefaultAnalysisResult(null, InvalidConfig("")) }.doesNotThrowAnyException()
        assertThatCode { DefaultAnalysisResult(EmptyContainer, null) }.doesNotThrowAnyException()
    }

    @Test
    fun `container and error null is not allowed`() {
        assertThatCode { DefaultAnalysisResult(null, null) }
            .isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun `partial results may have a container and an error additionally`() {
        assertThatCode { DefaultAnalysisResult(EmptyContainer, MaxIssuesReached("")) }
            .doesNotThrowAnyException()
    }
}
