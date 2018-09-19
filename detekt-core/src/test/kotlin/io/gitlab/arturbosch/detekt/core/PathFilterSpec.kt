package io.gitlab.arturbosch.detekt.core

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatIllegalArgumentException
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import java.nio.file.Paths

internal class PathFilterSpec : Spek({

	given("an invalid regex pattern") {
		it("throws an IllegalArgumentException") {
			assertThatIllegalArgumentException().isThrownBy { PathFilter("*.") }
		}
	}

	given("an empty pattern") {
		it("throws an IllegalArgumentException") {
			assertThatIllegalArgumentException().isThrownBy { PathFilter("") }
		}
	}

	given("an blank pattern") {
		it("throws an IllegalArgumentException") {
			assertThatIllegalArgumentException().isThrownBy { PathFilter("    ") }
		}
	}

	given("a single regex pattern") {
		val pathFilter = PathFilter(".*/build/.*")

		it("matches a corresponding path") {
			val path = Paths.get("/tmp/whatever/detekt/build/should/match")

			assertThat(pathFilter.matches(path)).isTrue()
		}

		it("does not match an unrelated path") {
			val path = Paths.get("/tmp/whatever/detekt/this/should/NOT/match")

			assertThat(pathFilter.matches(path)).isFalse()
		}
	}
})
