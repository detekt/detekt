package io.gitlab.arturbosch.detekt.rules

import io.gitlab.arturbosch.detekt.test.KtTestCompiler
import org.spekframework.spek2.dsl.Root

fun Root.setupKotlinCoreEnvironment() {
    @Suppress("UNUSED_VARIABLE")
    val wrapper by memoized(
        factory = { KtTestCompiler.createEnvironment() },
        destructor = { it.dispose() }
    )
}
