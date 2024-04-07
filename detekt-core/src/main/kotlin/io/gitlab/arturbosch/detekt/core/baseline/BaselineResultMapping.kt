package io.gitlab.arturbosch.detekt.core.baseline

import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.ReportingExtension
import io.gitlab.arturbosch.detekt.api.SetupContext
import io.gitlab.arturbosch.detekt.api.getOrNull
import io.gitlab.arturbosch.detekt.core.DetektResult
import java.nio.file.Path

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
        val facade = BaselineFacade()

        if (createBaseline) {
            facade.createOrUpdate(baselinePath, this)
        }

        return facade.transformResult(baselinePath, DetektResult(this)).issues
    }
}
