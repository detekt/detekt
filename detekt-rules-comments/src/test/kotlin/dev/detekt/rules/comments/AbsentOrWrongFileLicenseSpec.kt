package dev.detekt.rules.comments

import dev.detekt.api.Config
import dev.detekt.api.Finding
import dev.detekt.api.testfixtures.TestSetupContext
import dev.detekt.core.config.YamlConfig
import dev.detekt.test.assertj.assertThat
import dev.detekt.test.lint
import dev.detekt.test.utils.compileContentForTest
import dev.detekt.test.utils.resource
import dev.detekt.utils.openSafeStream
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class AbsentOrWrongFileLicenseSpec {

    @Test
    fun `file with correct license header reports nothing`() {
        val findings = checkLicence(
            """
                /* LICENSE */
                package cases
            """.trimIndent()
        )

        assertThat(findings).isEmpty()
    }

    @Test
    fun `file with incorrect license header reports missed license header`() {
        val findings = checkLicence(
            """
                /* WRONG LICENSE */
                package cases
            """.trimIndent()
        )

        assertThat(findings).hasSize(1)
    }

    @Test
    fun `file with absent license header reports missed license header`() {
        val findings = checkLicence(
            """
                package cases
            """.trimIndent()
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

private fun checkLicence(@Language("kotlin") content: String, isRegexLicense: Boolean = false): List<Finding> {
    val file = compileContentForTest(content.trimIndent())

    val configFileName = if (isRegexLicense) "license-config-regex.yml" else "license-config.yml"
    val config = yamlConfig(configFileName)

    LicenceHeaderLoaderExtension().apply {
        init(
            TestSetupContext(
                config = config,
                configUris = listOf(resource(configFileName)),
            )
        )
        onStart(listOf(file))
    }

    return AbsentOrWrongFileLicense(Config.empty).lint(file)
}

private fun yamlConfig(name: String): Config = resource(name).toURL().openSafeStream().reader().use(YamlConfig::load)
