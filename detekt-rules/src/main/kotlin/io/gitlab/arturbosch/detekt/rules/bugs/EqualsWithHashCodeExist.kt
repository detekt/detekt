package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.com.intellij.psi.PsiFile
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtNamedFunction
import java.util.ArrayDeque

/**
 * @author Artur Bosch
 */
class EqualsWithHashCodeExist(config: Config = Config.empty) : Rule(config) {

	override val issue = Issue("EqualsWithHashCodeExist",
			Severity.Defect,
			"Always override hashCode when you override equals. " +
					"All hash-based collections depend on objects meeting the equals-contract. " +
					"Two equal objects must produce the same hashcode. When inheriting equals or hashcode, " +
					"override the inherited and call the super method for clarification.")

	private val queue = ArrayDeque<ViolationHolder>(MAXIMUM_EXPECTED_NESTED_CLASSES)

	private data class ViolationHolder(var equals: Boolean = false, var hashCode: Boolean = false) {
		internal fun violation() = equals && !hashCode || !equals && hashCode
	}

	override fun visitFile(file: PsiFile?) {
		queue.clear()
		super.visitFile(file)
	}

	override fun visitClassOrObject(classOrObject: KtClassOrObject) {
		val isDataClass = classOrObject.modifierList?.hasModifier(KtTokens.DATA_KEYWORD) ?: false
		if (isDataClass) return

		queue.push(ViolationHolder())
		super.visitClassOrObject(classOrObject)
		if (queue.pop().violation()) report(CodeSmell(issue, Entity.from(classOrObject)))
	}

	override fun visitNamedFunction(function: KtNamedFunction) {
		if (!function.isTopLevel) {
			function.name?.let {
				if (it == "equals") queue.peek().equals = true
				if (it == "hashCode") queue.peek().hashCode = true
			}
		}
	}

}

private const val MAXIMUM_EXPECTED_NESTED_CLASSES = 5
