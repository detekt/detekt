package io.gitlab.arturbosch.detekt.core.extensions

import dev.detekt.api.ConfigValidator
import io.gitlab.arturbosch.detekt.core.createNullLoggingSpec
import io.gitlab.arturbosch.detekt.core.tooling.withSettings
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class LoadingSpec {

    @Test
    fun `extensions can be excluded via ExtensionSpec`() {
        val providers = createNullLoggingSpec {
            extensions {
                disableExtension("SampleConfigValidator")
            }
        }.withSettings { loadExtensions<ConfigValidator>(this) }

        assertThat(providers.map { it.id })
            .doesNotContain("SampleConfigValidator")
    }
}
