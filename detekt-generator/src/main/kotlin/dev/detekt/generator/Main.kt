@file:JvmName("Main")

package dev.detekt.generator

import com.beust.jcommander.JCommander
import java.io.OutputStream
import java.io.PrintStream
import kotlin.system.exitProcess

@Suppress("detekt.SpreadOperator")
fun main(args: Array<String>) {
    val options = GeneratorArgs()
    val parser = JCommander(options)
    parser.parse(*args)

    if (options.help) {
        parser.usage()
        exitProcess(0)
    }

    val generator = Generator(
        inputPaths = options.inputPath,
        textReplacements = options.textReplacements,
        documentationPath = options.documentationPath,
        configPath = options.configPath,
        outPrinter = if (options.debug) System.out else NullPrintStream
    )
    if (options.generateCustomRuleConfig) {
        generator.executeCustomRuleConfig()
    } else {
        generator.execute()
    }
}

private object NullPrintStream : PrintStream(
    object : OutputStream() {
        override fun write(b: Int) {
            // no-op
        }
    }
)
