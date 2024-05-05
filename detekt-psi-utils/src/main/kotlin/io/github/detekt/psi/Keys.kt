package io.github.detekt.psi

import org.jetbrains.kotlin.com.intellij.openapi.util.Key
import org.jetbrains.kotlin.com.intellij.psi.PsiFile
import org.jetbrains.kotlin.psi.NotNullableUserDataProperty
import org.jetbrains.kotlin.psi.UserDataProperty
import java.nio.file.Path

var PsiFile.relativePath: Path? by UserDataProperty(Key("relativePath"))
var PsiFile.lineSeparator: String by NotNullableUserDataProperty(Key("lineSeparator"), System.lineSeparator())
