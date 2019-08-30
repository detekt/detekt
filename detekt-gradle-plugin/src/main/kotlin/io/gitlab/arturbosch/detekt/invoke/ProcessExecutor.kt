package io.gitlab.arturbosch.detekt.invoke

import io.gitlab.arturbosch.detekt.BuildFailure
import org.gradle.api.logging.Logger
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.InputStreamReader

object ProcessExecutor {
    fun startProcess(args: Array<String>, logger: Logger) {
        val process = Runtime.getRuntime().exec(args)

        BufferedReader(InputStreamReader(BufferedInputStream(process.inputStream))).use {
            val inputs = it.readLines().joinToString("\n")
            logger.debug(inputs)
        }

        BufferedReader(InputStreamReader(BufferedInputStream(process.errorStream))).use {
            val errors = it.readLines().joinToString("\n")
            if (errors.isNotEmpty()) {
                throw BuildFailure(errors)
            }
        }

        process.destroy()
    }
}
