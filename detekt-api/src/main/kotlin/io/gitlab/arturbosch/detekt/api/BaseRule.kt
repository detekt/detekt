package io.gitlab.arturbosch.detekt.api

import org.jetbrains.kotlin.psi.KtFile

/**
 * The type to use when referring to rule ids giving it more context then a String would.
 */
typealias RuleId = String

/**
 * Defines the visiting mechanism for [KtFile]'s.
 *
 * Custom rule implementations should actually use [Rule] as base class.
 *
 * The extraction of this class from [Rule] actually resulted from the need
 * of running many different checks on the same [KtFile] but within a single
 * potential costly visiting process, see [MultiRule].
 *
 * This base rule class abstracts over single and multi rules and allows the
 * detekt core engine to only care about a single type.
 */
abstract class BaseRule(
		protected val context: Context = DefaultContext()
) : DetektVisitor(), Context by context {

	open val ruleId: RuleId = javaClass.simpleName

	/**
	 * Before starting visiting kotlin elements, a check is performed if this rule should be triggered.
	 * Pre- and post-visit-hooks are executed before/after the visiting process.
	 */
	fun visitFile(root: KtFile) {
		if (visitCondition(root)) {
			clearFindings()
			preVisit(root)
			visit(root)
			postVisit(root)
		}
	}

	open fun visit(root: KtFile) {
		root.accept(this)
	}

	/**
	 * Basic mechanism to decide if a rule should run or not.
	 *
	 * By default any rule which is declared 'active' in the [Config]
	 * or not suppressed by a [Suppress] annotation on file level should run.
	 */
	abstract fun visitCondition(root: KtFile): Boolean

	/**
	 * Could be overridden by subclasses to specify a behaviour which should be done before
	 * visiting kotlin elements.
	 */
	protected open fun preVisit(root: KtFile) {
		// nothing to do by default
	}

	/**
	 * Could be overridden by subclasses to specify a behaviour which should be done after
	 * visiting kotlin elements.
	 */
	protected open fun postVisit(root: KtFile) {
		// nothing to do by default
	}
}
