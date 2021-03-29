// Remove the suppression when https://github.com/pinterest/ktlint/issues/1125 is fixed
@file:Suppress("SpacingBetweenDeclarationsWithAnnotations")

package io.gitlab.arturbosch.detekt.rules

import io.github.detekt.test.utils.createEnvironment
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.spekframework.spek2.dsl.Root
import org.spekframework.spek2.lifecycle.CachingMode

fun Root.setupKotlinEnvironment() {
    val wrapper by memoized(CachingMode.SCOPE, { createEnvironment() }, { it.dispose() })

    // name is used for delegation
    @Suppress("UNUSED_VARIABLE")
    val env: KotlinCoreEnvironment by memoized(CachingMode.EACH_GROUP) { wrapper.env }
}
