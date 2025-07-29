package dev.detekt.parser

import io.github.detekt.test.utils.resourceAsPath
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatIllegalArgumentException
import org.jetbrains.kotlin.psi.KtTreeVisitorVoid
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.io.path.Path

class KtCompilerSpec {
    @Nested
    inner class `Kotlin Compiler` {

        val path = resourceAsPath("/cases")
        private val ktCompiler = KtCompiler()

        @Test
        fun `Kotlin file with LF line separators has extra user data`() {
            val ktFile = ktCompiler.compile(path.resolve("DefaultLf.kt"))

            // visit file to trigger line detection
            ktFile.accept(KtTreeVisitorVoid())

            assertThat(ktFile.virtualFile.detectedLineSeparator).isEqualTo("\n")
        }

        @Test
        fun `Kotlin file with CRLF line separators has extra user data`() {
            val ktFile = ktCompiler.compile(path.resolve("DefaultCrLf.kt"))

            // visit file to trigger line detection
            ktFile.accept(KtTreeVisitorVoid())

            assertThat(ktFile.virtualFile.detectedLineSeparator).isEqualTo("\r\n")
        }

        @Test
        fun `throws an exception for an invalid path`() {
            assertThatIllegalArgumentException()
                .isThrownBy { ktCompiler.compile(path) }
                .withMessage("Given path '$path' should be a regular file!")
        }

        @Test
        fun `throws an exception for an non existent path`() {
            assertThatIllegalArgumentException()
                .isThrownBy { ktCompiler.compile(Path("nonExistent")) }
                .withMessage("Given path 'nonExistent' should be a regular file!")
        }

        @Test
        fun `parses with errors for non kotlin files`() {
            val cssPath = resourceAsPath("css/test.css")

            assertThatIllegalArgumentException()
                .isThrownBy { ktCompiler.compile(cssPath) }
                .withMessage("$cssPath is not a Kotlin file")
        }
    }
}
