package io.gitlab.arturbosch.detekt.test

import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtNamed
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.psiUtil.getChildOfType
import org.jetbrains.kotlin.utils.addToStdlib.safeAs

inline fun <reified T : KtElement> KtElement.getNodeByName(name: String): T {
    val node = getChildOfType<T>() ?: error("Expected node of type ${T::class}")
    require(node.safeAs<KtNamed>()?.nameAsName?.identifier == name)
    return node
}

fun KtElement.getFunctionByName(name: String): KtNamedFunction = getNodeByName(name)
