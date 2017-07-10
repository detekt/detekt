package io.gitlab.arturbosch.detekt.api

import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtTreeVisitorVoid

/**
 * Base visitor for detekt rules.
 *
 * @author Artur Bosch
 */
open class DetektVisitor (protected val context: Context = DefaultContext()) : KtTreeVisitorVoid(), Context by context {

	/**
	 * Before starting visiting kotlin elements, a check is performed if this rule should be triggered.
	 * Pre- and post-visit-hooks are executed before/after the visiting process.
	 */
	open fun visit(root: KtFile) {
		preVisit(root)
		root.accept(this)
		postVisit(root)
	}

	/**
	 * Could be overriden by subclasses to specify a behaviour which should be done before
	 * visiting kotlin elements.
	 */
	protected open fun postVisit(root: KtFile) {
	}

	/**
	 * Could be overriden by subclasses to specify a behaviour which should be done after
	 * visiting kotlin elements.
	 */
	protected open fun preVisit(root: KtFile) {
	}
}