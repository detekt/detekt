package io.gitlab.arturbosch.detekt.cli.baseline

import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.cli.createFinding
import io.gitlab.arturbosch.detekt.test.resource
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.ListAssert
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

/**
 * @author Artur Bosch
 * @author schalkms
 */
class BaselineFacadeTest : Spek({
	describe("a baseline facade") {

        val dir = Files.createTempDirectory("baseline_format")

        it("create") {
            val fullPath = dir.resolve("baseline.xml")
            val baselineFacade = BaselineFacade(fullPath)
            baselineFacade.create(emptyList())
            Files.newInputStream(fullPath).use<InputStream?, ListAssert<String>?> {
                val reader = BufferedReader(InputStreamReader(it))
                assertThat(reader.lines()).isNotEmpty
            }
        }

        it("filterWithExistingBaseline") {
            assertFilter(dir)
        }

        it("filterWithoutExistingBaseline") {
            val path = Paths.get(resource("/smell-baseline.xml"))
            assertFilter(path)
        }
    }
})

private fun assertFilter(path: Path) {
    val findings = listOf<Finding>(createFinding())
    val result = BaselineFacade(path).filter(findings)
    assertThat(result).isEqualTo(findings)
}
