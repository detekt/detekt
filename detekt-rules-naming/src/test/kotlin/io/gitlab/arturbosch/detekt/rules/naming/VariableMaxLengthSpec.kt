package io.gitlab.arturbosch.detekt.rules.naming

import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.junit.jupiter.api.Test

class VariableMaxLengthSpec {

    @Test
    fun `should not report underscore variable names`() {
        val code = """
            fun getResult(): Pair<String, String> = TODO()
            fun function() {
                val (_, status) = getResult()
            }
        """.trimIndent()
        assertThat(VariableMaxLength().compileAndLint(code)).isEmpty()
    }

    @Test
    fun `should not report a variable with 64 letters`() {
        val code = "private val varThatIsExactly64LettersLongWhichYouMightNotWantToBelieveInLolz = 3"
        assertThat(VariableMaxLength().compileAndLint(code)).isEmpty()
    }

    @Test
    fun `should report a variable name that is too long`() {
        val code = "private val thisVariableIsDefinitelyWayTooLongLongerThanEverythingAndShouldBeMuchShorter = 3"
        assertThat(VariableMaxLength().compileAndLint(code)).hasSize(1)
    }
}
