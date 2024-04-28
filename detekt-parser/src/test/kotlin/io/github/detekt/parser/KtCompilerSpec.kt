package io.github.detekt.parser

import io.github.detekt.psi.lineSeparator
import io.github.detekt.psi.relativePath
import io.github.detekt.test.utils.resourceAsPath
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatIllegalArgumentException
import org.jetbrains.kotlin.com.intellij.psi.PsiErrorElement
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
            val ktFile = ktCompiler.compile(path, path.resolve("DefaultLf.kt"))

            assertThat(ktFile.lineSeparator).isEqualTo("\n")
            assertThat(ktFile.relativePath)
                .isEqualTo(Path("DefaultLf.kt"))
        }

        @Test
        fun `Kotlin file with CRLF line separators has extra user data`() {
            val ktFile = ktCompiler.compile(path, path.resolve("DefaultCrLf.kt"))

            assertThat(ktFile.lineSeparator).isEqualTo("\r\n")
            assertThat(ktFile.relativePath)
                .isEqualTo(Path("DefaultCrLf.kt"))
        }

        @Test
        fun `throws an exception for an invalid path`() {
            assertThatIllegalArgumentException()
                .isThrownBy { ktCompiler.compile(path, path) }
                .withMessage("Given path '$path' should be a regular file!")
        }

        @Test
        fun `throws an exception for an non existent path`() {
            assertThatIllegalArgumentException()
                .isThrownBy { ktCompiler.compile(Path(""), Path("nonExistent")) }
                .withMessage("Given path 'nonExistent' should be a regular file!")
        }

        @Test
        fun `parses with errors for non kotlin files`() {
            val cssPath = resourceAsPath("css")
            val ktFile = ktCompiler.compile(cssPath, cssPath.resolve("test.css"))

            val errors = mutableListOf<PsiErrorElement>()
            ktFile.accept(object : KtTreeVisitorVoid() {
                override fun visitErrorElement(element: PsiErrorElement) {
                    errors.add(element)
                }
            })

            assertThat(errors).isNotEmpty()
        }
    }

    @Nested
    inner class `line ending detection` {
        @Test
        fun `detects CRLF line endings`() {
            assertThat("1\r\n2".determineLineSeparator()).isEqualTo("\r\n")
        }

        @Test
        fun `detects LF line endings`() {
            assertThat("1\n2".determineLineSeparator()).isEqualTo("\n")
        }

        @Test
        fun `detects CR line endings`() {
            assertThat("1\r2".determineLineSeparator()).isEqualTo("\r")
        }
    }
}
