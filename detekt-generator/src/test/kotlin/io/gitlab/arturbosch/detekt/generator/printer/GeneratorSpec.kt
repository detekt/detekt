package io.gitlab.arturbosch.detekt.generator.printer

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.io.path.readText

class GeneratorSpec {
    @Test
    fun `config files generated successfully`() {
        assertThat(Paths.get(tempDir1.toString(), configPath)).exists()
        assertThat(Paths.get(tempDir2.toString(), configPath)).exists()
    }

    @Test
    fun `config files have their own content`() {
        assertThat(Paths.get(tempDir1.toString(), configPath).readText())
            .contains("complexity:")
            .doesNotContain("coroutines:")

        assertThat(Paths.get(tempDir2.toString(), configPath).readText())
            .contains("coroutines:")
            .doesNotContain("complexity:")
    }

    companion object {
        private const val sourceDir1 = "../detekt-rules-complexity"
        private const val sourceDir2 = "../detekt-rules-coroutines"
        private const val configPath = "/src/main/resources/config/config.yml"

        private val tempDir1: File = Files.createTempDirectory(null).toFile()
        private val tempDir2: File = Files.createTempDirectory(null).toFile()

        @JvmStatic
        @BeforeAll
        fun init() {
            File(Paths.get(sourceDir1).toString()).copyRecursively(tempDir1)
            File(Paths.get(sourceDir2).toString()).copyRecursively(tempDir2)

            val args = arrayOf(
                "--generate-custom-rule-config",
                "--input",
                "$tempDir1, $tempDir2"
            )
            io.gitlab.arturbosch.detekt.generator.main(args)
        }

        @JvmStatic
        @AfterAll
        fun tearDown() {
            tempDir1.deleteRecursively()
            tempDir2.deleteRecursively()
        }
    }
}
