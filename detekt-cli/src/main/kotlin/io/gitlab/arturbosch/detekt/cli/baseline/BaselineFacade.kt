package io.gitlab.arturbosch.detekt.cli.baseline

import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.core.exists
import io.gitlab.arturbosch.detekt.core.isFile
import java.nio.file.Files
import java.nio.file.Path

class BaselineFacade {

    fun createOrUpdate(baselineFile: Path, findings: List<Finding>) {
        val ids = findings.map { it.baselineId }.toSortedSet()
        val baseline = if (baselineExists(baselineFile)) {
            Baseline.load(baselineFile).copy(whitelist = ids)
        } else {
            Baseline(emptySet(), ids)
        }
        baselineFile.parent?.let { Files.createDirectories(it) }
        BaselineFormat().write(baseline, baselineFile)
    }

    private fun baselineExists(baseline: Path) = baseline.exists() && baseline.isFile()
}
