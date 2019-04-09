package io.gitlab.arturbosch.detekt.cli

import com.beust.jcommander.JCommander
import com.beust.jcommander.ParameterException
import kotlin.system.exitProcess

inline fun <reified T : Args> parseArguments(args: Array<String>): Pair<T, JCommander> {
    val cli = T::class.java.declaredConstructors.firstOrNull()?.newInstance() as? T
        ?: throw IllegalStateException("Could not create Args object for class ${T::class.java}")

    val jCommander = JCommander()

    jCommander.addObject(cli)
    jCommander.programName = "detekt"

    try {
        @Suppress("SpreadOperator")
        jCommander.parse(*args)
    } catch (ex: ParameterException) {
        val message = ex.message
        jCommander.failWithErrorMessages(message)
    }

    if (cli.help) {
        jCommander.usage()
        exitProcess(0)
    }

    return cli to jCommander
}

fun JCommander.failWithErrorMessages(vararg messages: String?) {
    failWithErrorMessages(messages.asIterable())
}

fun JCommander.failWithErrorMessages(messages: Iterable<String?>) {
    messages.forEach {
        LOG.error(it)
    }
    LOG.error("")
    this.usage()
    exitProcess(1)
}
