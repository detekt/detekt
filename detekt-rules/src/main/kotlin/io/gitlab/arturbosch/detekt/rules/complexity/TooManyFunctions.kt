package io.gitlab.arturbosch.detekt.rules.complexity

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Metric
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.ThresholdedCodeSmell
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtObjectDeclaration

/**
 * This rule reports files, classes, interfaces, objects and enums which contain too many functions.
 * Each element can be configured with different thresholds.
 *
 * Too many functions indicate a violation of the single responsibility principle. Prefer extracting functionality
 * which clearly belongs together in separate parts of the code.
 *
 * @configuration thresholdInFiles - threshold in files (default: 11)
 * @configuration thresholdInClasses - threshold in classes (default: 11)
 * @configuration thresholdInInterfaces - threshold in interfaces (default: 11)
 * @configuration thresholdInObjects - threshold in objects (default: 11)
 * @configuration thresholdInEnums - threshold in enums (default: 11)
 *
 * @active since v1.0.0
 * @author Artur Bosch
 * @author Marvin Ramin
 */
class TooManyFunctions(config: Config = Config.empty) : Rule(config) {

	override val issue = Issue("TooManyFunctions",
			Severity.Maintainability,
			"Too many functions inside a/an file/class/object/interface always indicate a violation of "
					+ "the single responsibility principle. Maybe the file/class/object/interface wants to manage to " +
					"many things at once. Extract functionality which clearly belongs together.",
			Debt.TWENTY_MINS)

	private val thresholdInFiles = valueOrDefault(THRESHOLD_IN_FILES, DEFAULT_THRESHOLD)
	private val thresholdInClasses = valueOrDefault(THRESHOLD_IN_CLASSES, DEFAULT_THRESHOLD)
	private val thresholdInObjects = valueOrDefault(THRESHOLD_IN_OBJECTS, DEFAULT_THRESHOLD)
	private val thresholdInInterfaces = valueOrDefault(THRESHOLD_IN_INTERFACES, DEFAULT_THRESHOLD)
	private val thresholdInEnums = valueOrDefault(THRESHOLD_IN_ENUMS, DEFAULT_THRESHOLD)

	private var amountOfTopLevelFunctions: Int = 0

	override fun visitKtFile(file: KtFile) {
		super.visitKtFile(file)
		if (amountOfTopLevelFunctions >= thresholdInFiles) {
			report(ThresholdedCodeSmell(issue,
					Entity.from(file),
					Metric("SIZE", amountOfTopLevelFunctions, thresholdInFiles),
					"File '${file.name}' with '$amountOfTopLevelFunctions' functions detected. " +
							"Allowed maximum amount of functions inside files is set to '$thresholdInFiles'"))
		}
		amountOfTopLevelFunctions = 0
	}

	override fun visitNamedFunction(function: KtNamedFunction) {
		if (function.isTopLevel) {
			amountOfTopLevelFunctions++
		}
	}

	override fun visitClass(klass: KtClass) {
		val amount = calcFunctions(klass)
		when {
			klass.isInterface() && amount >= thresholdInInterfaces -> {
				report(ThresholdedCodeSmell(issue,
						Entity.from(klass),
						Metric("SIZE", amount, thresholdInInterfaces),
						"Interface '${klass.name}' with '$amount' functions detected. " +
								"Allowed maximum amount of functions inside interfaces is set to " +
								"'$thresholdInInterfaces'"))
			}
			klass.isEnum() && amount >= thresholdInEnums -> {
				report(ThresholdedCodeSmell(issue,
						Entity.from(klass),
						Metric("SIZE", amount, thresholdInEnums),
						"Enum class '${klass.name}' with '$amount' functions detected. " +
								"Allowed maximum amount of functions inside enum classes is set to " +
								"'$thresholdInEnums'"))
			}
			else -> {
				if (amount >= thresholdInClasses) {
					report(ThresholdedCodeSmell(issue,
							Entity.from(klass),
							Metric("SIZE", amount, thresholdInClasses),
							"Class '${klass.name}' with '$amount' functions detected. " +
									"Allowed maximum amount of functions inside classes is set to '$thresholdInClasses'"))
				}
			}
		}
		super.visitClass(klass)
	}

	override fun visitObjectDeclaration(declaration: KtObjectDeclaration) {
		val amount = calcFunctions(declaration)
		if (amount >= thresholdInObjects) {
			report(ThresholdedCodeSmell(issue,
					Entity.from(declaration),
					Metric("SIZE", amount, thresholdInObjects),
					"Object '${declaration.name}' with '$amount' functions detected. " +
							"Allowed maximum amount of functions inside objects is set to '$thresholdInObjects'"))
		}
		super.visitObjectDeclaration(declaration)
	}

	private fun calcFunctions(classOrObject: KtClassOrObject): Int = classOrObject.getBody()?.declarations
			?.filterIsInstance<KtNamedFunction>()
			?.size ?: 0

	companion object {
		const val DEFAULT_THRESHOLD = 11
		const val THRESHOLD_IN_FILES = "thresholdInFiles"
		const val THRESHOLD_IN_CLASSES = "thresholdInClasses"
		const val THRESHOLD_IN_INTERFACES = "thresholdInInterfaces"
		const val THRESHOLD_IN_OBJECTS = "thresholdInObjects"
		const val THRESHOLD_IN_ENUMS = "thresholdInEnums"
	}
}
