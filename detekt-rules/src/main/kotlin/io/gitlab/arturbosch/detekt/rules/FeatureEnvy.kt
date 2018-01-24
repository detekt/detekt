package io.gitlab.arturbosch.detekt.rules

import io.gitlab.arturbosch.detekt.api.CodeSmellWithReferenceAndMetric
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Metric
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtNamedDeclaration
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtVariableDeclaration
import java.util.IdentityHashMap

/**
 * Rule that detects feature envy in classes.
 *
 * @author Artur Bosch
 */
class FeatureEnvy(config: Config = Config.empty) : Rule(config) {

	override val issue = Issue(javaClass.simpleName, Severity.Style, "")

	private val factor = FeatureEnvyFactor(
			valueOrDefault("threshold", DEFAULT_FEF_THRESHOLD),
			valueOrDefault("base", DEFAULT_FEF_BASE_MULTIPLIER),
			valueOrDefault("weight", DEFAULT_FEF_WEIGHT_MULTIPLIER))

	override fun visitClass(klass: KtClass) {
		val properties = klass.getProperties()
		val functions = klass.declarations.filterIsInstance(KtNamedFunction::class.java)
		val visitor = FeatureEnvyMethodInspector(properties, functions)
		visitor.run()
		super.visitClass(klass)
	}

	inner class FeatureEnvyMethodInspector(val properties: List<KtProperty>,
										   val functions: List<KtNamedFunction>) {

		fun run() {
			functions.filter { it.funKeyword != null }
					.filter { it.hasBlockBody() }
					.forEach { function -> analyzeFunction(function) }
		}

		private fun analyzeFunction(function: KtNamedFunction) {
			val entityOfFunction = Entity.from(function)
			val allCalls = function.collectByType<KtCallExpression>()
			val sumCalls = allCalls.map { 1 }.sum()
			println("${function.name} " + sumCalls)
			val parameters = function.valueParameters
			val locals = function.collectByType<KtVariableDeclaration>()
			println(locals.map { it.text })

			val elementCallMap = IdentityHashMap<KtNamedDeclaration, Int>()

			allCalls.map { it.parent }
					.filterIsInstance(KtDotQualifiedExpression::class.java)
					.map { it.receiverExpression }
					.filterIsInstance(KtNameReferenceExpression::class.java)
					.forEach {
						findCalledVariable(elementCallMap, properties, it)
						findCalledVariable(elementCallMap, parameters, it)
						findCalledVariable(elementCallMap, locals, it)
					}

			println(elementCallMap)
			elementCallMap.forEach { ktElement, i ->
				val value = factor.calc(i, sumCalls)
				val threshold = factor.threshold
				println(ktElement.text)
				println("factor: $value")
				if (threshold < value) {
					report(CodeSmellWithReferenceAndMetric(issue, entityOfFunction,
							Entity.from(ktElement),
							message = "",
							metric = Metric("FeatureEnvyFactor", value, threshold)))
				}
			}
		}

		private fun findCalledVariable(elementCallMap: IdentityHashMap<KtNamedDeclaration, Int>,
									   list: List<KtNamedDeclaration>,
									   name: KtNameReferenceExpression) {
			list.find { name.getReferencedName() == it.name }
					?.run { elementCallMap.merge(this, 1) { v1, v2 -> v1 + v2 } }
		}

	}

	inner class FeatureEnvyFactor(val threshold: Double = DEFAULT_FEF_BASE_MULTIPLIER,
								  private val base: Double = DEFAULT_FEF_BASE_MULTIPLIER,
								  private val weight: Double = DEFAULT_FEF_WEIGHT_MULTIPLIER) {

		internal fun calc(entityCalls: Int, allCalls: Int): Double {
			if (allCalls == 0 || allCalls == 1) {
				return 0.0
			}
			return weight * (entityCalls / allCalls) + (1 - weight) * (1 - Math.pow(base, entityCalls.toDouble()))
		}
	}

	companion object {
		const val DEFAULT_FEF_THRESHOLD = 0.5
		const val DEFAULT_FEF_BASE_MULTIPLIER = 0.5
		const val DEFAULT_FEF_WEIGHT_MULTIPLIER = 0.45
	}
}
