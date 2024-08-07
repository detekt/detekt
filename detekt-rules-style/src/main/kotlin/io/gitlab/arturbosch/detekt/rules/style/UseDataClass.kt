package io.gitlab.arturbosch.detekt.rules.style

import io.github.detekt.psi.AnnotationExcluder
import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Configuration
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.RequiresTypeResolution
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.config
import io.gitlab.arturbosch.detekt.rules.isOpen
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtSuperTypeListEntry
import org.jetbrains.kotlin.psi.psiUtil.forEachDescendantOfType
import org.jetbrains.kotlin.psi.psiUtil.isAbstract
import org.jetbrains.kotlin.psi.psiUtil.isPrivate
import org.jetbrains.kotlin.psi.psiUtil.isPropertyParameter
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.types.KotlinType

/**
 * Classes that simply hold data should be refactored into a `data class`. Data classes are specialized to hold data
 * and generate `hashCode`, `equals` and `toString` implementations as well.
 *
 * Read more about [data classes](https://kotlinlang.org/docs/data-classes.html)
 *
 * <noncompliant>
 * class DataClassCandidate(val i: Int) {
 *     val i2: Int = 0
 * }
 * </noncompliant>
 *
 * <compliant>
 * data class DataClass(val i: Int, val i2: Int)
 *
 * // classes with delegating interfaces are compliant
 * interface I
 * class B() : I
 * class A(val b: B) : I by b
 * </compliant>
 */
class UseDataClass(config: Config) :
    Rule(
        config,
        "Classes that do nothing but hold data should be replaced with a data class."
    ),
    RequiresTypeResolution {
    @Configuration("allows to provide a list of annotations that disable this check")
    @Deprecated("Use `ignoreAnnotated` instead")
    private val excludeAnnotatedClasses: List<Regex> by config(emptyList<String>()) { list ->
        list.map { it.replace(".", "\\.").replace("*", ".*").toRegex() }
    }

    @Configuration("allows to relax this rule in order to exclude classes that contains one (or more) vars")
    private val allowVars: Boolean by config(false)

    override fun visit(root: KtFile) {
        super.visit(root)
        val annotationExcluder = AnnotationExcluder(
            root,
            @Suppress("DEPRECATION") excludeAnnotatedClasses,
            bindingContext,
        )
        root.forEachDescendantOfType<KtClass> { visitKlass(it, annotationExcluder) }
    }

    @Suppress("ComplexMethod")
    private fun visitKlass(klass: KtClass, annotationExcluder: AnnotationExcluder) {
        if (isIncorrectClassType(klass) || hasOnlyPrivateConstructors(klass)) {
            return
        }
        if (klass.isClosedForExtension() &&
            klass.onlyExtendsSimpleInterfaces() &&
            !annotationExcluder.shouldExclude(klass.annotationEntries)
        ) {
            val declarations = klass.body?.declarations.orEmpty()
            val properties = declarations.filterIsInstance<KtProperty>()
            val functions = declarations.filterIsInstance<KtNamedFunction>()

            val propertyParameters = klass.extractConstructorPropertyParameters()

            val primaryConstructor = bindingContext[BindingContext.CONSTRUCTOR, klass.primaryConstructor]
            val primaryConstructorParameterTypes = primaryConstructor?.valueParameters?.map { it.type }.orEmpty()
            val classType = primaryConstructor?.containingDeclaration?.defaultType
            val containsFunctions = functions.all { it.isDefaultFunction(classType, primaryConstructorParameterTypes) }
            val containsPropertyOrPropertyParameters = properties.isNotEmpty() || propertyParameters.isNotEmpty()
            val containsVars = properties.any { it.isVar } || propertyParameters.any { it.isMutable }
            val containsDelegatedProperty = properties.any { it.hasDelegate() }
            val containsNonPropertyParameter = klass.extractConstructorNonPropertyParameters().isNotEmpty()
            val containsOnlyPropertyParameters = containsPropertyOrPropertyParameters && !containsNonPropertyParameter

            if (containsFunctions && !containsDelegatedProperty && containsOnlyPropertyParameters) {
                if (allowVars && containsVars) {
                    return
                }
                report(
                    CodeSmell(
                        Entity.atName(klass),
                        "The class ${klass.nameAsSafeName} defines no " +
                            "functionality and only holds data. Consider converting it to a data class."
                    )
                )
            }
        }
    }

    private fun KtClass.isClosedForExtension(): Boolean = !isAbstract() && !isOpen()

    private fun KtClass.onlyExtendsSimpleInterfaces(): Boolean =
        superTypeListEntries.all { it.isInterfaceInSameFile() && " by " !in it.text }

    private fun KtSuperTypeListEntry.isInterfaceInSameFile(): Boolean {
        val matchingDeclaration = containingKtFile.declarations
            .firstOrNull { it.name == typeAsUserType?.referencedName }
        return matchingDeclaration is KtClass && matchingDeclaration.isInterface()
    }

    private fun isIncorrectClassType(klass: KtClass) =
        klass.isData() ||
            klass.isEnum() ||
            klass.isAnnotation() ||
            klass.isSealed() ||
            klass.isInline() ||
            klass.isValue() ||
            klass.isInner()

    private fun hasOnlyPrivateConstructors(klass: KtClass): Boolean {
        val primaryConstructor = klass.primaryConstructor
        return (primaryConstructor == null || primaryConstructor.isPrivate()) &&
            klass.secondaryConstructors.all { it.isPrivate() }
    }

    private fun KtClass.extractConstructorPropertyParameters(): List<KtParameter> =
        getPrimaryConstructorParameterList()
            ?.parameters
            ?.filter { it.isPropertyParameter() }
            .orEmpty()

    private fun KtClass.extractConstructorNonPropertyParameters(): List<KtParameter> =
        getPrimaryConstructorParameterList()
            ?.parameters
            ?.filter { !it.isPropertyParameter() }
            .orEmpty()

    private fun KtNamedFunction.isDefaultFunction(
        classType: KotlinType?,
        primaryConstructorParameterTypes: List<KotlinType>
    ): Boolean =
        when (name) {
            !in DEFAULT_FUNCTION_NAMES -> false
            "copy" -> {
                if (classType != null) {
                    val descriptor = bindingContext[BindingContext.FUNCTION, this]
                    val returnType = descriptor?.returnType
                    val parameterTypes = descriptor?.valueParameters?.map { it.type }.orEmpty()
                    returnType == classType &&
                        parameterTypes.size == primaryConstructorParameterTypes.size &&
                        parameterTypes.zip(primaryConstructorParameterTypes).all { it.first == it.second }
                } else {
                    true
                }
            }
            else -> true
        }

    companion object {
        private val DEFAULT_FUNCTION_NAMES = hashSetOf("hashCode", "equals", "toString", "copy")
    }
}
