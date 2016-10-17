package io.gitlab.arturbosch.detekt.core

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
			val ktFiles = KtTreeCompiler(path).compile()
			assertTrue(ktFiles.size >= 2, "It should compile more than two files, but did ${ktFiles.size}")
		}

		it("should filter the file 'Default.kt'") {
			val filter = PathFilter(".*Default.kt")
			val ktFiles = KtTreeCompiler(path, listOf(filter)).compile()
			val ktFile = ktFiles.find { it.name == "Default.kt" }
			assertNull(ktFile, "It should have no Default.kt file")
		}

		it("should also compile regular files") {
			assertTrue { KtTreeCompiler(path.resolve("Default.kt")).compile().size == 1 }
		}
	}

})
