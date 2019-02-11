package io.gitlab.arturbosch.detekt.core

import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

/**
 * @author Artur Bosch
 */
class KtCompilerTest : Spek({
	describe("Kotlin Compiler") {

        it("ktFileHasExtraUserData") {
            val ktCompiler = KtCompiler()

            val ktFile = ktCompiler.compile(path, path.resolve("Default.kt"))

            assertThat(ktFile.getUserData(KtCompiler.LINE_SEPARATOR)).isEqualTo("\n")
            assertThat(ktFile.getUserData(KtCompiler.RELATIVE_PATH))
                    .isEqualTo(path.fileName.resolve("Default.kt").toString())
        }
    }
})
