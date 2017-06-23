package io.gitlab.arturbosch.detekt.core

import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.it
import java.nio.file.Paths

/**
 * @author Artur Bosch
 */
class PathFilterTest : Spek({

	it("should convert unix style filters to windows filters") {
		val currentOS = System.getProperty("os.name")
		System.getProperties().put("os.name", "Windows 10")

		val unixFilter = ".*/test/.*"
		val pathFilter = PathFilter(unixFilter)
		System.getProperties().put("os.name", currentOS)
		val windowsPath = Paths.get("C:\\test\\file.kt")

		assertThat(pathFilter.matches(windowsPath)).isTrue()
	}
})
