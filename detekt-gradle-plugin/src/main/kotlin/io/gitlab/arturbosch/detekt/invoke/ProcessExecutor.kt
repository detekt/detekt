package io.gitlab.arturbosch.detekt.invoke

import io.gitlab.arturbosch.detekt.BuildFailure
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * @author Artur Bosch
 * @author Marvin Ramin
 */
object ProcessExecutor {
    fun startProcess(args: Array<String>, debug: Boolean = false) {
        val process = Runtime.getRuntime().exec(args)

        BufferedReader(InputStreamReader(BufferedInputStream(process.inputStream))).use {
            val inputs = it.readLines().joinToString("\n")
            if (debug) println(inputs)
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
