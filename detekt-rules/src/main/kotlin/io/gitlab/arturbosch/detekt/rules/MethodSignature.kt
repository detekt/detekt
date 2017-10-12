package io.gitlab.arturbosch.detekt.rules

import org.jetbrains.kotlin.psi.KtFunction

fun KtFunction.isEqualsFunction() =
	this.name == "equals" && hasCorrectEqualsParameter()

fun KtFunction.isHashCodeFunction() =
		this.name == "hashCode" && this.valueParameters.isEmpty()

fun KtFunction.hasCorrectEqualsParameter() =
		this.valueParameters.firstOrNull()?.typeReference?.text == "Any?"
