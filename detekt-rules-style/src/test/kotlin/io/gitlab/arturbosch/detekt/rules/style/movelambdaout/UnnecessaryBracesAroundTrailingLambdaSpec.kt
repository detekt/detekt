package io.gitlab.arturbosch.detekt.rules.style.movelambdaout

import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import io.gitlab.arturbosch.detekt.test.lintWithContext
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.junit.jupiter.api.Test

// Source https://github.com/JetBrains/intellij-community/tree/master/plugins/kotlin/idea/tests/testData/inspectionsLocal/moveLambdaOutsideParentheses
@KotlinCoreEnvironmentTest
class UnnecessaryBracesAroundTrailingLambdaSpec(val env: KotlinCoreEnvironment) {
    private val subject = UnnecessaryBracesAroundTrailingLambda()

    @Test
    fun `does report when trailing lambda had braces`() {
        val code = """
            fun foo() {
                bar({ it })
            }

            fun bar(a: Int = 0, f: (Int) -> Int) { }
            fun bar(a: Int, b: Int, f: (Int) -> Int) { }
        """.trimIndent()
        val findings = subject.lintWithContext(env, code)
        assertThat(findings).hasSize(1)
    }

    @Test
    fun `does not report when lambda inside braces is used in class delegation`() {
        val code = """
            interface I
            class C1(s: String, f: (String) -> String) : I
            class C2 : I by C1("", { "" })
        """.trimIndent()
        val findings = subject.compileAndLintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `does report when lambda inside braces is used for function param`() {
        val code = """
            fun foo(p: (Int, () -> Int) -> Unit) {
                p(1, { 2 })
            }
        """.trimIndent()
        val findings = subject.compileAndLintWithContext(env, code)
        assertThat(findings).hasSize(1)
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
        val findings = subject.compileAndLintWithContext(env, code)
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
        val findings = subject.compileAndLintWithContext(env, code)
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
        val findings = subject.compileAndLintWithContext(env, code)
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
        val findings = subject.compileAndLintWithContext(env, code)
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
        val findings = subject.compileAndLintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `does report second lambda with label can not be moved out of the braces`() {
        val code = """
            fun foo() {
                bar(2, l@{ it })
            }

            fun bar(a: Int, b: (Int) -> Int) {
                b(a)
            }
        """.trimIndent()
        val findings = subject.compileAndLintWithContext(env, code)
        assertThat(findings).hasSize(1)
    }

    @Test
    fun `does report lambda with multiline expression can not be moved out of the braces`() {
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
        val findings = subject.compileAndLintWithContext(env, code)
        assertThat(findings).hasSize(1).hasStartSourceLocation(2, 5).hasEndSourceLocation(2, 8)
    }

    @Test
    fun `does not report lambda with label which can be moved out of the braces`() {
        val code = """
            fun foo() {
                bar(name1 = 3, name2 = 2, name3 = 1, name4 = { it })
            }

            fun bar(name1: Int, name2: Int, name3: Int, name4: (Int) -> Int): Int {
                return name4(name1) + name2 + name3
            }
        """.trimIndent()
        val findings = subject.compileAndLintWithContext(env, code)
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
        val findings = subject.compileAndLintWithContext(env, code)
        assertThat(findings).hasSize(1)
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
        val findings = subject.compileAndLintWithContext(env, code)
        assertThat(findings).hasSize(1)
    }

    @Test
    fun `does not report when suspend lambda inside braces`() {
        val code = """
            fun runSuspend(block: suspend () -> Unit) {}

            fun println() {}

            fun usage() {
                runSuspend({ println() })
            }
        """.trimIndent()
        val findings = subject.compileAndLintWithContext(env, code)
        assertThat(findings).hasSize(1)
    }

    @Test
    fun `does not report lambda has nested labels`() {
        val code = """
            fun test() {
                foo(bar@ foo@{ bar(it) })
            }

            fun foo(f: (String) -> Int) {
                f("")
            }

            fun bar(s: String) = s.length
        """.trimIndent()
        val findings = subject.compileAndLintWithContext(env, code)
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
        val findings = subject.compileAndLintWithContext(env, code)
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
        val findings = subject.compileAndLintWithContext(env, code)
        assertThat(findings).isEmpty()
    }
}
