package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.Alias
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
import org.jetbrains.kotlin.descriptors.ClassConstructorDescriptor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptorWithSource
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.descriptors.Visibilities
import org.jetbrains.kotlin.descriptors.isTopLevelInPackage
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
import org.jetbrains.kotlin.resolve.source.getPsi

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
@ActiveByDefault(since = "1.23.0")
@Alias("unused")
class UnusedPrivateProperty(config: Config) :
    Rule(
        config,
        "Property is unused and should be removed."
    ),
    RequiresTypeResolution {
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

    private val topLevelProperties = hashSetOf<KtNamedDeclaration>()
    private val usedTopLevelProperties = hashSetOf<PsiElement>()

    private val classProperties = hashSetOf<KtNamedDeclaration>()
    private val usedClassProperties = hashSetOf<PsiElement>()

    private val constructorParameters = hashSetOf<KtNamedDeclaration>()
    private val usedConstructorParameters = hashSetOf<PsiElement>()

    fun getUnusedReports(): List<CodeSmell> {
        val propertiesReport = classProperties
            .filter { it.psiOrParent !in usedClassProperties }
            .filter { !allowedNames.matches(it.nameAsSafeName.identifier) }
            .map {
                CodeSmell(
                    entity = Entity.atName(it),
                    message = "Private property `${it.nameAsSafeName.identifier}` is unused."
                )
            }

        val constructorParametersReport = constructorParameters
            .filter { it.psiOrParent !in usedConstructorParameters }
            .filter { !allowedNames.matches(it.nameAsSafeName.identifier) }
            .map {
                CodeSmell(
                    entity = Entity.atName(it),
                    message = "Constructor parameter `${it.nameAsSafeName.identifier}` is unused.",
                )
            }

        val topLevelPropertyReport = topLevelProperties
            .filter { it.psiOrParent !in usedTopLevelProperties }
            .filter { !allowedNames.matches(it.nameAsSafeName.identifier) }
            .map {
                CodeSmell(
                    entity = Entity.atName(it),
                    message = "Private top level property `${it.nameAsSafeName.identifier}` is unused.",
                )
            }

        return propertiesReport + constructorParametersReport + topLevelPropertyReport
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

        if (!property.isPrivate()) {
            return
        }

        if (property.isTopLevel) {
            topLevelProperties.add(property)
        } else {
            classProperties.add(property)
        }
    }

    override fun visitReferenceExpression(expression: KtReferenceExpression) {
        super.visitReferenceExpression(expression)
        val references = when (expression) {
            is KtNameReferenceExpression -> expression.getReferenceTargets(bindingContext)
            is KtCallExpression -> expression.getChildrenOfType<KtValueArgumentList>()
                .flatMap { it.arguments }
                .flatMap {
                    it.getArgumentExpression()?.getReferenceTargets(bindingContext).orEmpty()
                }

            else -> return
        }

        references
            .filter {
                it.containingDeclaration is ClassConstructorDescriptor || it.isPrivateProperty()
            }
            .forEach { descriptor ->
                val psi = (descriptor as? DeclarationDescriptorWithSource)?.source?.getPsi() ?: return@forEach
                when {
                    descriptor.isTopLevelInPackage() -> usedTopLevelProperties.add(psi)
                    descriptor.isPropertyParameter() -> usedClassProperties.add(psi)
                    else -> usedConstructorParameters.add(psi)
                }
            }
    }
}

private fun KtConstructor<*>.isExpectClassConstructor() =
    containingClassOrObject?.isExpect() == true

private fun KtConstructor<*>.isDataOrValueClassConstructor(): Boolean {
    val parent = parent as? KtClass ?: return false
    return parent.isData() || parent.isValue() || parent.isInline()
}

fun DeclarationDescriptor.isPrivateProperty() =
    this is PropertyDescriptor && visibility.name == Visibilities.Private.name

private fun DeclarationDescriptor.isPropertyParameter() =
    this is PropertyDescriptor ||
        ((this as? DeclarationDescriptorWithSource)?.source?.getPsi() as? KtParameter)?.isPropertyParameter() ?: false
