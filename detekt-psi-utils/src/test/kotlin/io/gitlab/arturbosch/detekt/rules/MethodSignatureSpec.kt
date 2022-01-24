package io.gitlab.arturbosch.detekt.rules

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

@Suppress("DEPRECATION")
class MethodSignatureSpec {

    @Test
    fun `should return method name and null params list in case of simplifies signature`() {
        val methodSignature = "java.time.LocalDate.now"

        val (methodName, params) = extractMethodNameAndParams(methodSignature)

        assertThat(methodName).isEqualTo("java.time.LocalDate.now")
        assertThat(params).isNull()
    }

    @Test
    fun `should return method name and empty params list for full signature parameterless method`() {
        val methodSignature = "java.time.LocalDate.now()"

        val (methodName, params) = extractMethodNameAndParams(methodSignature)

        assertThat(methodName).isEqualTo("java.time.LocalDate.now")
        assertThat(params).isEmpty()
    }

    @Test
    fun `should return method name and params list for full signature method with single param`() {
        val methodSignature = "java.time.LocalDate.now(java.time.Clock)"

        val (methodName, params) = extractMethodNameAndParams(methodSignature)

        assertThat(methodName).isEqualTo("java.time.LocalDate.now")
        assertThat(params).containsExactly("java.time.Clock")
    }

    @Test
    fun `should return method name and params list for full signature method with multiple params`() {
        val methodSignature = "java.time.LocalDate.of(kotlin.Int, kotlin.Int, kotlin.Int)"

        val (methodName, params) = extractMethodNameAndParams(methodSignature)

        assertThat(methodName).isEqualTo("java.time.LocalDate.of")
        assertThat(params).containsExactly("kotlin.Int", "kotlin.Int", "kotlin.Int")
    }

    @Test
    fun `should return method name and params list for full signature method with multiple params where method name has spaces and special characters`() {
        val methodSignature = "io.gitlab.arturbosch.detekt.SomeClass.`some , method`(kotlin.String)"

        val (methodName, params) = extractMethodNameAndParams(methodSignature)

        assertThat(methodName).isEqualTo("io.gitlab.arturbosch.detekt.SomeClass.some , method")
        assertThat(params).containsExactly("kotlin.String")
    }
}
