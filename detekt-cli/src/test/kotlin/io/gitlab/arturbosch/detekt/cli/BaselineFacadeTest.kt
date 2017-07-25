package io.gitlab.arturbosch.detekt.cli

import io.gitlab.arturbosch.detekt.cli.baseline.BaselineFormat
import io.gitlab.arturbosch.detekt.cli.baseline.BaselineFacade
import org.junit.jupiter.api.Test
import java.nio.file.Files

/**
 * @author Artur Bosch
 */
internal class BaselineFacadeTest {

	private val path = Files.createTempDirectory("baseline_format")
	private val fullPath = path.resolve("baseline.xml")

	@Test
	fun create() {
		val format = BaselineFacade(fullPath)
		format.create(emptyList())
		BaselineFormat.read(fullPath)
	}

}
