package io.gitlab.arturbosch.detekt.rules

import org.jetbrains.kotlin.psi.KtFunction
import org.jetbrains.kotlin.psi.KtNamedFunction

fun KtFunction.isEqualsFunction() =
		this.name == "equals" && hasCorrectEqualsParameter() && this.isOverridden()

fun KtFunction.isHashCodeFunction() =
		this.name == "hashCode" && this.valueParameters.isEmpty() && this.isOverridden()

fun KtFunction.hasCorrectEqualsParameter() =
		this.valueParameters.firstOrNull()?.typeReference?.text == "Any?"

fun KtNamedFunction.isMainFunction() =
		this.name == "main" && this.isPublicNotOverridden() && this.isTopLevel
