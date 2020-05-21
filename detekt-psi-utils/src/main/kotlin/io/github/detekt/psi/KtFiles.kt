package io.github.detekt.psi

import org.jetbrains.kotlin.psi.KtFile

fun KtFile.absolutePath(): String =
    checkNotNull(getUserData(ABSOLUTE_PATH)) { "KtFile '$name' expected to have an absolute path." }

fun KtFile.relativePath(): String =
    checkNotNull(getUserData(RELATIVE_PATH)) { "KtFile '$name' expected to have an relative path." }
