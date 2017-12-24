package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.DetektVisitor
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtFunction
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtReferenceExpression
import org.jetbrains.kotlin.psi.psiUtil.getNonStrictParentOfType
import org.jetbrains.kotlin.psi.psiUtil.isPrivate

/**
 * Reports unused private properties. If private properties are unused they should be removed. Otherwise this dead code
 * can lead to confusion and potential bugs.
 *
 * @author Marvin Ramin
 */
class UnusedPrivateMember(config: Config = Config.empty) : Rule(config) {
	override val issue: Issue = Issue("UnusedPrivateMember",
			Severity.Maintainability,
			"Private property is unused.")

	override fun visitClassOrObject(classOrObject: KtClassOrObject) {
		val visitor = UnusedPropertyVisitor()
		classOrObject.accept(visitor)

		visitor.properties.forEach {
			report(CodeSmell(issue, Entity.from(it.value), "Private property ${it.key} is unused."))
		}

		super.visitClassOrObject(classOrObject)
	}

	class UnusedPropertyVisitor : DetektVisitor() {
		val properties = mutableMapOf<String, KtElement>()

		override fun visitProperty(property: KtProperty) {
			if (property.isPrivate() && property.isMember) {
				val name = property.name ?: throw IllegalStateException("Private properties should have a name")
				properties.put(name, property)
			}
			super.visitProperty(property)
		}

		override fun visitReferenceExpression(expression: KtReferenceExpression) {
			val function = expression.getNonStrictParentOfType(KtFunction::class.java)
			val localProperties = mutableMapOf<String, KtElement>()

			function?.accept(object : DetektVisitor() {
				override fun visitProperty(property: KtProperty) {
					if (property.isLocal) {
						val name = property.name ?: throw IllegalStateException("Properties should have a name")
						localProperties.put(name, property)
					}
					super.visitProperty(property)
				}
			})

			val name = expression.text
			if (!localProperties.contains(name) && properties.contains(name)) {
				properties.remove(name)
			}
			super.visitReferenceExpression(expression)
		}
	}
}
