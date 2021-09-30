package io.github.detekt.test.utils

import io.gitlab.arturbosch.detekt.rules.safeAs
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtNamed
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.psiUtil.getChildOfType

inline fun <reified T : KtElement> KtElement.getNodeByName(name: String): T {
    val node = getChildOfType<T>() ?: error("Expected node of type ${T::class}")
    val identifier = node.safeAs<KtNamed>()?.nameAsName?.identifier

    require(identifier == name) {
        "Node should be $name, but was $identifier"
    }

    return node
}

fun KtElement.getFunctionByName(name: String): KtNamedFunction = getNodeByName(name)
