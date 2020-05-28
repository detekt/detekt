package io.gitlab.arturbosch.detekt.cli.baseline

import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.core.exists
import io.gitlab.arturbosch.detekt.core.isFile
import java.nio.file.Path

typealias FindingsIdList = Set<String>
typealias FindingId = String

data class Baseline(val blacklist: FindingsIdList, val whitelist: FindingsIdList) {

    fun contains(id: FindingId): Boolean = whitelist.contains(id) || blacklist.contains(id)

    companion object {

        fun load(baselineFile: Path): Baseline {
            require(baselineFile.exists()) { "Baseline file does not exist." }
            require(baselineFile.isFile()) { "Baseline file is not a regular file." }
            return BaselineFormat().read(baselineFile)
        }
    }
}

const val SMELL_BASELINE = "SmellBaseline"
const val BLACKLIST = "Blacklist"
const val WHITELIST = "Whitelist"
const val ID = "ID"

val Finding.baselineId: String
    get() = this.id + ":" + this.signature
