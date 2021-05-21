package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.psi.KtClassBody
import org.jetbrains.kotlin.psi.KtClassInitializer
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtObjectDeclaration
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtSecondaryConstructor

/**
 * This rule ensures class contents are ordered as follows as recommended by the Kotlin
 * [Coding Conventions](https://kotlinlang.org/docs/reference/coding-conventions.html#class-layout):
 * - Property declarations and initializer blocks
 * - Secondary constructors
 * - Method declarations
 * - Companion object
 *
 * <noncompliant>
 * class OutOfOrder {
 *     companion object {
 *         const val IMPORTANT_VALUE = 3
 *     }
 *
 *     fun returnX(): Int {
 *         return x
 *     }
 *
 *     private val x = 2
 * }
 * </noncompliant>
 *
 * <compliant>
 * class InOrder {
 *     private val x = 2
 *
 *     fun returnX(): Int {
 *         return x
 *     }
 *
 *     companion object {
 *         const val IMPORTANT_VALUE = 3
 *     }
 * }
 * </compliant>
 */
class ClassOrdering(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue(
        javaClass.simpleName,
        Severity.Style,
        "Class contents should be in this order: Property declarations/initializer blocks; secondary constructors; " +
            "method declarations then companion objects.",
        Debt.FIVE_MINS
    )

    override fun visitClassBody(classBody: KtClassBody) {
        super.visitClassBody(classBody)

        var currentSection = Section(0)
        classBody.declarations.forEach { ktDeclaration ->
            val section = ktDeclaration.toSection()
            when {
                section != null && section < currentSection -> {
                    val message =
                        "${ktDeclaration.toDescription()} should be declared before ${currentSection.toDescription()}."
                    report(
                        CodeSmell(
                            issue = issue,
                            entity = Entity.from(ktDeclaration),
                            message = message,
                            references = listOf(Entity.from(classBody))
                        )
                    )
                }
                section != null && section > currentSection -> currentSection = section
            }
        }
    }
}

private fun KtDeclaration.toDescription(): String = when {
    this is KtProperty -> "property `$name`"
    this is KtClassInitializer -> "initializer blocks"
    this is KtSecondaryConstructor -> "secondary constructor"
    this is KtNamedFunction -> "method `$name()`"
    this is KtObjectDeclaration && isCompanion() -> "companion object"
    else -> ""
}

@Suppress("MagicNumber")
private fun KtDeclaration.toSection(): Section? = when {
    this is KtProperty -> Section(0)
    this is KtClassInitializer -> Section(0)
    this is KtSecondaryConstructor -> Section(1)
    this is KtNamedFunction -> Section(2)
    this is KtObjectDeclaration && isCompanion() -> Section(3)
    else -> null
}

@JvmInline
@Suppress("MagicNumber", "ModifierOrder") // TODO Remove ModifierOrder once value class is supported.
private value class Section(val priority: Int) : Comparable<Section> {

    init {
        require(priority in 0..3)
    }

    fun toDescription(): String = when (priority) {
        0 -> "property declarations and initializer blocks"
        1 -> "secondary constructors"
        2 -> "method declarations"
        3 -> "companion object"
        else -> ""
    }

    override fun compareTo(other: Section): Int = priority.compareTo(other.priority)
}
