package io.gitlab.arturbosch.detekt.cli.out

import org.assertj.core.api.Java6Assertions.assertThat
import org.junit.jupiter.api.Test

internal class SmellThresholdTest {

	@Test
	fun reached() {
		assertThat(0.reached(0)).isEqualTo(false)

		assertThat((-1).reached(0)).isEqualTo(false)

		assertThat(1.reached(0)).isEqualTo(false)
		assertThat(1.reached(1)).isEqualTo(true)
		assertThat(1.reached(2)).isEqualTo(true)

		assertThat(12.reached(11)).isEqualTo(false)
		assertThat(12.reached(12)).isEqualTo(true)
		assertThat(12.reached(13)).isEqualTo(true)
	}
}
