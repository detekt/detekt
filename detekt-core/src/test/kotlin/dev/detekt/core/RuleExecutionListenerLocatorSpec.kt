package dev.detekt.core

import dev.detekt.api.RuleExecutionListener
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class RuleExecutionListenerLocatorSpec {

    @Nested
    inner class `when profiling is enabled` {

        @Test
        fun `loads rule execution listeners`() {
            val settings = createProcessingSettings {
                execution {
                    profiling = true
                }
            }

            settings.use {
                val locator = RuleExecutionListenerLocator(it)
                val listeners = locator.load()

                assertThat(listeners).isNotEmpty()
                assertThat(listeners).anyMatch { listener -> listener.id == "RuleProfilingListener" }
            }
        }

        @Test
        fun `loads listeners implementing RuleExecutionListener`() {
            val settings = createProcessingSettings {
                execution {
                    profiling = true
                }
            }

            settings.use {
                val locator = RuleExecutionListenerLocator(it)
                val listeners = locator.load()

                assertThat(listeners).allMatch { listener -> listener is RuleExecutionListener }
            }
        }
    }

    @Nested
    inner class `when profiling is disabled` {

        @Test
        fun `returns empty list`() {
            val settings = createProcessingSettings {
                execution {
                    profiling = false
                }
            }

            settings.use {
                val locator = RuleExecutionListenerLocator(it)
                val listeners = locator.load()

                assertThat(listeners).isEmpty()
            }
        }

        @Test
        fun `returns empty list by default`() {
            // profiling is false by default
            val settings = createProcessingSettings()

            settings.use {
                val locator = RuleExecutionListenerLocator(it)
                val listeners = locator.load()

                assertThat(listeners).isEmpty()
            }
        }
    }
}
