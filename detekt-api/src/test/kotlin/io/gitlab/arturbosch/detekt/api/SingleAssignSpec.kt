package io.gitlab.arturbosch.detekt.api

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatIllegalStateException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_METHOD

@TestInstance(PER_METHOD)
internal class SingleAssignSpec {
    private var value: Int by SingleAssign()

    @Test
    fun `should fail when value is retrieved`() {
        assertThatIllegalStateException().isThrownBy {
            @Suppress("UNUSED_EXPRESSION")
            value
        }
    }

    @Test
    fun `should succeed when value is assigned`() {
        value = 15
    }

    @Test
    fun `should succeed when value is retrieved`() {
        value = 15
        assertThat(value).isEqualTo(15)
    }

    @Test
    fun `should fail when value is assigned`() {
        value = 15
        assertThatIllegalStateException().isThrownBy { value = -1 }
    }
}
