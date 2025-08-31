package dev.detekt.rules.style

import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.Location
import dev.detekt.api.Rule
import dev.detekt.api.TextLocation
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.KtAnnotationUseSiteTarget
import org.jetbrains.kotlin.psi.KtPrimaryConstructor
import org.jetbrains.kotlin.psi.KtProperty

/**
 * This rule inspects the use of the Annotation use-site Target. In case that the use-site Target is not needed it can
 * be removed. For more information check the kotlin documentation:
 * [Annotation use-site targets](https://kotlinlang.org/docs/annotations.html#annotation-use-site-targets)
 *
 * <noncompliant>
 * @@property:Inject private val foo: String = "bar" // violation: unnecessary @property:
 *
 * class Module(@param:Inject private val foo: String) // violation: unnecessary @param:
 * </noncompliant>
 *
 * <compliant>
 * class Module(@Inject private val foo: String)
 * </compliant>
 */
class UnnecessaryAnnotationUseSiteTarget(config: Config) : Rule(
    config,
    "Unnecessary Annotation use-site Target. It can be removed."
) {

    override fun visitPrimaryConstructor(constructor: KtPrimaryConstructor) {
        constructor.valueParameters.forEach { parameter ->
            checkForUnnecessaryUseSiteTarget(parameter.annotationEntries, UseSiteTarget.PARAM)
        }
        super.visitPrimaryConstructor(constructor)
    }

    override fun visitProperty(property: KtProperty) {
        checkForUnnecessaryUseSiteTarget(property.annotationEntries, UseSiteTarget.PROPERTY)
        super.visitProperty(property)
    }

    private fun checkForUnnecessaryUseSiteTarget(annotations: List<KtAnnotationEntry>, useSiteTarget: UseSiteTarget) {
        annotations.forEach { annotationEntry ->
            val useSite = annotationEntry.useSiteTarget
            if (useSite != null && useSite.text == useSiteTarget.useSiteTarget) {
                report(useSite, useSiteTarget.message)
            }
        }
    }

    private fun report(useSite: KtAnnotationUseSiteTarget, message: String) {
        val location = Location.from(useSite).let { location ->
            Location(
                location.source,
                location.endSource,
                TextLocation(location.text.start, location.text.end + 1),
                location.path
            )
        }
        report(Finding(Entity.from(useSite, location), message))
    }

    private enum class UseSiteTarget(val useSiteTarget: String, val message: String) {
        PARAM(
            "param",
            "An annotation over a parameter doesn't need the use-site target @param."
        ),
        PROPERTY(
            "property",
            "An annotation over a property, that it's not a parameter, doesn't need the use-site target @property."
        ),
    }
}
