package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import io.gitlab.arturbosch.detekt.test.lintWithContext
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class IgnoredReturnValueSpec {

    @Nested
    @KotlinCoreEnvironmentTest
    inner class `default config with non-annotated return values`(private val env: KotlinCoreEnvironment) {
        private val subject = IgnoredReturnValue(Config.empty)

        @Test
        fun `does not report when a function which returns a value is called and the return is ignored`() {
            val code = """
                fun foo() {
                    listOf("hello")
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `does not report when a function which returns a value is called before a valid return`() {
            val code = """
                fun foo() : Int {
                    listOf("hello")
                    return 42
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `does not report when a function which returns a value is called in chain and the return is ignored`() {
            val code = """
                fun foo() {
                    listOf("hello").isEmpty().not()
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `does not report when a function which returns a value is called before a semicolon`() {
            val code = """
                fun foo() {
                    listOf("hello");println("foo")
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `does not report when a function which returns a value is called after a semicolon`() {
            val code = """
                fun foo() {
                    println("foo");listOf("hello")
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `does not report when a function which returns a value is called between comments`() {
            val code = """
                fun foo() {
                    listOf("hello")//foo
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `does not report when an extension function which returns a value is called and the return is ignored`() {
            val code = """
                fun Int.isTheAnswer(): Boolean = this == 42
                fun foo(input: Int) {
                    input.isTheAnswer()
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `does not report when the return value is assigned to a pre-existing variable`() {
            val code = """
                package test
                
                annotation class CheckReturnValue
                
                @CheckReturnValue
                @Deprecated("Yes")
                fun listA() = listOf("hello")
                
                fun foo() {
                    var x: List<String>
                    x = listA()
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `does not report when a function which doesn't return a value is called`() {
            val code = """
                fun noReturnValue() {}
                
                fun foo() {
                    noReturnValue()
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `does not report when a function's return value is used in a test statement`() {
            val code = """
                fun returnsBoolean() = true
                
                fun f() {
                    if (returnsBoolean()) {}
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `does not report when a function's return value is used in a comparison`() {
            val code = """
                fun returnsInt() = 42
                
                fun f() {
                    if (42 == returnsInt()) {}
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `does not report when a function's return value is used as parameter for another call`() {
            val code = """
                fun returnsInt() = 42
                
                fun f() {
                    println(returnsInt())
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `does not report when a function's return value is used with named parameters`() {
            val code = """
                fun returnsInt() = 42
                
                fun f() {
                    println(message = returnsInt())
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `does not report when a function's return value is assigned to set`() {
            val code = """
                fun returnsInt() = 42
                
                fun f() {
                    val map = mutableMapOf<String, Int>()
                    map["some-key"] = returnsInt()
                    map.put("another-key", returnsInt())
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }
    }

    @Nested
    @KotlinCoreEnvironmentTest
    inner class `default config with annotated return values`(private val env: KotlinCoreEnvironment) {
        private val subject = IgnoredReturnValue(Config.empty)

        @Test
        fun `reports when a function which returns a value is called and the return is ignored`() {
            val code = """
                package annotation
                
                @CheckReturnValue
                fun listOfChecked(value: String) = listOf(value)
                
                fun foo() {
                    listOfChecked("hello")
                    println("foo")
                }
            """.trimIndent()
            val annotationClass = """
                package annotation
                
                annotation class CheckReturnValue
            """.trimIndent()

            val findings = subject.lintWithContext(env, code, annotationClass)
            assertThat(findings).singleElement()
                .hasMessage("The call listOfChecked is returning a value that is ignored.")
            assertThat(findings).hasStartSourceLocation(7, 5)
        }

        @Test
        fun `reports when a function which returns a value is called before a valid return`() {
            val code = """
                package noreturn
                
                annotation class CheckReturnValue
                
                @CheckReturnValue
                fun listOfChecked(value: String) = listOf(value)
                
                fun foo() : Int {
                    listOfChecked("hello")
                    return 42
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).singleElement()
                .hasMessage("The call listOfChecked is returning a value that is ignored.")
            assertThat(findings).hasStartSourceLocation(9, 5)
        }

        @Test
        fun `reports when a function which returns a value is called in chain as first statement and the return is ignored`() {
            val code = """
                package noreturn
                
                annotation class CheckReturnValue
                
                @CheckReturnValue
                fun listOfChecked(value: String) = listOf(value)
                
                fun foo() : Int {
                    listOfChecked("hello").isEmpty().not()
                    return 42
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `does not report when a function which returns a value is called in the middle of a chain and the return is ignored`() {
            val code = """
                package noreturn
                
                annotation class CheckReturnValue
                
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
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `reports when a function which returns a value is called in the end of a chain and the return is ignored`() {
            val code = """
                package noreturn
                
                annotation class CheckReturnValue
                
                @CheckReturnValue
                fun String.listOfChecked() = listOf(this)
                
                fun foo() : Int {
                    val hello = "world "
                    hello.toUpperCase()
                        .trim()
                        .listOfChecked()
                    return 42
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).singleElement()
                .hasMessage("The call listOfChecked is returning a value that is ignored.")
            assertThat(findings).hasStartSourceLocation(12, 10)
        }

        @Test
        fun `reports when a function which returns a value is called before a semicolon`() {
            val code = """
                package special
                
                annotation class CheckReturnValue
                
                @CheckReturnValue
                fun listOfChecked(value: String) = listOf(value)
                
                fun foo() {
                    listOfChecked("hello");println("foo")
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).singleElement()
                .hasMessage("The call listOfChecked is returning a value that is ignored.")
            assertThat(findings).hasStartSourceLocation(9, 5)
        }

        @Test
        fun `reports when a function which returns a value is called after a semicolon`() {
            val code = """
                package special
                
                annotation class CheckReturnValue
                
                @CheckReturnValue
                fun listOfChecked(value: String) = listOf(value)
                
                fun foo() : Int {
                    println("foo");listOfChecked("hello")
                    return 42
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).singleElement()
                .hasMessage("The call listOfChecked is returning a value that is ignored.")
            assertThat(findings).hasStartSourceLocation(9, 20)
        }

        @Test
        fun `reports when a function which returns a value is called between comments`() {
            val code = """
                package special
                
                annotation class CheckReturnValue
                
                @CheckReturnValue
                fun listOfChecked(value: String) = listOf(value)
                
                fun foo() : Int {
                    /* foo */listOfChecked("hello")//foo
                    return 42
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).singleElement()
                .hasMessage("The call listOfChecked is returning a value that is ignored.")
            assertThat(findings).hasStartSourceLocation(9, 14)
        }

        @Test
        fun `reports when an extension function which returns a value is called and the return is ignored`() {
            val code = """
                package specialize
                
                annotation class CheckReturnValue
                
                @CheckReturnValue
                fun Int.isTheAnswer(): Boolean = this == 42
                fun foo(input: Int) : Int {
                    input.isTheAnswer()
                    return 42
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).singleElement()
                .hasMessage("The call isTheAnswer is returning a value that is ignored.")
            assertThat(findings).hasStartSourceLocation(8, 11)
        }

        @Test
        fun `does not report when the return value is assigned to a pre-existing variable`() {
            val code = """
                package specialize
                
                annotation class CheckReturnValue
                
                @CheckReturnValue
                fun listOfChecked(value: String) = listOf(value)
                
                fun foo() : Int {
                    var x: List<String>
                    x = listOfChecked("hello")
                    return 42
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `does not report when a function which doesn't return a value is called`() {
            val code = """
                package specialize
                
                annotation class CheckReturnValue
                
                @CheckReturnValue
                fun noReturnValue() {}
                
                fun foo() : Int {
                    noReturnValue()
                    return 42
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `does not report when a function's return value is used in a test statement`() {
            val code = """
                package comparison
                
                annotation class CheckReturnValue
                
                @CheckReturnValue
                fun returnsBoolean() = true
                
                fun f() {
                    if (returnsBoolean()) {}
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `does not report when a function's return value is used in a comparison`() {
            val code = """
                package comparison
                
                annotation class CheckReturnValue
                
                @CheckReturnValue
                fun returnsInt() = 42
                
                fun f() {
                    if (42 == returnsInt()) {}
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `does not report when a function's return value is used as parameter for another call`() {
            val code = """
                package parameter
                
                annotation class CheckReturnValue
                
                @CheckReturnValue
                fun returnsInt() = 42
                
                fun f() {
                    println(returnsInt())
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `does not report when a function's return value is used with named parameters`() {
            val code = """
                package parameter
                
                annotation class CheckReturnValue
                
                @CheckReturnValue
                fun returnsInt() = 42
                
                fun f() {
                    println(message = returnsInt())
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `does not report when a function is the last statement in a block and it's used`() {
            val code = """
                package block
                
                annotation class CheckReturnValue
                
                @CheckReturnValue
                fun returnsInt() = 42
                
                val result = if (true) {
                    1
                } else {
                    returnsInt()
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `report when a function is not the last statement in a 'if' block and 'if' block is used`() {
            val code = """
                package block
                
                annotation class CheckReturnValue
                
                @CheckReturnValue
                fun returnsInt() = 42
                
                val result = if (true) {
                    1
                } else {
                    returnsInt()
                    2
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `does not report when a function is the last statement in a block and it's in a chain`() {
            val code = """
                package block
                
                annotation class CheckReturnValue
                
                @CheckReturnValue
                fun returnsInt() = 42
                
                fun test() {
                    if (true) {
                        1
                    } else {
                        returnsInt()
                    }.plus(1)
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `report when a function is not the last statement in a block and it's in a chain`() {
            val code = """
                package block
                
                annotation class CheckReturnValue
                
                @CheckReturnValue
                fun returnsInt() = 42
                
                fun test() {
                    if (true) {
                        1
                    } else {
                        returnsInt()
                        2
                    }.plus(1)
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `report when a function is the last statement in a block`() {
            val code = """
                package block
                
                annotation class CheckReturnValue
                
                @CheckReturnValue
                fun returnsInt() = 42
                
                fun test() {
                    if (true) {
                        println("hello")
                    } else {
                        returnsInt()
                    }
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `does not report when a function return value is consumed in a chain that returns a Unit`() {
            val code = """
                package callchain
                
                annotation class CheckReturnValue
                
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
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `does not report when a function's return value is assigned to set`() {
            val code = """
                annotation class CheckReturnValue

                @CheckReturnValue
                fun returnsInt() = 42
                
                fun f() {
                    val map = mutableMapOf<String, Int>()
                    map["some-key"] = returnsInt()
                    map.put("another-key", returnsInt())
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `reports when the containing class of a function has _@CheckReturnValue_`() {
            val code = """
                package foo
                
                annotation class CheckReturnValue
                
                @CheckReturnValue
                class Assertions {
                    fun listOfChecked(value: String) = listOf(value)
                }
                
                fun main() {
                    Assertions().listOfChecked("hello")
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `reports when the containing object of a function has _@CheckReturnValue_`() {
            val code = """
                package foo
                
                annotation class CheckReturnValue
                
                @CheckReturnValue
                object Assertions {
                    fun listOfChecked(value: String) = listOf(value)
                }
                
                fun main() {
                    Assertions.listOfChecked("hello")
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `does not report when the containing class of a function has _@CheckReturnValue_ but the function has _@CanIgnoreReturnValue_`() {
            val code = """
                package foo
                
                annotation class CheckReturnValue
                annotation class CanIgnoreReturnValue
                
                @CheckReturnValue
                class Assertions {
                    @CanIgnoreReturnValue
                    fun listOfChecked(value: String) = listOf(value)
                }
                
                fun main() {
                    Assertions().listOfChecked("hello")
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `does not report when the containing class of a function has no _@CheckReturnValue_ but the parent class has _@CheckReturnValue_`() {
            val code = """
                package foo
                
                annotation class CheckReturnValue
                
                @CheckReturnValue
                class Parent {
                    class Child {
                        fun listOfChecked(value: String) = listOf(value)
                    }
                }
                
                fun main() {
                    Parent.Child().listOfChecked("hello")
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `reports ignored return value in lambda of 'with' function`() {
            val code = """
                fun test(db: Database, sql: String) {
                    with(db) {
                        insert(sql) // Should be detected
                    }
                    with(db) {
                        return@with insert(sql) // Should be detected
                    }

                    with(db) { insert(sql).execute() }
                    with(db) { foo(insert(sql)) }
                    with(db) { insert(sql) }.execute()
                    val x = with(db) { insert(sql) }
                }
                
                annotation class CheckResult
                
                class Database {
                    @CheckResult
                    fun insert(query: String): Insert = TODO()
                }
                
                class Insert {
                    fun execute(): Int = TODO()
                }

                fun foo(insert: Insert) {}
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(2)
        }

        @Test
        fun `reports ignored return value in lambda of 'run' function`() {
            val code = """
                fun test(db: Database, sql: String) {
                    db.run {
                        insert(sql) // Should be detected
                    }
                }
                
                annotation class CheckResult
                
                class Database {
                    @CheckResult
                    fun insert(query: String): Insert = TODO()
                }
                
                class Insert {
                    fun execute(): Int = TODO()
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `reports ignored return value in lambda of 'let' function`() {
            val code = """
                 fun test(db: Database, sql: String) {
                     db.let {
                         it.insert(sql) // Should be detected
                     }
                 }
                
                 annotation class CheckResult
                
                 class Database {
                     @CheckResult
                     fun insert(query: String): Insert = TODO()
                 }
                
                 class Insert {
                     fun execute(): Int = TODO()
                 }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }
    }

    @Nested
    @KotlinCoreEnvironmentTest
    inner class `custom annotation config`(private val env: KotlinCoreEnvironment) {
        val subject = IgnoredReturnValue(
            TestConfig("returnValueAnnotations" to listOf("*.CustomReturn"))
        )

        @Test
        fun `reports when a function is annotated with the custom annotation`() {
            val code = """
                package config
                annotation class CustomReturn
                
                @CustomReturn
                fun listOfChecked(value: String) = listOf(value)
                
                fun foo() : Int {
                    listOfChecked("hello")
                    return 42
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).singleElement()
                .hasMessage("The call listOfChecked is returning a value that is ignored.")
            assertThat(findings).hasStartSourceLocation(8, 5)
        }

        @Test
        fun `does not report when a function is annotated with the not included annotation`() {
            val code = """
                package config
                
                annotation class CheckReturnValue
                
                @CheckReturnValue
                fun listOfChecked(value: String) = listOf(value)
                
                fun foo() : Int {
                    listOfChecked("hello")
                    return 42
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `does not report when a function is not annotated`() {
            val code = """
                package config
                
                fun listOfChecked(value: String) = listOf(value)
                
                fun foo() : Int {
                    listOfChecked("hello")
                    return 42
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }
    }

    @Nested
    @KotlinCoreEnvironmentTest
    inner class `restrict to config`(private val env: KotlinCoreEnvironment) {
        val subject = IgnoredReturnValue(TestConfig("restrictToConfig" to false))

        @Test
        fun `reports when a function is annotated with a custom annotation`() {
            val code = """
                package config
                
                annotation class CheckReturnValue
                
                @CheckReturnValue
                fun listOfChecked(value: String) = listOf(value)
                
                fun foo() : Int {
                    listOfChecked("hello")
                    return 42
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).singleElement()
                .hasMessage("The call listOfChecked is returning a value that is ignored.")
            assertThat(findings).hasStartSourceLocation(9, 5)
        }

        @Test
        fun `reports when a function is not annotated`() {
            val code = """
                fun listOfChecked(value: String) = listOf(value)
                
                fun foo() : Int {
                    listOfChecked("hello")
                    return 42
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).singleElement()
                .hasMessage("The call listOfChecked is returning a value that is ignored.")
            assertThat(findings).hasStartSourceLocation(4, 5)
        }

        @Test
        fun `reports when a single function inside main is not annotated - #5806`() {
            val code = """
                fun main() {
                    ignoredReturn()
                }
                
                fun ignoredReturn(): String = "asd"
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).singleElement()
                .hasMessage("The call ignoredReturn is returning a value that is ignored.")
            assertThat(findings).hasStartSourceLocation(2, 5)
        }

        @Test
        fun `reports when a function returns type that should not be ignored`() {
            val code = """
                import kotlinx.coroutines.flow.MutableStateFlow
                
                fun flowOfChecked(value: String) = MutableStateFlow(value)
                
                fun foo() : Int {
                    flowOfChecked("hello")
                    return 42
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings)
                .singleElement()
                .hasSourceLocation(6, 5)
                .hasMessage("The call flowOfChecked is returning a value that is ignored.")
        }

        @Test
        fun `does not report when a function has _@CanIgnoreReturnValue_`() {
            val code = """
                package foo
                
                annotation class CanIgnoreReturnValue
                
                @CanIgnoreReturnValue
                fun listOfChecked(value: String) = listOf(value)
                
                fun foo() : Int {
                    listOfChecked("hello")
                    return 42
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `does not report when a function has a custom annotation`() {
            val code = """
                package foo
                
                annotation class CustomIgnoreReturn
                
                @CustomIgnoreReturn
                fun listOfChecked(value: String) = listOf(value)
                
                fun foo() : Int {
                    listOfChecked("hello")
                    return 42
                }
            """.trimIndent()
            val rule = IgnoredReturnValue(
                TestConfig(
                    "ignoreReturnValueAnnotations" to listOf("*.CustomIgnoreReturn"),
                    "restrictToConfig" to false,
                )
            )
            val findings = rule.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `does not report when a function has a custom annotation on parent`() {
            val code = """
                package foo
                
                annotation class CustomIgnoreReturn
                
                @CustomIgnoreReturn
                object Foo {
                    fun listOfChecked(value: String) = listOf(value)
                }
                
                fun foo() : Int {
                    Foo.listOfChecked("hello")
                    return 42
                }
            """.trimIndent()
            val rule = IgnoredReturnValue(
                TestConfig(
                    "ignoreReturnValueAnnotations" to listOf("*.CustomIgnoreReturn"),
                    "restrictToConfig" to false,
                )
            )
            val findings = rule.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `does not report when a function is in ignoreFunctionCall`() {
            val code = """
                package foo
                
                fun listOfChecked(value: String) = listOf(value)
                
                fun foo() : Int {
                    listOfChecked("hello")
                    return 42
                }
            """.trimIndent()
            val rule = IgnoredReturnValue(
                TestConfig(
                    "ignoreFunctionCall" to listOf("foo.listOfChecked"),
                    "restrictToConfig" to false,
                )
            )
            val findings = rule.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }
    }

    @Nested
    @KotlinCoreEnvironmentTest
    inner class `return value types default config`(private val env: KotlinCoreEnvironment) {
        private val subject = IgnoredReturnValue(Config.empty)

        @Test
        fun `reports when result of function returning Flow is ignored`() {
            val code = """
                import kotlinx.coroutines.flow.flowOf
                
                fun foo() {
                    flowOf(1, 2, 3)
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)

            assertThat(findings)
                .singleElement()
                .hasSourceLocation(line = 4, column = 5)
                .hasMessage("The call flowOf is returning a value that is ignored.")
        }

        @Test
        fun `reports when a function returned result is used in a chain that returns a Flow`() {
            val code = """
                import kotlinx.coroutines.flow.*
                
                fun foo() {
                    flowOf(1, 2, 3)
                        .onEach { println(it) }
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)

            assertThat(findings)
                .singleElement()
                .hasSourceLocation(line = 5, column = 10)
                .hasMessage("The call onEach is returning a value that is ignored.")
        }

        @Test
        fun `does not report when a function returned value is used to be returned`() {
            val code = """
                import kotlinx.coroutines.flow.flowOf
                
                fun foo() = flowOf(1, 2, 3)
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `does not report when a function returned value is consumed in a chain that returns an Unit`() {
            val code = """
                import kotlinx.coroutines.flow.*
                
                suspend fun foo() {
                    flowOf(1, 2, 3)
                        .onEach { println(it) }
                        .collect()
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }
    }

    @Nested
    @KotlinCoreEnvironmentTest(additionalJavaSourcePaths = ["java"])
    inner class `Java sources`(val env: KotlinCoreEnvironment) {
        private val subject = IgnoredReturnValue(Config.empty)

        @Test
        fun `reports when annotation is on the method`() {
            val code = """
                import com.example.ignore_return_value.Foo
                
                fun test(foo: Foo) {
                    foo.foo()
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `reports when annotation is on the package`() {
            val code = """
                import com.example.ignore_return_value.annotation_on_package.Bar
                
                fun test(bar: Bar) {
                    bar.bar()
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `does not report when a function's return value is assigned to set`() {
            val code = """
                import com.example.ignore_return_value.Foo

                fun test(foo: Foo) {
                    val map = mutableMapOf<String, Any>()
                    map["some_key"] = foo.foo()
                    map.put("another-key", foo.foo())
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).isEmpty()
        }
    }
}
