package dev.detekt.rules.comments

import dev.detekt.api.testfixtures.TestSetupContext
import dev.detekt.test.utils.resource
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
