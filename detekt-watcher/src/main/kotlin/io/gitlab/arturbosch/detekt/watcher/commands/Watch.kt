package io.gitlab.arturbosch.detekt.watcher.commands

import io.gitlab.arturbosch.detekt.watcher.config.DetektHome
import io.gitlab.arturbosch.detekt.watcher.config.Injekt
import io.gitlab.arturbosch.detekt.watcher.config.WATCHER_CHANGE_TIMEOUT
import io.gitlab.arturbosch.detekt.watcher.service.DetektService
import io.gitlab.arturbosch.detekt.watcher.service.PathEvent
import io.gitlab.arturbosch.detekt.watcher.service.WatchedDir
import io.gitlab.arturbosch.detekt.watcher.state.State
import io.gitlab.arturbosch.ksh.api.ShellClass
import io.gitlab.arturbosch.ksh.api.ShellMethod
import io.gitlab.arturbosch.kutils.get
import java.nio.file.Path
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import kotlin.concurrent.thread

/**
 * @author Artur Bosch
 */
class Watch(
    private val state: State = Injekt.get(),
    private val detekt: DetektService = Injekt.get(),
    home: DetektHome = Injekt.get()
) : ShellClass {

    private val id = AtomicInteger(0)
    private var watcher: Thread? = null
    private val timeout = home.property(WATCHER_CHANGE_TIMEOUT)?.toLongOrNull() ?: 5L

    @ShellMethod(help = "Starts watching project, specified by 'project' command.")
    fun main() {
        start()
    }

    @ShellMethod(help = "Starts watching project, specified by 'project' command.")
    fun start() {
        check(watcher == null) { "Already a watcher in progress." }
        state.shouldWatch = true
        watcher = thread(
                start = true,
                isDaemon = true,
                name = "detekt-watcher#${id.getAndIncrement()}",
                block = startWatching())
    }

    @ShellMethod(help = "Stops watching project.")
    fun stop() {
        check(watcher != null) { "No watcher is currently running." }
        state.shouldWatch = false
        watcher = null
    }

    private fun startWatching() = {
        val watchService = state.newWatcher()
        while (state.shouldWatch) {
            val watchKey = watchService.poll(timeout, TimeUnit.SECONDS)
            if (watchKey != null) {

                val events = watchKey.pollEvents()
                        .mapNotNull { event ->
                            (event.context() as? Path)?.let { PathEvent(it, event.kind()) }
                        }
                val watchedPath = watchKey.watchable() as? Path
                    ?: throw IllegalStateException("Project path expected.")
                val watchedDir = WatchedDir(watchKey.isValid, watchedPath, events)

                detekt.check(watchedDir)
                watchKey.reset()
            }
        }
    }
}
