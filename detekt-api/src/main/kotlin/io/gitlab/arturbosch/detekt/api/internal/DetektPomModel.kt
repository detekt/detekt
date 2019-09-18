package io.gitlab.arturbosch.detekt.api.internal

import org.jetbrains.kotlin.com.intellij.openapi.extensions.ExtensionPoint
import org.jetbrains.kotlin.com.intellij.openapi.extensions.Extensions
import org.jetbrains.kotlin.com.intellij.openapi.project.Project
import org.jetbrains.kotlin.com.intellij.openapi.util.UserDataHolderBase
import org.jetbrains.kotlin.com.intellij.pom.PomModel
import org.jetbrains.kotlin.com.intellij.pom.PomModelAspect
import org.jetbrains.kotlin.com.intellij.pom.PomTransaction
import org.jetbrains.kotlin.com.intellij.pom.impl.PomTransactionBase
import org.jetbrains.kotlin.com.intellij.pom.tree.TreeAspect
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.TreeCopyHandler
import sun.reflect.ReflectionFactory

/**
 * Adapted from https://github.com/pinterest/ktlint/blob/master/ktlint-core/src/main/kotlin/com/pinterest/ktlint/core/KtLint.kt
 * Licenced under the MIT licence - https://github.com/pinterest/ktlint/blob/master/LICENSE
 */
@Suppress("UnsafeCallOnNullableType")
class DetektPomModel(project: Project) : UserDataHolderBase(), PomModel {

    init {
        val extensionPoint = "org.jetbrains.kotlin.com.intellij.treeCopyHandler"
        val extensionClassName = TreeCopyHandler::class.java.name!!
        arrayOf(Extensions.getArea(project), Extensions.getArea(null))
            .filter { !it.hasExtensionPoint(extensionPoint) }
            .forEach { it.registerExtensionPoint(extensionPoint, extensionClassName, ExtensionPoint.Kind.INTERFACE) }
    }

    override fun runTransaction(transaction: PomTransaction) {
        (transaction as? PomTransactionBase ?: error("PomTransactionBase type expected")).run()
    }

    override fun <T : PomModelAspect?> getModelAspect(aspect: Class<T>): T? {
        if (aspect == TreeAspect::class.java) {
            val constructor = ReflectionFactory.getReflectionFactory()
                .newConstructorForSerialization(aspect, Any::class.java.getDeclaredConstructor())
            @Suppress("UNCHECKED_CAST")
            return constructor.newInstance() as T
        }
        return null
    }
}
