package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.AnnotationExcluder
import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.LazyRegex
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.internal.valueOrDefaultCommaSeparated
import io.gitlab.arturbosch.detekt.rules.isLateinit
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.psiUtil.containingClass

/**
 * Turn on this rule to flag usages of the lateinit modifier.
 *
 * Using lateinit for property initialization can be error prone and the actual initialization is not
 * guaranteed. Try using constructor injection or delegation to initialize properties.
 *
 * <noncompliant>
 * class Foo {
 *     @JvmField lateinit var i1: Int
 *     @JvmField @SinceKotlin("1.0.0") lateinit var i2: Int
 * }
 * </noncompliant>
 *
 * @configuration excludeAnnotatedProperties - Allows you to provide a list of annotations that disable
 * this check. (default: `[]`)
 * @configuration ignoreOnClassesPattern - Allows you to disable the rule for a list of classes (default: `''`)
 */
class LateinitUsage(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue(javaClass.simpleName,
            Severity.Defect,
            "Usage of lateinit detected. Using lateinit for property initialization " +
                    "is error prone, try using constructor injection or delegation.",
            Debt.TWENTY_MINS)

    private val excludeAnnotatedProperties = valueOrDefaultCommaSeparated(EXCLUDE_ANNOTATED_PROPERTIES, emptyList())
        .map { it.removePrefix("*").removeSuffix("*") }

    private val ignoreOnClassesPattern by LazyRegex(key = IGNORE_ON_CLASSES_PATTERN, default = "")

    private var properties = mutableListOf<KtProperty>()

    override fun visitProperty(property: KtProperty) {
        if (property.isLateinit()) {
            properties.add(property)
        }
    }

    override fun visit(root: KtFile) {
        properties = mutableListOf()

        super.visit(root)

        val annotationExcluder = AnnotationExcluder(root, excludeAnnotatedProperties)

        properties.filterNot { annotationExcluder.shouldExclude(it.annotationEntries) }
                .filterNot { it.containingClass()?.name?.matches(ignoreOnClassesPattern) == true }
                .forEach {
                    report(CodeSmell(issue, Entity.from(it), "Usages of lateinit should be avoided."))
                }
    }

    companion object {
        const val EXCLUDE_ANNOTATED_PROPERTIES = "excludeAnnotatedProperties"
        const val IGNORE_ON_CLASSES_PATTERN = "ignoreOnClassesPattern"
    }
}
