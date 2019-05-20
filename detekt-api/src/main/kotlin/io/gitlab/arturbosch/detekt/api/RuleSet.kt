package io.gitlab.arturbosch.detekt.api

import io.gitlab.arturbosch.detekt.api.internal.PathFilters
import io.gitlab.arturbosch.detekt.api.internal.absolutePath
import io.gitlab.arturbosch.detekt.api.internal.validateIdentifier
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingContext
import java.nio.file.Paths

typealias RuleSetId = String

/**
 * A rule set is a collection of rules and must be defined within a rule set provider implementation.
 *
 * @author Artur Bosch
 */
class RuleSet(val id: RuleSetId, val rules: List<BaseRule>) {

    init {
        validateIdentifier(id)
    }

    /**
     * Is used to determine if a given [KtFile] should be analyzed at all.
     */
    var pathFilters: PathFilters? = null

    /**
     * Visits given file with all rules of this rule set, returning a list
     * of all code smell findings.
     */
    fun accept(file: KtFile, bindingContext: BindingContext = BindingContext.EMPTY): List<Finding> =
        if (isFileIgnored(file)) {
            emptyList()
        } else {
            rules.flatMap {
                it.visitFile(file, bindingContext)
                it.findings
            }
        }

    private fun isFileIgnored(file: KtFile) =
        pathFilters?.isIgnored(Paths.get(file.absolutePath())) == true
}
