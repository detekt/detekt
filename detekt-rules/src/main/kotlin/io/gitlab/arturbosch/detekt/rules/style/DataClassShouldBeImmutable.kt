package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtClass

/**
 * This rule reports mutable properties inside data classes.
 *
 * Data classes should mainly be used to store immutable data. This rule assumes that they should not contain any
 * mutable properties.
 *
 * <noncompliant>
 * data class MutableDataClass(var i: Int) {
 *     var s: String? = null
 * }
 * </noncompliant>
 *
 * <compliant>
 * data class ImmutableDataClass(
 *     val i: Int,
 *     val s: String?
 * )
 * </compliant>
 */
class DataClassShouldBeImmutable(config: Config = Config.empty) : Rule(config) {

    override val issue: Issue = Issue(
        "DataClassShouldBeImmutable",
        Severity.Style,
        "Data classes should mainly be immutable and should not have any side effects. " +
                "(To copy an object altering some of its properties use the copy function)",
        Debt.TWENTY_MINS
    )

    override fun visitClass(klass: KtClass) {
        if (klass.isData()) {
            klass.primaryConstructorParameters
                .filter { it.isMutable }
                .forEach { report(it, klass.name, it.name) }

            klass.getProperties()
                .filter { it.isVar }
                .forEach { report(it, klass.name, it.name) }
        }
        super.visitClass(klass)
    }

    private fun report(element: PsiElement, className: String?, propertyName: String?) {
        report(
            CodeSmell(
                issue,
                Entity.from(element),
                "The data class $className contains a mutable property. " +
                        "The offending property is called $propertyName"
            )
        )
    }
}
