package dev.detekt.rules.style

import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import org.jetbrains.kotlin.analysis.api.KaExperimentalApi
import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.analysis.api.components.annotationApplicableTargets
import org.jetbrains.kotlin.analysis.api.symbols.KaClassSymbol
import org.jetbrains.kotlin.analysis.api.symbols.KaPropertySymbol
import org.jetbrains.kotlin.analysis.api.symbols.KaValueParameterSymbol
import org.jetbrains.kotlin.analysis.api.types.symbol
import org.jetbrains.kotlin.config.LanguageFeature
import org.jetbrains.kotlin.descriptors.annotations.AnnotationUseSiteTarget
import org.jetbrains.kotlin.descriptors.annotations.KotlinTarget
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.psiUtil.containingClassOrObject

/**
 * Suggests using the `all` use-site target when the same annotation is manually applied to every
 * applicable target of a property.
 *
 * <noncompliant>
 * @@Target(
 *     AnnotationTarget.VALUE_PARAMETER,
 *     AnnotationTarget.PROPERTY,
 *     AnnotationTarget.FIELD,
 *     AnnotationTarget.PROPERTY_GETTER,
 * )
 * annotation class Marker
 *
 * class Example(
 *     @@param:Marker
 *     @@property:Marker
 *     @@field:Marker
 *     @@get:Marker
 *     val value: String,
 * )
 * </noncompliant>
 *
 * <compliant>
 * @@Target(
 *     AnnotationTarget.VALUE_PARAMETER,
 *     AnnotationTarget.PROPERTY,
 *     AnnotationTarget.FIELD,
 *     AnnotationTarget.PROPERTY_GETTER,
 * )
 * annotation class Marker
 *
 * class Example(
 *     @@all:Marker val value: String,
 * )
 * </compliant>
 */
@OptIn(KaExperimentalApi::class)
class CollapseAnnotationUseSiteTargets(config: Config) :
    Rule(
        config,
        "Use the `all` use-site target instead of listing every applicable property target."
    ),
    RequiresAnalysisApi {

    override fun visitProperty(property: KtProperty) {
        super.visitProperty(property)
        if (!languageVersionSettings.supportsFeature(LanguageFeature.AnnotationAllUseSiteTarget)) return
        if (property.hasDelegate()) return

        analyze(property) {
            val propertySymbol = property.symbol as? KaPropertySymbol ?: return
            check(property, propertySymbol, isConstructorParameter = false, isMutable = property.isVar)
        }
    }

    override fun visitParameter(parameter: KtParameter) {
        super.visitParameter(parameter)
        if (!languageVersionSettings.supportsFeature(LanguageFeature.AnnotationAllUseSiteTarget)) return
        if (!parameter.hasValOrVar()) return

        analyze(parameter) {
            val parameterSymbol = parameter.symbol as? KaValueParameterSymbol ?: return
            val propertySymbol = parameterSymbol.generatedPrimaryConstructorProperty ?: return
            check(parameter, propertySymbol, isConstructorParameter = true, isMutable = parameter.isMutable)
        }
    }

    private fun KaSession.check(
        declaration: KtDeclaration,
        propertySymbol: KaPropertySymbol,
        isConstructorParameter: Boolean,
        isMutable: Boolean,
    ) {
        if (isInJvmRecord(declaration)) return

        val annotations = declaration.annotationEntries.mapNotNull { resolvedAnnotation(it) }
        annotations.groupBy { AnnotationKey(it.classId, it.arguments) }.values.forEach { group ->
            val annotationClass = group.first().annotationClass
            val applicableTargets = annotationClass.annotationApplicableTargets ?: return@forEach
            val propagatedTargets = propagatedTargets(
                applicableTargets,
                propertySymbol,
                isConstructorParameter,
                isMutable,
            )
            val explicitTargets = group.mapNotNull { it.useSiteTarget }.toSet()
            if (
                propagatedTargets.size > 1 &&
                explicitTargets.size == group.size &&
                explicitTargets == propagatedTargets
            ) {
                val annotationName = group.first().classId.shortClassName.asString()
                report(
                    Finding(
                        Entity.from(group.first().entry),
                        "Annotation `$annotationName` manually targets every applicable property site. " +
                            "Replace these annotations with `@all:$annotationName`.",
                    )
                )
            }
        }
    }

    private fun propagatedTargets(
        applicableTargets: Set<KotlinTarget>,
        propertySymbol: KaPropertySymbol,
        isConstructorParameter: Boolean,
        isMutable: Boolean,
    ): Set<AnnotationUseSiteTarget> =
        buildSet {
            if (KotlinTarget.PROPERTY in applicableTargets) add(AnnotationUseSiteTarget.PROPERTY)
            if (KotlinTarget.FIELD in applicableTargets && propertySymbol.hasBackingField) {
                add(AnnotationUseSiteTarget.FIELD)
            }
            if (KotlinTarget.PROPERTY_GETTER in applicableTargets && propertySymbol.hasGetter) {
                add(AnnotationUseSiteTarget.PROPERTY_GETTER)
            }
            if (KotlinTarget.VALUE_PARAMETER in applicableTargets && isConstructorParameter) {
                add(AnnotationUseSiteTarget.CONSTRUCTOR_PARAMETER)
            }
            if (KotlinTarget.VALUE_PARAMETER in applicableTargets && isMutable && propertySymbol.hasSetter) {
                add(AnnotationUseSiteTarget.SETTER_PARAMETER)
            }
        }

    private fun KaSession.resolvedAnnotation(entry: KtAnnotationEntry): ResolvedAnnotation? {
        val annotationClass = entry.typeReference?.type?.symbol as? KaClassSymbol ?: return null
        val classId = annotationClass.classId ?: return null
        val useSiteTarget = entry.useSiteTarget?.getAnnotationUseSiteTarget()
        return ResolvedAnnotation(
            entry = entry,
            annotationClass = annotationClass,
            classId = classId,
            arguments = entry.valueArgumentList?.text.orEmpty(),
            useSiteTarget = useSiteTarget,
        )
    }

    private fun KaSession.isInJvmRecord(declaration: KtDeclaration): Boolean =
        declaration.containingClassOrObject?.annotationEntries?.any {
            it.typeReference?.type?.symbol?.classId == jvmRecordClassId
        } == true

    private data class AnnotationKey(val classId: ClassId, val arguments: String)

    private data class ResolvedAnnotation(
        val entry: KtAnnotationEntry,
        val annotationClass: KaClassSymbol,
        val classId: ClassId,
        val arguments: String,
        val useSiteTarget: AnnotationUseSiteTarget?,
    )

    private companion object {
        val jvmRecordClassId: ClassId = ClassId.topLevel(FqName("kotlin.jvm.JvmRecord"))
    }
}
