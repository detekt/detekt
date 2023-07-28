package io.github.detekt.psi

import org.jetbrains.kotlin.com.intellij.openapi.util.Key
import org.jetbrains.kotlin.com.intellij.psi.PsiFile
import org.jetbrains.kotlin.psi.UserDataProperty

var PsiFile.relativePath: String? by UserDataProperty(Key("relativePath"))
var PsiFile.basePath: String? by UserDataProperty(Key("basePath"))
var PsiFile.lineSeparator: String? by UserDataProperty(Key("lineSeparator"))
