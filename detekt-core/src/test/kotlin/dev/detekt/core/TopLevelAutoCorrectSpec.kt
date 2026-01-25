package dev.detekt.core

import dev.detekt.api.Config
import dev.detekt.api.Detektion
import dev.detekt.api.FileProcessListener
import dev.detekt.api.Rule
import dev.detekt.api.RuleSet
import dev.detekt.api.RuleSetId
import dev.detekt.api.RuleSetProvider
import dev.detekt.core.tooling.AnalysisFacade
import dev.detekt.core.tooling.Lifecycle
import dev.detekt.test.utils.NullPrintStream
import dev.detekt.test.utils.readResourceContent
import dev.detekt.test.utils.resourceAsPath
import dev.detekt.test.utils.resourceUrl
import dev.detekt.tooling.api.spec.ProcessingSpec
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.psi.KtAnnotation
import org.jetbrains.kotlin.psi.KtFile
import org.junit.jupiter.api.Test

class TopLevelAutoCorrectSpec {

    @Test
    fun `should format the test file but not print the modified content to disc`() {
        val fileContentBeforeAutoCorrect = readResourceContent("cases/Test.kt")
        val fileUnderTest = resourceAsPath("cases/Test.kt")
        val spec = ProcessingSpec {
            project {
                inputPaths = listOf(fileUnderTest)
            }
            config {
                resources = listOf(resourceUrl("configs/rule-and-ruleset-autocorrect-true.yaml"))
            }
            rules {
                autoCorrect = false // fixture
            }
            logging {
                outputChannel = NullPrintStream()
                errorChannel = NullPrintStream()
            }
        }

        val contentChangedListener = object : FileProcessListener {
            override val id: String = "ContentChangedListener"

            override fun onFinish(files: List<KtFile>, result: Detektion): Detektion {
                assertThat(files).hasSize(1)
                assertThat(files[0].text).isNotEqualToIgnoringWhitespace(fileContentBeforeAutoCorrect)
                return result
            }
        }

        AnalysisFacade(spec).runAnalysis { settings ->
            Lifecycle(
                settings.config,
                settings,
                processorsProvider = { listOf(contentChangedListener) },
                ruleSetsProvider = { listOf(TopLevelAutoCorrectProvider()) }
            )
        }

        assertThat(readResourceContent("cases/Test.kt")).isEqualTo(fileContentBeforeAutoCorrect)
    }
}

private class DeleteAnnotationsRule(config: Config) : Rule(config, "") {
    override fun visitAnnotation(annotation: KtAnnotation) {
        annotation.delete()
    }
}

private class TopLevelAutoCorrectProvider : RuleSetProvider {
    override val ruleSetId = RuleSetId("test-rule-set")
    override fun instance() = RuleSet(ruleSetId, listOf(::DeleteAnnotationsRule))
}
