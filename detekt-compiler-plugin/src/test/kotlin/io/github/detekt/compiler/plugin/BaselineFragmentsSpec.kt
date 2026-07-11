package io.github.detekt.compiler.plugin

import io.github.detekt.compiler.plugin.internal.toSpec
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.junit.jupiter.api.Test
import kotlin.io.path.Path

class BaselineFragmentsSpec {

    @Test
    @OptIn(CompilerConfiguration.Internals::class)
    fun `maps baseline fragments option to processing spec`() {
        val processor = DetektCommandLineProcessor()
        val option = processor.pluginOptions.single { it.optionName == Options.BASELINE_FRAGMENTS }
        val configuration = CompilerConfiguration()

        processor.processOption(option, "baseline.d", configuration)

        assertThat(configuration.toSpec(MessageCollector.NONE).baselineSpec.fragmentDirectory)
            .isEqualTo(Path("baseline.d"))
    }
}
