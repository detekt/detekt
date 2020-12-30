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
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtImportDirective
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
 */
class SleepInsteadOfDelay(config: Config = Config.empty) : Rule(config) {
    private var sleepImported = false

    override val issue = Issue(
        javaClass.simpleName,
        Severity.Defect,
        "Usage of Thread.sleep() in coroutines can potentially halt multiple coroutines at once.",
        Debt.FIVE_MINS
    )

    override fun postVisit(root: KtFile) {
        super.postVisit(root)
        sleepImported = false
    }

    override fun visitImportDirective(importDirective: KtImportDirective) {
        sleepImported = importDirective.importedFqName?.asString() in IMPORT_PATHS
    }

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
        if (fqName == LAUNCH_COROUTINE_NAME) {
            expression.checkDescendants(COROUTINE_MESSAGE)
        }
        super.visitQualifiedExpression(expression)
    }

    private fun PsiElement.checkDescendants(message: String) {
        if (sleepImported) {
            forEachDescendantOfType<KtCallExpression> { it.verifyExpression(message) }
        }
        forEachDescendantOfType<KtDotQualifiedExpression> { it.verifyExpression(message) }
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
        private const val SLEEP_CALL = "sleep"
        private const val THREAD_IMPORT_PATH = "java.lang.Thread"
        private const val FQ_NAME = "$THREAD_IMPORT_PATH.$SLEEP_CALL"
        private val IMPORT_PATHS = listOf(FQ_NAME, THREAD_IMPORT_PATH)
        private const val LAUNCH_COROUTINE_NAME = "kotlinx.coroutines.launch"
    }
}
