package io.gitlab.arturbosch.detekt.rules.complexity

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Metric
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.ThresholdRule
import io.gitlab.arturbosch.detekt.api.ThresholdedCodeSmell
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtConstructor
import org.jetbrains.kotlin.psi.KtUserType

/**
 * Complex classes which reference too many other classes indicate that this class is handling too
 * many things at once. Classes should follow the single-responsibility principle to also encourage implementations
 * of this interface to not handle too many things at once.
 *
 * Tightly coupled classes should be split into smaller interfaces which have a clear responsibility and are easier
 * to understand and implement.
 *
 * @configuration threshold - the amount of class references to trigger the rule (default: `10`)
 */
class CoupledClass(
    config: Config = Config.empty,
    threshold: Int = DEFAULT_MAX_REFERENCED_CLASSES
) : ThresholdRule(config, threshold) {

    override val issue = Issue(
        javaClass.simpleName, Severity.Maintainability,
        "A class is coupled to too many other classes. " +
                "Coupled classes violate the Single Responsibility Principle. " +
                "A class should have one responsibility. " +
                "Split up large classes into smaller ones that are easier to understand.",
        Debt.TWENTY_MINS
    )

    override fun visitClass(klass: KtClass) {
        if (!klass.isData() && !klass.isEnum()) {
            val referencedClasses = mutableListOf<String?>()

            referencedClasses.addAll(fetchReferencedClasses(klass.primaryConstructor))
            referencedClasses.addAll(fetchReferencedClasses(klass.secondaryConstructors))

            val totalUniqueReferencedClasses = referencedClasses.toSet().size

            if (totalUniqueReferencedClasses >= threshold) {
                report(
                    ThresholdedCodeSmell(
                        issue,
                        Entity.from(klass),
                        Metric("SIZE: ", totalUniqueReferencedClasses, threshold),
                        "The class ${klass.name} is coupled to too many other classes. " +
                                "Consider splitting it up to the maximum authorized of $threshold or less."
                    )
                )
            }
        }

        super.visitClass(klass)
    }

    private fun fetchReferencedClasses(secondaryConstructors: List<KtConstructor<*>>): List<String?> {
        return secondaryConstructors.flatMap { fetchReferencedClasses(it) }
    }

    private fun fetchReferencedClasses(constructor: KtConstructor<*>?): List<String?> {
        if (constructor != null) {
            return constructor.getValueParameters()
                .filter { it.typeReference?.typeElement is KtUserType }
                .map {
                    (it.typeReference?.typeElement as KtUserType).referencedName
                }
        }

        return emptyList()
    }

    companion object {
        const val DEFAULT_MAX_REFERENCED_CLASSES = 10
    }
}
