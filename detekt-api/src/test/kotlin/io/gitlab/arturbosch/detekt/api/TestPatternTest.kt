package io.gitlab.arturbosch.detekt.api

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
			a/b/c/test/abcTest.kt
			a/b/c/test/adeTest.kt
			a/b/c/test/afgTest.kt
			a/b/c/d/ab.kt
			a/b/c/d/bb.kt
			a/b/c/d/cb.kt
		"""

		val paths = pathContent.split("\n")
				.map { it.trim() }
				.filterNot { it.isEmpty() }
				.map { Paths.get(it) }

		it("should split the given paths to main and test sources") {
			val config = yamlConfig("test-pattern.yml")
			val pattern = createTestPattern(config)
			val (testSources, mainSources) = split(pattern, paths)

			Assertions.assertThat(testSources).allMatch { it.toString().endsWith("Test.kt") }
			Assertions.assertThat(mainSources).allMatch { it.toString().endsWith("b.kt") }
		}
	}

})

fun createTestPattern(config: Config) = with(config) {
	TestPattern(valueOrDefault(TestPattern.ACTIVE, false),
			valueOrDefault(TestPattern.PATTERNS, TestPattern.DEFAULT_PATTERNS),
			valueOrDefault(TestPattern.EXCLUDE_RULES, emptySet()),
			valueOrDefault(TestPattern.EXCLUDE_RULE_SETS, emptySet()))
}

fun split(pattern: TestPattern, paths: List<Path>): Pair<List<Path>, List<Path>> =
		paths.partition { pattern.matches(it.toString()) }

data class TestPattern(val active: Boolean,
					   private val _patterns: Set<String>,
					   val excludingRules: Set<String>,
					   val excludingRuleSets: Set<String>) {

	private val patterns = _patterns.map { Regex(it) }

	fun matches(path: String) = patterns.any { it.matches(path) }

	companion object {
		const val ACTIVE: String = "active"
		const val PATTERNS: String = "patterns"
		val DEFAULT_PATTERNS: Set<String> = setOf(".*/test/.*Test.kt")
		const val EXCLUDE_RULES: String = "exclude-rules"
		const val EXCLUDE_RULE_SETS: String = "exclude-rule-sets"
	}
}

