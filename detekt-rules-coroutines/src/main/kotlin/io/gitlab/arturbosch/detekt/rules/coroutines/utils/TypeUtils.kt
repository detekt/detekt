package io.gitlab.arturbosch.detekt.rules.coroutines.utils

import dev.detekt.psi.fqNameOrNull
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.typeUtil.supertypes

internal fun KotlinType.isCoroutineScope(): Boolean =
    sequence {
        yield(this@isCoroutineScope)
        yieldAll(this@isCoroutineScope.supertypes())
    }
        .mapNotNull { it.fqNameOrNull()?.asString() }
        .contains("kotlinx.coroutines.CoroutineScope")

internal fun KotlinType.isCoroutinesFlow(): Boolean =
    sequence {
        yield(this@isCoroutinesFlow)
        yieldAll(this@isCoroutinesFlow.supertypes())
    }
        .mapNotNull { it.fqNameOrNull()?.asString() }
        .contains("kotlinx.coroutines.flow.Flow")

internal object CoroutineClassIds {
    val Flow: ClassId = ClassId.fromString("kotlinx/coroutines/flow/Flow")
    val CoroutineScope: ClassId = ClassId.fromString("kotlinx/coroutines/CoroutineScope")
}
