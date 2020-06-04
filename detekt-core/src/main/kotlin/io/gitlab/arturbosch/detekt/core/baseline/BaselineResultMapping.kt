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

    override fun init(context: SetupContext) {
        baselineFile = context.getOrNull(DETEKT_BASELINE_PATH_KEY)
        createBaseline = context.getOrNull(DETEKT_BASELINE_CREATION_KEY) ?: false
    }

    override fun transformFindings(findings: Map<RuleSetId, List<Finding>>): Map<RuleSetId, List<Finding>> {
        require(
            !createBaseline ||
                (createBaseline && baselineFile != null)
        ) { "Invalid baseline options invariant." }

        if (baselineFile != null) {
            val facade = BaselineFacade()
            val baselinePath = checkNotNull(baselineFile)

            if (createBaseline) {
                val flatten = findings.flatMap { it.value }
                facade.createOrUpdate(baselinePath, flatten)
            }

            return facade.transformResult(baselinePath, DetektResult(findings)).findings
        }

        return findings
    }
}
