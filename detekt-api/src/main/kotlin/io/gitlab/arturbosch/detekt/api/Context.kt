package io.gitlab.arturbosch.detekt.api

/**
 * @author Artur Bosch
 * @author Marvin Ramin
 */
interface Context {
	val findings: List<Finding>
	fun report(finding: Finding)
	fun report(findings: List<Finding>)
	fun clearFindings()
}

open class DefaultContext : Context {

	/**
	 * Returns a copy of violations for this rule.
	 */
	override val findings
		get() = _findings.toList()

	private var _findings: MutableList<Finding> = mutableListOf()

	/**
	 * Reports a single code smell finding.
	 *
	 * Before adding a finding, it is checked if it is not suppressed
	 * by @Suppress or @SuppressWarnings annotations.
	 */
	override fun report(finding: Finding) {
		val ktElement = finding.entity.ktElement
		if (ktElement == null || !ktElement.isSuppressedBy(finding.id, finding.issue.aliases)) {
			_findings.add(finding)
		}
	}

	/**
	 * Reports a list of code smell findings.
	 *
	 * Before adding a finding, it is checked if it is not suppressed
	 * by @Suppress or @SuppressWarnings annotations.
	 */
	override fun report(findings: List<Finding>) {
		findings.forEach { report(it) }
	}

	final override fun clearFindings() {
		_findings = mutableListOf()
	}
}
