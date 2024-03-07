package io.gitlab.arturbosch.detekt.core.baseline

import io.github.detekt.tooling.api.Baseline
import io.github.detekt.tooling.api.FindingId
import io.github.detekt.tooling.api.FindingsIdList
import io.gitlab.arturbosch.detekt.api.Finding2
import java.nio.file.Path
import kotlin.io.path.exists
import kotlin.io.path.isRegularFile

internal data class DefaultBaseline(
    override val manuallySuppressedIssues: FindingsIdList,
    override val currentIssues: FindingsIdList,
) : Baseline {

    override fun contains(id: FindingId): Boolean =
        currentIssues.contains(id) || manuallySuppressedIssues.contains(id)

    companion object {

        fun load(baselineFile: Path): Baseline {
            require(baselineFile.exists()) { "Baseline file does not exist." }
            require(baselineFile.isRegularFile()) { "Baseline file is not a regular file." }
            return BaselineFormat().read(baselineFile)
        }
    }
}

const val DETEKT_BASELINE_PATH_KEY = "detekt.baseline.path.key"
const val DETEKT_BASELINE_CREATION_KEY = "detekt.baseline.creation.key"

internal const val SMELL_BASELINE = "SmellBaseline"
internal const val MANUALLY_SUPPRESSED_ISSUES = "ManuallySuppressedIssues"
internal const val CURRENT_ISSUES = "CurrentIssues"
internal const val ID = "ID"

internal val Finding2.baselineId: String
    get() = "${rule.id}:${this.signature}"
