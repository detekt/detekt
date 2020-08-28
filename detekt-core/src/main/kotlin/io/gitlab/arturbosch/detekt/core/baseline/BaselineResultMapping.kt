package io.gitlab.arturbosch.detekt.core.baseline

import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.ReportingExtension
import io.gitlab.arturbosch.detekt.api.RuleSetId
import io.gitlab.arturbosch.detekt.api.SetupContext
import io.gitlab.arturbosch.detekt.api.UnstableApi
import io.gitlab.arturbosch.detekt.api.getOrNull
import io.gitlab.arturbosch.detekt.core.DetektResult
import java.nio.file.Path

@OptIn(UnstableApi::class)
class BaselineResultMapping : ReportingExtension {

    private var baselineFile: Path? = null
    private var createBaseline: Boolean = false
    private var output: Appendable? = null

    override fun init(context: SetupContext) {
        baselineFile = context.getOrNull(DETEKT_BASELINE_PATH_KEY)
        createBaseline = context.getOrNull(DETEKT_BASELINE_CREATION_KEY) ?: false
        output = context.outputChannel
    }

    override fun transformFindings(findings: Map<RuleSetId, List<Finding>>): Map<RuleSetId, List<Finding>> {
        require(
            !createBaseline ||
                    (createBaseline && baselineFile != null)
        ) { "Invalid baseline options invariant." }

        return baselineFile?.let { findings.transformWithBaseline(it) } ?: findings
    }

    private fun Map<RuleSetId, List<Finding>>.transformWithBaseline(baselinePath: Path): Map<RuleSetId, List<Finding>> {
        val facade = BaselineFacade()
        val flatten = this.flatMap { it.value }

        if (flatten.isEmpty()) {
            val action = if (facade.baselineExists(baselinePath)) "updated" else "created"
            output?.appendLine("No issues found, baseline file will not be $action.")
            return this
        }

        if (createBaseline) {
            facade.createOrUpdate(baselinePath, flatten)
        }

        return facade.transformResult(baselinePath, DetektResult(this)).findings
    }
}
