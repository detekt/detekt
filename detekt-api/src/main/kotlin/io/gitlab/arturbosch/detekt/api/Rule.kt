package io.gitlab.arturbosch.detekt.api

import org.jetbrains.kotlin.psi.KtFile

/**
 * A rule defines how one specific code structure should look like. If code is found
 * which does not meet this structure, it is considered as harmful regarding maintainability
 * or readability.
 *
 * A rule is implemented using the visitor pattern and should be started using the visit(KtFile)
 * function. If calculations must be done before or after the visiting process, here are
 * two predefined (preVisit/postVisit) functions which can be overriden to setup/teardown additional data.
 *
 * @author Artur Bosch
 */
@Suppress("EmptyFunctionBlock")
abstract class Rule(val id: String,
					private val config: Config = Config.empty) : DetektVisitor() {

	protected val autoCorrect: Boolean = withConfig {
		valueOrDefault("autoCorrect", true)
	}
	private val active = withConfig {
		valueOrDefault("active", true)
	}

	/**
	 * Before starting visiting kotlin elements, a check is performed if this rule should be triggered.
	 * Pre- and post-visit-hooks are executed before/after the visiting process.
	 */
	open fun visit(context: Context, root: KtFile) {
		ifRuleActive {
			if (!root.isSuppressedBy(id)) {
				preVisit(context, root)
				root.accept(this, context)
				postVisit(context, root)
			}
		}
	}

	/**
	 * If custom configurable attributes are provided, use this method to retrieve
	 * properties from the sub configuration specified by the rule id.
	 */
	protected fun <T> withConfig(block: Config.() -> T): T {
		return config.subConfig(id).block()
	}

	/**
	 * If your rule supports to automatically correct the misbehaviour of underlying smell,
	 * specify your code inside this method call, to allow the user of your rule to trigger auto correction
	 * only when needed.
	 */
	protected fun withAutoCorrect(block: () -> Unit) {
		if (autoCorrect) {
			block()
		}
	}

	protected fun ifRuleActive(block: () -> Unit) {
		if (active) {
			block()
		}
	}

	/**
	 * Could be overriden by subclasses to specify a behaviour which should be done before
	 * visiting kotlin elements.
	 */
	protected open fun postVisit(context: Context, root: KtFile) {
	}

	/**
	 * Could be overriden by subclasses to specify a behaviour which should be done after
	 * visiting kotlin elements.
	 */
	protected open fun preVisit(context: Context, root: KtFile) {
	}
}

/**
 * Class describing the metadata of a {@link #Rule}.
 */
data class Issue(val id: String,
				 val severity: Severity = Severity.Minor,
				 val description: String = "") {
	init {
	    validateIdentifier(id)
	}

	/**
	 * Issues can be classified into different severity grades. Maintainer can choose
	 * a grade which is most harmful to their projects.
	 */
	enum class Severity {
		CodeSmell, Style, Warning, Defect, Minor, Maintainability, Security
	}
}