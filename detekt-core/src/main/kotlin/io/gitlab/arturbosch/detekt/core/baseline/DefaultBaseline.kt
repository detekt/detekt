package io.gitlab.arturbosch.detekt.core.baseline

import io.github.detekt.tooling.api.Baseline
import io.github.detekt.tooling.api.FindingId
import io.github.detekt.tooling.api.FindingsIdList
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.core.exists
import io.gitlab.arturbosch.detekt.core.isFile
import java.nio.file.Path

internal data class DefaultBaseline(
    override val manuallySuppressedIssues: FindingsIdList,
    override val currentIssues: FindingsIdList
) : Baseline {

    override fun contains(id: FindingId): Boolean =
        currentIssues.contains(id) || manuallySuppressedIssues.contains(id)

    companion object {

        fun load(baselineFile: Path): Baseline {
            require(baselineFile.exists()) { "Baseline file does not exist." }
            require(baselineFile.isFile()) { "Baseline file is not a regular file." }
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

internal val Finding.baselineId: String
    get() = this.id + ":" + this.signature
