package io.gitlab.arturbosch.detekt.api

import org.jetbrains.kotlin.com.intellij.lang.ASTNode
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtStringTemplateEntry
import org.jetbrains.kotlin.psi.psiUtil.getNonStrictParentOfType
import java.util.HashMap
import kotlin.reflect.KClass

private val identifierRegex = Regex("[aA-zZ]+([-][aA-zZ]+)*")

/**
 * Checks if given string matches the criteria of an id - [aA-zZ]+([-][aA-zZ]+)* .
 */
internal fun validateIdentifier(id: String) {
	require(id.matches(identifierRegex), { "id must match [aA-zZ]+([-][aA-zZ]+)*" })
}

/**
 * Tests if this element is part of given PsiElement.
 */
fun PsiElement.isPartOf(clazz: KClass<out PsiElement>) = getNonStrictParentOfType(clazz.java) != null

/**
 * Tests of this element is part of a kotlin string.
 */
fun PsiElement.isPartOfString() = isPartOf(KtStringTemplateEntry::class)

/*
 * When analyzing sub path 'testData' of the kotlin project, CompositeElement.getText() throws
 * a RuntimeException stating 'Underestimated text length' - #65.
 */
@Suppress("TooGenericExceptionCaught")
internal fun getTextSafe(defaultValue: () -> String, block: () -> String) = try {
	block()
} catch (e: RuntimeException) {
	val message = e.message
	if (message != null && message.contains("Underestimated text length")) {
		defaultValue() + "!<UnderestimatedTextLengthException>"
	} else {
		defaultValue()
	}
}

const val PREFIX = "\t- "

fun Any.format(prefix: String = "", suffix: String = "\n") = "$prefix$this$suffix"

fun <K, V> List<Pair<K, List<V>>>.toMergedMap(): Map<K, List<V>> {
	val map = HashMap<K, MutableList<V>>()
	this.forEach {
		map.merge(it.first, it.second.toMutableList(), { l1, l2 -> l1.apply { addAll(l2) } })
	}
	return map
}
