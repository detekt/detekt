package dev.detekt.core

import dev.detekt.api.RuleExecutionListener
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class RuleExecutionListenerLocatorSpec {

    @Test
    fun `loads listeners implementing RuleExecutionListener`() {
        val settings = createProcessingSettings()

        settings.use {
            val locator = RuleExecutionListenerLocator(it)
            val listeners = locator.load()

            assertThat(listeners).allMatch { listener -> listener is RuleExecutionListener }
        }
    }
}
