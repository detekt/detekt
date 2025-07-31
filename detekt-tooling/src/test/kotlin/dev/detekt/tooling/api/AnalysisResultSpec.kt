package dev.detekt.tooling.api

import dev.detekt.tooling.internal.DefaultAnalysisResult
import dev.detekt.tooling.internal.EmptyContainer
import org.assertj.core.api.Assertions.assertThatCode
import org.junit.jupiter.api.Test

class AnalysisResultSpec {

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
        assertThatCode { DefaultAnalysisResult(EmptyContainer, IssuesFound("")) }
            .doesNotThrowAnyException()
    }
}
