package io.gitlab.arturbosch.detekt.generator.printer

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.io.File
import kotlin.io.path.Path
import kotlin.io.path.createTempDirectory
import kotlin.io.path.readText

class GeneratorSpec {
    private val configPath = "/src/main/resources/config/config.yml"

    private val tempDir1: File = createTempDirectory().toFile()
    private val tempDir2: File = createTempDirectory().toFile()

    @BeforeAll
    fun init() {
        Path("../detekt-rules-complexity").toFile().copyRecursively(tempDir1)
        Path("../detekt-rules-coroutines").toFile().copyRecursively(tempDir2)

        val args = arrayOf(
            "--generate-custom-rule-config",
            "--input",
            "$tempDir1, $tempDir2",
        )
        io.gitlab.arturbosch.detekt.generator.main(args)
    }

    @Test
    fun `config files generated successfully`() {
        assertThat(Path(tempDir1.toString(), configPath)).exists()
        assertThat(Path(tempDir2.toString(), configPath)).exists()
    }

    @Test
    fun `config files have their own content`() {
        assertThat(Path(tempDir1.toString(), configPath).readText())
            .contains("complexity:")
            .doesNotContain("coroutines:")

        assertThat(Path(tempDir2.toString(), configPath).readText())
            .contains("coroutines:")
            .doesNotContain("complexity:")
    }

    @AfterAll
    fun tearDown() {
        tempDir1.deleteRecursively()
        tempDir2.deleteRecursively()
    }
}
