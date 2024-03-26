package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Configuration
import io.gitlab.arturbosch.detekt.api.DetektVisitor
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.RequiresTypeResolution
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.config
import io.gitlab.arturbosch.detekt.rules.isExpect
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.ParameterDescriptor
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.js.resolve.diagnostics.findPsi
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtConstructor
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtNamedDeclaration
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtPrimaryConstructor
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtReferenceExpression
import org.jetbrains.kotlin.psi.KtSecondaryConstructor
import org.jetbrains.kotlin.psi.KtValueArgumentList
import org.jetbrains.kotlin.psi.psiUtil.containingClassOrObject
import org.jetbrains.kotlin.psi.psiUtil.getChildrenOfType
import org.jetbrains.kotlin.psi.psiUtil.isPrivate
import org.jetbrains.kotlin.psi.psiUtil.isPropertyParameter
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.bindingContextUtil.getReferenceTargets

/**
 * An unused private property can be removed to simplify the source file.
 *
 * This rule also detects unused constructor parameters since these can become
 * properties of the class when they are declared with `val` or `var`.
 *
 * <noncompliant>
 * class Foo {
 *     private val unused = "unused"
 * }
 * </noncompliant>
 *
 * <compliant>
 * class Foo {
 *     private val used = "used"
 *
 *     fun greet() {
 *         println(used)
 *     }
 * }
 * </compliant>
 */
@RequiresTypeResolution
@ActiveByDefault(since = "1.23.0")
class UnusedPrivateProperty(config: Config) : Rule(
    config,
    "Property is unused and should be removed."
) {

    override val defaultRuleIdAliases: Set<String> =
        setOf("UNUSED_PARAMETER", "unused", "UnusedPrivateMember")

    @Configuration("unused property names matching this regex are ignored")
    private val allowedNames: Regex by config(
        "ignored|expected|serialVersionUID",
        String::toRegex
    )

    override fun visit(root: KtFile) {
        super.visit(root)
        val visitor = UnusedPrivatePropertyVisitor(allowedNames, bindingContext)
        root.accept(visitor)
        visitor.getUnusedReports().forEach { report(it) }
    }
}

@Suppress("unused")
private class UnusedPrivatePropertyVisitor(
    private val allowedNames: Regex,
    private val bindingContext: BindingContext,
) : DetektVisitor() {

    private val classProperties = hashSetOf<KtNamedDeclaration>()
    private val usedClassProperties = hashSetOf<PsiElement>()

    private val constructorParameters = hashSetOf<KtNamedDeclaration>()
    private val usedConstructorParameters = hashSetOf<PsiElement>()

    fun getUnusedReports(): List<CodeSmell> {
        val propertiesReport = classProperties.filter { classProperty ->
            classProperty.psiOrParent !in usedClassProperties
        }.filter { !allowedNames.matches(it.nameAsSafeName.identifier) }
            .map {
                CodeSmell(
                    entity = Entity.atName(it),
                    message = "Private property `${it.nameAsSafeName.identifier}` is unused."
                )
            }

        val constructorParametersReport = constructorParameters.filter { constructorParameter ->
            constructorParameter.psiOrParent !in usedConstructorParameters
        }.filter { !allowedNames.matches(it.nameAsSafeName.identifier) }
            .map {
                CodeSmell(
                    entity = Entity.atName(it),
                    message = "Constructor parameter `${it.nameAsSafeName.identifier}` is unused.",
                )
            }

        return propertiesReport + constructorParametersReport
    }

    override fun visitPrimaryConstructor(constructor: KtPrimaryConstructor) {
        super.visitPrimaryConstructor(constructor)

        constructor.valueParameters
            .filter {
                (it.isPrivate() || !it.isPropertyParameter()) &&
                    !constructor.isExpectClassConstructor() &&
                    !constructor.isDataOrValueClassConstructor()
            }
            .forEach { valueParameter ->
                if (valueParameter.isPropertyParameter()) {
                    classProperties.add(valueParameter)
                } else {
                    constructorParameters.add(valueParameter)
                }
            }
    }

    override fun visitSecondaryConstructor(constructor: KtSecondaryConstructor) {
        super.visitSecondaryConstructor(constructor)
        constructorParameters += constructor.valueParameters
    }

    override fun visitProperty(property: KtProperty) {
        super.visitProperty(property)
        if (property.isPrivate() && property.isMember()) {
            classProperties.add(property)
        }
    }

    override fun visitReferenceExpression(expression: KtReferenceExpression) {
        super.visitReferenceExpression(expression)
        val references = when (expression) {
            is KtNameReferenceExpression -> expression.getReferenceTargets(bindingContext)
            is KtCallExpression -> {
                expression.getChildrenOfType<KtValueArgumentList>()
                    .flatMap { it.arguments }
                    .flatMap {
                        it.getArgumentExpression()?.getReferenceTargets(bindingContext).orEmpty()
                    }
            }

            else -> null
        }?.filter {
            it is ParameterDescriptor || it is PropertyDescriptor
        } ?: return

        references.forEach { descriptor ->
            if (descriptor.isPropertyParameter()) {
                descriptor.findPsi()?.also(usedClassProperties::add)
            } else {
                descriptor.findPsi()?.also(usedConstructorParameters::add)
            }
        }
    }
}

private fun KtConstructor<*>.isExpectClassConstructor() = containingClassOrObject?.isExpect() == true
private fun KtConstructor<*>.isDataOrValueClassConstructor(): Boolean {
    val parent = parent as? KtClass ?: return false
    return parent.isData() || parent.isValue() || parent.isInline()
}

private fun DeclarationDescriptor.isPropertyParameter() =
    this is PropertyDescriptor || (findPsi() as? KtParameter)?.isPropertyParameter() ?: false
