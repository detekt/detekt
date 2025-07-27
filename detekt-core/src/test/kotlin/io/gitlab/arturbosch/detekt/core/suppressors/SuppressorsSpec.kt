package io.gitlab.arturbosch.detekt.core.suppressors

import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.Rule
import io.github.detekt.test.utils.compileContentForTest
import io.gitlab.arturbosch.detekt.test.TestConfig
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.resolve.BindingContext
import org.junit.jupiter.api.Test

class SuppressorsSpec {

    val noIgnorableFinding = Finding(
        entity = Entity.from(compileContentForTest("""fun foo() = Unit""".trimIndent())),
        message = "TestMessage"
    )

    val ignorableFinding = Finding(
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
        val suppress = buildSuppressors(rule, BindingContext.EMPTY)
            .fold(false) { acc, suppressor -> acc || suppressor.shouldSuppress(noIgnorableFinding) }

        assertThat(suppress).isFalse()
    }

    @Test
    fun `A finding that should not be suppressed`() {
        val rule = ARule(TestConfig("ignoreAnnotated" to listOf("Composable")))
        val suppress = buildSuppressors(rule, BindingContext.EMPTY)
            .fold(false) { acc, suppressor -> acc || suppressor.shouldSuppress(ignorableFinding) }

        assertThat(suppress).isTrue()
    }
}

private class ARule(config: Config = Config.empty) : Rule(config, "")
