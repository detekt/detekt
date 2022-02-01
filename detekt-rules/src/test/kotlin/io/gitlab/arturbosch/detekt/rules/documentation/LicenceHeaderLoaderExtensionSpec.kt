package io.gitlab.arturbosch.detekt.rules.documentation

import io.github.detekt.test.utils.NullPrintStream
import io.github.detekt.test.utils.resource
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.SetupContext
import io.gitlab.arturbosch.detekt.api.UnstableApi
import org.assertj.core.api.Assertions.assertThatCode
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.io.PrintStream
import java.net.URI

@OptIn(UnstableApi::class)
class LicenceHeaderLoaderExtensionSpec {

    @Nested
    inner class `Licence header extension - #2503` {

        @Test
        fun `should not crash when using resources`() {
            assertThatCode {
                LicenceHeaderLoaderExtension().init(object : SetupContext {
                    override val configUris: Collection<URI> = listOf(resource("extensions/config.yml"))
                    override val config: Config = Config.empty
                    override val outputChannel: PrintStream = NullPrintStream()
                    override val errorChannel: PrintStream = NullPrintStream()
                    override val properties: Map<String, Any?> = HashMap()
                    override fun register(key: String, value: Any) = Unit
                })
            }.doesNotThrowAnyException()
        }
    }
}
