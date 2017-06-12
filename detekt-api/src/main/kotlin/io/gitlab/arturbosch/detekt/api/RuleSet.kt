package io.gitlab.arturbosch.detekt.api

import org.jetbrains.kotlin.psi.KtFile

/**
 * A rule set is a collection of rules and must be defined within a rule set provider implementation.
 *
 * @author Artur Bosch
 */
class RuleSet(val id: String, val rules: List<Rule>) {

	init {
		validateIdentifier(id)
	}

	/**
	 * Iterates over the given kotlin file list and calling the accept method.
	 */
	fun acceptAll(context: Context, files: List<KtFile>) {
		return files.forEach { accept(context, it) }
	}

	/**
	 * Visits given file with all rules of this rule set, returning a list
	 * of all code smell findings.
	 */
	fun accept(context: Context, file: KtFile) {
		rules.forEach { it.visit(context, file) }
	}
}