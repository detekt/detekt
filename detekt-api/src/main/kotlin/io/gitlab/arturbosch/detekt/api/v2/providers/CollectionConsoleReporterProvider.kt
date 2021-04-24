package io.gitlab.arturbosch.detekt.api.v2.providers

import io.gitlab.arturbosch.detekt.api.v2.ConsoleReporter
import kotlinx.coroutines.flow.Flow

fun interface CollectionConsoleReporterProvider {
    fun get(): Flow<ConsoleReporter>
}
