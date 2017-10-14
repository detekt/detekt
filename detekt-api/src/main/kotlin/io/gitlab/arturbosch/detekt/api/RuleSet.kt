package io.gitlab.arturbosch.detekt.api

import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingContext

/**
 * A rule set is a collection of rules and must be defined within a rule set provider implementation.
 *
 * @author Artur Bosch
 */
class RuleSet(val id: String, val rules: List<BaseRule>) {

	init {
		validateIdentifier(id)
	}

	/**
	 * Visits given file with all rules of this rule set, returning a list
	 * of all code smell findings.
	 */
	fun accept(file: KtFile, bindingContext: BindingContext = BindingContext.EMPTY): List<Finding> = rules.flatMap {
		it.visitFile(file, bindingContext); it.findings
	}

	/**
	 * Visits given file with all non-filtered rules of this rule set.
	 * If a rule is a [MultiRule] the filters are passed to it via a setter
	 * and later used to filter sub rules of the [MultiRule].
	 *
	 * A list of findings is returned for given [KtFile]
	 */
	fun accept(file: KtFile,
			   ruleFilters: Set<String>,
			   bindingContext: BindingContext = BindingContext.EMPTY): List<Finding> =
			rules.filterNot { it.id in ruleFilters }
					.onEach { if (it is MultiRule) it.ruleFilters = ruleFilters }
					.flatMap { it.visitFile(file, bindingContext); it.findings }
}
