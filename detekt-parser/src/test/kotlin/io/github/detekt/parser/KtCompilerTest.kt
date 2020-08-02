package io.github.detekt.parser

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

class KtCompilerTest : Spek({

    describe("Kotlin Compiler") {

        val path = resourceAsPath("/cases")
        val ktCompiler by memoized(CachingMode.SCOPE) { KtCompiler() }

        it("Kotlin file has extra user data") {
            val ktFile = ktCompiler.compile(path, path.resolve("Default.kt"))

            assertThat(ktFile.getUserData(LINE_SEPARATOR)).isEqualTo(System.lineSeparator())
            assertThat(ktFile.getUserData(RELATIVE_PATH))
                .isEqualTo(path.fileName.resolve("Default.kt").toString())
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
