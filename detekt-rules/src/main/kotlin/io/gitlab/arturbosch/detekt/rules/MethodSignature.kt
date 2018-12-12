package io.gitlab.arturbosch.detekt.rules

import org.jetbrains.kotlin.psi.KtFunction
import org.jetbrains.kotlin.psi.KtNamedFunction

fun KtFunction.isEqualsFunction() =
		this.name == "equals" && hasCorrectEqualsParameter() && this.isOverride()

fun KtFunction.isHashCodeFunction() =
		this.name == "hashCode" && this.valueParameters.isEmpty() && this.isOverride()

private val knownAnys = setOf("Any?", "kotlin.Any?")
fun KtFunction.hasCorrectEqualsParameter() =
		this.valueParameters.firstOrNull()?.typeReference?.text in knownAnys

fun KtNamedFunction.isMainFunction() =
		this.name == "main" && this.isPublicNotOverridden() && this.isTopLevel
