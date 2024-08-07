package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.RequiresTypeResolution
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.rules.isMainFunction
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.psiUtil.getStrictParentOfType
import org.jetbrains.kotlin.resolve.calls.util.getResolvedCall
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameOrNull

/**
 * Reports the usage of `System.exit()`, `Runtime.exit()`, `Runtime.halt()` and Kotlin's `exitProcess()`
 * when used outside the `main` function.
 * This makes code more difficult to test, causes unexpected behaviour on Android, and is a poor way to signal a
 * failure in the program. In almost all cases it is more appropriate to throw an exception.
 *
 * <noncompliant>
 * fun randomFunction() {
 *     val result = doWork()
 *     if (result == FAILURE) {
 *         exitProcess(2)
 *     } else {
 *         exitProcess(0)
 *     }
 * }
 * </noncompliant>
 *
 * <compliant>
 * fun main() {
 *     val result = doWork()
 *     if (result == FAILURE) {
 *         exitProcess(2)
 *     } else {
 *         exitProcess(0)
 *     }
 * }
 * </compliant>
 *
 */
class ExitOutsideMain(config: Config) :
    Rule(
        config,
        "Do not directly exit the process outside the `main` function. Throw an exception instead."
    ),
    RequiresTypeResolution {
    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)

        if (expression.getStrictParentOfType<KtNamedFunction>()?.isMainFunction() == true) return
        val fqName = expression.getResolvedCall(bindingContext)?.resultingDescriptor?.fqNameOrNull() ?: return

        if (fqName.asString() in exitCalls) {
            report(CodeSmell(Entity.from(expression), description))
        }
    }

    companion object {
        val exitCalls = setOf(
            "kotlin.system.exitProcess",
            "java.lang.System.exit",
            "java.lang.Runtime.exit",
            "java.lang.Runtime.halt"
        )
    }
}
