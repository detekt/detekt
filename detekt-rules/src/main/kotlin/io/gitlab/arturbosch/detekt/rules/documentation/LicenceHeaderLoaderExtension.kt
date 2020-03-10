package io.gitlab.arturbosch.detekt.rules.documentation

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.FileProcessListener
import io.gitlab.arturbosch.detekt.api.SetupContext
import io.gitlab.arturbosch.detekt.api.SingleAssign
import io.gitlab.arturbosch.detekt.api.UnstableApi
import io.gitlab.arturbosch.detekt.rules.documentation.AbsentOrWrongFileLicense.Companion.DEFAULT_LICENSE_TEMPLATE_FILE
import io.gitlab.arturbosch.detekt.rules.documentation.AbsentOrWrongFileLicense.Companion.PARAM_LICENSE_TEMPLATE_FILE
import io.gitlab.arturbosch.detekt.rules.documentation.AbsentOrWrongFileLicense.Companion.RULE_NAME
import org.jetbrains.kotlin.com.intellij.openapi.util.Key
import org.jetbrains.kotlin.com.intellij.openapi.util.text.StringUtilRt
import org.jetbrains.kotlin.psi.KtFile
import java.io.BufferedReader
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

@OptIn(UnstableApi::class)
class LicenceHeaderLoaderExtension : FileProcessListener {

    private var config: Config by SingleAssign()
    private var configPath: Path? = null

    override fun init(context: SetupContext) {
        this.config = context.config
        this.configPath = context.configUris.lastOrNull()?.let(Paths::get)
    }

    override fun onStart(files: List<KtFile>) {
        fun Config.isActive() = this.valueOrDefault(Config.ACTIVE_KEY, false)

        fun shouldRuleRun(): Boolean {
            val comments = config.subConfig("comments")
            val ruleConfig = comments.subConfig(RULE_NAME)
            return comments.isActive() && ruleConfig.isActive()
        }

        fun getPathToTemplate(): String = config.subConfig("comments")
            .subConfig(RULE_NAME)
            .valueOrDefault(PARAM_LICENSE_TEMPLATE_FILE, DEFAULT_LICENSE_TEMPLATE_FILE)

        fun loadLicence(dir: Path): String {
            val templateFile = dir.resolve(getPathToTemplate())

            require(Files.exists(templateFile)) {
                """
                Rule '$RULE_NAME': License template file not found at `${templateFile.toAbsolutePath()}`.
                Create file license header file or check your running path.
            """.trimIndent()
            }

            return Files.newBufferedReader(templateFile)
                .use(BufferedReader::readText)
                .let(StringUtilRt::convertLineSeparators)
        }

        fun cacheLicence(dir: Path) {
            val licenceHeader = loadLicence(dir)
            for (file in files) {
                file.putUserData(LICENCE_KEY, licenceHeader)
            }
        }

        if (configPath != null && shouldRuleRun()) {
            val configDir = configPath?.parent
            if (configDir != null) {
                cacheLicence(configDir)
            }
        }
    }
}

internal val LICENCE_KEY = Key.create<String>("LICENCE_HEADER")

internal fun KtFile.hasLicenseHeader(): Boolean = this.getUserData(LICENCE_KEY) != null

internal fun KtFile.getLicenseHeader(): String = this.getUserData(LICENCE_KEY) ?: error("License header expected")
