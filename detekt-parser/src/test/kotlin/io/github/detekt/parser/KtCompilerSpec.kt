package io.github.detekt.parser

import io.github.davidburstrom.contester.ConTesterDriver
import io.github.detekt.psi.BASE_PATH
import io.github.detekt.psi.LINE_SEPARATOR
import io.github.detekt.psi.RELATIVE_PATH
import io.github.detekt.test.utils.resourceAsPath
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatIllegalArgumentException
import org.jetbrains.kotlin.com.intellij.psi.PsiErrorElement
import org.jetbrains.kotlin.psi.KtTreeVisitorVoid
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class KtCompilerSpec {
    @AfterEach
    internal fun tearDown() {
        ConTesterDriver.cleanUp()
    }

    @Test
    fun `parallel construction of KtCompilers should be thread safe`() {
        val thread1 = ConTesterDriver.thread { KtCompiler() }
        val thread2 = ConTesterDriver.thread { KtCompiler() }
        ConTesterDriver.runToBreakpoint(thread1, "DetektPomModel.registerExtensionPoint")
        ConTesterDriver.runUntilBlockedOrTerminated(thread2)
        ConTesterDriver.join(thread1)
    }

    @Nested
    inner class `Kotlin Compiler` {

        val path = resourceAsPath("/cases")
        private val ktCompiler = KtCompiler()

        @Test
        fun `Kotlin file with LF line separators has extra user data`() {
            val ktFile = ktCompiler.compile(path, path.resolve("DefaultLf.kt"))

            assertThat(ktFile.getUserData(LINE_SEPARATOR)).isEqualTo("\n")
            assertThat(ktFile.getUserData(RELATIVE_PATH))
                .isEqualTo("DefaultLf.kt")
            assertThat(ktFile.getUserData(BASE_PATH))
                .endsWith("cases")
        }

        @Test
        fun `Kotlin file with CRLF line separators has extra user data`() {
            val ktFile = ktCompiler.compile(path, path.resolve("DefaultCrLf.kt"))

            assertThat(ktFile.getUserData(LINE_SEPARATOR)).isEqualTo("\r\n")
            assertThat(ktFile.getUserData(RELATIVE_PATH))
                .isEqualTo("DefaultCrLf.kt")
            assertThat(ktFile.getUserData(BASE_PATH))
                .endsWith("cases")
        }

        @Test
        fun `Kotlin file with LF line separators does not store extra data for relative path if not provided`() {
            val ktFile = ktCompiler.compile(null, path.resolve("DefaultLf.kt"))

            assertThat(ktFile.getUserData(LINE_SEPARATOR)).isEqualTo("\n")
            assertThat(ktFile.getUserData(RELATIVE_PATH)).isNull()
            assertThat(ktFile.getUserData(BASE_PATH)).isNull()
        }

        @Test
        fun `Kotlin file with CRLF line separators does not store extra data for relative path if not provided`() {
            val ktFile = ktCompiler.compile(null, path.resolve("DefaultCrLf.kt"))

            assertThat(ktFile.getUserData(LINE_SEPARATOR)).isEqualTo("\r\n")
            assertThat(ktFile.getUserData(RELATIVE_PATH)).isNull()
            assertThat(ktFile.getUserData(BASE_PATH)).isNull()
        }

        @Test
        fun `throws an exception for an invalid sub path`() {
            assertThatIllegalArgumentException()
                .isThrownBy { ktCompiler.compile(path, path) }
                .withMessageStartingWith("Given sub path (")
                .withMessageEndingWith(") should be a regular file!")
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
