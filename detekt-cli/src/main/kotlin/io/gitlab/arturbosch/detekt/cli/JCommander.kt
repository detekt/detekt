package io.gitlab.arturbosch.detekt.cli

import com.beust.jcommander.JCommander
import com.beust.jcommander.ParameterException
import java.io.PrintStream

@Suppress("detekt.SpreadOperator", "detekt.ThrowsCount")
inline fun <reified T : Args> parseArguments(
    args: Array<out String>,
    outPrinter: PrintStream,
    errorPrinter: PrintStream,
    validateCli: T.(MessageCollector) -> Unit = {}
): T {
    val cli = T::class.java.declaredConstructors
        .firstOrNull()
        ?.newInstance() as? T
        ?: throw IllegalStateException("Could not create Args object for class ${T::class.java}")

    val jCommander = JCommander()

    jCommander.addObject(cli)
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
    validateCli(cli, object : MessageCollector {
        override fun plusAssign(msg: String) {
            violations += msg
        }
    })
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
