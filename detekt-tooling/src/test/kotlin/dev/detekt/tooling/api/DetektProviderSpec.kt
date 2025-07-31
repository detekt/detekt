package dev.detekt.tooling.api

import dev.detekt.tooling.api.spec.ProcessingSpec
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class DetektProviderSpec {

    @Test
    fun `load provider with highest priority`() {
        val provider = DetektProvider.load()

        assertThat(provider.priority).isEqualTo(100)
    }
}

class PrioritizedProvider : DetektProvider {
    override val priority: Int = 100
    override fun get(processingSpec: ProcessingSpec): Detekt = error("No instances.")
}
