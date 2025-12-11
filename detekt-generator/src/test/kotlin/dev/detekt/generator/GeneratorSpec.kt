package dev.detekt.generator

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import kotlin.io.path.Path
import kotlin.io.path.readText

class GeneratorSpec {
    @Test
    fun `config files generated successfully`() {
        assertThat(Path(RULE_PATH1, CONFIG_PATH)).exists()
        assertThat(Path(RULE_PATH2, CONFIG_PATH)).exists()
    }

    @Test
    fun `config files have their own content`() {
        assertThat(Path(RULE_PATH1, CONFIG_PATH).readText())
            .contains("complexity:")
            .doesNotContain("coroutines:")

        assertThat(Path(RULE_PATH2, CONFIG_PATH).readText())
            .contains("coroutines:")
            .doesNotContain("complexity:")
    }

    companion object {
        private const val CONFIG_PATH = "src/main/resources/config/config.yml"
        private const val RULE_PATH1 = "src/test/resources/ruleset1"
        private const val RULE_PATH2 = "src/test/resources/ruleset2"

        @BeforeAll
        @JvmStatic
        fun init() {
            val args = arrayOf(
                "--generate-custom-rule-config",
                "--input",
                "$RULE_PATH1;$RULE_PATH2",
            )
            main(args)
        }

        @AfterAll
        @JvmStatic
        fun tearDown() {
            Path(RULE_PATH1, CONFIG_PATH).toFile().delete()
            Path(RULE_PATH2, CONFIG_PATH).toFile().delete()
        }
    }
}
