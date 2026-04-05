package dev.detekt.rules.style

import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.analysis.api.symbols.KaClassKind
import org.jetbrains.kotlin.analysis.api.symbols.KaClassSymbol
import org.jetbrains.kotlin.idea.references.mainReference
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtCallableReferenceExpression
import org.jetbrains.kotlin.psi.KtConstructorCalleeExpression
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtImportDirective
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtPackageDirective
import org.jetbrains.kotlin.psi.KtReferenceExpression
import org.jetbrains.kotlin.psi.psiUtil.getParentOfType

/**
 * Reports redundant access to companion object members through an explicit companion qualifier.
 *
 * <noncompliant>
 * class A {
 *     companion object {
 *         fun foo() = 1
 *         val BAZ = 2
 *     }
 * }
 *
 * class AFactory {
 *     companion object Factory {
 *         fun foo() = 1
 *     }
 * }
 *
 * fun test() {
 *     A.Companion.foo()
 *     A.Companion.BAZ
 *     AFactory.Factory.foo()
 * }
 * </noncompliant>
 *
 * <compliant>
 * class A {
 *     companion object {
 *         fun foo() = 1
 *         val BAZ = 2
 *     }
 * }
 *
 * class AFactory {
 *     companion object Factory {
 *         fun foo() = 1
 *     }
 * }
 *
 * fun test() {
 *     A.foo()
 *     A.BAZ
 *     AFactory.foo()
 * }
 * </compliant>
 */
class UnnecessaryCompanionObjectAccess(config: Config) :
    Rule(
        config,
        "Explicit companion object qualifier can be omitted — use the enclosing class name to access companion members."
    ),
    RequiresAnalysisApi {

    override fun visitReferenceExpression(expression: KtReferenceExpression) {
        super.visitReferenceExpression(expression)
        val companionNameRef = expression as? KtNameReferenceExpression ?: return
        if (isInImportOrPackage(companionNameRef)) return

        val companionQualifier = companionNameRef.parent as? KtDotQualifiedExpression ?: return
        if (companionQualifier.selectorExpression != companionNameRef) return

        analyzeCompanionNameReference(companionNameRef, companionQualifier)
    }

    @Suppress("CyclomaticComplexMethod")
    private fun analyzeCompanionNameReference(
        companionNameRef: KtNameReferenceExpression,
        companionQualifier: KtDotQualifiedExpression,
    ) {
        analyze(companionNameRef) {
            val companionSymbol = companionNameRef.mainReference.resolveToSymbol() as? KaClassSymbol ?: return@analyze
            if (companionSymbol.classKind != KaClassKind.COMPANION_OBJECT) return@analyze

            fun reportRedundantDotAccess(outer: KtDotQualifiedExpression) {
                if (outer.receiverExpression != companionQualifier) return
                val memberReference = outer.memberNameReference() ?: return
                val memberSymbol = memberReference.mainReference.resolveToSymbol() as? KaClassSymbol
                if (memberSymbol != null && memberSymbol.containingSymbol == companionSymbol) return

                report(
                    Finding(
                        Entity.from(companionQualifier),
                        "Omit the explicit companion object qualifier " +
                            "(for example use `${companionQualifier.receiverExpression.text}.<member>`."
                    )
                )
            }

            fun reportRedundantCallableRef(outer: KtCallableReferenceExpression) {
                if (outer.receiverExpression != companionQualifier) return
                val memberReference = outer.callableReference as? KtNameReferenceExpression ?: return
                val memberSymbol = memberReference.mainReference.resolveToSymbol() as? KaClassSymbol
                if (memberSymbol != null && memberSymbol.containingSymbol == companionSymbol) return

                report(
                    Finding(
                        Entity.from(companionQualifier),
                        "Omit the explicit companion object qualifier for callable references to companion members."
                    )
                )
            }

            when (val outer = companionQualifier.parent) {
                is KtDotQualifiedExpression -> reportRedundantDotAccess(outer)
                is KtCallableReferenceExpression -> reportRedundantCallableRef(outer)
                else -> Unit
            }
        }
    }

    private fun KtDotQualifiedExpression.memberNameReference(): KtNameReferenceExpression? {
        val sel = selectorExpression ?: return null
        return when (sel) {
            is KtNameReferenceExpression -> sel
            is KtCallExpression -> sel.calleeNameReference()
            else -> null
        }
    }

    private fun KtCallExpression.calleeNameReference(): KtNameReferenceExpression? =
        when (val callee = calleeExpression) {
            is KtNameReferenceExpression -> callee
            is KtConstructorCalleeExpression -> callee.constructorReferenceExpression as? KtNameReferenceExpression
            else -> null
        }

    private fun isInImportOrPackage(element: KtElement): Boolean =
        element.getParentOfType<KtImportDirective>(strict = false) != null ||
            element.getParentOfType<KtPackageDirective>(strict = false) != null
}
