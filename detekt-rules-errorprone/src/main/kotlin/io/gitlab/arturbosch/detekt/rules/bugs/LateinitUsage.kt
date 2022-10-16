package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.AnnotationExcluder
import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.config
import io.gitlab.arturbosch.detekt.api.internal.Configuration
import io.gitlab.arturbosch.detekt.rules.isLateinit
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.psiUtil.containingClass

/**
 * Turn on this rule to flag usages of the lateinit modifier.
 *
 * Using lateinit for property initialization can be error-prone and the actual initialization is not
 * guaranteed. Try using constructor injection or delegation to initialize properties.
 *
 * <noncompliant>
 * class Foo {
 *     private lateinit var i1: Int
 *     lateinit var i2: Int
 * }
 * </noncompliant>
 */
@Suppress("ViolatesTypeResolutionRequirements")
class LateinitUsage(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue(
        javaClass.simpleName,
        Severity.Defect,
        "Usage of `lateinit` detected. Using `lateinit` for property initialization " +
            "is error prone, try using constructor injection or delegation.",
        Debt.TWENTY_MINS
    )

    @Configuration("Allows you to provide a list of annotations that disable this check.")
    @Deprecated("Use `ignoreAnnotated` instead")
    private val excludeAnnotatedProperties: List<Regex> by config(emptyList<String>()) { list ->
        list.map { it.replace(".", "\\.").replace("*", ".*").toRegex() }
    }

    @Configuration("Allows you to disable the rule for a list of classes")
    private val ignoreOnClassesPattern: Regex by config("", String::toRegex)

    private var properties = mutableListOf<KtProperty>()

    override fun visitProperty(property: KtProperty) {
        if (property.isLateinit()) {
            properties.add(property)
        }
    }

    override fun visit(root: KtFile) {
        properties = mutableListOf()

        super.visit(root)

        val annotationExcluder = AnnotationExcluder(
            root,
            @Suppress("DEPRECATION") excludeAnnotatedProperties,
            bindingContext,
        )

        properties.filterNot { annotationExcluder.shouldExclude(it.annotationEntries) }
            .filterNot { it.containingClass()?.name?.matches(ignoreOnClassesPattern) == true }
            .forEach {
                report(CodeSmell(issue, Entity.from(it), "Usages of lateinit should be avoided."))
            }
    }
}
