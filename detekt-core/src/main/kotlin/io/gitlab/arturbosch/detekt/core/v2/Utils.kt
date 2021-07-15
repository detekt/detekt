package io.gitlab.arturbosch.detekt.core.v2

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.takeWhile

fun <T> Flow<T>.reusable(
    scope: CoroutineScope,
    replay: Int = 0
): Flow<T> {
    return this
        .map { Result.success(it) }
        .onCompletion { e -> if (e == null) emit(Result.failure(Throwable())) }
        .shareIn(scope, SharingStarted.Lazily, replay)
        .takeWhile { it.isSuccess }
        .map { it.getOrThrow() }
}

suspend fun <T> Flow<T>.reusable(replay: Int): Flow<T> {
    return coroutineScope { reusable(this, replay) }
}
