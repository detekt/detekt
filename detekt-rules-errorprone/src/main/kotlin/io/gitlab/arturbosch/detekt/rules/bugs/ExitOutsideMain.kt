package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.rules.isMainFunction
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.psiUtil.getStrictParentOfType
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.calls.callUtil.getResolvedCall
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameOrNull

/**
 * Flags use of System.exit() and Kotlin's exitProcess() when used outside of the `main` function. This makes code more
 * difficult to test, causes unexpected behaviour on Android, and is a poor way to signal a failure in the program. In
 * almost all cases it is more appropriate to throw an exception.
 *
 * <noncompliant>
 * fun randomFunction() {
 *   val result = doWork()
 *   if (result == FAILURE) {
 *     exitProcess(2)
 *   } else {
 *     exitProcess(0)
 *   }
 * }
 * </noncompliant>
 *
 * <compliant>
 * fun main() {
 *   val result = doWork()
 *   if (result == FAILURE) {
 *     exitProcess(2)
 *   } else {
 *     exitProcess(0)
 *   }
 * }
 * </compliant>
 *
 * @requiresTypeResolution
 */
class ExitOutsideMain(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue(
        javaClass.simpleName,
        Severity.Warning,
        "Do not directly exit the process outside the `main` function. Throw an exception instead.",
        Debt.TEN_MINS
    )

    @Suppress("ReturnCount")
    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)

        if (context == BindingContext.EMPTY) return

        if (expression.getStrictParentOfType<KtNamedFunction>()?.isMainFunction() == true) return
        val fqName = expression.getResolvedCall(bindingContext)?.resultingDescriptor?.fqNameOrNull() ?: return

        if (fqName.asString() in setOf("kotlin.system.exitProcess", "java.lang.System.exit")) {
            report(CodeSmell(issue, Entity.from(expression), issue.description))
        }
    }
}
