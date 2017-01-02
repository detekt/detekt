package io.gitlab.arturbosch.detekt.cli

import io.gitlab.arturbosch.quide.format.BaselineFormat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import java.io.File
import java.nio.file.Files
import kotlin.test.assertTrue

/**
 * @author Artur Bosch
 */
internal class DetektBaselineFormatTest {

	private val path = File("./src/test/resources").toPath()
	private val fullPath = path.resolve(DetektBaselineFormat.BASELINE_FILE)

	@AfterEach
	fun cleanup() {
		Files.deleteIfExists(fullPath)
	}

	@Test
	fun create() {
		DetektBaselineFormat.create(emptyList(), path)
		assertTrue { BaselineFormat().read(fullPath) != null }
	}

}