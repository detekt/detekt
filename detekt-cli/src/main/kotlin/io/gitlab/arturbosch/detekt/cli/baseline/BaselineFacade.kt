package io.gitlab.arturbosch.detekt.cli.baseline

import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.core.exists
import io.gitlab.arturbosch.detekt.core.isFile
import java.nio.file.Files
import java.nio.file.Path

class BaselineFacade(private val baselineFile: Path) {

    private val baseline: Baseline by lazy(LazyThreadSafetyMode.NONE) {
        if (baselineExists()) {
            BaselineFormat().read(baselineFile)
        } else {
            Baseline(emptySet(), emptySet())
        }
    }

    fun filter(findings: List<Finding>): List<Finding> =
        findings.filterNot { baseline.contains(it.baselineId) }

    fun create(smells: List<Finding>) {
        val ids = smells.map { it.baselineId }.toSortedSet()
        val newBaseline = Baseline(baseline.blacklist, ids)
        baselineFile.parent?.let { Files.createDirectories(it) }
        BaselineFormat().write(newBaseline, baselineFile)
    }

    private fun baselineExists() = baselineFile.exists() && baselineFile.isFile()
}
