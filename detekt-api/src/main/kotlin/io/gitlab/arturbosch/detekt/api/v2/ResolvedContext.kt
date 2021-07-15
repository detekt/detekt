package io.gitlab.arturbosch.detekt.api.v2

import io.gitlab.arturbosch.detekt.api.internal.CompilerResources
import org.jetbrains.kotlin.resolve.BindingContext

interface ResolvedContext {
    val binding: BindingContext?
    val resources: CompilerResources
}
