package io.gitlab.arturbosch.detekt.core

import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

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
