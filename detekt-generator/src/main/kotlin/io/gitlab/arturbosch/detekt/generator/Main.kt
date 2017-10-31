@file:JvmName("Main")

package io.gitlab.arturbosch.detekt.generator

/**
 * @author Marvin Ramin
 */
fun main(args: Array<String>) {
	val arguments = parseArguments(args)
	val executable = Runner(arguments)
	executable.execute()
}
