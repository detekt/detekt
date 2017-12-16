package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.AnnotationExcluder
import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.SplitPattern
import org.jetbrains.kotlin.lexer.KtTokens
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
 * this check. (default: "")
 * @configuration ignoreOnClassesPattern - Allows you to disable the rule for a list of classes (default: "")
 *
 * @author Marvin Ramin
 */
class LateinitUsage(config: Config = Config.empty) : Rule(config) {

	override val issue = Issue(javaClass.simpleName,
			Severity.Defect,
			"Usage of lateinit. Using lateinit for property initialization " +
					"is error prone, try using constructor injection or delegation.")

	private val excludeAnnotatedProperties = SplitPattern(valueOrDefault(EXCLUDE_ANNOTATED_PROPERTIES, ""))

	private val ignoreOnClassesPattern = Regex(valueOrDefault(IGNORE_ON_CLASSES_PATTERN, ""))

	private var properties = mutableListOf<KtProperty>()

	override fun visitProperty(property: KtProperty) {
		if (isLateinitProperty(property)) {
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
					report(CodeSmell(issue, Entity.from(it), "Usages of latinit should be avoided."))
				}
	}

	private fun isLateinitProperty(property: KtProperty)
			= property.modifierList?.hasModifier(KtTokens.LATEINIT_KEYWORD) == true

	companion object {
		const val EXCLUDE_ANNOTATED_PROPERTIES = "excludeAnnotatedProperties"
		const val IGNORE_ON_CLASSES_PATTERN = "ignoreOnClassesPattern"
	}
}
