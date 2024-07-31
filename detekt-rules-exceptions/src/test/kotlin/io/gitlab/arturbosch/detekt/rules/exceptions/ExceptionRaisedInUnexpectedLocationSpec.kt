package io.gitlab.arturbosch.detekt.rules.exceptions

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ExceptionRaisedInUnexpectedLocationSpec {
    val subject = ExceptionRaisedInUnexpectedLocation(Config.empty)

    @Test
    fun `reports methods raising an unexpected exception`() {
        val code = """
            open class ExceptionRaisedInMethods {
            
                // reports 1 - method should not throw an exception
                override fun toString(): String {
                    throw IllegalStateException()
                }
            
                // reports 1 - method should not throw an exception
                override fun hashCode(): Int {
                    throw IllegalStateException()
                }
            
                // reports 1 - method should not throw an exception
                override fun equals(other: Any?): Boolean {
                    throw IllegalStateException()
                }
            
                // reports 1 - method should not throw an exception
                @Suppress("ConstantConditionIf", "RedundantSuppression")
                protected fun finalize() {
                    if (true) {
                        throw IllegalStateException()
                    }
                }
            }
            
            @Suppress("EqualsOrHashCode", "RedundantSuppression")
            object ExceptionRaisedInMethodsObject {
            
                // reports 1 - method should not throw an exception
                override fun equals(other: Any?): Boolean {
                    throw IllegalStateException()
                }
            }
        """.trimIndent()
        assertThat(subject.compileAndLint(code)).hasSize(5)
    }

    @Test
    fun `does not report methods raising no exception`() {
        val code = """
            @Suppress("RedundantOverride", "RedundantSuppression")
            open class NoExceptionRaisedInMethods {
            
                init {
                    throw IllegalStateException()
                }
            
                override fun toString(): String {
                    return super.toString()
                }
            
                override fun hashCode(): Int {
                    return super.hashCode()
                }
            
                override fun equals(other: Any?): Boolean {
                    return super.equals(other)
                }
            
                companion object {
                    init {
                        throw IllegalStateException()
                    }
                }
            
                fun doSomeEqualsComparison() {
                    throw IllegalStateException()
                }
            
                protected fun finalize() {
                }
            }
        """.trimIndent()
        assertThat(subject.compileAndLint(code)).isEmpty()
    }

    @Test
    fun `reports the configured method`() {
        val config = TestConfig("methodNames" to listOf("toDo", "todo2"))
        val findings = ExceptionRaisedInUnexpectedLocation(config).compileAndLint(
            """
                fun toDo() {
                    throw IllegalStateException()
                }
            """.trimIndent()
        )
        assertThat(findings).hasSize(1)
    }
}
