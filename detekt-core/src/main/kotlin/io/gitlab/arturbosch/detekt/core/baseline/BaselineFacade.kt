package io.gitlab.arturbosch.detekt.core.baseline

import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.Finding
import java.nio.file.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.exists
import kotlin.io.path.isRegularFile

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
            baselineFile.parent?.createDirectories()
            baselineFormat.write(baselineFile, baseline)
        }
    }

    private fun baselineExists(baseline: Path) = baseline.exists() && baseline.isRegularFile()
}
