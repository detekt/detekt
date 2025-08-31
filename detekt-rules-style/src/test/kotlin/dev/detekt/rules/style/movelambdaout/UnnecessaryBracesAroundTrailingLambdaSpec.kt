package dev.detekt.rules.style.movelambdaout

import dev.detekt.api.Config
import dev.detekt.test.assertj.assertThat
import dev.detekt.test.lintWithContext
import dev.detekt.test.utils.KotlinCoreEnvironmentTest
import dev.detekt.test.utils.KotlinEnvironmentContainer
import org.junit.jupiter.api.Test

// Source https://github.com/JetBrains/intellij-community/tree/master/plugins/kotlin/idea/tests/testData/inspectionsLocal/moveLambdaOutsideParentheses
@KotlinCoreEnvironmentTest
class UnnecessaryBracesAroundTrailingLambdaSpec(val env: KotlinEnvironmentContainer) {
    private val subject = UnnecessaryBracesAroundTrailingLambda(Config.empty)

    @Test
    fun `does not report when lambda inside braces is used in class delegation`() {
        val code = """
            interface I
            class C1(s: String, f: (String) -> String) : I
            class C2 : I by C1("", { "" })
        """.trimIndent()
        val findings = subject.lintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `does report when lambda inside braces is used for function param`() {
        val code = """
            fun foo(p: (Int, () -> Int) -> Unit) {
                p(1, { 2 })
            }
        """.trimIndent()
        val findings = subject.lintWithContext(env, code)
        assertThat(findings).hasSize(1)
    }

    @Test
    fun `does not report when trailing lambda is used for function param`() {
        val code = """
            fun foo(p: (Int, () -> Int) -> Unit) {
                p(1) { 2 }
            }
        """.trimIndent()
        val findings = subject.lintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `does not report when trailing lambda is not possible for function param`() {
        val code = """
            fun foo(p: (Int, Int) -> Unit) {
                p(1, 2)
            }
        """.trimIndent()
        val findings = subject.lintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `does report when lambda inside braces is used for lambda param with suspending lambda`() {
        val code = """
            fun foo(p: (Int, suspend () -> Int) -> Unit) {
                p(1, { 2 })
            }
        """.trimIndent()
        val findings = subject.lintWithContext(env, code)
        assertThat(findings).hasSize(1)
    }

    @Test
    fun `does not report when trailing lambda is used for function lambda param with suspending lambda`() {
        val code = """
            fun foo(p: (Int, suspend () -> Int) -> Unit) {
                p(1) { 2 }
            }
        """.trimIndent()
        val findings = subject.lintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `does when lambda inside braces is used for function suspending lambda param`() {
        val code = """
            suspend fun foo(p: suspend (Int, () -> Int) -> Unit) {
                p(1, { 2 })
            }
        """.trimIndent()
        val findings = subject.lintWithContext(env, code)
        assertThat(findings).hasSize(1)
    }

    @Test
    fun `does not report when trailing lambda is used for function suspending lambda param`() {
        val code = """
            suspend fun foo(p: suspend (Int, () -> Int) -> Unit) {
                p(1) { 2 }
            }
        """.trimIndent()
        val findings = subject.lintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `does report when lambda inside braces is used for ctor`() {
        val code = """
            class A(block: () -> Unit)
            fun foo() = A({ println() })
        """.trimIndent()
        val findings = subject.lintWithContext(env, code)
        assertThat(findings).hasSize(1)
    }

    @Test
    fun `does not report when trailing lambda is used for ctor`() {
        val code = """
            class A(block: () -> Unit)
            fun foo() = A { println() }
        """.trimIndent()
        val findings = subject.lintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `does not report when trailing lambda is used without braces`() {
        val code = """
            fun foo() {
                bar() { it }
            }

            fun bar(b: (Int) -> Int) {
                b(1)
            }
        """.trimIndent()
        val findings = subject.lintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `does not report when first and second lambda is present`() {
        val code = """
            fun foo() {
                bar({ it }) { it }
            }

            fun bar(p1: (Int) -> Int, p2: (Int) -> Int) {}
        """.trimIndent()
        val findings = subject.lintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `does not report when removing braces changes the code`() {
        val code = """
            fun test(a: (String) -> Unit = {}, b: (String) -> Unit = {}) {
                a("a")
                b("b")
            }

            fun foo() {
                test({ })
            }
        """.trimIndent()
        val findings = subject.lintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `does not report when second lambda can be moved out of the braces`() {
        val code = """
            fun test(a: (String) -> Unit = {}, b: (String) -> Unit = {}) {
                a("a")
                b("b")
            }

            fun foo() {
                test({ }, { }) // Don't flag it as IDE also doesn't flag it and trailing lambda might look more complicated in this case
            }
        """.trimIndent()
        val findings = subject.lintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `does not report first lambda can not be moved out of the braces`() {
        val code = """
            fun foo() {
                bar({ it })
            }

            fun bar(b: (Int) -> Int, option: Int = 0) {}
        """.trimIndent()
        val findings = subject.lintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `does report second lambda with label can be moved out of the braces`() {
        val code = """
            fun foo() {
                bar(2, l@{ it })
            }

            fun bar(a: Int, b: (Int) -> Int) {
                b(a)
            }
        """.trimIndent()
        val findings = subject.lintWithContext(env, code)
        assertThat(findings).hasSize(1)
    }

    @Test
    fun `does report lambda with multiline expression can be moved out of the braces`() {
        val code = """
            fun foo() {
                bar(2, {
                    val x = 3
                    it * x
                })
            }

            fun bar(a: Int, b: (Int) -> Int) {
                b(a)
            }
        """.trimIndent()
        val findings = subject.lintWithContext(env, code)
        assertThat(findings).singleElement()
            .hasStartSourceLocation(2, 5)
            .hasEndSourceLocation(2, 8)
    }

    @Test
    fun `does not report named lambda with label which can be moved out of the braces`() {
        val code = """
            fun foo() {
                bar(name1 = 3, name2 = 2, name3 = 1, name4 = { it })
            }

            fun bar(name1: Int, name2: Int, name3: Int, name4: (Int) -> Int): Int {
                return name4(name1) + name2 + name3
            }
        """.trimIndent()
        val findings = subject.lintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `does report when lambda returned from fun can have trailing lambda`() {
        val code = """
            fun bar() {
                foo { "one" } ({ "two" })
            }

            fun foo(a: () -> String): (() -> String) -> Unit {
                return { }
            }
        """.trimIndent()
        val findings = subject.lintWithContext(env, code)
        assertThat(findings).hasSize(1)
    }

    @Test
    fun `does not report when lambda returned from fun is using trailing lambda`() {
        val code = """
            fun bar() {
                foo { "one" }() { "two" }
            }

            fun foo(a: () -> String): (() -> String) -> Unit {
                return { }
            }
        """.trimIndent()
        val findings = subject.lintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `does report when suspend lambda inside braces`() {
        val code = """
            fun runSuspend(block: suspend () -> Unit) {}

            fun println() {}

            fun usage() {
                runSuspend({ println() })
            }
        """.trimIndent()
        val findings = subject.lintWithContext(env, code)
        assertThat(findings).hasSize(1)
    }

    @Test
    fun `does not report when suspend lambda using trailing syntax`() {
        val code = """
            fun runSuspend(block: suspend () -> Unit) {}

            fun println() {}

            fun usage() {
                runSuspend { println() }
            }
        """.trimIndent()
        val findings = subject.lintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `does report generic param lambda has braces around it`() {
        val code = """
            fun <T> foo(t: T) {}

            fun test() {
                foo({ "a" })
            }
        """.trimIndent()
        val findings = subject.lintWithContext(env, code)
        assertThat(findings).hasSize(1)
    }

    @Test
    fun `does not report when last parameter is vararg - #6593`() {
        val code = """
            fun foo(f: () -> Unit, vararg x: Unit) = f()
            fun test() {
                foo({})
            }
        """.trimIndent()
        val findings = subject.lintWithContext(env, code)
        assertThat(findings).isEmpty()
    }
}
