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
        javaClass.simpleName, Severity.Style,
        "Class contents should be in this order: Property declarations/initializer blocks; secondary constructors; " +
                "method declarations then companion objects.",
        Debt.FIVE_MINS
    )

    private val lengthComparator: Comparator<KtDeclaration> = Comparator { str1: KtDeclaration, str2: KtDeclaration ->
        if (orderPriority(str1) == null || orderPriority(str2) == null) return@Comparator 0
        compareValues(orderPriority(str1), orderPriority(str2))
    }

    override fun visitClassBody(classBody: KtClassBody) {
        super.visitClassBody(classBody)

        if (!classBody.declarations.isInOrder(lengthComparator)) {
            report(CodeSmell(issue, Entity.from(classBody), issue.description))
        }
    }

    @Suppress("MagicNumber")
    private fun orderPriority(declaration: KtDeclaration): Int? {
        return when (declaration) {
            is KtProperty -> 0
            is KtClassInitializer -> 0
            is KtSecondaryConstructor -> 1
            is KtNamedFunction -> 2
            is KtObjectDeclaration -> if (declaration.isCompanion()) 3 else null
            else -> null
        }
    }

    private fun Iterable<KtDeclaration>.isInOrder(comparator: Comparator<KtDeclaration>): Boolean {
        zipWithNext { a, b -> if (comparator.compare(a, b) > 0) return false }
        return true
    }
}
