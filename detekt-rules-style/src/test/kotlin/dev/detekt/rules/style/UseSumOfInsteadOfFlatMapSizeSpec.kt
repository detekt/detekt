package dev.detekt.rules.style

import dev.detekt.api.Config
import dev.detekt.test.assertj.assertThat
import dev.detekt.test.lintWithContext
import dev.detekt.test.utils.KotlinCoreEnvironmentTest
import dev.detekt.test.utils.KotlinEnvironmentContainer
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class UseSumOfInsteadOfFlatMapSizeSpec(val env: KotlinEnvironmentContainer) {
    val subject = UseSumOfInsteadOfFlatMapSize(Config.empty)

    @Test
    fun `reports flatMap and size`() {
        val code = """
            fun test(list: List<Foo>) {
                list.flatMap { it.foo }.size
            }
            class Foo(val foo: List<Int>)
        """.trimIndent()
        val actual = subject.lintWithContext(env, code)
        assertThat(actual).singleElement()
            .hasMessage("Use 'sumOf' instead of 'flatMap' and 'size'")
    }

    @Test
    fun `reports flatMap and count`() {
        val code = """
            fun test(list: List<Foo>) {
                list.flatMap { it.foo }.count()
            }
            class Foo(val foo: List<Int>)
        """.trimIndent()
        val actual = subject.lintWithContext(env, code)
        assertThat(actual).singleElement()
            .hasMessage("Use 'sumOf' instead of 'flatMap' and 'count'")
    }

    @Test
    fun `reports flatMap and count with a argument`() {
        val code = """
            fun test(list: List<Foo>) {
                list.flatMap { it.foo }.count { it > 2 }
            }
            class Foo(val foo: List<Int>)
        """.trimIndent()
        val actual = subject.lintWithContext(env, code)
        assertThat(actual).singleElement()
            .hasMessage("Use 'sumOf' instead of 'flatMap' and 'count'")
    }

    @Test
    fun `reports flatten and size`() {
        val code = """
            fun test(list: List<List<Int>>) {
                list.flatten().size
            }
            class Foo(val foo: List<Int>)
        """.trimIndent()
        val actual = subject.lintWithContext(env, code)
        assertThat(actual).singleElement()
            .hasMessage("Use 'sumOf' instead of 'flatten' and 'size'")
    }

    @Test
    fun `reports flatMap and size on nullable list`() {
        val code = """
            fun test(list: List<Foo>?) {
                list?.flatMap { it.foo }?.size
            }
            class Foo(val foo: List<Int>)
        """.trimIndent()
        val actual = subject.lintWithContext(env, code)
        assertThat(actual).hasSize(1)
    }

    @Test
    fun `reports flatMap and size on implicit list receiver`() {
        val code = """
            fun List<Foo>.test() {
                flatMap { it.foo }.size
                sumOf { it.foo.size }
            }
            class Foo(val foo: List<Int>)
        """.trimIndent()
        val actual = subject.lintWithContext(env, code)
        assertThat(actual).hasSize(1)
    }

    @Test
    fun `reports flatMap and count on Set`() {
        val code = """
            fun test(set: Set<Bar>) {
                set.flatMap { it.bar }.count()
            }
            class Bar(val bar: Set<Int>)
        """.trimIndent()
        val actual = subject.lintWithContext(env, code)
        assertThat(actual).hasSize(1)
    }

    @Test
    fun `does not report flatMap`() {
        val code = """
            fun test(list: List<Foo>) {
                list.flatMap { it.foo }
            }
            class Foo(val foo: List<Int>)
        """.trimIndent()
        val actual = subject.lintWithContext(env, code)
        assertThat(actual).isEmpty()
    }

    @Test
    fun `does not report flatMap and first`() {
        val code = """
            fun test(list: List<Foo>) {
                list.flatMap { it.foo }.first()
            }
            class Foo(val foo: List<Int>)
        """.trimIndent()
        val actual = subject.lintWithContext(env, code)
        assertThat(actual).isEmpty()
    }

    @Test
    fun `does not report flatten`() {
        val code = """
            fun test(list: List<List<Int>>) {
                list.flatten()
            }
            class Foo(val foo: List<Int>)
        """.trimIndent()
        val actual = subject.lintWithContext(env, code)
        assertThat(actual).isEmpty()
    }

    @Test
    fun `does not report flatten and last`() {
        val code = """
            fun test(list: List<List<Int>>) {
                list.flatten().last()
            }
            class Foo(val foo: List<Int>)
        """.trimIndent()
        val actual = subject.lintWithContext(env, code)
        assertThat(actual).isEmpty()
    }
}
