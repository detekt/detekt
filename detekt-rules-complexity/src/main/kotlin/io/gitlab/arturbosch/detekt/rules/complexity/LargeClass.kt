package io.gitlab.arturbosch.detekt.rules.complexity

import io.github.detekt.metrics.linesOfCode
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Metric
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.ThresholdedCodeSmell
import io.gitlab.arturbosch.detekt.api.config
import io.gitlab.arturbosch.detekt.api.internal.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.internal.Configuration
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.psiUtil.getStrictParentOfType
import org.jetbrains.kotlin.utils.addToStdlib.flattenTo
import java.util.IdentityHashMap

/**
 * This rule reports large classes. Classes should generally have one responsibility. Large classes can indicate that
 * the class does instead handle multiple responsibilities. Instead of doing many things at once prefer to
 * split up large classes into smaller classes. These smaller classes are then easier to understand and handle less
 * things.
 */
@ActiveByDefault(since = "1.0.0")
class LargeClass(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue(
        "LargeClass",
        Severity.Maintainability,
        "One class should have one responsibility. Large classes tend to handle many things at once. " +
            "Split up large classes into smaller classes that are easier to understand.",
        Debt.TWENTY_MINS
    )

    @Configuration("the size of class required to trigger the rule")
    private val threshold: Int by config(defaultValue = 600)

    private val classToLinesCache = IdentityHashMap<KtClassOrObject, Int>()
    private val nestedClassTracking = IdentityHashMap<KtClassOrObject, HashSet<KtClassOrObject>>()

    override fun preVisit(root: KtFile) {
        classToLinesCache.clear()
        nestedClassTracking.clear()
    }

    override fun postVisit(root: KtFile) {
        for ((clazz, lines) in classToLinesCache) {
            if (lines >= threshold) {
                report(
                    ThresholdedCodeSmell(
                        issue,
                        Entity.atName(clazz),
                        Metric("SIZE", lines, threshold),
                        "Class ${clazz.name} is too large. Consider splitting it into smaller pieces."
                    )
                )
            }
        }
    }

    override fun visitClassOrObject(classOrObject: KtClassOrObject) {
        val lines = classOrObject.linesOfCode()
        classToLinesCache[classOrObject] = lines
        classOrObject.getStrictParentOfType<KtClassOrObject>()
            ?.let { nestedClassTracking.getOrPut(it) { HashSet() }.add(classOrObject) }
        super.visitClassOrObject(classOrObject)
        findAllNestedClasses(classOrObject)
            .fold(0) { acc, next -> acc + (classToLinesCache[next] ?: 0) }
            .takeIf { it > 0 }
            ?.let { classToLinesCache[classOrObject] = lines - it }
    }

    private fun findAllNestedClasses(startClass: KtClassOrObject): Sequence<KtClassOrObject> = sequence {
        var nestedClasses = nestedClassTracking[startClass]
        while (!nestedClasses.isNullOrEmpty()) {
            yieldAll(nestedClasses)
            nestedClasses = nestedClasses.mapNotNull { nestedClassTracking[it] }.flattenTo(HashSet())
        }
    }
}
