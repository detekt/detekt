package io.gitlab.arturbosch.detekt.api

import com.intellij.lang.ASTNode

/**
 * @author Artur Bosch
 */
abstract class Rule(val id: String,
					val severity: Severity = Rule.Severity.Minor,
					val config: Config = Config.EMPTY) : KastVisitor() {

	enum class Severity {
		CodeSmell, Style, Warning, Defect, Minor, Maintainability, Security
	}

	private val active = config.valueOrDefault("active") { true }

	private var _findings: MutableList<Finding> = mutableListOf()
	val findings: List<Finding>
		get() = _findings.toList()

	init {
		validateIdentifier(id)
	}

	open fun visit(root: ASTNode) {
		ifRuleActive {
			preVisit(root)
			root.visit(this)
			postVisit(root)
		}
	}

	internal fun ifRuleActive(block: () -> Unit) {
		if (active) {
			clearFindings()
			block()
		}
	}

	internal fun clearFindings() {
		_findings = mutableListOf()
	}

	protected open fun postVisit(root: ASTNode) {
	}

	protected open fun preVisit(root: ASTNode) {
	}

	protected fun addFindings(vararg finding: Finding) {
		_findings.addAll(finding)
	}
}