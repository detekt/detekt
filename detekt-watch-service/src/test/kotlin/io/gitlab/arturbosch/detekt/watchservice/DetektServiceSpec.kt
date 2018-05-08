package io.gitlab.arturbosch.detekt.watchservice

import io.gitlab.arturbosch.detekt.test.resource
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import java.lang.UnsupportedOperationException
import java.nio.file.Paths
import java.nio.file.WatchEvent.Kind

class DetektServiceSpec : Spek({

	val content = ByteArrayOutputStream()

	beforeEachTest {
		System.setOut(PrintStream(content))
	}

	afterEachTest {
		System.setOut(System.out)
	}

	describe("tests the change detector for the watch service") {

		it("detects a change in a file") {
			val path = Paths.get(resource("Default.kt"))
			val service = DetektService(Parameters())
			val mock = object : Kind<String> {
				override fun type(): Class<String> = throw UnsupportedOperationException()
				override fun name(): String = ""
			}
			val dir = WatchedDir(true, path, listOf(PathEvent(path, mock)))
			service.check(dir)
			assertThat(content.toString()).startsWith("Change detected for")
		}
	}
})
