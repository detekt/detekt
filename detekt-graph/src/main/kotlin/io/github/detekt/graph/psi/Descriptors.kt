package io.github.detekt.graph.psi

import org.jetbrains.kotlin.descriptors.DeclarationDescriptor

fun extractFullName(declaration: DeclarationDescriptor): String =
    generateSequence(declaration) { it.containingDeclaration }
        .filterNot { it.name.isSpecial }
        .map { it.name.identifier }
        .fold("") { acc, name -> if (acc.isEmpty()) name else "$name.$acc" }
