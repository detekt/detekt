package dev.detekt.cli

import com.beust.jcommander.JCommander
import com.beust.jcommander.ParameterException
import dev.detekt.tooling.api.AnalysisMode
import kotlin.io.path.isRegularFile
import kotlin.io.path.notExists

fun parseArguments(args: Array<out String>): CliArgs {
    val cli = CliArgs()

    val jCommander = JCommander(cli)
    jCommander.programName = "detekt"

    try {
        @Suppress("SpreadOperator")
        jCommander.parse(*args)
    } catch (@Suppress("SwallowedException") ex: ParameterException) {
        // Stacktrace in jCommander is likely irrelevant
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
    var violation: String? = null
    val baseline = baseline

    if (createBaseline && baseline == null) {
        violation = "Creating a baseline.xml requires the --baseline parameter to specify a path."
    }

    if (!createBaseline && baseline != null) {
        if (baseline.notExists()) {
            violation = "The file specified by --baseline should exist '$baseline'."
        } else if (!baseline.isRegularFile()) {
            violation = "The path specified by --baseline should be a file '$baseline'."
        }
    }

    if (validateClasspath && analysisMode != AnalysisMode.full) {
        violation = "Validate Classpath can only be executed with `--analysis-mode full`."
    }

    if (violation != null) {
        throw HandledArgumentViolation(violation, jCommander.usageAsString())
    }
}
