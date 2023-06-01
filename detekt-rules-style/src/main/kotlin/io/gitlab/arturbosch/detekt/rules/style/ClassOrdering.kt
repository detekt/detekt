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

private typealias DeclarationToSectionPair = Pair<KtDeclaration, Section>

/**
 * This rule ensures class contents are ordered as follows as recommended by the Kotlin
 * [Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html#class-layout):
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

        val sectionList = classBody.declarations.filterNotNull()
        if (sectionList.isEmpty()) return
        val (violationDeclarationToSectionPair, listOfIncreasingSection) = getMinimalNumberOfViolations(sectionList)
            ?: return
        violationDeclarationToSectionPair.forEach { (declaration, section) ->
            val (directionMsg, anchorSection) = listOfIncreasingSection.find { it.priority > section.priority }?.let {
                "before" to it
            } ?: run {
                "after" to listOfIncreasingSection.findLast { it.priority < section.priority }
            }
            anchorSection ?: return@forEach
            val message =
                "${declaration.toDescription()} should be declared $directionMsg ${anchorSection.toDescription()}."
            report(
                CodeSmell(
                    issue = issue,
                    entity = Entity.from(declaration),
                    message = message,
                    references = listOf(Entity.from(classBody))
                )
            )
        }
    }

    private fun getMinimalNumberOfViolations(
        declarations: List<KtDeclaration>,
    ): Pair<List<DeclarationToSectionPair>, List<Section>>? {
        val declarationWithSectionList = declarations.mapNotNull { declaration ->
            declaration.toSection()?.let {
                declaration to it
            }
        }
        val dp = IntArray(declarationWithSectionList.size) {
            return@IntArray 1
        }
        val backTrack = IntArray(declarationWithSectionList.size) {
            return@IntArray it
        }
        for (i in dp.indices) {
            for (j in 0 until i) {
                if (declarationWithSectionList[i].second.priority >= declarationWithSectionList[j].second.priority &&
                    dp[i] < dp[j] + 1
                ) {
                    dp[i] = dp[j] + 1
                    backTrack[i] = j
                }
            }
        }

        var index = dp.indices.maxByOrNull { dp[it] } ?: return null

        val listOfIncreasingSection = buildList {
            var oldIndex: Int
            do {
                add(declarationWithSectionList[index])
                oldIndex = index
                index = backTrack[index]
            } while (index != oldIndex)
        }.reversed()
        return declarationWithSectionList.minus(listOfIncreasingSection.toSet()) to
            listOfIncreasingSection.map { it.second }
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
    else -> null // For declarations not relevant for ordering, such as nested classes.
}

@Suppress("MagicNumber")
private class Section(val priority: Int) : Comparable<Section> {

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
