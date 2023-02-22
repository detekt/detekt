@file:JvmName("Main")

package io.gitlab.arturbosch.detekt.generator

import com.beust.jcommander.JCommander
import kotlin.io.path.isDirectory
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

    if (options.generateCustomRuleConfig) {
        Generator(options).executeCustomRuleConfig()
        return
    }

    require(options.documentationPath.isDirectory()) { "Documentation path must be a directory." }
    require(options.configPath.isDirectory()) { "Config path must be a directory." }

    Generator(options).execute()
}
