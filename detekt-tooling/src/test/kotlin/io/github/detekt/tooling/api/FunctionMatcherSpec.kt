package io.github.detekt.tooling.api

import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class FunctionMatcherSpec : Spek({
    describe("FunctionMatcherSpec.fromFunctionSignature") {
        listOf(
            TestCase(
                testDescription = "should return method name and null params list in case of simplifies signature",
                functionSignature = "java.time.LocalDate.now",
                expectedFunctionMatcher = FunctionMatcher.NameOnly("java.time.LocalDate.now"),
            ),
            TestCase(
                testDescription = "should return method name and empty params list for full signature parameterless method",
                functionSignature = "java.time.LocalDate.now()",
                expectedFunctionMatcher = FunctionMatcher.WithParameters("java.time.LocalDate.now", emptyList()),
            ),
            TestCase(
                testDescription = "should return method name and params list for full signature method with single param",
                functionSignature = "java.time.LocalDate.now(java.time.Clock)",
                expectedFunctionMatcher = FunctionMatcher.WithParameters(
                    "java.time.LocalDate.now",
                    listOf("java.time.Clock"),
                ),
            ),
            TestCase(
                testDescription = "should return method name and params list for full signature method with multiple params",
                functionSignature = "java.time.LocalDate.of(kotlin.Int, kotlin.Int, kotlin.Int)",
                expectedFunctionMatcher = FunctionMatcher.WithParameters(
                    "java.time.LocalDate.of",
                    listOf("kotlin.Int", "kotlin.Int", "kotlin.Int"),
                ),
            ),
            TestCase(
                testDescription = "should return method name and params list for full signature method with multiple params " +
                    "where method name has spaces and special characters",
                functionSignature = "io.gitlab.arturbosch.detekt.SomeClass.`some , method`(kotlin.String)",
                expectedFunctionMatcher = FunctionMatcher.WithParameters(
                    "io.gitlab.arturbosch.detekt.SomeClass.some , method",
                    listOf("kotlin.String"),
                ),
            )
        ).forEach { testCase ->
            it(testCase.testDescription) {
                val functionMatcher = FunctionMatcher.fromFunctionSignature(testCase.functionSignature)

                assertThat(functionMatcher).isEqualTo(testCase.expectedFunctionMatcher)
            }
        }
    }
})

private class TestCase(
    val testDescription: String,
    val functionSignature: String,
    val expectedFunctionMatcher: FunctionMatcher,
)
