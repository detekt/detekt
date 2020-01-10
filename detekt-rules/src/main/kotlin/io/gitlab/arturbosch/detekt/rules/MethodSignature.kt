package io.gitlab.arturbosch.detekt.rules

import org.jetbrains.kotlin.psi.KtFunction
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtObjectDeclaration

fun KtFunction.isEqualsFunction() =
        this.name == "equals" && this.isOverride() && hasCorrectEqualsParameter()

fun KtFunction.isHashCodeFunction() =
        this.name == "hashCode" && this.isOverride() && this.valueParameters.isEmpty()

private val knownAnys = setOf("Any?", "kotlin.Any?")
fun KtFunction.hasCorrectEqualsParameter() =
        this.valueParameters.singleOrNull()?.typeReference?.text in knownAnys

fun KtNamedFunction.isMainFunction() =
        this.isTopLevelMain() || this.isMainInsideObject()

fun KtNamedFunction.isMainInsideObject() =
        this.name == "main" &&
                this.isPublicNotOverridden() &&
                this.parent?.parent is KtObjectDeclaration &&
                this.hasAnnotation("JvmStatic", "kotlin.jvm.JvmStatic")

fun KtNamedFunction.isTopLevelMain() =
        this.name == "main" &&
                this.isPublicNotOverridden() &&
                this.isTopLevel
