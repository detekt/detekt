package io.gitlab.arturbosch.detekt.rules.complexity

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Configuration
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.config
import io.gitlab.arturbosch.detekt.rules.companionObject
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtClassBody
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtObjectDeclaration
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtTypeParameterListOwner
import org.jetbrains.kotlin.psi.psiUtil.isExtensionDeclaration
import org.jetbrains.kotlin.psi.psiUtil.isPrivate

/**
 * Complex interfaces which contain too many functions and/or properties indicate that this interface is handling too
 * many things at once. Interfaces should follow the single-responsibility principle to also encourage implementations
 * of this interface to not handle too many things at once.
 *
 * Large interfaces should be split into smaller interfaces which have a clear responsibility and are easier
 * to understand and implement.
 */
class ComplexInterface(config: Config) : Rule(
    config,
    "An interface contains too many functions and properties. " +
        "Large classes tend to handle many things at once. " +
        "An interface should have one responsibility. " +
        "Split up large interfaces into smaller ones that are easier to understand."
) {

    @Configuration("The amount of allowed definitions in an interface.")
    private val allowedDefinitions: Int by config(defaultValue = 10)

    @Configuration("whether static declarations should be included")
    private val includeStaticDeclarations: Boolean by config(defaultValue = false)

    @Configuration("whether private declarations should be included")
    private val includePrivateDeclarations: Boolean by config(defaultValue = false)

    @Configuration("ignore overloaded methods - only count once")
    private val ignoreOverloaded: Boolean by config(defaultValue = false)

    override fun visitClass(klass: KtClass) {
        if (klass.isInterface()) {
            val body = klass.body ?: return
            var size = calculateMembers(body)
            if (includeStaticDeclarations) {
                size += countStaticDeclarations(klass.companionObject())
            }
            if (size > allowedDefinitions) {
                report(
                    CodeSmell(
                        Entity.atName(klass),
                        "The interface ${klass.name} is too complex. Consider splitting it up."
                    )
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
            this is KtTypeParameterListOwner &&
            !this.isPrivate()

        fun countFunctions(psiElements: List<PsiElement>): Int {
            val functions = psiElements.filterIsInstance<KtNamedFunction>()
            return if (ignoreOverloaded) {
                functions.distinctBy { function ->
                    val receiver = function.receiverTypeReference
                    if (function.isExtensionDeclaration() && receiver != null) {
                        "${receiver.text}.${function.name}"
                    } else {
                        function.name
                    }
                }.size
            } else {
                functions.size
            }
        }

        val psiElements = body.children
            .filter(PsiElement::considerPrivate)
        val propertyCount = psiElements.count { it is KtProperty }
        val functionCount = countFunctions(psiElements)
        return propertyCount + functionCount
    }
}
