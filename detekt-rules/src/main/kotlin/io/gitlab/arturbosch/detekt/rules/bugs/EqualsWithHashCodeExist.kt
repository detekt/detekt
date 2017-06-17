package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.*
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNamedFunction
import java.util.ArrayDeque

/**
 * @author Artur Bosch
 */
class EqualsWithHashCodeExist(config: Config = Config.empty) : Rule("EqualsWithHashCodeExist", config) {

	private val queue = ArrayDeque<ViolationHolder>(5)

	private data class ViolationHolder(var equals: Boolean = false, var hashCode: Boolean = false) {
		internal fun violation() = equals && !hashCode || !equals && hashCode
	}

	override fun preVisit(context: Context, root: KtFile) {
		queue.clear()
	}

	override fun visitClassOrObject(context: Context, classOrObject: KtClassOrObject) {
		val isDataClass = classOrObject.modifierList?.hasModifier(KtTokens.DATA_KEYWORD) ?: false
		if (isDataClass) return

		queue.push(ViolationHolder())
		super.visitClassOrObject(context, classOrObject)
		if (queue.pop().violation()) context.report(CodeSmell(ISSUE, Entity.from(classOrObject)))
	}

	override fun visitNamedFunction(context: Context, function: KtNamedFunction) {
		if (!function.isTopLevel) {
			function.name?.let {
				if (it == "equals") queue.peek().equals = true
				if (it == "hashCode") queue.peek().hashCode = true
			}
		}
	}

	companion object {
		val ISSUE = Issue("EqualsWithHashCodeExist", Issue.Severity.Defect)
	}
}