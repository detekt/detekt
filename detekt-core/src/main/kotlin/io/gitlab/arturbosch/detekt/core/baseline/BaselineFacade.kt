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
            BaselineFilteredResult(result, DefaultBaseline.load(baselineFile))
        } else {
            result
        }
    }

    fun createOrUpdate(baselineFile: Path, findings: List<Finding>) {
        val ids = findings.map { it.baselineId }.toSortedSet()
        val oldBaseline = if (baselineExists(baselineFile)) {
            DefaultBaseline.load(baselineFile)
        } else {
            DefaultBaseline(emptySet(), emptySet())
        }
        val baselineFormat = BaselineFormat()
        val baseline = baselineFormat.of(oldBaseline.manuallySuppressedIssues, ids)
        if (oldBaseline != baseline) {
            baselineFile.parent?.let { Files.createDirectories(it) }
            baselineFormat.write(baselineFile, baseline)
        }
    }

    private fun baselineExists(baseline: Path) = baseline.exists() && baseline.isFile()
}
