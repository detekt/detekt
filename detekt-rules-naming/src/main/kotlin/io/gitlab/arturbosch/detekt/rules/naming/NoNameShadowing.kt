package io.gitlab.arturbosch.detekt.rules.naming

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.internal.RequiresTypeResolution
import io.gitlab.arturbosch.detekt.rules.hasImplicitParameterReference
import io.gitlab.arturbosch.detekt.rules.implicitParameter
import org.jetbrains.kotlin.diagnostics.Errors
import org.jetbrains.kotlin.psi.KtDestructuringDeclarationEntry
import org.jetbrains.kotlin.psi.KtLambdaExpression
import org.jetbrains.kotlin.psi.KtNamedDeclaration
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.psiUtil.getStrictParentOfType
import org.jetbrains.kotlin.resolve.BindingContext

/**
 * Disallows shadowing variable declarations.
 * Shadowing makes it impossible to access a variable with the same name in the scope.
 *
 * <noncompliant>
 * fun test(i: Int, j: Int, k: Int) {
 *     val i = 1
 *     val (j, _) = 1 to 2
 *     listOf(1).map { k -> println(k) }
 *     listOf(1).forEach {
 *         listOf(2).forEach {
 *         }
 *     }
 * }
 * </noncompliant>
 *
 * <compliant>
 * fun test(i: Int, j: Int, k: Int) {
 *     val x = 1
 *     val (y, _) = 1 to 2
 *     listOf(1).map { z -> println(z) }
 *     listOf(1).forEach {
 *         listOf(2).forEach { x ->
 *         }
 *     }
 * }
 * </compliant>
 *
 */
@RequiresTypeResolution
class NoNameShadowing(config: Config = Config.empty) : Rule(config) {
    override val issue = Issue(
        javaClass.simpleName,
        Severity.Defect,
        "Disallows shadowing variable declarations.",
        Debt.FIVE_MINS
    )

    override fun visitProperty(property: KtProperty) {
        super.visitProperty(property)
        checkNameShadowing(property)
    }

    override fun visitDestructuringDeclarationEntry(multiDeclarationEntry: KtDestructuringDeclarationEntry) {
        super.visitDestructuringDeclarationEntry(multiDeclarationEntry)
        checkNameShadowing(multiDeclarationEntry)
    }

    override fun visitParameter(parameter: KtParameter) {
        super.visitParameter(parameter)
        checkNameShadowing(parameter)
    }

    private fun checkNameShadowing(declaration: KtNamedDeclaration) {
        val nameIdentifier = declaration.nameIdentifier ?: return
        if (bindingContext != BindingContext.EMPTY &&
            bindingContext.diagnostics.forElement(declaration).any { it.factory == Errors.NAME_SHADOWING }
        ) {
            report(CodeSmell(issue, Entity.from(nameIdentifier), "Name shadowed: ${nameIdentifier.text}"))
        }
    }

    override fun visitLambdaExpression(lambdaExpression: KtLambdaExpression) {
        super.visitLambdaExpression(lambdaExpression)
        if (bindingContext == BindingContext.EMPTY) return
        val implicitParameter = lambdaExpression.implicitParameter(bindingContext) ?: return
        if (lambdaExpression.hasImplicitParameterReference(implicitParameter, bindingContext) &&
            lambdaExpression.hasParentImplicitParameterLambda()
        ) {
            report(
                CodeSmell(
                    issue,
                    Entity.from(lambdaExpression),
                    "Name shadowed: implicit lambda parameter 'it'"
                )
            )
        }
    }

    private fun KtLambdaExpression.hasParentImplicitParameterLambda(): Boolean =
        getStrictParentOfType<KtLambdaExpression>()?.implicitParameter(bindingContext) != null
}
