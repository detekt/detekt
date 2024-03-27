package io.gitlab.arturbosch.detekt.generator.printer

import io.gitlab.arturbosch.detekt.generator.main
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import kotlin.io.path.Path
import kotlin.io.path.createTempDirectory
import kotlin.io.path.readText

class GeneratorSpec {
    private val configPath = "src/main/resources/config/config.yml"
    private val rulePath1 = "src/test/resources/ruleset1"
    private val rulePath2 = "src/test/resources/ruleset2"
    private val documentationOutput = createTempDirectory()
    private val configurationOutput = createTempDirectory()

    @BeforeAll
    fun init() {
        val args = arrayOf(
            "--generate-custom-rule-config",
            "--input",
            "$rulePath1,$rulePath2",
            "--documentation",
            documentationOutput.toString(),
            "--config",
            configurationOutput.toString(),
        )
        main(args)
    }

    @Test
    fun `config files generated successfully`() {
        assertThat(Path(rulePath1, configPath)).exists()
        assertThat(Path(rulePath2, configPath)).exists()
    }

    @Test
    fun `config files have their own content`() {
        assertThat(Path(rulePath1, configPath).readText())
            .contains("complexity:")
            .doesNotContain("coroutines:")

        assertThat(Path(rulePath2, configPath).readText())
            .contains("coroutines:")
            .doesNotContain("complexity:")
    }

    @AfterAll
    fun tearDown() {
        Path(rulePath1, configPath).toFile().delete()
        Path(rulePath2, configPath).toFile().delete()
        documentationOutput.toFile().deleteRecursively()
        configurationOutput.toFile().deleteRecursively()
    }
}
