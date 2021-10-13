package io.gitlab.arturbosch.detekt.report

import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.io.File
import java.net.URL

internal class SarifReportMergerSpec : Spek({
    describe("sarif report merger") {
        it("merges input into output successfully") {
            val input1 = resourceAsFile("input_1.sarif.json")
            val input2 = resourceAsFile("input_2.sarif.json")
            val output = File.createTempFile("output", "xml")
            SarifReportMerger.merge(listOf(input1, input2), output)

            val expectedOutput = resourceAsFile("output.sarif.json")
            assertThat(output.readText()).isEqualToNormalizingNewlines(expectedOutput.readText())
        }
    }
})

internal object Resources

internal fun resourceUrl(name: String): URL {
    val explicitName = if (name.startsWith("/")) name else "/$name"
    val resource = Resources::class.java.getResource(explicitName)
    requireNotNull(resource) { "Make sure the resource '$name' exists!" }
    return resource
}

internal fun resourceAsFile(name: String): File = File(resourceUrl(name).path)
