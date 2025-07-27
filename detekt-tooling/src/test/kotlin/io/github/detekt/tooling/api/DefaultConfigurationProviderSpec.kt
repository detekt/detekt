package io.github.detekt.tooling.api

import io.github.detekt.test.utils.createTempFileForTest
import io.github.detekt.tooling.api.spec.ExtensionId
import io.github.detekt.tooling.api.spec.ExtensionsSpec
import dev.detekt.api.Config
import org.assertj.core.api.Assertions.assertThatCode
import org.junit.jupiter.api.Test
import java.nio.file.Path

class DefaultConfigurationProviderSpec {

    @Test
    fun `loads first found instance`() {
        assertThatCode {
            DefaultConfigurationProvider.load(Spec)
                .copy(createTempFileForTest("test", "test"))
        }.doesNotThrowAnyException()
    }
}

internal class TestConfigurationProvider : DefaultConfigurationProvider {
    override fun init(extensionsSpec: ExtensionsSpec) {
        // no-op
    }

    override fun get(): Config = Config.empty

    override fun copy(targetLocation: Path) {
        // nothing
    }
}

private object Spec : ExtensionsSpec {
    override val plugins: ExtensionsSpec.Plugins?
        get() = null
    override val disabledExtensions: Set<ExtensionId>
        get() = error("No expected call")
}
