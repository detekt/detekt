package io.gitlab.arturbosch.detekt.core

import com.intellij.mock.MockProject
import com.intellij.openapi.extensions.ExtensionPoint
import com.intellij.openapi.extensions.Extensions.getArea
import com.intellij.openapi.util.UserDataHolderBase
import com.intellij.pom.PomModel
import com.intellij.pom.PomModelAspect
import com.intellij.pom.PomTransaction
import com.intellij.pom.impl.PomTransactionBase
import com.intellij.pom.tree.TreeAspect
import com.intellij.psi.impl.source.tree.TreeCopyHandler
import sun.reflect.ReflectionFactory

/**
 * Based on KtLint.
 *
 * @author Shyiko
 * @author Artur Bosch
 */
object MutableAST {

	fun forProject(project: MockProject) {
		val pomModel: PomModel = object : UserDataHolderBase(), PomModel {

			override fun runTransaction(transaction: PomTransaction) {
				(transaction as PomTransactionBase).run()
			}

			@Suppress("UNCHECKED_CAST")
			override fun <T : PomModelAspect> getModelAspect(aspect: Class<T>): T? {
				if (aspect == TreeAspect::class.java) {
					// using approach described in https://git.io/vKQTo due to the magical bytecode of TreeAspect
					// (check constructor signature and compare it to the source)
					// (org.jetbrains.kotlin:kotlin-compiler-embeddable:1.0.3)
					val constructor = ReflectionFactory.getReflectionFactory().newConstructorForSerialization(
							aspect, Any::class.java.getDeclaredConstructor(*arrayOfNulls<Class<*>>(0)))
					return constructor.newInstance(*emptyArray()) as T
				}
				return null
			}

		}
		val extensionPoint = "com.intellij.treeCopyHandler"
		val extensionClassName = TreeCopyHandler::class.java.name!!
		arrayOf(getArea(project), getArea(null))
				.filter { !it.hasExtensionPoint(extensionPoint) }
				.forEach { it.registerExtensionPoint(extensionPoint, extensionClassName, ExtensionPoint.Kind.INTERFACE) }
		project.registerService(PomModel::class.java, pomModel)
	}
}