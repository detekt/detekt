package io.gitlab.arturbosch.detekt.core.baseline

import io.gitlab.arturbosch.detekt.test.createFinding
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class DefaultBaselineKtTest {
    @Test
    fun `baselineId Extension`() {
        assertThat(createFinding().baselineId).isEqualTo("TestSmell:TestEntitySignature")
    }
}
