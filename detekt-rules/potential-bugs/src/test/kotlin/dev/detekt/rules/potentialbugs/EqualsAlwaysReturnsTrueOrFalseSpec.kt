package dev.detekt.rules.potentialbugs

import dev.detekt.api.Config
import dev.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class EqualsAlwaysReturnsTrueOrFalseSpec {
    private val subject = EqualsAlwaysReturnsTrueOrFalse(Config.empty)

    @Test
    fun `reports equals() methods`() {
        @Suppress("EqualsOrHashCode")
        val code = """
            // reports 1 for every equals method
            class EqualsReturnsTrue {
            
                override fun equals(other: Any?): Boolean {
                    return true
                }
            }
            
            class EqualsReturnsFalse {
            
                override fun equals(other: Any?): Boolean {
                    return false
                }
            }
            
            class EqualsReturnsFalseWithUnreachableReturnStatement {
            
                override fun equals(other: Any?): Boolean {
                    return false
                    return true
                }
            }
            
            class EqualsReturnsFalseWithUnreachableCode {
            
                override fun equals(other: Any?): Boolean {
                    return false
                    val i = 0
                }
            }
            
            class EqualsReturnsConstantExpression {
            
                override fun equals(other: Any?) = false
            }
            
            class EqualsWithTwoReturnExpressions {
            
                override fun equals(other: Any?): Boolean {
                    if (other is Int) {
                        return true
                    }
                    return true
                }
            }
        """.trimIndent()
        assertThat(subject.lint(code)).hasSize(6)
    }

    @Test
    fun `does not report equals() methods`() {
        @Suppress("EqualsOrHashCode")
        val code = """
            class EqualsReturnsTrueOrFalse {
            
                override fun equals(other: Any?): Boolean {
                    if (other is Int) {
                        return true
                    }
                    return false
                }
            }
            
            class CorrectEquals {
            
                override fun equals(other: Any?): Boolean {
                    return this.toString() == other.toString()
                }
            }
            
            class ReferentialEquality {
            
                override fun equals(other: Any?): Boolean {
                    return this === other
                }
            }
            
            fun equals(other: Any?): Boolean {
                return false
            }
            
            class NotOverridingEquals {
            
                fun equal(other: Any?): Boolean {
                    return true
                }
            }
            
            class WrongEqualsParameterList {
            
                fun equals(other: Any, i: Int): Boolean {
                    return true
                }
            }
        """.trimIndent()
        assertThat(subject.lint(code)).isEmpty()
    }

    @Test
    fun `detects and doesn't crash when return expression is annotated - #2021`() {
        val code = """
            class C {
                override fun equals(other: Any?): Boolean {
                    @Suppress("UnsafeCallOnNullableType")
                    return true
                }
            }
        """.trimIndent()
        assertThat(subject.lint(code)).hasSize(1)
    }

    @Test
    fun `detects and doesn't crash when the equals method contains no return expression - #2103`() {
        val code = """
            open class SuperClass
            
            data class Item(val text: String) : SuperClass() {
                override fun equals(other: Any?): Boolean = (other as? Item)?.text == this.text
                override fun hashCode(): Int = text.hashCode()
            }
        """.trimIndent()
        assertThat(subject.lint(code)).isEmpty()
    }

    @Test
    fun `doesn't report when custom method named equals with extension receiver is used - #6569`() {
        val code = """
            open class Base1 {
                open fun Base1.equals(other: Any?): Boolean {
                    return false
                }
            }
            class A1 : Base1() {
                override fun Base1.equals(other: Any?): Boolean {
                    return true
                }
            }
        """.trimIndent()
        assertThat(subject.lint(code)).isEmpty()
    }
}
