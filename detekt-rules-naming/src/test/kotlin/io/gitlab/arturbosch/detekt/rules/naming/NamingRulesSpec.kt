package io.gitlab.arturbosch.detekt.rules.naming

import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class NamingRulesSpec {

    @Nested
    inner class `naming like in constants is allowed for destructuring` {
        @Test
        fun `should not detect any`() {
            val code = """
                data class D(val i: Int, val j: Int)
                fun doStuff() {
                    val (_, HOLY_GRAIL) = D(5, 4)
                }
            """
            assertThat(NamingRules().compileAndLint(code)).isEmpty()
        }
    }
}
