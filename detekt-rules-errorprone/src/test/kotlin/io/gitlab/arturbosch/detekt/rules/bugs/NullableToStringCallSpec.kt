package io.gitlab.arturbosch.detekt.rules.bugs

import dev.detekt.api.Config
import io.github.detekt.test.utils.KotlinEnvironmentContainer
import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import io.gitlab.arturbosch.detekt.test.lintWithContext
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class NullableToStringCallSpec(private val env: KotlinEnvironmentContainer) {
    private val subject = NullableToStringCall(Config.empty)

    @Test
    fun `reports when a nullable toString is explicitly called`() {
        val code = """
            fun test(a: Any?) {
                println(a.toString())
            }
        """.trimIndent()
        val actual = subject.lintWithContext(env, code)
        assertThat(actual).hasSize(1)
        assertThat(actual.first().message).isEqualTo("This call 'a.toString()' may return the string \"null\".")
    }

    @Test
    fun `reports when a nullable toString is implicitly called in a string template`() {
        val code = """
            fun test(a: Any?) {
                println("${'$'}a")
            }
        """.trimIndent()
        val actual = subject.lintWithContext(env, code)
        assertThat(actual).hasSize(1)
        assertThat(actual.first().message).isEqualTo("This call '\$a' may return the string \"null\".")
    }

    @Test
    fun `reports when a nullable toString is implicitly called in curly braces in a string template`() {
        val code = """
            fun test(a: Any?) {
                println("${'$'}{a}")
            }
        """.trimIndent()
        val actual = subject.lintWithContext(env, code)
        assertThat(actual).hasSize(1)
        assertThat(actual.first().message).isEqualTo("This call '\${a}' may return the string \"null\".")
    }

    @Test
    fun `reports when a nullable toString is implicitly called in a raw string template`() {
        val code = """
            fun test(a: Any?) {
                println(${'"'}""${'$'}a""${'"'})
            }
        """.trimIndent()
        val actual = subject.lintWithContext(env, code)
        assertThat(actual).hasSize(1)
        assertThat(actual.first().message).isEqualTo("This call '\$a' may return the string \"null\".")
    }

    @Test
    fun `reports when a nullable toString is explicitly called and the expression is qualified or call expression`() {
        val code = """
            data class Foo(val a: Any?) {
                fun bar(): Int? = null
            }
            fun baz(): Long? = null
            
            fun test(foo: Foo) {
                val x = foo.a.toString()
                val y = foo.bar().toString()
                val z = baz().toString()
            }
        """.trimIndent()
        val actual = subject.lintWithContext(env, code)
        assertThat(actual).hasSize(3)
        assertThat(actual[0].message).isEqualTo("This call 'foo.a.toString()' may return the string \"null\".")
        assertThat(actual[1].message).isEqualTo("This call 'foo.bar().toString()' may return the string \"null\".")
        assertThat(actual[2].message).isEqualTo("This call 'baz().toString()' may return the string \"null\".")
    }

    @Test
    fun `reports when a nullable toString is implicitly called and the expression is qualified or call expression`() {
        val code = """
            data class Foo(val a: Any?) {
                fun bar(): Int? = null
            }
            fun baz(): Long? = null
            
            fun test(foo: Foo) {
                val x = "${'$'}{foo.a}"
                val y = "${'$'}{foo.bar()}"
                val z = "${'$'}{baz()}"
            }
        """.trimIndent()
        val actual = subject.lintWithContext(env, code)
        assertThat(actual).hasSize(3)
        assertThat(actual[0].message).isEqualTo("This call '\${foo.a}' may return the string \"null\".")
        assertThat(actual[1].message).isEqualTo("This call '\${foo.bar()}' may return the string \"null\".")
        assertThat(actual[2].message).isEqualTo("This call '\${baz()}' may return the string \"null\".")
    }

    @Test
    fun `reports when a nullable toString is implicitly called and the expression is safe qualified expression`() {
        val code = """
            data class Foo(val a: Any)
            
            fun test(foo: Foo?) {
                val y = "${'$'}{foo?.a}"
            }
        """.trimIndent()
        val actual = subject.lintWithContext(env, code)
        assertThat(actual).hasSize(1)
        assertThat(actual[0].message).isEqualTo("This call '\${foo?.a}' may return the string \"null\".")
    }

    @Test
    fun `does not report when a nullable toString is not called`() {
        val code = """
            fun test(a: Any?) {
                println(a?.toString())
                println("${'$'}{a ?: "-"}")
            }
            fun test2(a: Any) {
                println(a.toString())
                println("${'$'}a")
                println("${'$'}{a}")
                println(${'"'}""${'$'}a""${'"'})
            }
            fun test3(a: Any?) {
                if (a != null) {
                    println(a.toString())
                    println("${'$'}a")
                    println("${'$'}{a}")
                    println(${'"'}""${'$'}a""${'"'})
                }
            }
            fun test4(a: Any?) {
                requireNotNull(a)
                println(a.toString())
                println("${'$'}a")
                println("${'$'}{a}")
                println(${'"'}""${'$'}a""${'"'})
            }
            
            data class Foo(val a: Any?)
            fun test5(foo: Foo) {
                if (foo.a == null) return
                val x = foo.a.toString()
                val y = "${'$'}{foo.a}"
            }
            
            data class Bar(val a: Any)
            fun test6(bar: Bar?) {
                if (bar == null) return
                val y = "${'$'}{bar?.a}"
            }
        """.trimIndent()
        val actual = subject.lintWithContext(env, code)
        assertThat(actual).isEmpty()
    }

    // https://github.com/detekt/detekt/issues/4059
    @Test
    fun `ignores platform types`() {
        val code = """
            class Foo(val a: Any) {
                fun test(foo: Foo?) {
                    // getSimpleName() is not annotated with nullability information in the JDK, so compiler treats
                    // it as a platform type with unknown nullability. IDE behavior is different as it can take
                    // advantage of external annotations, which unfortunately isn't supported in the compiler, so we
                    // whitelist all platform types to avoid false positives.
                    val y = "${'$'}{javaClass.simpleName}"
                }
            }
        """.trimIndent()
        val actual = subject.lintWithContext(env, code)
        assertThat(actual).isEmpty()
    }

    // https://github.com/detekt/detekt/issues/6349
    @Test
    fun `reports expressions in lambda`() {
        val code = """
            fun undetected1(): String = 1.let {
                val x: String? = null
                "hi ${'$'}x"
            }
            
            fun undetected2(): String = 1.let {
                val x: Any? = null
                "hi ${'$'}{x.toString()}"
            }
            
            fun undetected3(): String {
                val x: String? = null
                return 1.let {
                    "hi ${'$'}x"
                }
            }
            
            fun undetected4(): String {
                val x: String? = null
                return x.let { y ->
                    "hi ${'$'}y"
                }
            }
        """.trimIndent()
        val actual = subject.lintWithContext(env, code)
        assertThat(actual).hasSize(4)
    }

    // https://github.com/detekt/detekt/issues/6378
    @Test
    fun `reports when toString is an argument`() {
        val code = """
            class Foo {
                fun bar(value: String?) {}
            }
            
            fun test(foo: Foo, x: Int?) {
                foo.bar(x.toString())
            }
        """.trimIndent()
        val actual = subject.lintWithContext(env, code)
        assertThat(actual).hasSize(1)
    }
}
