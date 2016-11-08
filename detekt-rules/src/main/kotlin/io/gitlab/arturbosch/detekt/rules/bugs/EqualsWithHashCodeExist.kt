package io.gitlab.arturbosch.detekt.rules.bugs

import com.intellij.psi.PsiFile
import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Rule
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtNamedFunction
import java.util.ArrayDeque

/**
 * @author Artur Bosch
 */
class EqualsWithHashCodeExist(config: Config = Config.empty) : Rule("EqualsWithHashCodeExist", Severity.Defect, config) {

	private val queue = ArrayDeque<ViolationHolder>(5)

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
		if (queue.pop().violation()) addFindings(CodeSmell(id, Entity.from(classOrObject)))
	}

	override fun visitNamedFunction(function: KtNamedFunction) {
		function.name?.let {
			if (it == "equals") queue.peek().equals = true
			if (it == "hashCode") queue.peek().hashCode = true
		}
	}

}