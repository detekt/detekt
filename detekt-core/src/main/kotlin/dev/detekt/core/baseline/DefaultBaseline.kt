package dev.detekt.core.baseline

import dev.detekt.api.Issue
import dev.detekt.tooling.api.Baseline
import dev.detekt.tooling.api.FindingId
import dev.detekt.tooling.api.FindingsIdList
import java.nio.file.Path
import kotlin.io.path.exists
import kotlin.io.path.isRegularFile
import kotlin.io.path.name

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

internal val Issue.baselineId: String
    get() = "${ruleInstance.id}:${this.location.path.name}:${this.entity.signature}"
