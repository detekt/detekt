package io.gitlab.arturbosch.detekt.core

import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.junit.jupiter.api.assertThrows
import java.nio.file.Paths
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class PathFilterSpec : Spek({

	given("an invalid regex pattern") {
		it("throws an IllegalArgumentException") {
			assertThrows<IllegalArgumentException> { PathFilter("*.") }
		}
	}

	given("a single regex pattern") {
		val pathFilter = PathFilter(".*/build/.*")

		it("matches a corresponding path") {
			val path = Paths.get("/tmp/whatever/detekt/build/should/match")

			assertTrue { pathFilter.matches(path) }
		}

		it("does not match an unrelated path") {
			val path = Paths.get("/tmp/whatever/detekt/this/should/NOT/match")

			assertFalse { pathFilter.matches(path) }
		}
	}
})
