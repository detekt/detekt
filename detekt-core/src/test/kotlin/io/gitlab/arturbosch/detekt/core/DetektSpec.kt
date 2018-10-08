package io.gitlab.arturbosch.detekt.core

import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

/**
 * @author Artur Bosch
 */
class DetektSpec : Spek({

	describe("default providers must be registered in META-INF/services") {

		val detekt = DetektFacade.create(ProcessingSettings(path))

		it("should detect findings from more than one provider") {
			assertThat(detekt.run().findings).isNotEmpty
		}
	}
})
