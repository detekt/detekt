package io.gitlab.arturbosch.detekt.rules.bugs

import io.github.detekt.test.utils.resourceAsPath
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.compileAndLint
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class EqualsAlwaysReturnsTrueOrFalseSpec {
    private val subject = EqualsAlwaysReturnsTrueOrFalse(Config.empty)

    @Nested
    inner class `Equals Always Returns True Or False rule` {

        @Test
        fun `reports equals() methods`() {
            assertThat(subject.lint(resourceAsPath("EqualsAlwaysReturnsTrueOrFalsePositive.kt"))).hasSize(6)
        }

        @Test
        fun `does not report equals() methods`() {
            assertThat(subject.lint(resourceAsPath("EqualsAlwaysReturnsTrueOrFalseNegative.kt"))).isEmpty()
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
            """
            assertThat(subject.compileAndLint(code)).hasSize(1)
        }

        @Test
        fun `detects and doesn't crash when the equals method contains no return expression - #2103`() {
            val code = """
                open class SuperClass
                
                data class Item(val text: String) : SuperClass() {
                    override fun equals(other: Any?): Boolean = (other as? Item)?.text == this.text
                    override fun hashCode(): Int = text.hashCode()
                }
            """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }
    }
}
