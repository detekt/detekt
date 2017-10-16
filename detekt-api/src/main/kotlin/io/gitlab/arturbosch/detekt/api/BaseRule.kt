package io.gitlab.arturbosch.detekt.api

import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingContext

@Suppress("EmptyFunctionBlock")
abstract class BaseRule(protected val context: Context = DefaultContext()) : DetektVisitor(), Context by context {

	open val id: String = javaClass.simpleName
	var bindingContext: BindingContext = BindingContext.EMPTY

	/**
	 * Before starting visiting kotlin elements, a check is performed if this rule should be triggered.
	 * Pre- and post-visit-hooks are executed before/after the visiting process.
	 * BindingContext holds the result of the semantic analysis of the source code by the Kotlin compiler. Rules that
	 * rely on symbols and types being resolved can use the BindingContext for this analysis. Note that
	 * BindingContext will have the value BindingContext.EMPTY unless detekt is provided the correct classpath while
	 * analyzing the code.
	 */
	fun visitFile(root: KtFile, bindingContext: BindingContext = BindingContext.EMPTY) {
		this.bindingContext = bindingContext
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
