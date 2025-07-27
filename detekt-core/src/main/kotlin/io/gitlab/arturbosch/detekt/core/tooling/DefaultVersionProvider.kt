package io.gitlab.arturbosch.detekt.core.tooling

import io.github.detekt.tooling.api.VersionProvider
import dev.detekt.api.internal.whichDetekt

class DefaultVersionProvider : VersionProvider {

    override fun current(): String = whichDetekt()
}
