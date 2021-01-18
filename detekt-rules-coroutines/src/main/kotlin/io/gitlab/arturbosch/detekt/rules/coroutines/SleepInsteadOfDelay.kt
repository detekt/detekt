package io.gitlab.arturbosch.detekt.rules.coroutines

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtQualifiedExpression
import org.jetbrains.kotlin.psi.psiUtil.forEachDescendantOfType
import org.jetbrains.kotlin.psi.psiUtil.hasSuspendModifier
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.calls.callUtil.getResolvedCall
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameOrNull

/**
 * Report usages of `Thread.sleep` in suspending functions and coroutine blocks. A thread can
 * contain multiple coroutines at one time due to coroutines' lightweight nature, so if one
 * coroutine invokes `Thread.sleep`, it could potentially halt the execution of unrelated coroutines
 * and cause unpredictable behavior.
 *
 * <noncompliant>
 * suspend fun foo() {
 *     Thread.sleep(1_000L)
 * }
 * </noncompliant>
 *
 * <compliant>
 * suspend fun foo() {
 *     delay(1_000L)
 * }
 * </compliant>
 *
 * @requiresTypeResolution
 */
class SleepInsteadOfDelay(config: Config = Config.empty) : Rule(config) {
    override val issue = Issue(
        javaClass.simpleName,
        Severity.Defect,
        "Usage of Thread.sleep() in coroutines can potentially halt multiple coroutines at once.",
        Debt.FIVE_MINS
    )

    override fun visitNamedFunction(function: KtNamedFunction) {
        if (bindingContext == BindingContext.EMPTY) return
        if (function.modifierList?.hasSuspendModifier() == true) {
            function.checkDescendants(SUSPEND_FUN_MESSAGE)
        }
        super.visitNamedFunction(function)
    }

    override fun visitQualifiedExpression(expression: KtQualifiedExpression) {
        val fqName = expression.getResolvedCall(bindingContext)
            ?.resultingDescriptor
            ?.fqNameOrNull()
            ?.asString()
        if (fqName in COROUTINE_NAMES) {
            expression.checkDescendants(COROUTINE_MESSAGE)
        }
        super.visitQualifiedExpression(expression)
    }

    private fun PsiElement.checkDescendants(message: String) {
        forEachDescendantOfType<KtCallExpression> { it.verifyExpression(message) }
    }

    private fun KtExpression.verifyExpression(message: String) {
        val fqName = getResolvedCall(bindingContext)
            ?.resultingDescriptor
            ?.fqNameOrNull()
            ?.asString()
        if (fqName == FQ_NAME) {
            report(CodeSmell(issue, Entity.from(this), message))
        }
    }

    companion object {
        private const val SUSPEND_FUN_MESSAGE =
            "This use of Thread.sleep() inside a suspend function should be replaced by delay()."
        private const val COROUTINE_MESSAGE =
            "This use of Thread.sleep() inside a coroutine should be replaced by delay()."
        private const val FQ_NAME = "java.lang.Thread.sleep"
        private val COROUTINE_NAMES = listOf("kotlinx.coroutines.launch", "kotlinx.coroutines.async")
    }
}
