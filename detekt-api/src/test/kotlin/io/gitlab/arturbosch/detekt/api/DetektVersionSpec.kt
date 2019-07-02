package io.gitlab.arturbosch.detekt.api

import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class DetektVersionSpec : Spek({

    describe("version can be retrieved correctly") {
        it("version is not blank") {
            assert(DetektVersion.current.isNotBlank())
        }

        it("version has '.'") {
            assert(DetektVersion.current.contains('.'))
        }
    }
})
