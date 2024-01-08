package io.gitlab.arturbosch.detekt.core.suppressors

import io.github.detekt.test.utils.compileContentForTest
import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.test.TestConfig
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.resolve.BindingContext
import org.junit.jupiter.api.Test

class SuppressorsSpec {

    val noIgnorableCodeSmell = CodeSmell(
        issue = ARule().issue,
        entity = Entity.from(compileContentForTest("""fun foo() = Unit""".trimIndent())),
        message = "TestMessage"
    )

    val ignorableCodeSmell = CodeSmell(
        issue = ARule().issue,
        entity = Entity.from(
            compileContentForTest(
                """
                    @file:Composable
                    
                    import androidx.compose.runtime.Composable
                    
                    fun foo() = Unit
                """.trimIndent()
            )
        ),
        message = "TestMessage"
    )

    @Test
    fun `A finding that should be suppressed`() {
        val rule = ARule(TestConfig("ignoreAnnotated" to listOf("Composable")))
        val suppress = getSuppressors(rule, BindingContext.EMPTY)
            .fold(false) { acc, suppressor -> acc || suppressor.shouldSuppress(noIgnorableCodeSmell) }

        assertThat(suppress).isFalse()
    }

    @Test
    fun `A finding that should not be suppressed`() {
        val rule = ARule(TestConfig("ignoreAnnotated" to listOf("Composable")))
        val suppress = getSuppressors(rule, BindingContext.EMPTY)
            .fold(false) { acc, suppressor -> acc || suppressor.shouldSuppress(ignorableCodeSmell) }

        assertThat(suppress).isTrue()
    }
}

private class ARule(config: Config = Config.empty) : Rule(config) {
    override val issue = Issue(javaClass.simpleName, "")
}
