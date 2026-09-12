package dev.detekt.generator

import com.beust.jcommander.JCommander
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class GeneratorArgsSpec {

    @ParameterizedTest
    @ValueSource(strings = ["-h", "--help"])
    fun `parses help flag and outputs usage text`(helpFlag: String) {
        val args = GeneratorArgs()
        val parser = JCommander(args)
        parser.parse(helpFlag)
        assertThat(args.help).isTrue()

        val usage = StringBuilder()
        parser.usageFormatter.usage(usage)
        assertThat(usage.toString().trim()).isEqualTo(expectedHelp)
    }
}

private val expectedHelp = """
    |Usage: <main class> [options]
    |  Options:
    |    --config, -c
    |      Output path for generated detekt config.
    |    --documentation, -d
    |      Output path for generated documentation.
    |    --generate-custom-rule-config, -gcrc
    |      Generate config for user-defined rules. Path to user rules can be 
    |      specified with --input option
    |      Default: false
    |    --help, -h
    |      Shows the usage.
    |  * --input, -i
    |      Input paths to analyze.
    |      Default: []
""".trimMargin()
