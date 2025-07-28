package dev.detekt.core.baseline

import dev.detekt.api.test.createIssue
import dev.detekt.core.baseline.baselineId
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class DefaultBaselineKtTest {
    @Test
    fun `baselineId Extension`() {
        assertThat(createIssue().baselineId).isEqualTo("TestSmell/id:TestFile.kt:TestEntitySignature")
    }
}
