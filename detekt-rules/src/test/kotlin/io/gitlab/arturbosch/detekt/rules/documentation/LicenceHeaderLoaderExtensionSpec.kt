package io.gitlab.arturbosch.detekt.rules.documentation

import dev.detekt.api.testfixtures.TestSetupContext
import io.github.detekt.test.utils.resource
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
