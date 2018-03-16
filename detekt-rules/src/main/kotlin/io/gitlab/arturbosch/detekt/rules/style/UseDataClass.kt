package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.AnnotationExcluder
import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.SplitPattern
import io.gitlab.arturbosch.detekt.rules.collectByType
import io.gitlab.arturbosch.detekt.rules.isOpen
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.psiUtil.isAbstract
import org.jetbrains.kotlin.psi.psiUtil.isPrivate
import org.jetbrains.kotlin.psi.psiUtil.isPropertyParameter

/**
 * Classes that simply hold data should be refactored into a `data class`. Data classes are specialized to hold data
 * and generate `hashCode`, `equals` and `toString` implementations as well.
 *
 * Read more about `data class`: https://kotlinlang.org/docs/reference/data-classes.html
 *
 * <noncompliant>
 * class DataClassCandidate(val i: Int) {
 *
 *     val i2: Int = 0
 * }
 * </noncompliant>
 *
 * <compliant>
 * data class DataClass(val i: Int, val i2: Int)
 * </compliant>
 *
 * @configuration excludeAnnotatedClasses - allows to provide a list of annotations that disable (default: "")
 *
 * @author Ivan Balaksha
 * @author Artur Bosch
 * @author schalkms
 * @author Marvin Ramin
 */
class UseDataClass(config: Config = Config.empty) : Rule(config) {

	override val issue: Issue = Issue("UseDataClass",
			Severity.Style,
			"Classes that do nothing but hold data should be replaced with a data class.",
			Debt.FIVE_MINS)

	private val excludeAnnotatedClasses = SplitPattern(valueOrDefault(EXCLUDE_ANNOTATED_CLASSES, ""))
	private val defaultFunctionNames = hashSetOf("hashCode", "equals", "toString", "copy")

	override fun visit(root: KtFile) {
		super.visit(root)
		val annotationExcluder = AnnotationExcluder(root, excludeAnnotatedClasses)
		root.collectByType<KtClass>().forEach { visitKlass(it, annotationExcluder) }
	}

	private fun visitKlass(klass: KtClass, annotationExcluder: AnnotationExcluder) {
		if (isIncorrectClassType(klass) || hasOnlyPrivateConstructors(klass)) {
			return
		}
		if (klass.isClosedForExtension() && klass.doesNotExtendAnything()
				&& !annotationExcluder.shouldExclude(klass.annotationEntries)) {
			val declarations = klass.extractDeclarations()
			val properties = declarations.filterIsInstance<KtProperty>()
			val functions = declarations.filterIsInstance<KtNamedFunction>()

			val propertyParameters = klass.extractConstructorPropertyParameters()

			val containsFunctions = functions.none { !defaultFunctionNames.contains(it.name) }
			val containsPropertyOrPropertyParameters = properties.isNotEmpty() || propertyParameters.isNotEmpty()

			if (containsFunctions && containsPropertyOrPropertyParameters) {
				report(CodeSmell(issue, Entity.from(klass), "The class ${klass.nameAsSafeName} defines no" +
						"functionality and only holds data. Consider converting it to a data class."))
			}
		}
	}

	private fun isIncorrectClassType(klass: KtClass) =
			klass.isData() || klass.isEnum() || klass.isAnnotation() || klass.isSealed()

	private fun hasOnlyPrivateConstructors(klass: KtClass): Boolean {
		val primaryConstructor = klass.primaryConstructor
		return (primaryConstructor == null || primaryConstructor.isPrivate())
				&& klass.secondaryConstructors.all { it.isPrivate() }
	}

	private fun KtClass.doesNotExtendAnything() = superTypeListEntries.isEmpty()

	private fun KtClass.isClosedForExtension() = !isAbstract() && !isOpen()

	private fun KtClass.extractDeclarations(): List<KtDeclaration> = getBody()?.declarations ?: emptyList()

	private fun KtClass.extractConstructorPropertyParameters(): List<KtParameter> =
			getPrimaryConstructorParameterList()
					?.parameters
					?.filter { it.isPropertyParameter() } ?: emptyList()

	companion object {
		const val EXCLUDE_ANNOTATED_CLASSES = "excludeAnnotatedClasses"
	}
}
