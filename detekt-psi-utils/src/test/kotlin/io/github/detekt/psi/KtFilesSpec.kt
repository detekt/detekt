package io.github.detekt.psi

import io.github.detekt.test.utils.internal.FakePsiFile
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.com.intellij.psi.PsiFile
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.io.path.Path
import kotlin.io.path.absolute

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

    @Test
    fun `FilePath toString`() {
        val basePath = Path("/").absolute().resolve("a/b")
        val relativePath = Path("c/d")
        val absolutePath = Path("/").absolute().resolve("a/b/c/d")
        val filePath = FilePath(absolutePath, basePath, relativePath)

        assertThat(filePath.toString())
            .isEqualTo("FilePath(absolutePath=$absolutePath, basePath=$basePath, relativePath=$relativePath)")
    }
}
