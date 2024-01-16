package io.github.detekt.psi

import io.github.detekt.test.utils.internal.FakePsiFile
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.com.intellij.psi.PsiFile
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.io.File

class KtFilesSpec {

    @Nested
    inner class fileNameWithoutSuffix {

        @Test
        fun `should remove kt suffix`() {
            val filename = makeFile("C.kt").fileNameWithoutSuffix()
            assertThat(filename).isEqualTo("C")
        }

        @Test
        fun `should remove kts suffix`() {
            val filename = makeFile("C.kts").fileNameWithoutSuffix()
            assertThat(filename).isEqualTo("C")
        }

        @Test
        fun `should not remove non kotlin suffixes`() {
            val filename = makeFile("C.java").fileNameWithoutSuffix()
            assertThat(filename).isEqualTo("C.java")
        }

        @Test
        fun `should not remove common suffix`() {
            val filename = makeFile("C.common").fileNameWithoutSuffix()
            assertThat(filename).isEqualTo("C.common")
        }

        @Test
        fun `should not remove common_ktx suffix`() {
            val filename = makeFile("C.common.ktx").fileNameWithoutSuffix()
            assertThat(filename).isEqualTo("C.common.ktx")
        }
    }

    private fun makeFile(filename: String): PsiFile = FakePsiFile(name = filename)

    @Nested
    inner class FilePathSpec {

        // Path separator for the current platform, short name, because lots of usages.
        private val ps = File.separator

        @Test fun `toString of absolute path`() {
            val filePath = FilePath.fromAbsolute(File("/a/b/c").toPath())

            assertThat(filePath.toString())
                .isEqualTo("FilePath(absolutePath=${ps}a${ps}b${ps}c, basePath=null, relativePath=null)")
        }

        @Test fun `toString of relative path`() {
            val filePath = FilePath.fromRelative(File("/a/b").toPath(), File("c/d").toPath())

            assertThat(filePath.toString()).isEqualTo(
                "FilePath(absolutePath=${ps}a${ps}b${ps}c${ps}d, basePath=${ps}a${ps}b, relativePath=c${ps}d)"
            )
        }
    }
}
