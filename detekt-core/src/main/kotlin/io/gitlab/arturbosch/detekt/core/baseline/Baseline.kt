package io.gitlab.arturbosch.detekt.core.baseline

import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.core.exists
import io.gitlab.arturbosch.detekt.core.isFile
import java.nio.file.Path

internal typealias FindingsIdList = Set<String>
internal typealias FindingId = String

internal data class Baseline(val blacklist: FindingsIdList, val whitelist: FindingsIdList) {

    fun contains(id: FindingId): Boolean = whitelist.contains(id) || blacklist.contains(id)

    companion object {

        fun load(baselineFile: Path): Baseline {
            require(baselineFile.exists()) { "Baseline file does not exist." }
            require(baselineFile.isFile()) { "Baseline file is not a regular file." }
            return BaselineFormat().read(baselineFile)
        }
    }
}

internal const val SMELL_BASELINE = "SmellBaseline"
internal const val BLACKLIST = "Blacklist"
internal const val WHITELIST = "Whitelist"
internal const val ID = "ID"

internal val Finding.baselineId: String
    get() = this.id + ":" + this.signature
