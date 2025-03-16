@file:JvmName("Main")

package dev.detekt.generator

import com.beust.jcommander.JCommander
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
    )
    if (options.generateCustomRuleConfig) {
        generator.executeCustomRuleConfig()
    } else {
        generator.execute()
    }
    // We need an explicit call here due to https://youtrack.jetbrains.com/issue/KT-73127/Analysis-API-standalone-mode-keeps-its-process-alive
    exitProcess(0)
}
