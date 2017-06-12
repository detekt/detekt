package io.gitlab.arturbosch.detekt.rules

import io.gitlab.arturbosch.detekt.api.*
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
 * @author Artur Bosch
 */
class FeatureEnvy(config: Config = Config.empty) : Rule("FeatureEnvy", config) {

	private val factor = withConfig {
		FeatureEnvyFactor(valueOrDefault("threshold", 0.5),
				valueOrDefault("base", 0.5),
				valueOrDefault("weight", 0.45))
	}

	override fun visitClass(context: Context, klass: KtClass) {
		val properties = klass.getProperties()
		val functions = klass.declarations.filterIsInstance(KtNamedFunction::class.java)
		val visitor = FeatureEnvyMethodInspector(context, properties, functions)
		visitor.run()
		super.visitClass(context, klass)
	}

	inner class FeatureEnvyMethodInspector(val context: Context,
										   val properties: List<KtProperty>,
										   val functions: List<KtNamedFunction>) {

		fun run() {
			functions.filter { it.funKeyword != null }
					.filter { it.hasBlockBody() }
					.forEach { function -> analyzeFunction(context, function) }
		}

		private fun analyzeFunction(context: Context, function: KtNamedFunction) {
			val entityOfFunction = Entity.from(function)
			val allCalls = function.collectByType<KtCallExpression>(context)
			val sumCalls = allCalls.map { 1 }.sum()
			println("${function.name} " + sumCalls)
			val parameters = function.valueParameters
			val locals = function.collectByType<KtVariableDeclaration>(context)
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
					context.report(CodeSmellWithReferenceAndMetric(ISSUE, entityOfFunction,
							Entity.from(ktElement), Metric("FeatureEnvyFactor", value, threshold, 100)))
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

	inner class FeatureEnvyFactor(val threshold: Double = 0.5,
								  private val base: Double = 0.5,
								  private val weight: Double = 0.45) {

		internal fun calc(entityCalls: Int, allCalls: Int): Double {
			if (allCalls == 0 || allCalls == 1) {
				return 0.0
			}
			return weight * (entityCalls / allCalls) + (1 - weight) * (1 - Math.pow(base, entityCalls.toDouble()))
		}
	}

	companion object {
		val ISSUE = Issue("FeatureEnvy", Issue.Severity.CodeSmell)
	}
}
