package io.gitlab.arturbosch.detekt.core.baseline

import io.gitlab.arturbosch.detekt.api.Finding2
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

    override fun transformFindings(findings: List<Finding2>): List<Finding2> {
        val baselineFile = baselineFile
        require(!createBaseline || (createBaseline && baselineFile != null)) {
            "Invalid baseline options invariant."
        }

        return baselineFile?.let { findings.transformWithBaseline(it) } ?: findings
    }

    private fun List<Finding2>.transformWithBaseline(
        baselinePath: Path,
    ): List<Finding2> {
        val facade = BaselineFacade()

        if (createBaseline) {
            facade.createOrUpdate(baselinePath, this)
        }

        return facade.transformResult(baselinePath, DetektResult(this)).findings
    }
}
