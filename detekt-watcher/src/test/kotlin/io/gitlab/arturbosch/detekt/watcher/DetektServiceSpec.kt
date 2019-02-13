package io.gitlab.arturbosch.detekt.watcher

import io.gitlab.arturbosch.detekt.test.resource
import io.gitlab.arturbosch.detekt.watcher.config.DetektHome
import io.gitlab.arturbosch.detekt.watcher.service.DetektService
import io.gitlab.arturbosch.detekt.watcher.service.PathEvent
import io.gitlab.arturbosch.detekt.watcher.service.WatchedDir
import io.gitlab.arturbosch.detekt.watcher.state.Parameters
import io.gitlab.arturbosch.detekt.watcher.state.State
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import java.lang.UnsupportedOperationException
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.WatchEvent.Kind
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class DetektServiceSpec : Spek({

    val tmpDir = Files.createTempDirectory("detekt-watcher")
    val content = ByteArrayOutputStream()

    beforeEachTest {
        content.reset()
        System.setOut(PrintStream(content))
    }

    afterEachTest {
        System.setOut(System.out)
    }

    describe("tests the watch service") {

        it("detects a change in a file") {
            val path = Paths.get(resource("Default.kt"))
            val home = DetektHome(tmpDir)
            val state = State(home)
            state.use(Parameters(tmpDir.toString()))
            val service = DetektService(state, home)
            val mock = object : Kind<String> {
                override fun type(): Class<String> = throw UnsupportedOperationException()
                override fun name(): String = ""
            }
            val dir = WatchedDir(true, path, listOf(PathEvent(path, mock)))
            service.check(dir)
            assertThat(content.toString()).contains("Change detected for")
        }
    }
})
