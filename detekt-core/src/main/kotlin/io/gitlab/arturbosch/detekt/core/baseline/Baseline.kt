package io.gitlab.arturbosch.detekt.core.baseline

import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.core.exists
import io.gitlab.arturbosch.detekt.core.isFile
import java.nio.file.Path

internal typealias FindingsIdList = Set<String>
internal typealias FindingId = String

internal data class Baseline(
    val suppressedFalsePositives: FindingsIdList,
    val temporarySuppressedIssues: FindingsIdList
) {

    fun contains(id: FindingId): Boolean =
        temporarySuppressedIssues.contains(id) || suppressedFalsePositives.contains(id)

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
internal const val SUPPRESSED_FALSE_POSITIVES = "SuppressedFalsePositives"
internal const val TEMPORARY_SUPPRESSED_ISSUES = "TemporarySuppressedIssues"
internal const val ID = "ID"

internal val Finding.baselineId: String
    get() = this.id + ":" + this.signature
