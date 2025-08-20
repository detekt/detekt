package io.gitlab.arturbosch.detekt.rules.naming

import com.intellij.psi.PsiElement
import com.intellij.psi.util.parentOfType
import com.intellij.psi.util.parents
import dev.detekt.api.ActiveByDefault
import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import dev.detekt.psi.hasImplicitParameterReference
import dev.detekt.psi.implicitParameterOrNull
import org.jetbrains.kotlin.builtins.StandardNames
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtClassInitializer
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtDestructuringDeclaration
import org.jetbrains.kotlin.psi.KtDestructuringDeclarationEntry
import org.jetbrains.kotlin.psi.KtFunction
import org.jetbrains.kotlin.psi.KtLambdaExpression
import org.jetbrains.kotlin.psi.KtNamedDeclaration
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtPrimaryConstructor
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.psiUtil.containingClassOrObject
import org.jetbrains.kotlin.psi.psiUtil.getStrictParentOfType
import org.jetbrains.kotlin.psi.psiUtil.hasInnerModifier

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
@ActiveByDefault(since = "1.21.0")
class NoNameShadowing(config: Config) :
    Rule(
        config,
        "Disallow shadowing variable declarations."
    ),
    RequiresAnalysisApi {

    override fun visitProperty(property: KtProperty) {
        super.visitProperty(property)
        checkNameShadowing(
            property,
            property,
            property.accessibleClasses,
        )
    }

    override fun visitDestructuringDeclarationEntry(multiDeclarationEntry: KtDestructuringDeclarationEntry) {
        super.visitDestructuringDeclarationEntry(multiDeclarationEntry)
        checkNameShadowing(
            multiDeclarationEntry,
            multiDeclarationEntry.parent as KtDestructuringDeclaration,
            multiDeclarationEntry.accessibleClasses,
        )
    }

    override fun visitParameter(parameter: KtParameter) {
        super.visitParameter(parameter)
        parameter.parentOfType<KtFunction>(false)
        checkNameShadowing(
            parameter,
            // if this param is from lambda or we fallback to function(ctor)
            (parameter.parent.parent.parent as? KtLambdaExpression)
                ?: parameter.parentOfType<KtFunction>(false).let {
                    if (it is KtPrimaryConstructor) {
                        // if this param is from primary constructor then pass the class to
                        // start(excluding) the search from
                        parameter.parentOfType<KtClass>(false)
                    } else {
                        it
                    }
                },
            parameter.accessibleClasses,
        )
    }

    private fun checkNameShadowing(
        declaration: KtNamedDeclaration,
        parentToSkipSearchFrom: PsiElement?,
        accessibleClasses: List<KtClassOrObject>,
    ) {
        parentToSkipSearchFrom ?: return
        val nameIdentifier = declaration.nameIdentifier ?: return
        val matched = parentToSkipSearchFrom
            .parents(false)
            .filter {
               it is KtClass || it is KtFunction || it is KtLambdaExpression
            }.any { parent ->
                when (parent) {
                    is KtClass -> {
                        parent in accessibleClasses &&
                            parent
                            .primaryConstructor
                            ?.valueParameters
                            .orEmpty()
                            .filter { declaration.parentOfType<KtClassInitializer>(false) != null || it.hasValOrVar() }
                            .any { it.name == nameIdentifier.text }
                    }

                    is KtFunction -> {
                        parent.containingClassOrObject in accessibleClasses &&
                            parent.valueParameters.any { it.name == nameIdentifier.text }
                    }

                    is KtLambdaExpression -> {
                        if (parent.valueParameters.isNotEmpty()) {
                            parent.valueParameters.any { it.name == nameIdentifier.text }
                        } else {
                            parent.implicitParameterOrNull() != null && nameIdentifier.text ==
                                StandardNames.IMPLICIT_LAMBDA_PARAMETER_NAME.asString()
                        }
                    }

                    else -> false
                }
            }
        if (matched) {
            report(
                Finding(
                    Entity.from(nameIdentifier),
                    "Name shadowed: ${nameIdentifier.text}"
                )
            )
        }
    }

    override fun visitLambdaExpression(lambdaExpression: KtLambdaExpression) {
        super.visitLambdaExpression(lambdaExpression)
        if (lambdaExpression.hasImplicitParameterReference() &&
            lambdaExpression.hasParentImplicitParameterLambda()
        ) {
            report(
                Finding(
                    Entity.from(lambdaExpression),
                    "Name shadowed: implicit lambda parameter 'it'"
                )
            )
        }
    }

    private fun KtLambdaExpression.hasParentImplicitParameterLambda(): Boolean =
        getStrictParentOfType<KtLambdaExpression>()?.implicitParameterOrNull() != null

    private val KtNamedDeclaration.accessibleClasses: List<KtClassOrObject>
        get() = buildList {
            var currentClass: KtClassOrObject?
            do {
                currentClass = containingClassOrObject
                add(currentClass)
            } while (currentClass?.hasInnerModifier() == true)
        }.filterNotNull()
}
