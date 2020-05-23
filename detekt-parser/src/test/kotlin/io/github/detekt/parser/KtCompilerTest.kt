package io.github.detekt.parser

import io.github.detekt.psi.LINE_SEPARATOR
import io.github.detekt.psi.RELATIVE_PATH
import io.github.detekt.test.utils.resource
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatIllegalArgumentException
import org.assertj.core.api.Assertions.assertThatIllegalStateException
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.nio.file.Paths

class KtCompilerTest : Spek({

    describe("Kotlin Compiler") {

        val path = Paths.get(resource("/cases"))
        val ktCompiler = KtCompiler()

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

        it("throws an exception for a css file") {
            val cssPath = Paths.get(resource("css"))
            assertThatIllegalStateException()
                .isThrownBy { ktCompiler.compile(cssPath, cssPath.resolve("test.css")) }
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
