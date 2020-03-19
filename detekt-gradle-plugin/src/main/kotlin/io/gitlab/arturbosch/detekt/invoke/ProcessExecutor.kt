package io.gitlab.arturbosch.detekt.invoke

import org.gradle.api.GradleException
import org.gradle.api.logging.Logging
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.InputStreamReader

object ProcessExecutor {
    fun startProcess(args: Array<String>) {
        val process = Runtime.getRuntime().exec(args)
        val logger = Logging.getLogger(this::class.java)

        BufferedReader(InputStreamReader(BufferedInputStream(process.inputStream))).use {
            val inputs = it.readLines().joinToString("\n")
            logger.debug(inputs)
        }

        BufferedReader(InputStreamReader(BufferedInputStream(process.errorStream))).use {
            val errors = it.readLines().joinToString("\n")
            if (errors.isNotEmpty()) {
                throw GradleException(errors)
            }
        }

        process.destroy()
    }
}
