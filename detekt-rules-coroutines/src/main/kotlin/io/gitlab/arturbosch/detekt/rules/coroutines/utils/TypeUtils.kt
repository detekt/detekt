package io.gitlab.arturbosch.detekt.rules.coroutines.utils

import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.types.KaType
import org.jetbrains.kotlin.analysis.api.types.symbol
import org.jetbrains.kotlin.name.ClassId

context(session: KaSession)
internal fun KaType.isCoroutineScope(): Boolean = with(session) {
    sequence {
        yield(this@isCoroutineScope)
        yieldAll(this@isCoroutineScope.allSupertypes)
    }
        .mapNotNull { it.symbol?.classId }
        .contains(CoroutineClassIds.CoroutineScope)
}

context(session: KaSession)
internal fun KaType.isCoroutinesFlow(): Boolean = with(session) {
    sequence {
        yield(this@isCoroutinesFlow)
        yieldAll(this@isCoroutinesFlow.allSupertypes)
    }
        .mapNotNull { it.symbol?.classId }
        .contains(CoroutineClassIds.Flow)
}

internal object CoroutineClassIds {
    val Flow: ClassId = ClassId.fromString("kotlinx/coroutines/flow/Flow")
    val CoroutineScope: ClassId = ClassId.fromString("kotlinx/coroutines/CoroutineScope")
}
