package io.gitlab.arturbosch.detekt.cli.baseline

import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.cli.createFinding
import io.github.detekt.test.utils.createTempDirectoryForTest
import io.gitlab.arturbosch.detekt.test.resource
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class BaselineFacadeSpec : Spek({

    describe("a baseline facade") {

        val dir = createTempDirectoryForTest("baseline_format")

        it("creates a baseline file") {
            val fullPath = dir.resolve("baseline.xml")
            assertNonEmptyBaseline(fullPath)
        }

        it("creates on top of an existing a baseline file") {
            val fullPath = dir.resolve("baseline2.xml")
            val existingFile = Paths.get(resource("/smell-baseline.xml")).toFile()
            existingFile.copyTo(fullPath.toFile(), overwrite = true)

            assertNonEmptyBaseline(fullPath)
        }

        it("filters without an existing baseline file") {
            assertFilter(dir)
        }

        it("filters with an existing baseline file") {
            val path = Paths.get(resource("/smell-baseline.xml"))
            assertFilter(path)
        }
    }
})

private fun assertNonEmptyBaseline(fullPath: Path) {
    val baselineFacade = BaselineFacade(fullPath)
    baselineFacade.create(emptyList())
    val lines = Files.readAllLines(fullPath)
    assertThat(lines).isNotEmpty
}

private fun assertFilter(path: Path) {
    val findings = listOf<Finding>(createFinding())
    val result = BaselineFacade(path).filter(findings)
    assertThat(result).isEqualTo(findings)
}
