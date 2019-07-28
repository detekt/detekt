package io.gitlab.arturbosch.detekt.cli.baseline

import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.cli.baselineId
import io.gitlab.arturbosch.detekt.core.exists
import io.gitlab.arturbosch.detekt.core.isFile
import java.nio.file.Files
import java.nio.file.Path

class BaselineFacade(val baselineFile: Path) {

    private val listings: Pair<Whitelist, Blacklist>? =
            if (baselineExists()) {
                val format = BaselineFormat().read(baselineFile)
                format.whitelist to format.blacklist
            } else null

    fun filter(smells: List<Finding>) =
            if (listings != null) {
                val whiteFiltered = smells.filterNot { finding -> listings.first.ids.contains(finding.baselineId) }
                val blackFiltered = whiteFiltered.filterNot { finding ->
                    listings.second.ids.contains(finding.baselineId)
                }
                blackFiltered
            } else smells

    fun create(smells: List<Finding>) {
        val blacklist = if (baselineExists()) {
            BaselineFormat().read(baselineFile).blacklist
        } else {
            Blacklist(emptySet())
        }
        val ids = smells.map { it.baselineId }.toSortedSet()
        val smellBaseline = Baseline(blacklist, Whitelist(ids))
        baselineFile.parent?.let { Files.createDirectories(it) }
        BaselineFormat().write(smellBaseline, baselineFile)
        println("Successfully wrote smell baseline to $baselineFile")
    }

    private fun baselineExists() = baselineFile.exists() && baselineFile.isFile()
}
