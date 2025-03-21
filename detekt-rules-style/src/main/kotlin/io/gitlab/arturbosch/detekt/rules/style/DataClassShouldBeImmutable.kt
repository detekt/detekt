package io.gitlab.arturbosch.detekt.rules.style

import com.intellij.psi.PsiElement
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.Rule
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
class DataClassShouldBeImmutable(config: Config) : Rule(
    config,
    "Data classes should mainly be immutable and should not have any side effects " +
        "(To copy an object altering some of its properties use the copy function)."
) {

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
            Finding(
                Entity.from(element),
                "The data class $className contains a mutable property. " +
                    "The offending property is called $propertyName"
            )
        )
    }
}
