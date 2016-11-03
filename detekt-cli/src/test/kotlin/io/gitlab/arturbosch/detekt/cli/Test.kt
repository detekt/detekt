package io.gitlab.arturbosch.detekt.cli

import io.gitlab.arturbosch.detekt.api.YamlConfig
import io.gitlab.arturbosch.detekt.core.Detekt
import io.gitlab.arturbosch.detekt.core.PathFilter
import org.junit.Test
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.system.measureTimeMillis

/**
 * @author Artur Bosch
 */
class Test {

	@Test
	fun test() {
		val sum = (0..10).map {
			measureTimeMillis {
//				val ruleset = Paths.get("/home/abosch/Repos/detekt/build/libs/kast-1.0.M1.jar")
//				val path = Paths.get("/home/abosch/Repos/kdit/src/main")
//				val path = Paths.get("/home/abosch/Repos/Tinbo/src/main")
				val path = Paths.get("/home/abosch/Repos/detekt/")
				val config = Paths.get("/home/abosch/Repos/detekt/detekt-api/src/test/resources/detekt.yml")
				val pathFilters = listOf(PathFilter(".*test.*"))
				val ruleset = Paths.get("/home/abosch/Repos/detekt/detekt-sample-ruleset/build/libs/detekt-sample-ruleset-1.0.0.M1.jar")
				val rules = if (Files.exists(ruleset)) listOf(ruleset) else listOf()
				Detekt(path, YamlConfig.load(config), ruleSets = rules, pathFilters = pathFilters).run().forEach { it.printFindings() }
			}
		}.sum()
		println((sum / 10).toString() + "ms")
	}
}