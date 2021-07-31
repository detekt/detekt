package io.gitlab.arturbosch.detekt.rules

import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class MethodSignatureSpec : Spek({
    describe("extractMethodNameAndParams") {
        listOf(
            TestCase(
                testDescription = "should return method name and null params list in case of simplifies signature",
                methodSignature = "java.time.LocalDate.now",
                expectedMethodName = "java.time.LocalDate.now",
                expectedParams = null
            ),
            TestCase(
                testDescription = "should return method name and empty params list for full signature parameterless method",
                methodSignature = "java.time.LocalDate.now()",
                expectedMethodName = "java.time.LocalDate.now",
                expectedParams = emptyList()
            ),
            TestCase(
                testDescription = "should return method name and params list for full signature method with single param",
                methodSignature = "java.time.LocalDate.now(java.time.Clock)",
                expectedMethodName = "java.time.LocalDate.now",
                expectedParams = listOf("java.time.Clock")
            ),
            TestCase(
                testDescription = "should return method name and params list for full signature method with multiple params",
                methodSignature = "java.time.LocalDate.of(kotlin.Int, kotlin.Int, kotlin.Int)",
                expectedMethodName = "java.time.LocalDate.of",
                expectedParams = listOf("kotlin.Int", "kotlin.Int", "kotlin.Int")
            ),
            TestCase(
                testDescription = "should return method name and params list for full signature method with multiple params " +
                    "where method name has spaces and special characters",
                methodSignature = "io.gitlab.arturbosch.detekt.SomeClass.`some , method`(kotlin.String)",
                expectedMethodName = "io.gitlab.arturbosch.detekt.SomeClass.some , method",
                expectedParams = listOf("kotlin.String")
            )
        ).forEach { testCase ->
            it(testCase.testDescription) {
                val (methodName, params) = extractMethodNameAndParams(testCase.methodSignature)

                assertThat(methodName).isEqualTo(testCase.expectedMethodName)
                assertThat(params).isEqualTo(testCase.expectedParams)
            }
        }
    }
})

private data class TestCase(
    val testDescription: String,
    val methodSignature: String,
    val expectedMethodName: String,
    val expectedParams: List<String>?
)
