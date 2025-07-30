package dev.detekt.tooling.api

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class VersionProviderSpec {

    @Test
    fun `retries current running detekt version`() {
        assertThat(VersionProvider.load().current()).isEqualTo("unknown")
    }
}

internal class TestVersionProvider : VersionProvider {

    override fun current(): String = "unknown"
}
