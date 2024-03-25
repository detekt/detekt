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
import org.jetbrains.kotlin.descriptors.ClassConstructorDescriptor
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.ParameterDescriptor
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.descriptors.impl.ClassDescriptorBase
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtConstructor
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtNamedDeclaration
import org.jetbrains.kotlin.psi.KtPrimaryConstructor
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtReferenceExpression
import org.jetbrains.kotlin.psi.KtSecondaryConstructor
import org.jetbrains.kotlin.psi.KtValueArgumentList
import org.jetbrains.kotlin.psi.psiUtil.containingClass
import org.jetbrains.kotlin.psi.psiUtil.containingClassOrObject
import org.jetbrains.kotlin.psi.psiUtil.getChildrenOfType
import org.jetbrains.kotlin.psi.psiUtil.isPrivate
import org.jetbrains.kotlin.psi.psiUtil.isPropertyParameter
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.bindingContextUtil.getReferenceTargets
import org.jetbrains.kotlin.resolve.descriptorUtil.classId

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
        setOf("UNUSED_VARIABLE", "UNUSED_PARAMETER", "unused", "UnusedPrivateMember")

    @Configuration("unused property names matching this regex are ignored")
    private val allowedNames: Regex by config(
        "_|ignored|expected|serialVersionUID",
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

    private val classProperties = mutableMapOf<Name, MutableSet<KtNamedDeclaration>>()
    private val constructorParameters = mutableMapOf<Name, MutableMap<String, MutableSet<KtNamedDeclaration>>>()

    private val usedConstructorParameters = mutableMapOf<Name, MutableMap<String, MutableSet<String>>>()
    private val usedClassProperties = mutableMapOf<Name, MutableSet<String>>()

    fun getUnusedReports(): List<CodeSmell> {
        val unusedProperties = classProperties.flatMap { (classId, properties) ->
            val usedProperties = usedClassProperties[classId].orEmpty()
            properties.filter { classProperty ->
                classProperty.nameAsSafeName.identifier !in usedProperties
            }
        }.filter { !allowedNames.matches(it.nameAsSafeName.identifier) }
            .map {
                CodeSmell(
                    entity = Entity.atName(it),
                    message = "Private property `${it.nameAsSafeName.identifier}` is unused."
                )
            }

        val unusedConstructorParameters = constructorParameters.flatMap { (classId, constructors) ->
            constructors.flatMap { (constructor, parameters) ->
                val usedParameters = usedConstructorParameters[classId].orEmpty()[constructor].orEmpty()
                parameters.filter { constructorParameter ->
                    constructorParameter.nameAsSafeName.identifier !in usedParameters
                }
            }
        }.filter { !allowedNames.matches(it.nameAsSafeName.identifier) }
            .map {
                CodeSmell(
                    entity = Entity.atName(it),
                    message = "Constructor parameter `${it.nameAsSafeName.identifier}` is unused.",
                )
            }

        return unusedProperties + unusedConstructorParameters
    }

    override fun visitPrimaryConstructor(constructor: KtPrimaryConstructor) {
        super.visitPrimaryConstructor(constructor)
        val containingClass = constructor.containingClass() ?: return
        val containingClassId = containingClass.identifier()

        constructor.valueParameters
            .filter {
                (it.isPrivate() || !it.isPropertyParameter()) &&
                    !constructor.isExpectClassConstructor() &&
                    !constructor.isDataOrValueClassConstructor()
            }
            .forEach { valueParameter ->
                if (valueParameter.isPropertyParameter()) {
                    classProperties.put(containingClassId, valueParameter)
                } else {
                    constructorParameters.put(containingClassId, constructor.signature(), valueParameter)
                }
            }
    }

    override fun visitSecondaryConstructor(constructor: KtSecondaryConstructor) {
        super.visitSecondaryConstructor(constructor)
        constructor.valueParameters.forEach { valueParameter ->
            constructor.containingClass()?.identifier()?.also { classId ->
                constructorParameters.put(classId, constructor.signature(), valueParameter)
            }
        }
    }

    override fun visitProperty(property: KtProperty) {
        super.visitProperty(property)
        if (property.isPrivate() && property.isMemberOrTopLevel() || property.isLocal) {
            val classId = property.containingClassOrObject?.identifier() ?: return
            classProperties.put(classId, parameter = property)
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
            val classId = descriptor.containingDeclaration?.let {
                it as? ClassDescriptorBase ?: (it as? ClassConstructorDescriptor)?.constructedClass
            }?.identifier() ?: return@forEach

            when (descriptor) {
                is PropertyDescriptor -> usedClassProperties.getOrPut(classId) { mutableSetOf() }
                    .add(descriptor.name.identifier)

                is ParameterDescriptor -> {
                    val constructor = descriptor.containingDeclaration as ClassConstructorDescriptor
                    usedConstructorParameters.put(classId, constructor.signature(), descriptor.name.identifier)
                }
            }
        }
    }
}

fun <T> MutableMap<Name, MutableMap<String, MutableSet<T>>>.put(
    classId: Name,
    signature: String,
    parameter: T
) {
    getOrPut(classId) { mutableMapOf() }
        .getOrPut(signature) { mutableSetOf() }
        .add(parameter)
}

fun <T> MutableMap<Name, MutableSet<T>>.put(
    classId: Name,
    parameter: T
) {
    getOrPut(classId) { mutableSetOf() }
        .add(parameter)
}

private fun KtConstructor<*>.isExpectClassConstructor() = containingClassOrObject?.isExpect() == true
private fun KtConstructor<*>.isDataOrValueClassConstructor(): Boolean {
    val parent = parent as? KtClass ?: return false
    return parent.isData() || parent.isValue() || parent.isInline()
}

private fun KtClassOrObject.identifier(): Name = this.nameAsSafeName
private fun ClassDescriptor.identifier(): Name = this.name

private fun KtProperty.isMemberOrTopLevel() = isMember || isTopLevel

private fun ClassConstructorDescriptor.signature() = valueParameters.joinToString(",") {
    "${it.name}:${it.type}"
}

private fun KtConstructor<*>.signature() = getValueParameters().joinToString(",") {
    "${it.name}:${it.typeReference?.getTypeText()}"
}
