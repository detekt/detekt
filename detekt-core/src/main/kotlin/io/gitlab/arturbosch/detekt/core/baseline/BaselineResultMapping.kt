package io.gitlab.arturbosch.detekt.core.baseline

import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.ReportingExtension
import io.gitlab.arturbosch.detekt.api.SetupContext
import io.gitlab.arturbosch.detekt.api.getOrNull
import java.nio.file.Path
import kotlin.io.path.createParentDirectories
import kotlin.io.path.exists
import kotlin.io.path.isRegularFile

class BaselineResultMapping : ReportingExtension {

    private var baselineFile: Path? = null
    private var createBaseline: Boolean = false

    override val id: String = "BaselineResultMapping"

    override fun init(context: SetupContext) {
        baselineFile = context.getOrNull(DETEKT_BASELINE_PATH_KEY)
        createBaseline = context.getOrNull(DETEKT_BASELINE_CREATION_KEY) ?: false
    }

    override fun transformIssues(issues: List<Issue>): List<Issue> {
        val baselineFile = baselineFile
        require(!createBaseline || (createBaseline && baselineFile != null)) {
            "Invalid baseline options invariant."
        }

        return baselineFile?.let { issues.transformWithBaseline(it) } ?: issues
    }

    private fun List<Issue>.transformWithBaseline(
        baselinePath: Path,
    ): List<Issue> {
        if (createBaseline) {
            createOrUpdate(baselinePath, this)
        }

        return filterByBaseline(baselinePath, this)
    }

    fun filterByBaseline(baselineFile: Path, issues: List<Issue>): List<Issue> {
        return if (baselineExists(baselineFile)) {
            val baseline = DefaultBaseline.load(baselineFile)
            issues.filterNot { baseline.contains(it.baselineId) }
        } else {
            issues
        }
    }

    fun createOrUpdate(baselineFile: Path, issues: List<Issue>) {
        val ids = issues.map { it.baselineId }.toSortedSet()
        val oldBaseline = if (baselineExists(baselineFile)) {
            DefaultBaseline.load(baselineFile)
        } else {
            DefaultBaseline(emptySet(), emptySet())
        }
        val baselineFormat = BaselineFormat()
        val baseline = baselineFormat.of(oldBaseline.manuallySuppressedIssues, ids)
        if (oldBaseline != baseline) {
            baselineFile.createParentDirectories()
            baselineFormat.write(baselineFile, baseline)
        }
    }

    private fun baselineExists(baseline: Path) = baseline.exists() && baseline.isRegularFile()
}
