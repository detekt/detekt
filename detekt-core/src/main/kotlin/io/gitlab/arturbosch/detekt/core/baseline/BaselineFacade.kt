package io.gitlab.arturbosch.detekt.core.baseline

import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.core.exists
import io.gitlab.arturbosch.detekt.core.isFile
import java.nio.file.Files
import java.nio.file.Path

class BaselineFacade {

    fun transformResult(baselineFile: Path, result: Detektion): Detektion {
        return if (baselineExists(baselineFile)) {
            BaselineFilteredResult(result, Baseline.load(baselineFile))
        } else {
            result
        }
    }

    fun createOrUpdate(baselineFile: Path, findings: List<Finding>) {
        val ids = findings.map { it.baselineId }.toSortedSet()
        val oldBaseline = if (baselineExists(baselineFile)) {
            Baseline.load(baselineFile)
        } else {
            Baseline(emptySet(), emptySet())
        }
        val baseline = oldBaseline.copy(currentIssues = ids)
        if (oldBaseline != baseline) {
            baselineFile.parent?.let { Files.createDirectories(it) }
            BaselineFormat().write(baseline, baselineFile)
        }
    }

    private fun baselineExists(baseline: Path) = baseline.exists() && baseline.isFile()
}
