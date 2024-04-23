@file:JvmName("Main")

package io.gitlab.arturbosch.detekt.generator

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
}
