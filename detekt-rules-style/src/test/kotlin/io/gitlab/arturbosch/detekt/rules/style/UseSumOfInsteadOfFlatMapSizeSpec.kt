package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class UseSumOfInsteadOfFlatMapSizeSpec(val env: KotlinCoreEnvironment) {
    val subject = UseSumOfInsteadOfFlatMapSize()

    @Test
    @DisplayName("Reports flatMap and size")
    fun reportFlatMapAndSize() {
        val code = """
            fun test(list: List<Foo>) {
                list.flatMap { it.foo }.size
            }
            class Foo(val foo: List<Int>)
        """.trimIndent()
        val actual = subject.compileAndLintWithContext(env, code)
        assertThat(actual).hasSize(1)
        assertThat(actual[0].message).isEqualTo("Use 'sumOf' instead of 'flatMap' and 'size'")
    }

    @Test
    @DisplayName("Reports flatMap and count")
    fun reportFlatMapAndCount() {
        val code = """
            fun test(list: List<Foo>) {
                list.flatMap { it.foo }.count()
            }
            class Foo(val foo: List<Int>)
        """.trimIndent()
        val actual = subject.compileAndLintWithContext(env, code)
        assertThat(actual).hasSize(1)
        assertThat(actual[0].message).isEqualTo("Use 'sumOf' instead of 'flatMap' and 'count'")
    }

    @Test
    @DisplayName("Reports flatMap and count with a argument")
    fun reportFlatMapAndCountWithArgument() {
        val code = """
            fun test(list: List<Foo>) {
                list.flatMap { it.foo }.count { it > 2 }
            }
            class Foo(val foo: List<Int>)
        """.trimIndent()
        val actual = subject.compileAndLintWithContext(env, code)
        assertThat(actual).hasSize(1)
        assertThat(actual[0].message).isEqualTo("Use 'sumOf' instead of 'flatMap' and 'count'")
    }

    @Test
    @DisplayName("Reports flatten and size")
    fun reportFlattenAndSize() {
        val code = """
            fun test(list: List<List<Int>>) {
                list.flatten().size
            }
            class Foo(val foo: List<Int>)
        """.trimIndent()
        val actual = subject.compileAndLintWithContext(env, code)
        assertThat(actual).hasSize(1)
        assertThat(actual[0].message).isEqualTo("Use 'sumOf' instead of 'flatten' and 'size'")
    }

    @Test
    @DisplayName("Reports flatMap and size on nullable list")
    fun reportFlatMapAndSizeOnNullableList() {
        val code = """
            fun test(list: List<Foo>?) {
                list?.flatMap { it.foo }?.size
            }
            class Foo(val foo: List<Int>)
        """.trimIndent()
        val actual = subject.compileAndLintWithContext(env, code)
        assertThat(actual).hasSize(1)
    }

    @Test
    @DisplayName("Reports flatMap and size on implicit list receiver")
    fun reportFlatMapAndSizeOnImplicitListReceiver() {
        val code = """
            fun List<Foo>.test() {
                flatMap { it.foo }.size
                sumOf { it.foo.size }
            }
            class Foo(val foo: List<Int>)
        """.trimIndent()
        val actual = subject.compileAndLintWithContext(env, code)
        assertThat(actual).hasSize(1)
    }

    @Test
    @DisplayName("Reports flatMap and count on Set")
    fun reportFlatMapAndCountOnSet() {
        val code = """
            fun test(set: Set<Bar>) {
                set.flatMap { it.bar }.count()
            }
            class Bar(val bar: Set<Int>)
        """.trimIndent()
        val actual = subject.compileAndLintWithContext(env, code)
        assertThat(actual).hasSize(1)
    }

    @Test
    @DisplayName("Does not report flatMap")
    fun noReportFlatMap() {
        val code = """
            fun test(list: List<Foo>) {
                list.flatMap { it.foo }
            }
            class Foo(val foo: List<Int>)
        """.trimIndent()
        val actual = subject.compileAndLintWithContext(env, code)
        assertThat(actual).isEmpty()
    }

    @Test
    @DisplayName("Does not report flatMap and first")
    fun noReportFlatMapAndFirst() {
        val code = """
            fun test(list: List<Foo>) {
                list.flatMap { it.foo }.first()
            }
            class Foo(val foo: List<Int>)
        """.trimIndent()
        val actual = subject.compileAndLintWithContext(env, code)
        assertThat(actual).isEmpty()
    }

    @Test
    @DisplayName("Does not report flatten")
    fun noReportFlatten() {
        val code = """
            fun test(list: List<List<Int>>) {
                list.flatten()
            }
            class Foo(val foo: List<Int>)
        """.trimIndent()
        val actual = subject.compileAndLintWithContext(env, code)
        assertThat(actual).isEmpty()
    }

    @Test
    @DisplayName("Does not report flatten and last")
    fun noReportFlattenAndLast() {
        val code = """
            fun test(list: List<List<Int>>) {
                list.flatten().last()
            }
            class Foo(val foo: List<Int>)
        """.trimIndent()
        val actual = subject.compileAndLintWithContext(env, code)
        assertThat(actual).isEmpty()
    }
}
