package io.gitlab.arturbosch.detekt.cli

import io.github.detekt.test.utils.NullPrintStream
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.SetupContext
import io.gitlab.arturbosch.detekt.api.UnstableApi
import io.gitlab.arturbosch.detekt.rules.documentation.LicenceHeaderLoaderExtension
import org.assertj.core.api.Assertions.assertThatCode
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.io.PrintStream
import java.net.URI

@UnstableApi
class ExtensionsSpec : Spek({

    describe("Licence header extension - #2503") {

        it("should not crash when using resources") {
            assertThatCode {
                val uris = CliArgs {
                    configResource = "extensions/config.yml"
                }.extractUris()

                LicenceHeaderLoaderExtension().init(object : SetupContext {
                    override val configUris: Collection<URI> = uris
                    override val config: Config = Config.empty
                    override val outPrinter: PrintStream = NullPrintStream()
                    override val errPrinter: PrintStream = NullPrintStream()
                    override val properties: Map<String, Any?> = HashMap()
                    override fun register(key: String, value: Any?) = TODO()
                })
            }.doesNotThrowAnyException()
        }
    }
})
