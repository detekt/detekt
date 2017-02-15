package io.gitlab.arturbosch.detekt.rules

import io.gitlab.arturbosch.detekt.api.CodeSmellRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.DetektVisitor
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtProperty

/**
 * @author Artur Bosch
 */
class FeatureEnvy(config: Config = Config.empty) : CodeSmellRule("FeatureEnvy", config) {

	override fun visitClass(klass: KtClass) {
		val properties = klass.getProperties()
		val functions = klass.declarations
				.filterIsInstance(KtNamedFunction::class.java)
		val visitor = FeatureEnvyClassVisitor(klass.nameAsSafeName.identifier, properties, functions)
		visitor.run()
		super.visitClass(klass)
	}

	class FeatureEnvyClassVisitor(val className: String,
								  val properties: List<KtProperty>,
								  val functions: List<KtNamedFunction>) : DetektVisitor() {

		fun run() {
			functions.filter { it.funKeyword != null }
					.filter { it.hasBlockBody() }
					.forEach { function -> analyzeFunction(function) }
		}

		private fun analyzeFunction(function: KtNamedFunction) {
			val allCalls = function.collectByType<KtCallExpression>()
			println("${function.name} " + allCalls.map { 1 }.sum())
		}
	}
}

inline fun <reified T : KtElement> KtNamedFunction.collectByType(): List<T> {
	val list = mutableListOf<T>()
	this.accept(object : DetektVisitor() {
		override fun visitKtElement(element: KtElement) {
			if (element is T) {
				list.add(element)
			}
			element.children.forEach { it.accept(this) }
		}
	})
	return list
}
