package io.gitlab.arturbosch.detekt.rules.bugs

import dev.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class UselessPostfixExpressionSpec {
    private val subject = UselessPostfixExpression(Config.empty)

    @Nested
    inner class `check several types of postfix increments` {

        @Test
        fun `overrides the incremented integer`() {
            val code = """
                fun f() {
                    var i = 0
                    i = i-- // invalid
                    i = 1 + i++ // invalid
                    i = i++ + 1 // invalid
                }
            """.trimIndent()
            assertThat(subject.lint(code)).hasSize(3)
        }

        @Test
        fun `does not override the incremented integer`() {
            val code = """
                fun f() {
                    var i = 0
                    var j = 0
                    j = i++
                }
            """.trimIndent()
            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `returns no incremented value`() {
            val code = """
                fun f(): Int {
                    var i = 0
                    var j = 0
                    if (i == 0) return 1 + j++
                    return i++
                }
            """.trimIndent()
            assertThat(subject.lint(code)).hasSize(2)
        }

        @Test
        fun `return with dot expression`() {
            val code = """
                fun f(): String {
                    var id = 0
                    return id++.toString()
                }
            """.trimIndent()
            assertThat(subject.lint(code)).hasSize(1)
        }

        @Test
        fun `should not report field increments`() {
            val code = """
                class Test {
                    private var runningId: Long = 0
                
                    fun increment() {
                        runningId++
                    }
                
                    fun getId(): Long {
                        return runningId++
                    }

                    fun getIdString(): String {
                        return runningId++.toString()
                    }

                }
                
                class Foo(var i: Int = 0) {
                    fun getIdAndIncrement(): Int {
                        return i++
                    }
                }
            """.trimIndent()
            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `should detect properties shadowing fields that are incremented`() {
            val code = """
                class Test {
                    private var runningId: Long = 0
                
                    fun getId(): Long {
                        var runningId: Long = 0
                        return runningId++
                    }
                }
                
                class Foo(var i: Int = 0) {
                    fun foo(): Int {
                        var i = 0
                        return i++
                    }
                }
            """.trimIndent()
            assertThat(subject.lint(code)).hasSize(2)
        }
    }

    @Nested
    inner class `Only ++ and -- postfix operators should be considered` {

        @Test
        fun `should not report !! in a return statement`() {
            val code = """
                val str: String? = ""
                
                fun f1(): String {
                    return str!!
                }
                
                fun f2(): Int {
                    return str!!.count()
                }
            """.trimIndent()
            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `should not report !! in a standalone expression`() {
            val code = """
                fun f() {
                    val str: String? = ""
                    str!!
                }
            """.trimIndent()
            assertThat(subject.lint(code)).isEmpty()
        }
    }
}
