package io.gitlab.arturbosch.detekt.rules.complexity

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Configuration
import io.gitlab.arturbosch.detekt.api.DetektVisitor
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.config
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
class StringLiteralDuplication(config: Config) : Rule(
    config,
    "Multiple occurrences of the same string literal within a single file detected. " +
        "Prefer extracting the string literal into a property or constant."
) {

    @Configuration("The maximum allowed amount of duplications.")
    private val allowedDuplications: Int by config(defaultValue = 2)

    @Configuration("if values in Annotations should be ignored")
    private val ignoreAnnotation: Boolean by config(true)

    @Configuration("The maximum string length below which duplications are allowed")
    private val allowedWithLengthLessThan: Int by config(5)

    @Configuration("RegEx of Strings that should be ignored")
    private val ignoreStringsRegex: Regex by config("$^", String::toRegex)

    override fun visitKtFile(file: KtFile) {
        val visitor = StringLiteralVisitor()
        file.accept(visitor)
        for ((name, _) in visitor.getLiteralsOverThreshold()) {
            val (main, references) = visitor.entitiesForLiteral(name)
            report(
                CodeSmell(
                    main,
                    description,
                    references
                )
            )
        }
    }

    internal inner class StringLiteralVisitor : DetektVisitor() {

        private val literals = HashMap<String, Int>()
        private val literalReferences = HashMap<String, MutableList<KtStringTemplateExpression>>()
        private val pass: Unit = Unit

        fun getLiteralsOverThreshold(): Map<String, Int> = literals.filterValues { it > allowedDuplications }
        fun entitiesForLiteral(literal: String): Pair<Entity, List<Entity>> {
            val references = literalReferences[literal]
            if (!references.isNullOrEmpty()) {
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
                text.length < allowedWithLengthLessThan -> pass
                text.matches(ignoreStringsRegex) -> pass
                else -> add(expression)
            }
        }

        private fun add(str: KtStringTemplateExpression) {
            val text = str.plainContent
            literals.compute(text) { _, oldValue -> oldValue?.plus(1) ?: 1 }
            literalReferences.compute(text) { _, entries ->
                entries?.add(str)
                entries ?: mutableListOf(str)
            }
        }
    }
}
