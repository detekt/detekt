package io.gitlab.arturbosch.detekt

import io.gitlab.arturbosch.detekt.cli.out.SmellThreshold
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * @author Artur Bosch
 */
fun startProcess(args: Array<String>) {
	val process = Runtime.getRuntime().exec(args)

	BufferedReader(InputStreamReader(BufferedInputStream(process.inputStream))).use {
		val inputs = it.readLines().joinToString("\n")
		println(inputs)
	}

	BufferedReader(InputStreamReader(BufferedInputStream(process.errorStream))).use {
		val errors = it.readLines().joinToString("\n")
		if (errors.isNotEmpty()) {
			throw SmellThreshold.BuildFailure(errors)
		}
	}

	process.destroy()
}
