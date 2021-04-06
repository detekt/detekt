package io.gitlab.arturbosch.detekt.rules

import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameOrNull
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.TypeUtils

fun KotlinType.fqNameOrNull(): FqName? {
    return TypeUtils.getClassDescriptor(this)?.fqNameOrNull()
}
