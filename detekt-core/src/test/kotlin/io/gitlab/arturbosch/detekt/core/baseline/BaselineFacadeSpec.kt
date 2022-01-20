package io.gitlab.arturbosch.detekt.core.baseline

import io.github.detekt.test.utils.createTempDirectoryForTest
import io.github.detekt.test.utils.resourceAsPath
import io.gitlab.arturbosch.detekt.test.TestDetektion
import io.gitlab.arturbosch.detekt.test.createFinding
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.nio.file.Files

class BaselineFacadeSpec : Spek({

    describe("a baseline facade") {

        val baselineFile by memoized { createTempDirectoryForTest("baseline_format").resolve("baseline.xml") }
        val validBaseline = resourceAsPath("/baseline_feature/valid-baseline.xml")

        afterEachTest {
            Files.deleteIfExists(baselineFile)
        }

        it("returns a BaselineFilteredResult when the baseline exists") {
            val detektion = BaselineFacade().transformResult(validBaseline, TestDetektion())

            assertThat(detektion).isInstanceOf(BaselineFilteredResult::class.java)
        }

        it("returns the same detektion when the baseline doesn't exist") {
            val initialDetektion = TestDetektion()
            val detektion = BaselineFacade().transformResult(baselineFile, initialDetektion)

            assertThat(detektion).isEqualTo(initialDetektion)
        }

        it("doesn't create a baseline file without findings") {
            BaselineFacade().createOrUpdate(baselineFile, emptyList())

            assertThat(baselineFile).doesNotExist()
        }

        it("creates on top of an existing a baseline file without findings") {
            Files.copy(validBaseline, baselineFile)

            BaselineFacade().createOrUpdate(baselineFile, emptyList())

            assertThat(baselineFile).hasContent(
                """
                <?xml version="1.0" ?>
                <SmellBaseline>
                  <ManuallySuppressedIssues>
                    <ID>LongParameterList:Signature</ID>
                    <ID>LongMethod:Signature</ID>
                  </ManuallySuppressedIssues>
                  <CurrentIssues></CurrentIssues>
                </SmellBaseline>
                """.trimIndent()
            )
        }

        it("creates on top of an existing a baseline file with findings") {
            Files.copy(validBaseline, baselineFile)

            BaselineFacade().createOrUpdate(baselineFile, listOf(createFinding()))

            assertThat(baselineFile).hasContent(
                """
                <?xml version="1.0" ?>
                <SmellBaseline>
                  <ManuallySuppressedIssues>
                    <ID>LongParameterList:Signature</ID>
                    <ID>LongMethod:Signature</ID>
                  </ManuallySuppressedIssues>
                  <CurrentIssues>
                    <ID>TestSmell:TestEntitySignature</ID>
                  </CurrentIssues>
                </SmellBaseline>
                """.trimIndent()
            )
        }
    }
})
