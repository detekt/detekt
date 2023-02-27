package io.github.detekt.psi

import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.com.intellij.psi.PsiFile
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class KtFilesSpec {

    @Nested
    inner class fileNameWithoutSuffix {

        @Test
        fun `should remove kt suffix`() {
            val filename = makeFile("C.kt").fileNameWithoutKotlinSuffix()
            assertThat(filename).isEqualTo("C")
        }

        @Test
        fun `should remove kts suffix`() {
            val filename = makeFile("C.kts").fileNameWithoutKotlinSuffix()
            assertThat(filename).isEqualTo("C")
        }

        @Test
        fun `should remove common_kt suffix`() {
            val filename = makeFile("C.common.kt").fileNameWithoutKotlinSuffix()
            assertThat(filename).isEqualTo("C")
        }

        @Test
        fun `should not remove non kotlin suffixes`() {
            val filename = makeFile("C.java").fileNameWithoutKotlinSuffix()
            assertThat(filename).isEqualTo("C.java")
        }

        @Test
        fun `should not remove common suffix`() {
            val filename = makeFile("C.common").fileNameWithoutKotlinSuffix()
            assertThat(filename).isEqualTo("C.common")
        }

        @Test
        fun `should not remove common_ktx suffix`() {
            val filename = makeFile("C.common.ktx").fileNameWithoutKotlinSuffix()
            assertThat(filename).isEqualTo("C.common.ktx")
        }
    }

    private fun makeFile(filename: String): PsiFile {
        return mockk {
            every { name } returns filename
        }
    }
}
