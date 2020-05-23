package io.gitlab.arturbosch.detekt.cli

import com.beust.jcommander.JCommander
import com.beust.jcommander.ParameterException
import io.gitlab.arturbosch.detekt.core.exists
import io.gitlab.arturbosch.detekt.core.isFile
import java.io.PrintStream

@Suppress("detekt.SpreadOperator", "detekt.ThrowsCount")
fun parseArguments(
    args: Array<out String>,
    outPrinter: PrintStream,
    errorPrinter: PrintStream
): CliArgs {
    val cli = CliArgs()

    val jCommander = JCommander(cli)
    jCommander.programName = "detekt"

    try {
        jCommander.parse(*args)
    } catch (ex: ParameterException) {
        errorPrinter.println("${ex.message}\n")
        jCommander.usage(outPrinter)
        throw HandledArgumentViolation()
    }

    if (cli.help) {
        jCommander.usage(outPrinter)
        throw HelpRequest()
    }

    val violations = mutableListOf<String>()
    val baseline = cli.baseline

    if (cli.createBaseline && baseline == null) {
        violations += "Creating a baseline.xml requires the --baseline parameter to specify a path."
    }

    if (!cli.createBaseline && baseline != null) {
        if (!baseline.exists()) {
            violations += "The file specified by --baseline should exist '$baseline'."
        } else if (!baseline.isFile()) {
            violations += "The path specified by --baseline should be a file '$baseline'."
        }
    }

    if (violations.isNotEmpty()) {
        violations.forEach(errorPrinter::println)
        errorPrinter.println()
        jCommander.usage(outPrinter)
        throw HandledArgumentViolation()
    }

    return cli
}

fun JCommander.usage(outPrinter: PrintStream) {
    val usage = StringBuilder()
    this.usageFormatter.usage(usage)
    outPrinter.println(usage.toString())
}
