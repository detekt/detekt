package io.gitlab.arturbosch.detekt.api

import org.jetbrains.kotlin.psi.KtFile

@Suppress("EmptyFunctionBlock")
abstract class BaseRule(protected val context: Context = DefaultContext()) : DetektVisitor(), Context by context {

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

	abstract fun visitCondition(root: KtFile): Boolean

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
}
