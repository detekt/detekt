package io.gitlab.arturbosch.detekt.api.internal

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Context
import io.gitlab.arturbosch.detekt.api.DefaultContext
import io.gitlab.arturbosch.detekt.api.DetektVisitor
import io.gitlab.arturbosch.detekt.api.RuleId
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingContext

abstract class BaseRule(
    protected val context: Context = DefaultContext()
) : DetektVisitor(), Context by context {

    open val ruleId: RuleId = javaClass.simpleName
    var bindingContext: BindingContext = BindingContext.EMPTY

    /**
     * Before starting visiting kotlin elements, a check is performed if this rule should be triggered.
     * Pre- and post-visit-hooks are executed before/after the visiting process.
     * BindingContext holds the result of the semantic analysis of the source code by the Kotlin compiler. Rules that
     * rely on symbols and types being resolved can use the BindingContext for this analysis. Note that detekt must
     * receive the correct compile classpath for the code being analyzed otherwise the default value
     * BindingContext.EMPTY will be used and it will not be possible for detekt to resolve types or symbols.
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

    /**
     * Init function to start visiting the [KtFile].
     * Can be overridden to start a different visiting process.
     */
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
