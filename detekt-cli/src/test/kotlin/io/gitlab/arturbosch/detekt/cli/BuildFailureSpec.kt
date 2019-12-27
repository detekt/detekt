package io.gitlab.arturbosch.detekt.cli

import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class BuildFailureSpec : Spek({

    describe("isValidAndSmallerOrEqual extension function tests") {
        assertThat(0.isValidAndSmallerOrEqual(0)).isEqualTo(false)
        assertThat((-1).isValidAndSmallerOrEqual(0)).isEqualTo(false)
        assertThat(1.isValidAndSmallerOrEqual(0)).isEqualTo(false)
        assertThat(1.isValidAndSmallerOrEqual(1)).isEqualTo(true)
        assertThat(1.isValidAndSmallerOrEqual(2)).isEqualTo(true)
        assertThat(12.isValidAndSmallerOrEqual(11)).isEqualTo(false)
        assertThat(12.isValidAndSmallerOrEqual(12)).isEqualTo(true)
        assertThat(12.isValidAndSmallerOrEqual(13)).isEqualTo(true)
    }
})
