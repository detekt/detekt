package dev.detekt.psi

import dev.detekt.test.utils.KotlinEnvironmentContainer
import dev.detekt.test.utils.compileContentForTest
import dev.detekt.test.utils.KotlinCoreEnvironmentTest
import dev.detekt.test.createBindingContext
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.psiUtil.findFunctionByName
import org.jetbrains.kotlin.resolve.BindingContext
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

@KotlinCoreEnvironmentTest
class FunctionMatcherSpec(private val env: KotlinEnvironmentContainer) {

    @TestFactory
    @Suppress("LongMethod")
    fun `FunctionMatcher#fromFunctionSignature`(): List<DynamicTest> =
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
                testDescription = "should return method name and params list for full signature method with multiple " +
                    "params where method name has spaces and special characters",
                functionSignature = "io.gitlab.arturbosch.detekt.SomeClass.`some , method`(kotlin.String)",
                expectedFunctionMatcher = FunctionMatcher.WithParameters(
                    "io.gitlab.arturbosch.detekt.SomeClass.some , method",
                    listOf("kotlin.String"),
                ),
            ),
            TestCase(
                testDescription = "should return method name and param list when it has lambdas",
                functionSignature = "hello((Bar, Foo) -> Unit, (Bar) -> Bar, Foo, () -> Foo)",
                expectedFunctionMatcher = FunctionMatcher.WithParameters(
                    "hello",
                    listOf(
                        "kotlin.Function2",
                        "kotlin.Function1",
                        "Foo",
                        "kotlin.Function0",
                    ),
                ),
            ),
            TestCase(
                testDescription = "should return method name and param list when it has complex lambdas",
                functionSignature = "hello((Bar, (Bar) -> Unit) -> (Bar) -> Foo, () -> Unit)",
                expectedFunctionMatcher = FunctionMatcher.WithParameters(
                    "hello",
                    listOf(
                        "kotlin.Function2",
                        "kotlin.Function0",
                    ),
                ),
            ),
        ).map { testCase ->
            dynamicTest(testCase.testDescription) {
                val functionMatcher = FunctionMatcher.fromFunctionSignature(testCase.functionSignature)

                assertThat(functionMatcher).isEqualTo(testCase.expectedFunctionMatcher)
            }
        }

    @Nested
    inner class `match KtNamedFunctions` {
        @DisplayName("When toString")
        @ParameterizedTest(name = "in case {0} it return {1}")
        @CsvSource(
            "fun toString() = Unit,                            true",
            "fun toString(hello: String) = Unit,               true",
            "fun toString(vararg hello: String) = Unit,        true",
            "'fun toString(hello: String, world: Int) = Unit', true",
            "fun compare() = Unit,                             false",
            "fun compare(hello: String) = Unit,                false",
            "fun compare(vararg hello: String) = Unit,         false",
            "'fun compare(hello: String, world: Int) = Unit',  false",
        )
        fun `When toString`(code: String, result: Boolean) {
            val (function, bindingContext) = buildKtFunction(env, code)
            val methodSignature = FunctionMatcher.fromFunctionSignature("toString")
            assertThat(methodSignature.match(function, bindingContext)).isEqualTo(result)
        }

        @DisplayName("When toString")
        @ParameterizedTest(name = "in case {0} it return {1}")
        @CsvSource(
            "fun toString() = Unit,                            true",
            "fun toString(hello: String) = Unit,               true",
            "fun toString(vararg hello: String) = Unit,        true",
            "'fun toString(hello: String, world: Int) = Unit', true",
            "fun compare() = Unit,                             false",
            "fun compare(hello: String) = Unit,                false",
            "fun compare(vararg hello: String) = Unit,         false",
            "'fun compare(hello: String, world: Int) = Unit',  false",
        )
        fun `When toString without package`(code: String, result: Boolean) {
            val (function, bindingContext) = buildKtFunction(env, code, false)
            val methodSignature = FunctionMatcher.fromFunctionSignature("toString")
            assertThat(methodSignature.match(function, bindingContext)).isEqualTo(result)
        }

        @DisplayName("When toString()")
        @ParameterizedTest(name = "in case {0} it return {1}")
        @CsvSource(
            "fun toString() = Unit,                            true",
            "fun toString(hello: String) = Unit,               false",
            "fun toString(vararg hello: String) = Unit,        false",
            "'fun toString(hello: String, world: Int) = Unit', false",
            "fun compare() = Unit,                             false",
            "fun compare(hello: String) = Unit,                false",
            "fun compare(vararg hello: String) = Unit,         false",
            "'fun compare(hello: String, world: Int) = Unit',  false",
        )
        fun `When toString()`(code: String, result: Boolean) {
            val (function, bindingContext) = buildKtFunction(env, code)
            val methodSignature = FunctionMatcher.fromFunctionSignature("toString()")
            assertThat(methodSignature.match(function, bindingContext)).isEqualTo(result)
        }

        @DisplayName("When toString(kotlin.String)")
        @ParameterizedTest(name = "in case {0} it return {1}")
        @CsvSource(
            "fun toString() = Unit,                            false",
            "fun toString(hello: String) = Unit,               true",
            "fun toString(vararg hello: String) = Unit,        false",
            "'fun toString(hello: String, world: Int) = Unit', false",
            "fun compare() = Unit,                             false",
            "fun compare(hello: String) = Unit,                false",
            "fun compare(vararg hello: String) = Unit,         false",
            "'fun compare(hello: String, world: Int) = Unit',  false",
        )
        fun `When toString(kotlin#String)`(code: String, result: Boolean) {
            val (function, bindingContext) = buildKtFunction(env, code)
            val methodSignature = FunctionMatcher.fromFunctionSignature("toString(kotlin.String)")
            assertThat(methodSignature.match(function, bindingContext)).isEqualTo(result)
        }

        @DisplayName("When toString(vararg String)")
        @ParameterizedTest(name = "in case {0} it return {1}")
        @CsvSource(
            "fun toString() = Unit,                            false",
            "fun toString(hello: String) = Unit,               false",
            "fun toString(vararg hello: String) = Unit,        true",
            "'fun toString(hello: String, world: Int) = Unit', false",
            "fun compare() = Unit,                             false",
            "fun compare(hello: String) = Unit,                false",
            "fun compare(vararg hello: String) = Unit,         false",
            "'fun compare(hello: String, world: Int) = Unit',  false",
        )
        fun `When toString(vararg kotlin#String)`(code: String, result: Boolean) {
            val (function, bindingContext) = buildKtFunction(env, code)
            val methodSignature = FunctionMatcher.fromFunctionSignature("toString(vararg kotlin.String)")
            assertThat(methodSignature.match(function, bindingContext)).isEqualTo(result)
        }

        @DisplayName("When toString(kotlin.Int)")
        @ParameterizedTest(name = "in case {0} it return {1}")
        @CsvSource(
            "fun toString() = Unit,                            false",
            "fun toString(hello: String) = Unit,               false",
            "fun toString(vararg hello: String) = Unit,        false",
            "'fun toString(hello: String, world: Int) = Unit', false",
            "fun compare() = Unit,                             false",
            "fun compare(hello: String) = Unit,                false",
            "fun compare(vararg hello: String) = Unit,         false",
            "'fun compare(hello: String, world: Int) = Unit',  false",
        )
        fun `When toString(kotlin#Int)`(code: String, result: Boolean) {
            val (function, bindingContext) = buildKtFunction(env, code)
            val methodSignature = FunctionMatcher.fromFunctionSignature("toString(kotlin.Int)")
            assertThat(methodSignature.match(function, bindingContext)).isEqualTo(result)
        }

        @DisplayName("When toString(kotlin.String, kotlin.Int)")
        @ParameterizedTest(name = "in case {0} it return {1}")
        @CsvSource(
            "fun toString() = Unit,                            false",
            "fun toString(hello: String) = Unit,               false",
            "fun toString(vararg hello: String) = Unit,        false",
            "'fun toString(hello: String, world: Int) = Unit', true",
            "fun compare() = Unit,                             false",
            "fun compare(hello: String) = Unit,                false",
            "fun compare(vararg hello: String) = Unit,         false",
            "'fun compare(hello: String, world: Int) = Unit',  false",
        )
        fun `When toString(kotlin#String, kotlin#Int)`(code: String, result: Boolean) {
            val (function, bindingContext) = buildKtFunction(env, code)
            val methodSignature = FunctionMatcher.fromFunctionSignature("toString(kotlin.String, kotlin.Int)")
            assertThat(methodSignature.match(function, bindingContext)).isEqualTo(result)
        }

        @DisplayName("When toString(String)")
        @ParameterizedTest(name = "in case {0} it return {1}")
        @CsvSource(
            "fun toString() = Unit,                            false",
            "fun toString(hello: String) = Unit,               false",
            "fun toString(vararg hello: String) = Unit,        false",
            "'fun toString(hello: String, world: Int) = Unit', false",
            "fun compare() = Unit,                             false",
            "fun compare(hello: String) = Unit,                false",
            "fun compare(vararg hello: String) = Unit,         false",
            "'fun compare(hello: String, world: Int) = Unit',  false",
        )
        fun `When toString(String)`(code: String, result: Boolean) {
            val (function, bindingContext) = buildKtFunction(env, code)
            val methodSignature = FunctionMatcher.fromFunctionSignature("toString(String)")
            assertThat(methodSignature.match(function, bindingContext)).isEqualTo(result)
        }

        @DisplayName("When toString(String)")
        @ParameterizedTest(name = "in case {0} it return {1}")
        @CsvSource(
            "fun toString() = Unit,                            false",
            "fun toString(hello: String) = Unit,               false",
            "fun toString(vararg hello: String) = Unit,        false",
            "'fun toString(hello: String, world: Int) = Unit', false",
            "fun compare() = Unit,                             false",
            "fun compare(hello: String) = Unit,                false",
            "fun compare(vararg hello: String) = Unit,         false",
            "'fun compare(hello: String, world: Int) = Unit',  false",
        )
        fun `When toString(String) without package`(code: String, result: Boolean) {
            val (function, bindingContext) = buildKtFunction(env, code, false)
            val methodSignature = FunctionMatcher.fromFunctionSignature("toString(String)")
            assertThat(methodSignature.match(function, bindingContext)).isEqualTo(result)
        }

        @DisplayName("When lambdas foo(() -> kotlin.String)")
        @ParameterizedTest(name = "in case {0} it return {1}")
        @CsvSource(
            "fun foo(a: () -> String),          true",
            "fun foo(a: () -> Unit),            true",
            "fun foo(a: (String) -> String),    false",
            "fun foo(a: (String) -> Unit),      false",
            "fun foo(a: (Int) -> Unit),         false",
        )
        fun `When foo(() - kotlin#String)`(code: String, result: Boolean) {
            val (function, bindingContext) = buildKtFunction(env, code)
            val methodSignature = FunctionMatcher.fromFunctionSignature("foo(() -> kotlin.String)")
            assertThat(methodSignature.match(function, bindingContext)).isEqualTo(result)
        }

        @DisplayName("When lambdas foo((kotlin.String) -> Unit)")
        @ParameterizedTest(name = "in case {0} it return {1}")
        @CsvSource(
            "fun foo(a: () -> String),          false",
            "fun foo(a: () -> Unit),            false",
            "fun foo(a: (String) -> String),    true",
            "fun foo(a: (String) -> Unit),      true",
            "fun foo(a: (Int) -> Unit),         true",
        )
        fun `When foo((kotlin#String) - Unit)`(code: String, result: Boolean) {
            val (function, bindingContext) = buildKtFunction(env, code)
            val methodSignature = FunctionMatcher.fromFunctionSignature("foo((kotlin.String) -> Unit)")
            assertThat(methodSignature.match(function, bindingContext)).isEqualTo(result)
        }

        @DisplayName("When extension functions foo(kotlin.String)")
        @ParameterizedTest(name = "in case {0} it return {1}")
        @CsvSource(
            "fun String.foo(),              true",
            "fun foo(a: String),            true",
            "fun Int.foo(),                 false",
            "fun String.foo(a: Int),        false",
            "'fun foo(a: String, ba: Int)', false",
        )
        fun `When foo(kotlin#String)`(code: String, result: Boolean) {
            val (function, bindingContext) = buildKtFunction(env, code)
            val methodSignature = FunctionMatcher.fromFunctionSignature("foo(kotlin.String)")
            assertThat(methodSignature.match(function, bindingContext)).isEqualTo(result)
        }

        @DisplayName("When extension functions foo(kotlin.String, kotlin.Int)")
        @ParameterizedTest(name = "in case {0} it return {1}")
        @CsvSource(
            "fun String.foo(),              false",
            "fun foo(a: String),            false",
            "fun Int.foo(),                 false",
            "fun String.foo(a: Int),        true",
            "'fun foo(a: String, ba: Int)', true",
        )
        fun `When foo(kotlin#String, kotlin#Int)`(code: String, result: Boolean) {
            val (function, bindingContext) = buildKtFunction(env, code)
            val methodSignature = FunctionMatcher.fromFunctionSignature("foo(kotlin.String, kotlin.Int)")
            assertThat(methodSignature.match(function, bindingContext)).isEqualTo(result)
        }

        @DisplayName("When generics foo(T, U)")
        @ParameterizedTest(name = "in case {0} it return {1}")
        @CsvSource(
            "'fun <T, U> foo(a: T, b: U)',      true",
            "'fun <T, U> foo(a: U, b: T)',      false",
            "'fun <T, U> foo(a: String, b: U)', false",
            "'fun <T, U> T.foo(a: U)',          true",
            "'fun <T, U> U.foo(a: T)',          false",
        )
        fun `When foo(T, U)`(code: String, result: Boolean) {
            val (function, bindingContext) = buildKtFunction(env, code)
            val methodSignature = FunctionMatcher.fromFunctionSignature("foo(T, U)")
            assertThat(methodSignature.match(function, bindingContext)).isEqualTo(result)
        }

        @ParameterizedTest
        @CsvSource(
            "fun bar(), class BarClass, io.github.detekt.BarClass.bar, true",
            "fun bar(p: String), class BarClass, io.github.detekt.BarClass.bar, true",
            "fun bar(p: T), class BarClass<T>, io.github.detekt.BarClass.bar, true",
            "fun T.bar(), class BarClass<T>, io.github.detekt.BarClass.bar, true",
            "fun bar(), class BarClass, io.github.detekt.BarClass.bar(), true",
            "fun bar(p: String), class BarClass, io.github.detekt.BarClass.bar(kotlin.String), true",
            "fun bar(p: T), class BarClass<T>, io.github.detekt.BarClass.bar(T), true",
            "fun T.bar(), class BarClass<T>, io.github.detekt.BarClass.bar(T), true",
            "'fun <V> bar(p1: V, p2: T)', class BarClass<T>, 'io.github.detekt.BarClass.bar(V, T)', true",
            "fun bar(), class BarClass, io.github.detekt.BarClass.bar(kotlin.String), false",
            "fun bar(p: String), class BarClass, io.github.detekt.BarClass.bar(kotlin.Int), false",
            "fun bar(p: T), class BarClass, io.github.detekt.BarClass.bar(T), false",
            "fun T.bar(), class BarClass, io.github.detekt.BarClass.bar(T), false",
            "'fun <V> bar(p1: V, p2: T)', class BarClass<T>, 'io.github.detekt.BarClass.bar(T, V)', false",
        )
        fun `When function signature is fully qualified and declaration is enclosed into class`(
            functionSignature: String,
            classSignature: String,
            pattern: String,
            result: Boolean,
        ) {
            val ktFile = compileContentForTest(
                """
                    package io.github.detekt

                    $classSignature {
                        $functionSignature {}
                    }
                """.trimIndent()
            )
            val bindingContext = env.createBindingContext(listOf(ktFile))
            val function = ktFile.findChildByClass(KtClass::class.java)!!
                .findFunctionByName("bar") as KtNamedFunction

            val methodSignature = FunctionMatcher.fromFunctionSignature(pattern)
            assertThat(methodSignature.match(function, bindingContext)).isEqualTo(result)
        }
    }
}

private class TestCase(
    val testDescription: String,
    val functionSignature: String,
    val expectedFunctionMatcher: FunctionMatcher,
)

private fun buildKtFunction(
    environment: KotlinEnvironmentContainer,
    code: String,
    includePackage: Boolean = true,
): Pair<KtNamedFunction, BindingContext> {
    val ktFile = compileContentForTest(
        """
            ${if (includePackage) "package io.github.detekt" else ""}
            $code
        """.trimIndent()
    )
    val bindingContext = environment.createBindingContext(listOf(ktFile))
    return ktFile.findChildByClass(KtNamedFunction::class.java)!! to bindingContext
}
