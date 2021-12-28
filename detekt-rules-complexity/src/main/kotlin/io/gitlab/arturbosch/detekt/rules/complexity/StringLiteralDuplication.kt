package io.gitlab.arturbosch.detekt.rules.complexity

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.DetektVisitor
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Metric
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.ThresholdedCodeSmell
import io.gitlab.arturbosch.detekt.api.config
import io.gitlab.arturbosch.detekt.api.internal.Configuration
import io.gitlab.arturbosch.detekt.rules.isPartOf
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtStringTemplateExpression
import org.jetbrains.kotlin.psi.psiUtil.plainContent

/**
 * This rule detects and reports duplicated String literals. Repeatedly typing out the same String literal across the
 * codebase makes it harder to change and maintain.
 *
 * Instead, prefer extracting the String literal into a property or constant.
 *
 * <noncompliant>
 * class Foo {
 *
 *     val s1 = "lorem"
 *     fun bar(s: String = "lorem") {
 *         s1.equals("lorem")
 *     }
 * }
 * </noncompliant>
 *
 * <compliant>
 * class Foo {
 *     val lorem = "lorem"
 *     val s1 = lorem
 *     fun bar(s: String = lorem) {
 *         s1.equals(lorem)
 *     }
 * }
 * </compliant>
 */
class StringLiteralDuplication(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue(
        javaClass.simpleName,
        Severity.Maintainability,
        "Multiple occurrences of the same string literal within a single file detected. " +
            "Prefer extracting the string literal into a property or constant.",
        Debt.FIVE_MINS
    )

    @Configuration("amount of duplications to trigger rule")
    private val threshold: Int by config(defaultValue = 3)

    @Configuration("if values in Annotations should be ignored")
    private val ignoreAnnotation: Boolean by config(true)

    @Configuration("if short strings should be excluded")
    private val excludeStringsWithLessThan5Characters: Boolean by config(true)

    @Configuration("RegEx of Strings that should be ignored")
    private val ignoreStringsRegex: Regex by config("$^", String::toRegex)

    override fun visitKtFile(file: KtFile) {
        val visitor = StringLiteralVisitor()
        file.accept(visitor)
        val type = "SIZE: "
        for ((name, value) in visitor.getLiteralsOverThreshold()) {
            val (main, references) = visitor.entitiesForLiteral(name)
            report(
                ThresholdedCodeSmell(
                    issue,
                    main,
                    Metric(type + name, value, threshold),
                    issue.description,
                    references
                )
            )
        }
    }

    internal inner class StringLiteralVisitor : DetektVisitor() {

        private var literals = HashMap<String, Int>()
        private var literalReferences = HashMap<String, MutableList<KtStringTemplateExpression>>()
        private val pass: Unit = Unit

        fun getLiteralsOverThreshold(): Map<String, Int> = literals.filterValues { it >= threshold }
        fun entitiesForLiteral(literal: String): Pair<Entity, List<Entity>> {
            val references = literalReferences[literal]
            if (references != null && references.isNotEmpty()) {
                val mainEntity = references[0]
                val referenceEntities = references.subList(1, references.size)
                return Entity.from(mainEntity) to referenceEntities.map { Entity.from(it) }
            }
            error("No KtElements for literal '$literal' found!")
        }

        override fun visitStringTemplateExpression(expression: KtStringTemplateExpression) {
            val text = expression.plainContent
            when {
                ignoreAnnotation && expression.isPartOf<KtAnnotationEntry>() -> pass
                excludeStringsWithLessThan5Characters && text.length < STRING_EXCLUSION_LENGTH -> pass
                text.matches(ignoreStringsRegex) -> pass
                else -> add(expression)
            }
        }

        private fun add(str: KtStringTemplateExpression) {
            val text = str.plainContent
            literals.compute(text) { _, oldValue -> oldValue?.plus(1) ?: 1 }
            literalReferences.compute(text) { _, entries -> entries?.add(str); entries ?: mutableListOf(str) }
        }
    }

    companion object {
        private const val STRING_EXCLUSION_LENGTH = 5
    }
}
