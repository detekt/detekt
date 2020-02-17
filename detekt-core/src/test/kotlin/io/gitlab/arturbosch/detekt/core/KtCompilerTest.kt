package io.gitlab.arturbosch.detekt.core

import io.gitlab.arturbosch.detekt.api.internal.RELATIVE_PATH
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class KtCompilerTest : Spek({
    describe("Kotlin Compiler") {

        it("ktFileHasExtraUserData") {
            val ktCompiler = KtCompiler()

            val ktFile = ktCompiler.compile(path, path.resolve("Default.kt"))

            assertThat(ktFile.getUserData(LINE_SEPARATOR)).isEqualTo(System.lineSeparator())
            assertThat(ktFile.getUserData(RELATIVE_PATH))
                    .isEqualTo(path.fileName.resolve("Default.kt").toString())
        }
    }
})
