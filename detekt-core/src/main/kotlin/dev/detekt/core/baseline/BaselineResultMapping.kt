package dev.detekt.core.baseline

import dev.detekt.api.Issue
import dev.detekt.api.ReportingExtension
import dev.detekt.api.SetupContext
import dev.detekt.api.getOrNull
import java.nio.file.Path
import kotlin.io.path.createParentDirectories
import kotlin.io.path.exists
import kotlin.io.path.isRegularFile

class BaselineResultMapping : ReportingExtension {

    private var baselineFile: Path? = null
    private var fragmentDirectory: Path? = null
    private var createBaseline: Boolean = false

    override val id: String = "BaselineResultMapping"

    override fun init(context: SetupContext) {
        baselineFile = context.getOrNull(DETEKT_BASELINE_PATH_KEY)
        fragmentDirectory = context.getOrNull(DETEKT_BASELINE_FRAGMENTS_PATH_KEY)
        createBaseline = context.getOrNull(DETEKT_BASELINE_CREATION_KEY) ?: false
    }

    override fun transformIssues(issues: List<Issue>): List<Issue> {
        val baselineFile = baselineFile
        val fragmentDirectory = fragmentDirectory
        require(baselineFile == null || fragmentDirectory == null) { "Invalid baseline options invariant." }
        require(!createBaseline || baselineFile != null || fragmentDirectory != null) {
            "Invalid baseline options invariant."
        }

        return when {
            baselineFile != null -> issues.transformWithBaseline(baselineFile)
            fragmentDirectory != null -> issues.transformWithFragments(fragmentDirectory)
            else -> issues
        }
    }

    private fun List<Issue>.transformWithBaseline(baselinePath: Path): List<Issue> {
        if (createBaseline) {
            createOrUpdate(baselinePath, this)
        }

        return filterByBaseline(baselinePath, this)
    }

    private fun List<Issue>.transformWithFragments(directory: Path): List<Issue> {
        val format = BaselineFragmentFormat()
        if (createBaseline) {
            val ids = map { it.baselineId }.toSortedSet()
            format.write(directory, DefaultBaseline(emptySet(), ids))
        }
        val baseline = format.read(directory)
        return filterNot { baseline.contains(it.baselineId) }
    }

    fun filterByBaseline(baselineFile: Path, issues: List<Issue>): List<Issue> =
        if (baselineExists(baselineFile)) {
            val baseline = DefaultBaseline.load(baselineFile)
            issues.filterNot { baseline.contains(it.baselineId) }
        } else {
            issues
        }

    fun createOrUpdate(baselineFile: Path, issues: List<Issue>) {
        val ids = issues.map { it.baselineId }.toSortedSet()
        val oldBaseline = if (baselineExists(baselineFile)) {
            DefaultBaseline.load(baselineFile)
        } else {
            DefaultBaseline(emptySet(), emptySet())
        }
        val baselineFormat = BaselineFormat()
        val baseline = baselineFormat.of(oldBaseline.manuallySuppressedIssues, ids)
        if (oldBaseline != baseline) {
            baselineFile.createParentDirectories()
            baselineFormat.write(baselineFile, baseline)
        }
    }

    private fun baselineExists(baseline: Path) = baseline.exists() && baseline.isRegularFile()
}
