package io.gitlab.arturbosch.detekt.rules.documentation

import dev.detekt.test.utils.resource
import dev.detekt.api.test.TestSetupContext
import org.assertj.core.api.Assertions.assertThatCode
import org.junit.jupiter.api.Test

class LicenceHeaderLoaderExtensionSpec {

    @Test
    fun `should not crash when using resources - #2503`() {
        assertThatCode {
            LicenceHeaderLoaderExtension().init(
                TestSetupContext(configUris = listOf(resource("extensions/config.yml")))
            )
        }.doesNotThrowAnyException()
    }
}
