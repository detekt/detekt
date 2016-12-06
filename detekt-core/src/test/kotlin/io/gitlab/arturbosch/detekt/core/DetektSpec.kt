package io.gitlab.arturbosch.detekt.core

import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import kotlin.test.assertTrue

/**
 * @author Artur Bosch
 */
class DetektSpec : Spek({

	describe("default providers must be registered in META-INF/services") {

		val detekt = Detekt(path)

		it("should detect findings from more than one provider") {
			assertTrue { detekt.run().findings.isNotEmpty() }
		}

	}
})