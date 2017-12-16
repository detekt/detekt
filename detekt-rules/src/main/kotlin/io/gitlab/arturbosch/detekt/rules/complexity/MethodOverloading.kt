package io.gitlab.arturbosch.detekt.rules.complexity

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.DetektVisitor
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Metric
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.ThresholdRule
import io.gitlab.arturbosch.detekt.api.ThresholdedCodeSmell
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNamedFunction

/**
 * @configuration threshold - (default: 5)
 *
 * @author schalkms
 * @author Marvin Ramin
 */
class MethodOverloading(config: Config = Config.empty,
						threshold: Int = ACCEPTED_OVERLOAD_COUNT) : ThresholdRule(config, threshold) {

	override val issue = Issue("MethodOverloading", Severity.Maintainability,
			"Methods which are overloaded often might be harder to maintain. " +
					"Furthermore, these methods are tightly coupled. " +
					"Refactor these methods and try to use optional parameters.")

	override fun visitClass(klass: KtClass) {
		val visitor = OverloadedMethodVisitor()
		klass.accept(visitor)
		visitor.reportIfThresholdExceeded(klass)
		super.visitClass(klass)
	}

	override fun visitKtFile(file: KtFile) {
		val visitor = OverloadedMethodVisitor()
		file.children.filterIsInstance<KtNamedFunction>().forEach { it.accept(visitor) }
		visitor.reportIfThresholdExceeded(file)
		super.visitKtFile(file)
	}

	internal inner class OverloadedMethodVisitor : DetektVisitor() {

		private var methods = HashMap<String, Int>()

		fun reportIfThresholdExceeded(element: PsiElement) {
			methods.filterValues { it > threshold }.forEach {
				report(ThresholdedCodeSmell(issue,
						Entity.from(element),
						Metric("OVERLOAD SIZE: ", it.value, threshold),
						message = "This method is overloaded too many times."))
			}
		}

		override fun visitNamedFunction(function: KtNamedFunction) {
			val name = function.name ?: return
			methods.put(name, methods.getOrDefault(name, 0) + 1)
		}
	}
}

private const val ACCEPTED_OVERLOAD_COUNT = 5
