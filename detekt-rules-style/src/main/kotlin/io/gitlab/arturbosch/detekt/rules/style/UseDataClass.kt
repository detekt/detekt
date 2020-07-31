package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.AnnotationExcluder
import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.internal.valueOrDefaultCommaSeparated
import io.gitlab.arturbosch.detekt.rules.isInline
import io.gitlab.arturbosch.detekt.rules.isOpen
import org.jetbrains.kotlin.descriptors.ClassConstructorDescriptor
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
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
 * Read more about `data class`: https://kotlinlang.org/docs/reference/data-classes.html
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
 *
 * @configuration excludeAnnotatedClasses - allows to provide a list of annotations that disable this check
 * (default: `[]`)
 * @configuration allowVars - allows to relax this rule in order to exclude classes that contains one (or more) Vars (default: `false`)
 */
class UseDataClass(config: Config = Config.empty) : Rule(config) {

    override val issue: Issue = Issue("UseDataClass",
            Severity.Style,
            "Classes that do nothing but hold data should be replaced with a data class.",
            Debt.FIVE_MINS)

    private val excludeAnnotatedClasses = valueOrDefaultCommaSeparated(EXCLUDE_ANNOTATED_CLASSES, emptyList())
        .map { it.removePrefix("*").removeSuffix("*") }
    private val defaultFunctionNames = hashSetOf("hashCode", "equals", "toString", "copy")
    private val allowVars = valueOrDefault(ALLOW_VARS, false)

    override fun visit(root: KtFile) {
        super.visit(root)
        val annotationExcluder = AnnotationExcluder(root, excludeAnnotatedClasses)
        root.forEachDescendantOfType<KtClass> { visitKlass(it, annotationExcluder) }
    }

    @Suppress("ComplexMethod")
    private fun visitKlass(klass: KtClass, annotationExcluder: AnnotationExcluder) {
        if (isIncorrectClassType(klass) || hasOnlyPrivateConstructors(klass)) {
            return
        }
        if (klass.isClosedForExtension() && klass.onlyExtendsSimpleInterfaces() &&
            !annotationExcluder.shouldExclude(klass.annotationEntries)) {
            val declarations = klass.body?.declarations.orEmpty()
            val properties = declarations.filterIsInstance<KtProperty>()
            val functions = declarations.filterIsInstance<KtNamedFunction>()

            val propertyParameters = klass.extractConstructorPropertyParameters()

            val primaryConstructor = bindingContext[BindingContext.DECLARATION_TO_DESCRIPTOR, klass.primaryConstructor]
                    as? ClassConstructorDescriptor
            val primaryConstructorParameterTypes = primaryConstructor?.valueParameters?.map { it.type }.orEmpty()
            val classType = primaryConstructor?.containingDeclaration?.defaultType
            val containsFunctions = functions.all { it.isDefaultFunction(classType, primaryConstructorParameterTypes) }
            val containsPropertyOrPropertyParameters = properties.isNotEmpty() || propertyParameters.isNotEmpty()
            val containsVars = properties.any { it.isVar } || propertyParameters.any { it.isMutable }
            val containsDelegatedProperty = properties.any { it.hasDelegate() }

            if (containsFunctions && containsPropertyOrPropertyParameters && !containsDelegatedProperty) {
                if (allowVars && containsVars) {
                    return
                }
                report(
                    CodeSmell(
                        issue, Entity.from(klass), "The class ${klass.nameAsSafeName} defines no " +
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
                    klass.isInline()

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

    private fun KtNamedFunction.isDefaultFunction(
        classType: KotlinType?,
        primaryConstructorParameterTypes: List<KotlinType>
    ): Boolean {
        return when (name) {
            !in defaultFunctionNames -> false
            "copy" -> {
                if (classType != null) {
                    val descriptor =
                        bindingContext[BindingContext.DECLARATION_TO_DESCRIPTOR, this] as? FunctionDescriptor
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
    }

    companion object {
        const val ALLOW_VARS = "allowVars"
        const val EXCLUDE_ANNOTATED_CLASSES = "excludeAnnotatedClasses"
    }
}
