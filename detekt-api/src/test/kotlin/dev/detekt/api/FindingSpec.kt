package dev.detekt.api

import dev.detekt.test.internal.FakeKtElement
import dev.detekt.test.location
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import kotlin.io.path.Path
import kotlin.io.path.absolute

class FindingSpec {

    @Test
    fun `toString contains all information`() {
        val finding = Finding(
            entity = Entity(
                signature = "TestEntitySignature",
                location = Location(
                    source = SourceLocation(1, 1),
                    endSource = SourceLocation(1, 1),
                    text = TextLocation(0, 0),
                    path = Path("/").absolute().resolve("Users/tester/detekt/TestFile.kt"),
                ),
                ktElement = FakeKtElement()
            ),
            message = "TestMessage",
            suppressReasons = listOf("Baseline"),
        )

        assertThat(finding.toString()).isEqualTo(
            "Finding(entity=Entity(signature=TestEntitySignature, " +
                "location=Location(source=1:1, endSource=1:1, text=0:0, path=${finding.location.path}), " +
                "ktElement=FakeKtElement), message=TestMessage, references=[], suppressReasons=[Baseline])"
        )
    }
}
