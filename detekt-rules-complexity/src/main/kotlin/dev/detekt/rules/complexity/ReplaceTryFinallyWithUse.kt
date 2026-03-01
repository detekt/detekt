package dev.detekt.rules.complexity

import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.analysis.api.resolution.singleFunctionCallOrNull
import org.jetbrains.kotlin.analysis.api.resolution.symbol
import org.jetbrains.kotlin.analysis.api.symbols.KaClassSymbol
import org.jetbrains.kotlin.analysis.api.symbols.KaNamedFunctionSymbol
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtFinallySection
import org.jetbrains.kotlin.psi.KtTryExpression
import org.jetbrains.kotlin.psi.psiUtil.collectDescendantsOfType

/**
 *
 * In Kotlin, the idiomatic way to manage resources that implement the AutoClosable interface,
 * is to use the `.use()` function, to automatically close the resource when the block of code completes.
 *
 * This rule checks if a `AutoCloseable.close()` call has been made in a finally block, suggesting to
 * refactor the code using `AutoCloseable.use { }` to be less error-prone and more readable.
 *
 * <noncompliant>
 *     val fileWriter = FileWriter("example.txt")
 *     try {
 *         fileWriter.write("example")
 *     } finally {
 *         fileWriter.close()
 *     }
 * </noncompliant>
 *
 * <compliant>
 *     FileWriter("example.txt").use { fileWriter ->
 *         fileWriter.write("example")
 *     }
 * </compliant>
 *
 */
class ReplaceTryFinallyWithUse(config: Config) :
    Rule(
        config,
        "`closeable.close()` in a try-finally block can be replaced with `closeable.use {}`."
    ),
    RequiresAnalysisApi {

    override fun visitFinallySection(expression: KtFinallySection) {
        super.visitFinallySection(expression)

        analyze(expression) {
            val tryExpression = expression.parent as? KtTryExpression ?: return

            val closeCallInFinally = expression
                .collectDescendantsOfType<KtCallExpression>()
                .firstOrNull { isCloseCall(it) }
                ?: return

            val receiver = (closeCallInFinally.parent as? KtDotQualifiedExpression)?.receiverExpression ?: return

            report(
                Finding(
                    Entity.from(tryExpression),
                    "This try-finally block can be replaced with ${receiver.text}.use { ... }`."
                )
            )
        }
    }

    private fun KaSession.isCloseCall(callExpression: KtCallExpression): Boolean {
        val functionCall = callExpression.resolveToCall()?.singleFunctionCallOrNull() ?: return false

        val isCloseFunction = (functionCall.symbol as KaNamedFunctionSymbol).name.asString() == FUNCTION_NAME_CLOSE &&
            functionCall.symbol.valueParameters.isEmpty()

        val containingClass = functionCall.symbol.containingDeclaration as? KaClassSymbol ?: return false
        return (isCloseable(containingClass) || isSubtypeOfCloseable(containingClass)) &&
            isCloseFunction
    }

    private fun KaSession.isSubtypeOfCloseable(classSymbol: KaClassSymbol): Boolean {
        val superTypes = classSymbol.superTypes.flatMap { listOf(it) + it.allSupertypes }
        return superTypes.any { it.expandedSymbol?.classId?.asSingleFqName() == FQ_NAME_AUTO_CLOSEABLE }
    }

    private fun isCloseable(classSymbol: KaClassSymbol): Boolean =
        classSymbol.classId?.asSingleFqName() == FQ_NAME_AUTO_CLOSEABLE

    companion object {
        private const val FUNCTION_NAME_CLOSE = "close"
        private val FQ_NAME_AUTO_CLOSEABLE = FqName("java.lang.AutoCloseable")
    }
}
