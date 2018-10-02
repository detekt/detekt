package io.gitlab.arturbosch.detekt.watcher.service

import java.nio.file.FileVisitResult
import java.nio.file.Path
import java.nio.file.SimpleFileVisitor
import java.nio.file.StandardWatchEventKinds.ENTRY_CREATE
import java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY
import java.nio.file.WatchService
import java.nio.file.attribute.BasicFileAttributes

/**
 * @author Artur Bosch
 */
class DirectoryRegisteringVisitor(
		private val watchService: WatchService
) : SimpleFileVisitor<Path>() {

	override fun preVisitDirectory(dir: Path, attrs: BasicFileAttributes): FileVisitResult {
		dir.register(watchService, ENTRY_CREATE, ENTRY_MODIFY)
		return FileVisitResult.CONTINUE
	}
}
