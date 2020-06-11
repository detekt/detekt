package io.gitlab.arturbosch.detekt.rules.complexity

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Metric
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.ThresholdRule
import io.gitlab.arturbosch.detekt.api.ThresholdedCodeSmell
import io.gitlab.arturbosch.detekt.rules.companionObject
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtClassBody
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtObjectDeclaration
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtTypeParameterListOwner
import org.jetbrains.kotlin.psi.psiUtil.isPrivate

/**
 * Complex interfaces which contain too many functions and/or properties indicate that this interface is handling too
 * many things at once. Interfaces should follow the single-responsibility principle to also encourage implementations
 * of this interface to not handle too many things at once.
 *
 * Large interfaces should be split into smaller interfaces which have a clear responsibility and are easier
 * to understand and implement.
 *
 * @configuration threshold - the amount of definitions in an interface to trigger the rule (default: `10`)
 * @configuration includeStaticDeclarations - whether static declarations should be included (default: `false`)
 * @configuration includePrivateDeclarations - whether private declarations should be included (default: `false`)
 */
class ComplexInterface(
    config: Config = Config.empty,
    threshold: Int = DEFAULT_LARGE_INTERFACE_COUNT
) : ThresholdRule(config, threshold) {

    override val issue = Issue(javaClass.simpleName, Severity.Maintainability,
            "An interface contains too many functions and properties. " +
                    "Large classes tend to handle many things at once. " +
                    "An interface should have one responsibility. " +
                    "Split up large interfaces into smaller ones that are easier to understand.",
            Debt.TWENTY_MINS)

    private val includeStaticDeclarations = valueOrDefault(INCLUDE_STATIC_DECLARATIONS, false)
    private val includePrivateDeclarations = valueOrDefault(INCLUDE_PRIVATE_DECLARATIONS, false)

    override fun visitClass(klass: KtClass) {
        if (klass.isInterface()) {
            val body = klass.body ?: return
            var size = calculateMembers(body)
            if (includeStaticDeclarations) {
                size += countStaticDeclarations(klass.companionObject())
            }
            if (size >= threshold) {
                report(
                    ThresholdedCodeSmell(issue,
                        Entity.atName(klass),
                        Metric("SIZE: ", size, threshold),
                        "The interface ${klass.name} is too complex. Consider splitting it up.")
                )
            }
        }
        super.visitClass(klass)
    }

    private fun countStaticDeclarations(companionObject: KtObjectDeclaration?): Int {
        val body = companionObject?.body
        return if (body != null) calculateMembers(body) else 0
    }

    private fun calculateMembers(body: KtClassBody): Int {
        fun PsiElement.considerPrivate() = includePrivateDeclarations ||
                this is KtTypeParameterListOwner && !this.isPrivate()

        fun PsiElement.isMember() = this is KtNamedFunction || this is KtProperty

        return body.children
            .filter(PsiElement::considerPrivate)
            .count(PsiElement::isMember)
    }

    companion object {
        const val INCLUDE_STATIC_DECLARATIONS = "includeStaticDeclarations"
        const val INCLUDE_PRIVATE_DECLARATIONS = "includePrivateDeclarations"
        const val DEFAULT_LARGE_INTERFACE_COUNT = 10
    }
}
