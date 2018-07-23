package io.gitlab.arturbosch.detekt.core

import io.gitlab.arturbosch.detekt.api.SplitPattern
import io.gitlab.arturbosch.detekt.test.yamlConfig
import org.assertj.core.api.Assertions
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import java.nio.file.Path
import java.nio.file.Paths

/**
 * @author Artur Bosch
 */
class TestPatternTest : Spek({

	given("a bunch of paths") {
		val pathContent = """
			a/b/c/test/abcTest.kt,
			a/b/c/test/adeTest.kt,
			a/b/c/test/afgTest.kt,
			a/b/c/d/ab.kt,
			a/b/c/d/bb.kt,
			a/b/c/d/cb.kt
		"""

		val paths = SplitPattern(pathContent).mapAll { Paths.get(it) }

		fun splitSources(pattern: TestPattern, paths: List<Path>): Pair<List<Path>, List<Path>> =
				paths.partition { pattern.matches(it.toString()) }

		fun preparePattern() = createTestPattern(yamlConfig("patterns/test-pattern.yml"))

		it("should split the given paths to main and test sources") {
			val pattern = preparePattern()
			val (testSources, mainSources) = splitSources(pattern, paths)

			Assertions.assertThat(testSources).allMatch { it.toString().endsWith("Test.kt") }
			Assertions.assertThat(mainSources).allMatch { it.toString().endsWith("b.kt") }
		}
	}
})
