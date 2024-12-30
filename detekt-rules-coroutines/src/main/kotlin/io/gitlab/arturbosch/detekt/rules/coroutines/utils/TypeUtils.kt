package io.gitlab.arturbosch.detekt.rules.coroutines.utils

import io.gitlab.arturbosch.detekt.rules.fqNameOrNull
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
