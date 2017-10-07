package io.gitlab.arturbosch.detekt.rules.bugs.util

import org.jetbrains.kotlin.psi.KtFunction

fun KtFunction.isEqualsMethod() =
	this.name == "equals" && hasCorrectEqualsParameter()

fun KtFunction.isHashCodeMethod() =
		this.name == "hashCode" && this.valueParameters.isEmpty()

fun KtFunction.hasCorrectEqualsParameter() =
		this.valueParameters.firstOrNull()?.typeReference?.text == "Any?"
