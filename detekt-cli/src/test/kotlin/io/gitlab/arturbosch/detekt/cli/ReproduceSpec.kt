package io.gitlab.arturbosch.detekt.cli

import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.core.Detekt
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.xdescribe
import java.nio.file.Paths
import kotlin.system.measureTimeMillis

/**
 * @author Artur Bosch
 */
class ReproduceSpec : Spek({

	describe("no tests dummy") {}
	xdescribe("no tests dummy") {}

	xdescribe("common system and debug tests") {
		it("analyze common projects") {
			measureTimeMillis {
				val ruleset = Paths.get("/home/artur/Repos/detekt/build/libs/kast-1.0.M1.jar")
//				val path = Paths.get("/home/artur/Repos/kdit/src/main")
//				val path = Paths.get("/home/artur/Repos/Tinbo/src/main")
				val path = Paths.get("/home/artur/Repos/detekt/src/main")
				Detekt(path, listOf(ruleset)).run().forEach { it.printFindings() }
			}.print(suffix = " ms")
		}
	}

})

fun Map.Entry<String, List<Finding>>.printFindings() {
	key.print("Ruleset: ")
	value.each { it.compact().print("\t") }
}