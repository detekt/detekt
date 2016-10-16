package io.gitlab.arturbosch.detekt.core

import io.gitlab.arturbosch.detekt.Case
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.context
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import kotlin.test.assertTrue

/**
 * @author Artur Bosch
 */
class DetektSpec : Spek({

	context("default providers must be registered in META-INF/services") {
		describe("run detekt on test cases with no additional rulesets") {

			val detekt = Detekt(Case.CasesFolder.path())

			it("should detect findings of more than one provider") {
				assertTrue { detekt.run().size >= 1 }
			}

		}
	}
})