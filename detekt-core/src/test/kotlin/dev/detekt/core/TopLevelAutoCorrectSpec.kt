package dev.detekt.core

import dev.detekt.api.Config
import dev.detekt.api.Detektion
import dev.detekt.api.Entity
import dev.detekt.api.FileProcessListener
import dev.detekt.api.Finding
import dev.detekt.api.Rule
import dev.detekt.api.RuleSet
import dev.detekt.api.RuleSetId
import dev.detekt.api.RuleSetProvider
import dev.detekt.api.internal.AutoCorrectable
import dev.detekt.core.tooling.AnalysisFacade
import dev.detekt.core.tooling.Lifecycle
import dev.detekt.test.utils.NullPrintStream
import dev.detekt.test.utils.readResourceContent
import dev.detekt.test.utils.resourceAsPath
import dev.detekt.test.utils.resourceUrl
import dev.detekt.tooling.api.spec.ProcessingSpec
import dev.detekt.tooling.api.spec.RulesSpec
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.psi.KtAnnotation
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtPsiFactory
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path
import kotlin.io.path.readText
import kotlin.io.path.writeText

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

    @Test
    fun `autoCorrect PSI mutations on analysis API source files do not crash and are written to disc`(
        @TempDir tempDir: Path,
    ) {
        val fileUnderTest = tempDir.resolve("Foo.kt")
        val originalContent = """
            class Foo {
            }

        """.trimIndent()
        fileUnderTest.writeText(originalContent)

        val spec = ProcessingSpec {
            project {
                inputPaths = listOf(fileUnderTest)
                basePath = tempDir
            }
            config {
                resources = listOf(resourceUrl("configs/minimal-auto-correct.yaml"))
            }
            rules {
                autoCorrect = true
                failurePolicy = RulesSpec.FailurePolicy.NeverFail
            }
            logging {
                outputChannel = NullPrintStream()
                errorChannel = NullPrintStream()
            }
        }

        val result = AnalysisFacade(spec).runAnalysis { settings ->
            Lifecycle(
                settings.config,
                settings,
                ruleSetsProvider = { listOf(MinimalAutoCorrectProvider()) }
            )
        }

        assertThat(result.error).isNull()
        assertThat(fileUnderTest.readText()).contains("// autoCorrect")
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

@AutoCorrectable(since = "2.0.0")
private class MinimalAutoCorrectRule(config: Config) : Rule(config, "Triggers PSI modification via auto-correct.") {
    override fun visitClass(klass: KtClass) {
        super.visitClass(klass)
        report(Finding(Entity.atName(klass), "Found a class: ${klass.name}"))
        if (autoCorrect) {
            val factory = KtPsiFactory(klass.project)
            val body = klass.body ?: return
            body.addBefore(factory.createWhiteSpace(" "), body.firstChild)
            body.addBefore(factory.createComment("// autoCorrect"), body.rBrace)
        }
    }
}

private class MinimalAutoCorrectProvider : RuleSetProvider {
    override val ruleSetId = RuleSetId("test-rule-set")
    override fun instance() = RuleSet(ruleSetId, listOf(::MinimalAutoCorrectRule))
}
