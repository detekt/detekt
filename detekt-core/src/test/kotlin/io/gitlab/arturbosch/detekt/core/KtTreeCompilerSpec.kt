package io.gitlab.arturbosch.detekt.core

import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * @author Artur Bosch
 */
class KtTreeCompilerSpec : Spek({

	describe("tree compiler functionality") {

		it("should compile all files") {
			val ktFiles = KtTreeCompiler().compile(path)
			assertTrue(ktFiles.size >= 3, "It should compile more than three files, but did ${ktFiles.size}")
		}

		it("should filter the file 'Default.kt'") {
			val filter = PathFilter(".*Default.kt")
			val ktFiles = KtTreeCompiler(filters = listOf(filter)).compile(path)
			val ktFile = ktFiles.find { it.name == "Default.kt" }
			assertNull(ktFile, "It should have no Default.kt file")
		}

		it("should work with two or more filters") {
			val filter = PathFilter(".*Default.kt")
			val filterTwo = PathFilter(".*Test.*")
			val filterThree = PathFilter(".*Complex.*")
			val filterFour = PathFilter(".*KotlinScript.*")
			val ktFiles = KtTreeCompiler(filters = listOf(filter, filterTwo, filterThree, filterFour)).compile(path)
			assertThat(ktFiles).isEmpty()
		}

		it("should also compile regular files") {
			assertTrue { KtTreeCompiler().compile(path.resolve("Default.kt")).size == 1 }
		}
	}

})
