package io.github.detekt.test.utils

import io.github.detekt.parser.KtCompiler
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.io.path.Path
import kotlin.io.path.absolute

class KtTestCompilerTest {
    @Nested
    inner class `KtTestCompiler_createKtFile is the same as KtCompile_compile` {
        private val path = Path("src/test/resources/Example.kt")
        private val ktFile = KtCompiler().compile(path)
        private val ktFileContent = KtTestCompiler.createKtFile("", path)

        @Test
        fun name() {
            assertThat(ktFileContent.name).isEqualTo(ktFile.name)
        }

        @Test
        fun virtualFilePath() {
            assertThat(ktFileContent.virtualFilePath).isEqualTo(ktFile.virtualFilePath)
        }
    }

    @Nested
    inner class `KtTestCompiler_createKtFile is the same as KtCompile_compile absolute` {
        private val path = Path("src/test/resources/Example.kt").absolute()
        private val ktFile = KtCompiler().compile(path)
        private val ktFileContent = KtTestCompiler.createKtFile("", path)

        @Test
        fun name() {
            assertThat(ktFileContent.name).isEqualTo(ktFile.name)
        }

        @Test
        fun virtualFilePath() {
            assertThat(ktFileContent.virtualFilePath).isEqualTo(ktFile.virtualFilePath)
        }
    }
}
