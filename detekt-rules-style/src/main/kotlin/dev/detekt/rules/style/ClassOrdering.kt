package dev.detekt.rules.style

import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.Rule
import org.jetbrains.kotlin.psi.KtClassBody
import org.jetbrains.kotlin.psi.KtClassInitializer
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtObjectDeclaration
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtSecondaryConstructor

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
class ClassOrdering(config: Config) :
    Rule(
        config,
        "Class contents should be in this order: Property declarations/initializer blocks; secondary constructors; " +
            "method declarations then companion objects."
    ) {

    override fun visitClassBody(classBody: KtClassBody) {
        super.visitClassBody(classBody)

        val declarations = classBody.declarations.filterNotNull()
        if (declarations.isEmpty()) return
        val (violatingDeclarationWithSections, increasingDeclarationWithSections) = getMinimalNumberOfViolations(
            declarations
        ) ?: return
        violatingDeclarationWithSections.forEach { (violatingDeclaration, violatingSection) ->
            val increasingDeclarationsBeforeViolatingElement =
                declarations.takeWhile { it != violatingDeclaration }
            val increasingDeclarationSectionBeforeViolatingElement =
                increasingDeclarationWithSections.takeWhile {
                    it.declaration in increasingDeclarationsBeforeViolatingElement
                }
            // for finding section from which violatingSection should be before we are only
            // taking declarations which is already before the violatingSection
            val (directionMsg, anchorSection) = increasingDeclarationSectionBeforeViolatingElement
                .find {
                    it.section.priority > violatingSection.priority
                }
                ?.let {
                    "before" to it
                }
                ?: run {
                    "after" to
                        increasingDeclarationWithSections
                            .findLast { it.section.priority < violatingSection.priority }
                }
            anchorSection ?: return@forEach
            val message =
                "${violatingDeclaration.toDescription()} should be declared $directionMsg " +
                    "${anchorSection.section.toDescription()}."
            report(
                Finding(
                    entity = Entity.from(violatingDeclaration),
                    message = message,
                    references = listOf(Entity.from(classBody))
                )
            )
        }
    }

    private fun getMinimalNumberOfViolations(
        declarations: List<KtDeclaration>,
    ): Pair<List<DeclarationWithSection>, List<DeclarationWithSection>>? {
        val declarationWithSectionList = declarations.mapNotNull { declaration ->
            declaration.toSection()?.let {
                DeclarationWithSection(
                    declaration,
                    it
                )
            }
        }
        val dp = IntArray(declarationWithSectionList.size) {
            return@IntArray 1
        }
        val backTrack = IntArray(declarationWithSectionList.size) {
            return@IntArray it
        }
        for (i in dp.indices) {
            for (j in 0..<i) {
                if (declarationWithSectionList[i].section.priority >=
                    declarationWithSectionList[j].section.priority &&
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
            listOfIncreasingSection
    }

    private data class DeclarationWithSection(val declaration: KtDeclaration, val section: Section)
}

private fun KtDeclaration.toDescription(): String =
    when {
        this is KtProperty -> "property `$name`"
        this is KtClassInitializer -> "initializer blocks"
        this is KtSecondaryConstructor -> "secondary constructor"
        this is KtNamedFunction -> "method `$name()`"
        this is KtObjectDeclaration && isCompanion() -> "companion object"
        else -> ""
    }

@Suppress("MagicNumber")
private fun KtDeclaration.toSection(): Section? =
    when {
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

    fun toDescription(): String =
        when (priority) {
            0 -> "property declarations and initializer blocks"
            1 -> "secondary constructors"
            2 -> "method declarations"
            3 -> "companion object"
            else -> ""
        }

    override fun compareTo(other: Section): Int = priority.compareTo(other.priority)
}
