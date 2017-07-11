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
abstract class Rule(override val config: Config = Config.empty,
					private val context: Context = DefaultContext()) :
		DetektVisitor(), Context by context, ConfigAware {

	abstract val issue: Issue
	final override val id: String by lazy(LazyThreadSafetyMode.NONE) { issue.id }

	/**
	 * Before starting visiting kotlin elements, a check is performed if this rule should be triggered.
	 * Pre- and post-visit-hooks are executed before/after the visiting process.
	 */
	open fun visit(root: KtFile) {
		ifRuleActive {
			if (!root.isSuppressedBy(id)) {
				preVisit(root)
				root.accept(this)
				postVisit(root)
			}
		}
	}

	/**
	 * Could be overridden by subclasses to specify a behaviour which should be done before
	 * visiting kotlin elements.
	 */
	protected open fun postVisit(root: KtFile) {
	}

	/**
	 * Could be overridden by subclasses to specify a behaviour which should be done after
	 * visiting kotlin elements.
	 */
	protected open fun preVisit(root: KtFile) {
	}

	internal fun ifRuleActive(block: () -> Unit) {
		if (active) {
			clearFindings()
			block()
		}
	}

}
