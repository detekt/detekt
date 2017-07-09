package io.gitlab.arturbosch.detekt.api

/**
 * @author Artur Bosch
 */
interface Context {
	val findings: List<Finding>
	fun report(finding: Finding)
	fun clearFindings()
}

open class DefaultContext : Context {

	/**
	 * Returns a copy of violations for this rule.
	 * Resets the context.
	 *
	 */
	override val findings
		get() = _findings.toList()

	private var _findings: MutableList<Finding> = mutableListOf()

	/**
	 * The only way to add code smell findings.
	 *
	 * Before adding a finding, it is checked if it is not suppressed
	 * by @Suppress or @SuppressWarnings annotations.
	 */
	override fun report(finding: Finding) {
		val ktElement = finding.entity.ktElement
		if (ktElement == null || !ktElement.isSuppressedBy(finding.id)) {
			_findings.add(finding)
		}
	}

	override final fun clearFindings() {
		_findings = mutableListOf()
	}
}
