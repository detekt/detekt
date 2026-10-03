package dev.detekt.rules.potentialbugs

import dev.detekt.api.Config
import dev.detekt.api.Configuration
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.Rule
import dev.detekt.api.config
import dev.detekt.psi.isEqualsFunction
import dev.detekt.psi.isHashCodeFunction
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.psiUtil.collectDescendantsOfType
import org.jetbrains.kotlin.psi.psiUtil.isPropertyParameter

/**
 * When a class overrides both equals() and hashCode(), both methods should typically use the
 * same set of properties. Using fields in hashCode() that are not compared in equals() or vice versa
 * can lead to bugs where equal objects have different hash codes, or unequal objects have identical hash codes.
 *
 * <noncompliant>
 * class Person(val name: String, val age: Int) {
 *     override fun equals(other: Any?): Boolean {
 *         if (this === other) return true
 *         if (other !is Person) return false
 *         return name == other.name && age == other.age
 *     }
 *
 *     override fun hashCode(): Int {
 *         return name.hashCode()
 *     }
 * }
 * </noncompliant>
 *
 * <compliant>
 * class Person(val name: String, val age: Int) {
 *     override fun equals(other: Any?): Boolean {
 *         if (this === other) return true
 *         if (other !is Person) return false
 *         return name == other.name && age == other.age
 *     }
 *
 *     override fun hashCode(): Int {
 *         return name.hashCode() * 31 + age.hashCode()
 *     }
 * }
 * </compliant>
 */
class EqualsWithHashCodeSameFields(config: Config) :
    Rule(
        config,
        "Classes overriding equals() and hashCode() should use consistent properties across both methods."
    ) {

    @Configuration("Whether hashCode() is allowed to use a subset of properties used in equals().")
    private val allowHashCodeSubset: Boolean by config(false)

    override fun visitClass(klass: KtClass) {
        if (klass.isData()) return

        val functions = klass.body?.functions.orEmpty()
        val equalsFunction = functions.firstOrNull { it.isEqualsFunction() }
        val hashCodeFunction = functions.firstOrNull { it.isHashCodeFunction() }

        if (equalsFunction != null && hashCodeFunction != null) {
            val constructorProps = klass.primaryConstructorParameters
                .filter { it.isPropertyParameter() }
                .mapNotNull { it.name }
            val memberProps = klass.getProperties().mapNotNull { it.name }
            val classPropertyNames = (constructorProps + memberProps).toSet()

            if (classPropertyNames.isNotEmpty()) {
                val equalsFields = extractReferencedFields(equalsFunction, classPropertyNames)
                val hashCodeFields = extractReferencedFields(hashCodeFunction, classPropertyNames)

                val missingInHashCode = equalsFields - hashCodeFields
                val missingInEquals = hashCodeFields - equalsFields

                val message = when {
                    missingInEquals.isNotEmpty() && (!allowHashCodeSubset && missingInHashCode.isNotEmpty()) -> {
                        "Properties used in equals() and hashCode() do not match. " +
                            "Missing in hashCode(): ${missingInHashCode.sorted().joinToString(", ")}; " +
                            "missing in equals(): ${missingInEquals.sorted().joinToString(", ")}."
                    }

                    missingInEquals.isNotEmpty() -> {
                        "Properties used in hashCode() are missing in equals(): " +
                            "${missingInEquals.sorted().joinToString(", ")}."
                    }

                    !allowHashCodeSubset && missingInHashCode.isNotEmpty() -> {
                        "Properties used in equals() are missing in hashCode(): " +
                            "${missingInHashCode.sorted().joinToString(", ")}."
                    }

                    else -> null
                }

                if (message != null) {
                    report(Finding(Entity.atName(klass), message))
                }
            }
        }

        super.visitClass(klass)
    }

    private fun extractReferencedFields(function: KtNamedFunction, classProperties: Set<String>): Set<String> {
        val body = function.bodyExpression ?: return emptySet()
        return body.collectDescendantsOfType<KtNameReferenceExpression> {
            it.getReferencedName() in classProperties
        }.map { it.getReferencedName() }.toSet()
    }
}
