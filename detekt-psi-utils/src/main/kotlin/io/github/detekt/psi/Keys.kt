package io.github.detekt.psi

import org.jetbrains.kotlin.com.intellij.openapi.util.Key
import org.jetbrains.kotlin.com.intellij.psi.PsiFile
import org.jetbrains.kotlin.psi.NotNullableUserDataProperty

var PsiFile.lineSeparator: String by NotNullableUserDataProperty(Key("lineSeparator"), System.lineSeparator())
