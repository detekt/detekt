package io.gitlab.arturbosch.detekt.generator

import com.beust.jcommander.Parameter

/**
 * @author Marvin Ramin
 */
class Args {
	@Parameter(names = arrayOf("--help", "-h"),
			help = true, description = "Shows the usage.")
	var help: Boolean = false
}
