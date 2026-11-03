package dev.detekt.core.baseline

import dev.detekt.api.Issue
import dev.detekt.tooling.api.Baseline
import dev.detekt.tooling.api.spec.BaselineSpec
import java.nio.file.Path
import kotlin.io.path.createParentDirectories
import kotlin.io.path.exists
import kotlin.io.path.isRegularFile

internal class BaselineResultMapping(baselineSpec: BaselineSpec) {

    private val baselineFile: Path? = baselineSpec.path
    private val createBaseline: Boolean = baselineSpec.shouldCreateDuringAnalysis

    fun transformIssues(issues: List<Issue>): List<Issue> {
        require(!createBaseline || baselineFile != null) {
            "Invalid baseline options invariant."
        }

        return baselineFile?.let { issues.transformWithBaseline(it) } ?: issues
    }

    private fun List<Issue>.transformWithBaseline(baselinePath: Path): List<Issue> {
        val baseline = if (createBaseline) {
            createOrUpdate(baselinePath, this)
        } else {
            loadBaseline(baselinePath)
        }
        return baseline?.let { currentBaseline ->
            filterNot { currentBaseline.contains(it.baselineId) }
        } ?: this
    }

    private fun createOrUpdate(baselineFile: Path, issues: List<Issue>): Baseline {
        val ids = issues.map { it.baselineId }.toSortedSet()
        val oldBaseline = loadBaseline(baselineFile) ?: DefaultBaseline(emptySet(), emptySet())
        val baselineFormat = BaselineFormat()
        val baseline = baselineFormat.of(oldBaseline.manuallySuppressedIssues, ids)
        if (oldBaseline != baseline) {
            baselineFile.createParentDirectories()
            baselineFormat.write(baselineFile, baseline)
        }
        return baseline
    }

    private fun loadBaseline(baselineFile: Path): Baseline? =
        baselineFile
            .takeIf { it.exists() && it.isRegularFile() }
            ?.let(DefaultBaseline::load)
}
