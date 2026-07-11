package dev.detekt.cli

import com.beust.jcommander.JCommander
import com.beust.jcommander.ParameterException
import java.nio.file.Path
import kotlin.io.path.isDirectory
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
    val baseline = baseline
    val baselineFragments = baselineFragments
    val violation = when {
        baseline != null && baselineFragments != null ->
            "--baseline and --baseline-fragments cannot be used together."

        createBaseline && baseline == null && baselineFragments == null ->
            "Creating a baseline requires either --baseline or --baseline-fragments to specify an output path."

        !createBaseline && baseline != null -> baseline.validationError()

        baselineFragments != null -> baselineFragments.validationError(createBaseline)

        else -> null
    }

    if (violation != null) {
        throw HandledArgumentViolation(violation, jCommander.usageAsString())
    }
}

private fun Path.validationError(): String? =
    when {
        notExists() -> "The file specified by --baseline should exist '$this'."
        !isRegularFile() -> "The path specified by --baseline should be a file '$this'."
        else -> null
    }

private fun Path.validationError(createBaseline: Boolean): String? =
    when {
        !createBaseline && notExists() -> "The directory specified by --baseline-fragments should exist '$this'."
        !notExists() && !isDirectory() -> "The path specified by --baseline-fragments should be a directory '$this'."
        else -> null
    }
