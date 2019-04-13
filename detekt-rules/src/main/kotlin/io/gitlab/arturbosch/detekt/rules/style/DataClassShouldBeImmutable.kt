package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtProperty

/**
 * This rule reports mutable properties inside data classes.
 *
 * Data classes should mainly be used to store immutable data. This rule assumes that they should not contain any
 * mutable properties.
 *
 * <noncompliant>
 * data class MutableDataClass(var i: Int)
 * </noncompliant>
 *
 * <compliant>
 * data class ImmutableDataClass(val i: Int)
 * </compliant>
 *
 * @author Maxim Pestryakov
 */
class DataClassShouldBeImmutable(config: Config = Config.empty) : Rule(config) {

    override val issue: Issue = Issue(
        "DataClassShouldBeImmutable",
        Severity.Style,
        "Data classes should mainly be immutable and should not have any side effects. " +
                "(To change some property use copy function)",
        Debt.TWENTY_MINS
    )

    override fun visitClass(klass: KtClass) {
        if (klass.isData()) {
            klass.primaryConstructorParameters.forEach { checkParameter(klass, it) }
            klass.getProperties().forEach { checkProperty(klass, it) }
        }
        super.visitClass(klass)
    }

    private fun checkParameter(klass: KtClass, parameter: KtParameter) {
        if (parameter.isMutable) {
            report(
                CodeSmell(
                    issue, Entity.from(parameter),
                    "The data class ${klass.name} contains mutable parameter. " +
                            "The offending parameter is called ${parameter.name}"
                )
            )
        }
    }

    private fun checkProperty(klass: KtClass, property: KtProperty) {
        if (property.isVar) {
            report(
                CodeSmell(
                    issue, Entity.from(property),
                    "The data class ${klass.name} contains mutable property. " +
                            "The offending property is called ${property.name}"
                )
            )
        }
    }
}
