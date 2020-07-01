package io.gitlab.arturbosch.detekt.core.tooling

import io.github.detekt.tooling.api.VersionProvider
import io.gitlab.arturbosch.detekt.api.internal.whichDetekt

class DefaultVersionProvider : VersionProvider {

    override fun current(): String = checkNotNull(whichDetekt()) { "No version packaged. Invalid jar." }
}
