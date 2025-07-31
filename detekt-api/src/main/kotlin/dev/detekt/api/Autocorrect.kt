package dev.detekt.api

import com.intellij.openapi.util.Key
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.UserDataProperty

var KtFile.modifiedText: String? by UserDataProperty(Key("modifiedText"))
