package io.gitlab.arturbosch.detekt.api

import org.jetbrains.kotlin.psi.KtFile

/**
 * @author Artur Bosch
 */
class RuleSet(val id: String, val rules: List<Rule>) {

	init {
		validateIdentifier(id)
	}

	fun acceptAll(files: List<KtFile>): Pair<String, List<Finding>> {
		return id to files.flatMap { accept(it) }
	}

	private fun accept(file: KtFile): List<Finding> {
		val findings: MutableList<Finding> = mutableListOf()
		rules.forEach {
			it.visit(file)
			findings += it.findings
		}
		return findings
	}

}