package io.gitlab.arturbosch.detekt.watchservice

import com.beust.jcommander.JCommander
import com.beust.jcommander.ParameterException
import java.nio.file.FileSystems
import java.nio.file.Files

/**
 * @author Artur Bosch
 */
fun main(args: Array<String>) {
	with(parseArgs(args)) {
		val root = extractWatchDirectory()
		val watchService = FileSystems.getDefault().newWatchService()
		Files.walkFileTree(root, WatchServiceDirectoryRegisteringVisitor(watchService))
		println("Starting detekt watch service for $root")
		startWatching(watchService, DetektService(this))
	}
}

private fun parseArgs(args: Array<String>): Parameters = with(Parameters()) {
	val jco = JCommander(this)
	try {
		jco.parse(*args)
	} catch (error: ParameterException) {
		jco.usage()
		System.exit(0)
	}
	this
}
