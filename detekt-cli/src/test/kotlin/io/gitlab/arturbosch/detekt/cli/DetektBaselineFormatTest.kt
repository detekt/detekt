package io.gitlab.arturbosch.detekt.cli

import io.gitlab.arturbosch.detekt.cli.baseline.BaselineFormat
import org.junit.jupiter.api.Test
import java.nio.file.Files

/**
 * @author Artur Bosch
 */
internal class DetektBaselineFormatTest {

	private val path = Files.createTempDirectory("baseline_format")
	private val fullPath = path.resolve(DetektBaselineFormat.BASELINE_FILE)

	@Test
	fun create() {
		DetektBaselineFormat.create(emptyList(), path)
		BaselineFormat.read(fullPath)
	}

}