package dev.detekt.parser

import com.intellij.openapi.util.UserDataHolderBase
import com.intellij.pom.PomModel
import com.intellij.pom.PomModelAspect
import com.intellij.pom.PomTransaction
import com.intellij.pom.impl.PomTransactionBase
import com.intellij.pom.tree.TreeAspect
import sun.reflect.ReflectionFactory

/**
 * Adapted from https://github.com/pinterest/ktlint/blob/0.50.0/ktlint-rule-engine/src/main/kotlin/com/pinterest/ktlint/rule/engine/internal/KotlinPsiFileFactory.kt
 * Licenced under the MIT licence - https://github.com/pinterest/ktlint/blob/master/LICENSE
 */
object DetektPomModel : UserDataHolderBase(), PomModel {

    override fun runTransaction(transaction: PomTransaction) {
        val transactionCandidate = transaction as? PomTransactionBase

        val pomTransaction = requireNotNull(transactionCandidate) {
            "${PomTransactionBase::class.simpleName} type expected, actual is ${transaction.javaClass.simpleName}"
        }

        pomTransaction.run()
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
