package io.gitlab.arturbosch.detekt.core.tooling

import dev.detekt.api.internal.whichDetekt
import dev.detekt.tooling.api.VersionProvider

class DefaultVersionProvider : VersionProvider {

    override fun current(): String = whichDetekt()
}
