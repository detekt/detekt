package io.gitlab.arturbosch.detekt.core

import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

/**
 * @author Artur Bosch
 */
class KtTreeCompilerSpec : Spek({

	describe("tree compiler functionality") {

		it("should compile all files") {
			val ktFiles = KtTreeCompiler().compile(path)
			assertThat(ktFiles.size)
					.describedAs("It should compile at least three files, but did ${ktFiles.size}")
					.isGreaterThanOrEqualTo(3)
		}

		it("should filter the file 'Default.kt'") {
			val filter = PathFilter(".*Default.kt")
			val ktFiles = KtTreeCompiler(filters = listOf(filter)).compile(path)
			val ktFile = ktFiles.find { it.name == "Default.kt" }
			assertThat(ktFile).describedAs("It should have no Default.kt file").isNull()
		}

		it("should work with two or more filters") {
			val filter = PathFilter(".*Default.kt")
			val filterTwo = PathFilter(".*Test.*|.*SomeUnusedClass.*")
			val filterThree = PathFilter(".*Complex.*")
			val filterFour = PathFilter(".*KotlinScript.*")
			val ktFiles = KtTreeCompiler(filters = listOf(filter, filterTwo, filterThree, filterFour)).compile(path)
			assertThat(ktFiles).isEmpty()
		}

		it("should also compile regular files") {
			assertThat(KtTreeCompiler().compile(path.resolve("Default.kt")).size).isEqualTo(1)
		}
	}
})
