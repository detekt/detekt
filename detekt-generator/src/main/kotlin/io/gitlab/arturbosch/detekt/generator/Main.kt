@file:JvmName("Main")

package io.gitlab.arturbosch.detekt.generator

import com.beust.jcommander.JCommander
import java.nio.file.Files
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

    require(Files.isDirectory(options.documentationPath)) { "Documentation path must be a directory." }
    require(Files.isDirectory(options.configPath)) { "Config path must be a directory." }

    Generator(options).execute()
}
