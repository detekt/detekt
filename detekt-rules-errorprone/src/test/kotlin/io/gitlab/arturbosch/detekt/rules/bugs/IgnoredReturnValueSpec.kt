package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.rules.setupKotlinEnvironment
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object IgnoredReturnValueSpec : Spek({
    setupKotlinEnvironment()

    val env: KotlinCoreEnvironment by memoized()

    describe("default config with non-annotated return values") {
        val subject by memoized { IgnoredReturnValue() }

        it("does not report when a function which returns a value is called and the return is ignored") {
            val code = """
                fun foo() {
                    listOf("hello")
                }
            """
            val findings = subject.compileAndLintWithContext(env, code, checkReturnValueAnnotationCode)
            assertThat(findings).isEmpty()
        }

        it("does not report when a function which returns a value is called before a valid return") {
            val code = """
                fun foo() : Int {
                    listOf("hello")
                    return 42
                }
            """
            val findings = subject.compileAndLintWithContext(env, code, checkReturnValueAnnotationCode)
            assertThat(findings).isEmpty()
        }

        it("does not report when a function which returns a value is called in chain and the return is ignored") {
            val code = """
                fun foo() {
                    listOf("hello").isEmpty().not()
                }
            """
            val findings = subject.compileAndLintWithContext(env, code, checkReturnValueAnnotationCode)
            assertThat(findings).isEmpty()
        }

        it("does not report when a function which returns a value is called before a semicolon") {
            val code = """
                fun foo() {
                    listOf("hello");println("foo")
                }
            """
            val findings = subject.compileAndLintWithContext(env, code, checkReturnValueAnnotationCode)
            assertThat(findings).isEmpty()
        }

        it("does not report when a function which returns a value is called after a semicolon") {
            val code = """
                fun foo() {
                    println("foo");listOf("hello")
                }
            """
            val findings = subject.compileAndLintWithContext(env, code, checkReturnValueAnnotationCode)
            assertThat(findings).isEmpty()
        }

        it("does not report when a function which returns a value is called between comments") {
            val code = """
                fun foo() {
                    listOf("hello")//foo
                }
            """
            val findings = subject.compileAndLintWithContext(env, code, checkReturnValueAnnotationCode)
            assertThat(findings).isEmpty()
        }

        it("does not report when an extension function which returns a value is called and the return is ignored") {
            val code = """
                fun Int.isTheAnswer(): Boolean = this == 42
                fun foo(input: Int) {
                    input.isTheAnswer()
                }
            """
            val findings = subject.compileAndLintWithContext(env, code, checkReturnValueAnnotationCode)
            assertThat(findings).isEmpty()
        }

        it("does not report when the return value is assigned to a pre-existing variable") {
            val code = """
                package test
                
                @CheckReturnValue
                @Deprecated("Yes")
                fun listA() = listOf("hello")
                
                fun foo() {
                    var x: List<String>
                    x = listA()
                }
            """
            val findings = subject.compileAndLintWithContext(env, code, checkReturnValueAnnotationCode)
            assertThat(findings).isEmpty()
        }

        it("does not report when a function which doesn't return a value is called") {
            val code = """
                fun noReturnValue() {}

                fun foo() {
                    noReturnValue()
                }
            """
            val findings = subject.compileAndLintWithContext(env, code, checkReturnValueAnnotationCode)
            assertThat(findings).isEmpty()
        }

        it("does not report when a function's return value is used in a test statement") {
            val code = """
                fun returnsBoolean() = true
                
                if (returnsBoolean()) {
                    // no-op
                }
            """
            val findings = subject.compileAndLintWithContext(env, code, checkReturnValueAnnotationCode)
            assertThat(findings).isEmpty()
        }

        it("does not report when a function's return value is used in a comparison") {
            val code = """
                fun returnsInt() = 42
                
                if (42 == returnsInt()) {
                    // no-op
                }
            """
            val findings = subject.compileAndLintWithContext(env, code, checkReturnValueAnnotationCode)
            assertThat(findings).isEmpty()
        }

        it("does not report when a function's return value is used as parameter for another call") {
            val code = """
                fun returnsInt() = 42
                
                println(returnsInt())
            """
            val findings = subject.compileAndLintWithContext(env, code, checkReturnValueAnnotationCode)
            assertThat(findings).isEmpty()
        }

        it("does not report when a function's return value is used with named parameters") {
            val code = """
                fun returnsInt() = 42
                
                println(message = returnsInt())
            """
            val findings = subject.compileAndLintWithContext(env, code, checkReturnValueAnnotationCode)
            assertThat(findings).isEmpty()
        }
    }

    describe("default config with annotated return values") {
        val subject by memoized { IgnoredReturnValue() }

        it("reports when a function which returns a value is called and the return is ignored") {
            val code = """
                package test
                
                @CheckReturnValue
                fun listOfChecked(value: String) = listOf(value)
                
                fun foo() {
                    listOfChecked("hello")
                    println("foo")
                }
            """
            val findings = subject.compileAndLintWithContext(env, code, checkReturnValueAnnotationCode)
            assertThat(findings).hasSize(1)
            assertThat(findings).hasSourceLocation(7, 5)
            assertThat(findings[0]).hasMessage("The call listOfChecked is returning a value that is ignored.")
        }

        it("reports when a function which returns a value is called before a valid return") {
            val code = """
                package test
                
                @CheckReturnValue
                fun listOfChecked(value: String) = listOf(value)
                
                fun foo() : Int {
                    listOfChecked("hello")
                    return 42
                }
            """
            val findings = subject.compileAndLintWithContext(env, code, checkReturnValueAnnotationCode)
            assertThat(findings).hasSize(1)
            assertThat(findings).hasSourceLocation(7, 5)
            assertThat(findings[0]).hasMessage("The call listOfChecked is returning a value that is ignored.")
        }

        it("reports when a function which returns a value is called in chain as first statement and the return is ignored") {
            val code = """
                package test
                
                @CheckReturnValue
                fun listOfChecked(value: String) = listOf(value)
                
                fun foo() : Int {
                    listOfChecked("hello").isEmpty().not()
                    return 42
                }
            """
            val findings = subject.compileAndLintWithContext(env, code, checkReturnValueAnnotationCode)
            assertThat(findings).isEmpty()
        }

        it("does not report when a function which returns a value is called in the middle of a chain and the return is ignored") {
            val code = """
                package test
                
                @CheckReturnValue
                fun String.listOfChecked() = listOf(this)
                
                fun foo() : Int {
                    val hello = "world "
                    hello.toUpperCase()
                        .trim()
                        .listOfChecked()
                        .isEmpty()
                        .not()
                    return 42
                }
            """
            val findings = subject.compileAndLintWithContext(env, code, checkReturnValueAnnotationCode)
            assertThat(findings).isEmpty()
        }

        it("reports when a function which returns a value is called in the end of a chain and the return is ignored") {
            val code = """
                package test

                @CheckReturnValue
                fun String.listOfChecked() = listOf(this)

                fun foo() : Int {
                    val hello = "world "
                    hello.toUpperCase()
                        .trim()
                        .listOfChecked()
                    return 42
                }
            """
            val findings = subject.compileAndLintWithContext(env, code, checkReturnValueAnnotationCode)
            assertThat(findings).hasSize(1)
            assertThat(findings).hasSourceLocation(10, 10)
            assertThat(findings[0]).hasMessage("The call listOfChecked is returning a value that is ignored.")
        }

        it("reports when a function which returns a value is called before a semicolon") {
            val code = """
                package test
                
                @CheckReturnValue
                fun listOfChecked(value: String) = listOf(value)
                
                fun foo() {
                    listOfChecked("hello");println("foo")
                }
            """
            val findings = subject.compileAndLintWithContext(env, code, checkReturnValueAnnotationCode)
            assertThat(findings).hasSize(1)
            assertThat(findings).hasSourceLocation(7, 5)
            assertThat(findings[0]).hasMessage("The call listOfChecked is returning a value that is ignored.")
        }

        it("reports when a function which returns a value is called after a semicolon") {
            val code = """
                package test
                
                @CheckReturnValue
                fun listOfChecked(value: String) = listOf(value)
                
                fun foo() : Int {
                    println("foo");listOfChecked("hello")
                    return 42
                }
            """
            val findings = subject.compileAndLintWithContext(env, code, checkReturnValueAnnotationCode)
            assertThat(findings).hasSize(1)
            assertThat(findings).hasSourceLocation(7, 20)
            assertThat(findings[0]).hasMessage("The call listOfChecked is returning a value that is ignored.")
        }

        it("reports when a function which returns a value is called between comments") {
            val code = """
                package test
                
                @CheckReturnValue
                fun listOfChecked(value: String) = listOf(value)
                
                fun foo() : Int {
                    /* foo */listOfChecked("hello")//foo
                    return 42
                }
            """
            val findings = subject.compileAndLintWithContext(env, code, checkReturnValueAnnotationCode)
            assertThat(findings).hasSize(1)
            assertThat(findings).hasSourceLocation(7, 14)
            assertThat(findings[0]).hasMessage("The call listOfChecked is returning a value that is ignored.")
        }

        it("reports when an extension function which returns a value is called and the return is ignored") {
            val code = """
                package test
                
                @CheckReturnValue
                fun Int.isTheAnswer(): Boolean = this == 42
                fun foo(input: Int) : Int {
                    input.isTheAnswer()
                    return 42
                }
            """
            val findings = subject.compileAndLintWithContext(env, code, checkReturnValueAnnotationCode)
            assertThat(findings).hasSize(1)
            assertThat(findings).hasSourceLocation(6, 11)
            assertThat(findings[0]).hasMessage("The call isTheAnswer is returning a value that is ignored.")
        }

        it("does not report when the return value is assigned to a pre-existing variable") {
            val code = """
                @CheckReturnValue
                fun listOfChecked(value: String) = listOf(value)
                
                fun foo() : Int {
                    var x: List<String>
                    x = listOfChecked("hello")
                    return 42
                }
            """
            val findings = subject.compileAndLintWithContext(env, code, checkReturnValueAnnotationCode)
            assertThat(findings).isEmpty()
        }

        it("does not report when a function which doesn't return a value is called") {
            val code = """
                @CheckReturnValue
                fun noReturnValue() {}

                fun foo() : Int {
                    noReturnValue()
                    return 42
                }
            """
            val findings = subject.compileAndLintWithContext(env, code, checkReturnValueAnnotationCode)
            assertThat(findings).isEmpty()
        }

        it("does not report when a function's return value is used in a test statement") {
            val code = """
                @CheckReturnValue
                fun returnsBoolean() = true
                
                if (returnsBoolean()) {
                    // no-op
                }
            """
            val findings = subject.compileAndLintWithContext(env, code, checkReturnValueAnnotationCode)
            assertThat(findings).isEmpty()
        }

        it("does not report when a function's return value is used in a comparison") {
            val code = """
                @CheckReturnValue
                fun returnsInt() = 42
                
                if (42 == returnsInt()) {
                    // no-op
                }
            """
            val findings = subject.compileAndLintWithContext(env, code, checkReturnValueAnnotationCode)
            assertThat(findings).isEmpty()
        }

        it("does not report when a function's return value is used as parameter for another call") {
            val code = """
                @CheckReturnValue
                fun returnsInt() = 42
                
                println(returnsInt())
            """
            val findings = subject.compileAndLintWithContext(env, code, checkReturnValueAnnotationCode)
            assertThat(findings).isEmpty()
        }

        it("does not report when a function's return value is used with named parameters") {
            val code = """
                @CheckReturnValue
                fun returnsInt() = 42
                
                println(message = returnsInt())
            """
            val findings = subject.compileAndLintWithContext(env, code, checkReturnValueAnnotationCode)
            assertThat(findings).isEmpty()
        }

        it("does not report when a function is the last statement in a block") {
            val code = """
                import kotlin.random.Random

                @CheckReturnValue
                fun returnsInt() = 42
                
                if (Random.nextBoolean()) {
                    println("hello")
                } else {
                    returnsInt()
                }
                
                val result = if (Random.nextBoolean()) {
                    1
                } else {
                    returnsInt()
                }
            """
            val findings = subject.compileAndLintWithContext(env, code, checkReturnValueAnnotationCode)
            assertThat(findings).isEmpty()
        }

        it("does not report when a function return value is consumed in a chain that returns a Unit") {
            val code = """
                package test

                @CheckReturnValue
                fun String.listOfChecked() = listOf(this)
                fun List<String>.print() { println(this) }
                
                fun foo() : Int {
                    val hello = "world "
                    hello.toUpperCase()
                        .trim()
                        .listOfChecked() 
                        .print()         
                    return 42
                }
            """
            val findings = subject.compileAndLintWithContext(env, code, checkReturnValueAnnotationCode)
            assertThat(findings).isEmpty()
        }
    }

    describe("custom annotation config") {
        val subject by memoized {
            IgnoredReturnValue(
                TestConfig(mapOf(IgnoredReturnValue.RETURN_VALUE_ANNOTATIONS to listOf("*.CustomReturn")))
            )
        }

        it("reports when a function is annotated with the custom annotation") {
            val code = """
                package test
                annotation class CustomReturn
                
                @CustomReturn
                fun listOfChecked(value: String) = listOf(value)
                
                fun foo() : Int {
                    listOfChecked("hello")
                    return 42
                }
            """
            val findings = subject.compileAndLintWithContext(env, code, checkReturnValueAnnotationCode)
            assertThat(findings).hasSize(1)
            assertThat(findings).hasSourceLocation(8, 5)
            assertThat(findings[0]).hasMessage("The call listOfChecked is returning a value that is ignored.")
        }

        it("does not report when a function is annotated with the not included annotation") {
            val code = """
                package test
                
                @CheckReturnValue
                fun listOfChecked(value: String) = listOf(value)
                
                fun foo() : Int {
                    listOfChecked("hello")
                    return 42
                }
            """
            val findings = subject.compileAndLintWithContext(env, code, checkReturnValueAnnotationCode)
            assertThat(findings).isEmpty()
        }

        it("does not report when a function is not annotated") {
            val code = """
                fun listOfChecked(value: String) = listOf(value)
                
                fun foo() : Int {
                    listOfChecked("hello")
                    return 42
                }
            """
            val findings = subject.compileAndLintWithContext(env, code, checkReturnValueAnnotationCode)
            assertThat(findings).isEmpty()
        }
    }

    describe("restrict to annotated methods config") {
        val subject by memoized {
            IgnoredReturnValue(TestConfig(mapOf(IgnoredReturnValue.RESTRICT_TO_ANNOTATED_METHODS to false)))
        }

        it("reports when a function is annotated with a custom annotation") {
            val code = """
                package test
                
                @CheckReturnValue
                fun listOfChecked(value: String) = listOf(value)
                
                fun foo() : Int {
                    listOfChecked("hello")
                    return 42
                }
            """
            val findings = subject.compileAndLintWithContext(env, code, checkReturnValueAnnotationCode)
            assertThat(findings).hasSize(1)
            assertThat(findings).hasSourceLocation(7, 5)
            assertThat(findings[0]).hasMessage("The call listOfChecked is returning a value that is ignored.")
        }

        it("reports when a function is not annotated") {
            val code = """
                fun listOfChecked(value: String) = listOf(value)
                
                fun foo() : Int {
                    listOfChecked("hello")
                    return 42
                }
            """
            val findings = subject.compileAndLintWithContext(env, code, checkReturnValueAnnotationCode)
            assertThat(findings).hasSize(1)
            assertThat(findings).hasSourceLocation(4, 5)
            assertThat(findings[0]).hasMessage("The call listOfChecked is returning a value that is ignored.")
        }
    }
})

private const val checkReturnValueAnnotationCode = """
package test

annotation class CheckReturnValue
"""
