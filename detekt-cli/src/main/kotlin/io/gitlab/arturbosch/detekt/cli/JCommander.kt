package io.gitlab.arturbosch.detekt.cli

import com.beust.jcommander.JCommander
import com.beust.jcommander.ParameterException
import io.gitlab.arturbosch.detekt.core.exists
import io.gitlab.arturbosch.detekt.core.isFile

fun parseArguments(args: Array<out String>): CliArgs {
    val cli = CliArgs()

    val jCommander = JCommander(cli)
    jCommander.programName = "detekt"

    try {
        @Suppress("detekt.SpreadOperator")
        jCommander.parse(*args)
    } catch (ex: ParameterException) {
        throw HandledArgumentViolation(ex.message, jCommander.usageAsString())
    }

    if (cli.help) {
        throw HelpRequest(jCommander.usageAsString())
    }

    return cli.apply { validate(jCommander) }
}

private fun JCommander.usageAsString(): String {
    val usage = StringBuilder()
    this.usageFormatter.usage(usage)
    return usage.toString()
}

private fun CliArgs.validate(jCommander: JCommander) {
    val violations = StringBuilder()

    if (createBaseline && baseline == null) {
        violations.appendln("Creating a baseline.xml requires the --baseline parameter to specify a path.")
    }

    if (!createBaseline && baseline != null) {
        if (!checkNotNull(baseline).exists()) {
            violations.appendln("The file specified by --baseline should exist '$baseline'.")
        } else if (!checkNotNull(baseline).isFile()) {
            violations.appendln("The path specified by --baseline should be a file '$baseline'.")
        }
    }

    if (violations.isNotEmpty()) {
        throw HandledArgumentViolation(violations.toString(), jCommander.usageAsString())
    }
}
