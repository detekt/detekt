package dev.detekt.rules.style

import dev.detekt.api.Config
import dev.detekt.test.TestConfig
import dev.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

private const val IGNORE_OVERRIDABLE_FUNCTION = "ignoreOverridableFunction"
private const val IGNORE_ACTUAL_FUNCTION = "ignoreActualFunction"
private const val EXCLUDED_FUNCTIONS = "excludedFunctions"

class FunctionOnlyReturningConstantSpec {
    val subject = FunctionOnlyReturningConstant(Config.Empty)

    @Nested
    inner class `FunctionOnlyReturningConstant rule - positive cases` {

        private val code = """
            fun functionReturningConstantString() = "1" // reports 1
            
            fun functionReturningConstantString(str: String) = "str: ${'$'}${'$'}" // reports 1
            
            fun functionReturningConstantEscapedString(str: String) = "str: \${'$'}str" // reports 1
            
            fun functionReturningConstantChar() = '1' // reports 1
            
            fun functionReturningConstantInt(): Int { // reports 1
                return 1
            }
            
            @Suppress("EqualsOrHashCode", "RedundantSuppression")
            open class FunctionReturningConstant {
            
                open fun f() = 1 // reports 1
                override fun hashCode() = 1 // reports 1
            }
            
            interface InterfaceFunctionReturningConstant {
            
                fun interfaceFunctionWithImplementation() = 1 // reports 1
            
                class NestedClassFunctionReturningConstant {
            
                    fun interfaceFunctionWithImplementation() = 1 // reports 1
                }
            }
        """.trimIndent()

        private val actualFunctionCode = """
            actual class ActualFunctionReturningConstant {
                actual fun f() = 1
            }
        """.trimIndent()

        @Test
        fun `reports functions which return constants`() {
            assertThat(subject.lint(code)).hasSize(6)
        }

        @Test
        fun `reports overridden functions which return constants`() {
            val config = TestConfig(IGNORE_OVERRIDABLE_FUNCTION to false)
            val rule = FunctionOnlyReturningConstant(config)
            assertThat(rule.lint(code)).hasSize(9)
        }

        @Test
        fun `does not report actual functions which return constants`() {
            assertThat(subject.lint(actualFunctionCode, compile = false)).isEmpty()
        }

        @Test
        fun `reports actual functions which return constants`() {
            val config = TestConfig(IGNORE_ACTUAL_FUNCTION to false)
            val rule = FunctionOnlyReturningConstant(config)
            assertThat(rule.lint(actualFunctionCode, compile = false)).hasSize(1)
        }

        @Test
        fun `does not report excluded function which returns a constant`() {
            val code = "fun f() = 1"
            val config = TestConfig(EXCLUDED_FUNCTIONS to listOf("f"))
            val rule = FunctionOnlyReturningConstant(config)
            assertThat(rule.lint(code)).isEmpty()
        }

        @Test
        fun `does not report wildcard excluded function which returns a constant`() {
            val code = "fun function() = 1"
            val config = TestConfig(EXCLUDED_FUNCTIONS to listOf("f*ion"))
            val rule = FunctionOnlyReturningConstant(config)
            assertThat(rule.lint(code)).isEmpty()
        }
    }

    @Nested
    inner class `FunctionOnlyReturningConstant rule - negative cases` {

        @Test
        fun `does not report functions which do not return constants`() {
            val code = """
                fun functionNotReturningConstant1() = 1 + 1
                
                fun functionNotReturningConstant2(): Int {
                    return 1 + 1
                }
                
                fun functionNotReturningConstantString1(str: String) = "str: ${'$'}str"
            """.trimIndent()
            assertThat(subject.lint(code)).isEmpty()
        }
    }
}
