package io.github.detekt.parser

import io.github.detekt.psi.BASE_PATH
import io.github.detekt.psi.LINE_SEPARATOR
import io.github.detekt.psi.RELATIVE_PATH
import io.github.detekt.test.utils.resourceAsPath
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatIllegalArgumentException
import org.jetbrains.kotlin.com.intellij.psi.PsiErrorElement
import org.jetbrains.kotlin.psi.KtTreeVisitorVoid
import org.spekframework.spek2.Spek
import org.spekframework.spek2.lifecycle.CachingMode
import org.spekframework.spek2.style.specification.describe

class KtCompilerSpec : Spek({

    describe("Kotlin Compiler") {

        val path = resourceAsPath("/cases")
        val ktCompiler by memoized(CachingMode.SCOPE) { KtCompiler() }

        it("Kotlin file with LF line separators has extra user data") {
            val ktFile = ktCompiler.compile(path, path.resolve("DefaultLf.kt"))

            assertThat(ktFile.getUserData(LINE_SEPARATOR)).isEqualTo("\n")
            assertThat(ktFile.getUserData(RELATIVE_PATH))
                .isEqualTo("DefaultLf.kt")
            assertThat(ktFile.getUserData(BASE_PATH))
                .endsWith("cases")
        }

        it("Kotlin file with CRLF line separators has extra user data") {
            val ktFile = ktCompiler.compile(path, path.resolve("DefaultCrLf.kt"))

            assertThat(ktFile.getUserData(LINE_SEPARATOR)).isEqualTo("\r\n")
            assertThat(ktFile.getUserData(RELATIVE_PATH))
                .isEqualTo("DefaultCrLf.kt")
            assertThat(ktFile.getUserData(BASE_PATH))
                .endsWith("cases")
        }

        it("Kotlin file with LF line separators does not store extra data for relative path if not provided") {
            val ktFile = ktCompiler.compile(null, path.resolve("DefaultLf.kt"))

            assertThat(ktFile.getUserData(LINE_SEPARATOR)).isEqualTo("\n")
            assertThat(ktFile.getUserData(RELATIVE_PATH)).isNull()
            assertThat(ktFile.getUserData(BASE_PATH)).isNull()
        }

        it("Kotlin file with CRLF line separators does not store extra data for relative path if not provided") {
            val ktFile = ktCompiler.compile(null, path.resolve("DefaultCrLf.kt"))

            assertThat(ktFile.getUserData(LINE_SEPARATOR)).isEqualTo("\r\n")
            assertThat(ktFile.getUserData(RELATIVE_PATH)).isNull()
            assertThat(ktFile.getUserData(BASE_PATH)).isNull()
        }

        it("throws an exception for an invalid sub path") {
            assertThatIllegalArgumentException()
                .isThrownBy { ktCompiler.compile(path, path) }
                .withMessageStartingWith("Given sub path (")
                .withMessageEndingWith(") should be a regular file!")
        }

        it("parses with errors for non kotlin files") {
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

    describe("line ending detection") {
        it("detects CRLF line endings") {
            assertThat("1\r\n2".determineLineSeparator()).isEqualTo("\r\n")
        }

        it("detects LF line endings") {
            assertThat("1\n2".determineLineSeparator()).isEqualTo("\n")
        }

        it("detects CR line endings") {
            assertThat("1\r2".determineLineSeparator()).isEqualTo("\r")
        }
    }
})
