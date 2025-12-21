package dev.detekt.rules.style

import dev.detekt.api.ActiveByDefault
import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.Rule
import dev.detekt.psi.isOpen
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtClassInitializer
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtObjectDeclaration
import org.jetbrains.kotlin.psi.KtSecondaryConstructor
import org.jetbrains.kotlin.psi.psiUtil.isPublic

/**
 * A class which only contains utility variables and functions with no concrete implementation can be refactored
 * into an `object` or a class with a non-public constructor.
 * Furthermore, this rule reports utility classes which are not final.
 *
 * <noncompliant>
 * class UtilityClassViolation {
 *
 *     // public constructor here
 *     constructor() {
 *         // ...
 *     }
 *
 *     companion object {
 *         val i = 0
 *     }
 * }
 *
 * open class UtilityClassViolation private constructor() {
 *
 *     // ...
 * }
 * </noncompliant>
 *
 * <compliant>
 * class UtilityClass {
 *
 *     private constructor() {
 *         // ...
 *     }
 *
 *     companion object {
 *         val i = 0
 *     }
 * }
 * object UtilityClass {
 *
 *     val i = 0
 * }
 * </compliant>
 */
@ActiveByDefault(since = "1.2.0")
class UtilityClassWithPublicConstructor(config: Config) :
    Rule(
        config,
        "The class declaration is unnecessary because it only contains utility functions. " +
            "An object declaration should be used instead."
    ) {

    override fun visitClass(klass: KtClass) {
        if (canBeCheckedForUtilityClass(klass)) {
            val utilityClassConstructor = UtilityClassConstructor(klass)
            val declarations = klass.body?.declarations
            if (hasOnlyUtilityClassMembers(declarations)) {
                if (utilityClassConstructor.hasPublicConstructorWithoutParameters()) {
                    report(
                        Finding(
                            Entity.from(klass),
                            "The class ${klass.nameAsSafeName} only contains" +
                                " utility functions. Consider defining it as an object."
                        )
                    )
                } else if (klass.isOpen() && utilityClassConstructor.hasNonPublicConstructorWithoutParameters()) {
                    report(
                        Finding(
                            Entity.from(klass),
                            "The utility class ${klass.nameAsSafeName} should be final."
                        )
                    )
                }
            }
        }
        super.visitClass(klass)
    }

    private fun canBeCheckedForUtilityClass(klass: KtClass): Boolean =
        !klass.isInterface() &&
            !klass.superTypeListEntries.any() &&
            !klass.isAnnotation() &&
            !klass.isSealed()

    private fun hasOnlyUtilityClassMembers(declarations: List<KtDeclaration>?): Boolean {
        if (declarations.isNullOrEmpty()) {
            return false
        }
        var containsCompanionObject = false
        var isUtilityClassCandidate = true
        declarations.forEach {
            if (isCompanionObject(it)) {
                containsCompanionObject = true
            } else if (it !is KtSecondaryConstructor && it !is KtClassInitializer) {
                isUtilityClassCandidate = false
            }
        }
        return containsCompanionObject && isUtilityClassCandidate
    }

    private fun isCompanionObject(declaration: KtDeclaration) =
        (declaration as? KtObjectDeclaration)?.isCompanion() == true

    internal class UtilityClassConstructor(private val klass: KtClass) {

        internal fun hasPublicConstructorWithoutParameters() = hasConstructorWithoutParameters(true)

        internal fun hasNonPublicConstructorWithoutParameters() = hasConstructorWithoutParameters(false)

        private fun hasConstructorWithoutParameters(publicModifier: Boolean): Boolean {
            val primaryConstructor = klass.primaryConstructor
            if (primaryConstructor != null) {
                return primaryConstructor.isPublic == publicModifier && primaryConstructor.valueParameters.isEmpty()
            }
            val secondaryConstructors = klass.secondaryConstructors
            return secondaryConstructors.isEmpty() ||
                secondaryConstructors.any { it.isPublic == publicModifier && it.valueParameters.isEmpty() }
        }
    }
}
