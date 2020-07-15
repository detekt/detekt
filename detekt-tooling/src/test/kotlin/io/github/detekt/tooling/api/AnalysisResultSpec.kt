package io.github.detekt.tooling.api

import io.github.detekt.tooling.internal.DefaultAnalysisResult
import io.github.detekt.tooling.internal.EmptyContainer
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatCode
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

internal class AnalysisResultSpec : Spek({

    describe("returns different exit codes based on error") {

        it("returns zero on no error") {
            assertThat(DefaultAnalysisResult(EmptyContainer, null).exitCode()).isEqualTo(0)
        }

        it("returns one on any UnexpectedError") {
            val unexpectedError = UnexpectedError(IllegalArgumentException())
            assertThat(DefaultAnalysisResult(null, unexpectedError).exitCode()).isEqualTo(1)
        }

        it("returns two on MaxIssuesReached") {
            assertThat(DefaultAnalysisResult(null, MaxIssuesReached("")).exitCode()).isEqualTo(2)
        }

        it("returns three on InvalidConfig") {
            assertThat(DefaultAnalysisResult(null, InvalidConfig("")).exitCode()).isEqualTo(3)
        }
    }

    test("either container or error must be present") {
        assertThatCode { DefaultAnalysisResult(null, InvalidConfig("")) }.doesNotThrowAnyException()
        assertThatCode { DefaultAnalysisResult(EmptyContainer, null) }.doesNotThrowAnyException()
    }

    test("container and error null is not allowed") {
        assertThatCode { DefaultAnalysisResult(null, null) }
            .isInstanceOf(IllegalArgumentException::class.java)
    }

    test("partial results may have a container and an error additionally") {
        assertThatCode { DefaultAnalysisResult(EmptyContainer, MaxIssuesReached("")) }
            .doesNotThrowAnyException()
    }
})
