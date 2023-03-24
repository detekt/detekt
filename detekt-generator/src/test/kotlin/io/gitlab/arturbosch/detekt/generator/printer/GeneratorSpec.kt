package io.gitlab.arturbosch.detekt.generator.printer

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.io.File
import kotlin.io.path.Path
import kotlin.io.path.readText

class GeneratorSpec {
    private val configPath = "/src/main/resources/config/config.yml"

    private val rulePath1: File = Path("../detekt-generator/src/test/resources/ruleset1").toFile()
    private val rulePath2: File = Path("../detekt-generator/src/test/resources/ruleset2").toFile()

    @BeforeAll
    fun init() {
        val args = arrayOf(
            "--generate-custom-rule-config",
            "--input",
            "$rulePath1, $rulePath2",
        )
        io.gitlab.arturbosch.detekt.generator.main(args)
    }

    @Test
    fun `config files generated successfully`() {
        assertThat(Path(rulePath1.toString(), configPath)).exists()
        assertThat(Path(rulePath2.toString(), configPath)).exists()
    }

    @Test
    fun `config files have their own content`() {
        assertThat(Path(rulePath1.toString(), configPath).readText())
            .contains("complexity:")
            .doesNotContain("coroutines:")

        assertThat(Path(rulePath2.toString(), configPath).readText())
            .contains("coroutines:")
            .doesNotContain("complexity:")
    }

    @AfterAll
    fun tearDown() {
        Path(rulePath1.toString(), configPath).toFile().delete()
        Path(rulePath2.toString(), configPath).toFile().delete()
    }
}
