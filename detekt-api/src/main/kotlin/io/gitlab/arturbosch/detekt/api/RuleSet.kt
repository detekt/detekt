package io.gitlab.arturbosch.detekt.api

import io.gitlab.arturbosch.detekt.api.internal.PathFilters
import io.gitlab.arturbosch.detekt.api.internal.relativePath
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

    var pathFilters: PathFilters? = null

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
        pathFilters?.isIgnored(Paths.get(file.relativePath())) == true

    /**
     * Visits given file with all non-filtered rules of this rule set.
     * If a rule is a [MultiRule] the filters are passed to it via a setter
     * and later used to filter sub rules of the [MultiRule].
     *
     * A list of findings is returned for given KtFile
     */
    fun accept(
        file: KtFile,
        ruleFilters: Set<RuleId>,
        bindingContext: BindingContext = BindingContext.EMPTY
    ): List<Finding> =
        if (isFileIgnored(file)) {
            emptyList()
        } else {
            rules.asSequence()
                .filterNot { it.ruleId in ruleFilters }
                .onEach { if (it is MultiRule) it.ruleFilters = ruleFilters }
                .flatMap {
                    it.visitFile(file, bindingContext)
                    it.findings.asSequence()
                }.toList()
        }
}
