package io.gitlab.arturbosch.detekt.core

import io.gitlab.arturbosch.detekt.api.internal.RELATIVE_PATH
import io.gitlab.arturbosch.detekt.test.resource
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatIllegalArgumentException
import org.assertj.core.api.Assertions.assertThatIllegalStateException
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.nio.file.Paths

class KtCompilerTest : Spek({

    describe("Kotlin Compiler") {

        val ktCompiler = KtCompiler()

        it("Kotlin file has extra user data") {
            val ktFile = ktCompiler.compile(path, path.resolve("Default.kt"))

            assertThat(ktFile.getUserData(LINE_SEPARATOR)).isEqualTo("\n")
            assertThat(ktFile.getUserData(RELATIVE_PATH))
                .isEqualTo(path.fileName.resolve("Default.kt").toString())
        }

        it("throws an exception for an invalid sub path") {
            assertThatIllegalArgumentException()
                .isThrownBy { ktCompiler.compile(path, path) }
                .withMessage("Given sub path should be a regular file!")
        }

        it("throws an exception for a css file") {
            val cssPath = Paths.get(resource("css"))
            assertThatIllegalStateException()
                .isThrownBy { ktCompiler.compile(cssPath, cssPath.resolve("test.css")) }
        }
    }
})
