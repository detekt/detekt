package io.gitlab.arturbosch.detekt.rules.documentation

import io.github.detekt.test.utils.NullPrintStream
import io.github.detekt.test.utils.compileContentForTest
import io.github.detekt.test.utils.resourceAsPath
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.SetupContext
import io.gitlab.arturbosch.detekt.api.UnstableApi
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.lint
import io.gitlab.arturbosch.detekt.test.yamlConfig
import org.jetbrains.kotlin.resolve.BindingContext
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.io.PrintStream
import java.net.URI

class AbsentOrWrongFileLicenseSpec {

    @Test
    fun `file with correct license header reports nothing`() {
        val findings = checkLicence(
            """
            /* LICENSE */
            package cases
            """
        )

        assertThat(findings).isEmpty()
    }

    @Test
    fun `file with incorrect license header reports missed license header`() {
        val findings = checkLicence(
            """
            /* WRONG LICENSE */
            package cases
            """
        )

        assertThat(findings).hasSize(1)
    }

    @Test
    fun `file with absent license header reports missed license header`() {
        val findings = checkLicence(
            """
            package cases
            """
        )

        assertThat(findings).hasSize(1)
    }

    @Nested
    inner class `file with correct license header using regex matching` {

        @Test
        fun `reports nothing for 2016`() {
            val findings = checkLicence(
                """
                    //
                    // Copyright 2016 Artur Bosch & Contributors
                    //     http://www.apache.org/licenses/LICENSE-2.0
                    // See the License for the specific language governing permissions and
                    // limitations under the License.
                    //
                    package cases
                """.trimIndent(),
                isRegexLicense = true
            )

            assertThat(findings).isEmpty()
        }

        @Test
        fun `reports nothing for 2021`() {
            val findings = checkLicence(
                """
                    //
                    // Copyright 2021 Artur Bosch & Contributors
                    //     http://www.apache.org/licenses/LICENSE-2.0
                    // See the License for the specific language governing permissions and
                    // limitations under the License.
                    //
                    package cases
                """.trimIndent(),
                isRegexLicense = true
            )

            assertThat(findings).isEmpty()
        }
    }

    @Nested
    inner class `file with incorrect license header using regex matching` {

        @Test
        fun `file with missing license header`() {
            val findings = checkLicence(
                """
                    package cases
                """.trimIndent(),
                isRegexLicense = true
            )

            assertThat(findings).hasSize(1)
        }

        @Test
        fun `file with license header not on the first line`() {
            val findings = checkLicence(
                """
                    package cases
                    //
                    // Copyright 2021 Artur Bosch & Contributors
                    //     http://www.apache.org/licenses/LICENSE-2.0
                    // See the License for the specific language governing permissions and
                    // limitations under the License.
                    //
                """.trimIndent(),
                isRegexLicense = true
            )

            assertThat(findings).hasSize(1)
        }

        @Test
        fun `file with incomplete license header`() {
            val findings = checkLicence(
                """
                    //
                    // Copyright 2021 Artur Bosch & Contributors
                    //
                    package cases
                """.trimIndent(),
                isRegexLicense = true
            )

            assertThat(findings).hasSize(1)
        }

        @Test
        fun `file with too many empty likes in license header`() {
            val findings = checkLicence(
                """
                    //
                    //
                    // Copyright 2021 Artur Bosch & Contributors
                    //     http://www.apache.org/licenses/LICENSE-2.0
                    // See the License for the specific language governing permissions and
                    // limitations under the License.
                    //
                    package cases
                """.trimIndent(),
                isRegexLicense = true
            )

            assertThat(findings).hasSize(1)
        }

        @Test
        fun `file with incorrect year in license header`() {
            val findings = checkLicence(
                """
                    //
                    // Copyright 202 Artur Bosch & Contributors
                    //     http://www.apache.org/licenses/LICENSE-2.0
                    // See the License for the specific language governing permissions and
                    // limitations under the License.
                    //
                    package cases
                """.trimIndent(),
                isRegexLicense = true
            )

            assertThat(findings).hasSize(1)
        }
    }
}

@OptIn(UnstableApi::class)
private fun checkLicence(content: String, isRegexLicense: Boolean = false): List<Finding> {
    val file = compileContentForTest(content.trimIndent())

    val configFileName = if (isRegexLicense) "license-config-regex.yml" else "license-config.yml"
    val resource = resourceAsPath(configFileName)
    val config = yamlConfig(configFileName)

    LicenceHeaderLoaderExtension().apply {
        init(object : SetupContext {
            override val configUris: Collection<URI> = listOf(resource.toUri())
            override val config: Config = config
            override val outputChannel: PrintStream = NullPrintStream()
            override val errorChannel: PrintStream = NullPrintStream()
            override val properties: MutableMap<String, Any?> = HashMap()
            override fun register(key: String, value: Any) {
                properties[key] = value
            }
        })
        onStart(listOf(file), BindingContext.EMPTY)
    }

    return AbsentOrWrongFileLicense().lint(file)
}
