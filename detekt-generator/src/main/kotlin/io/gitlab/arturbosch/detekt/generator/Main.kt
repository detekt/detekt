@file:JvmName("Main")

package io.gitlab.arturbosch.detekt.generator

import io.gitlab.arturbosch.detekt.cli.parseArguments
import io.gitlab.arturbosch.detekt.core.isFile
import java.nio.file.Files

fun main(args: Array<String>) {
    val arguments = parseArguments<GeneratorArgs>(
        args,
        System.out,
        System.err
    ) { messages ->
        if (Files.exists(documentationPath) && documentationPath.isFile()) {
            messages += "Documentation path must be a directory."
        }

        if (Files.exists(configPath) && configPath.isFile()) {
            messages += "Config path must be a directory."
        }
        // input paths are validated by MultipleExistingPathConverter
    }
    val executable = Runner(arguments)
    executable.execute()
}
