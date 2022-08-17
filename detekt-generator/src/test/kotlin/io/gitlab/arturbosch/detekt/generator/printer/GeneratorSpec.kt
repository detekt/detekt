package io.gitlab.arturbosch.detekt.generator.printer

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

class GeneratorSpec {
    @Test
    fun `config files generated successfully`() {
        assertThat(File("$folder1$configPath").canonicalFile).exists()
        assertThat(File("$folder2$configPath").canonicalFile).exists()
    }

    @Test
    fun `config files have proper content`() {
        assertThat(File("$folder1$configPath").canonicalFile.readText())
            .contains("complexity:")
            .doesNotContain("coroutines:")

        assertThat(File("$folder2$configPath").canonicalFile.readText())
            .contains("coroutines:")
            .doesNotContain("complexity:")
    }

    companion object {
        private const val folder1 = "../detekt-rules-complexity"
        private const val folder2 = "../detekt-rules-coroutines"
        private const val configPath = "/src/main/resources/config/config.yml"

        @JvmStatic
        @BeforeAll
        fun init() {
            val args = arrayOf(
                "--generate-custom-rule-config",
                "--input",
                "$folder1, $folder2",
            )
            io.gitlab.arturbosch.detekt.generator.main(args)
        }

        @JvmStatic
        @AfterAll
        fun tearDown() {
            Files.deleteIfExists(Paths.get(folder1, configPath))
            Files.deleteIfExists(Paths.get(folder2, configPath))
        }
    }
}
