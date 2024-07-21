package io.gitlab.arturbosch.detekt.api

import io.github.detekt.test.utils.internal.FakeKtElement
import io.gitlab.arturbosch.detekt.test.location
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import kotlin.io.path.Path

class CorrectableCodeSmellSpec {

    @Test
    fun `toString contains all information`() {
        val codeSmell: CorrectableCodeSmell = object : CorrectableCodeSmell(
            entity = Entity(
                name = "TestEntity",
                signature = "TestEntitySignature",
                location = Location(
                    source = SourceLocation(1, 1),
                    endSource = SourceLocation(1, 1),
                    text = TextLocation(0, 0),
                    path = Path(""),
                ),
                ktElement = FakeKtElement()
            ),
            message = "TestMessage",
            autoCorrectEnabled = true
        ) {}

        assertThat(codeSmell.toString()).isEqualTo(
            "CorrectableCodeSmell(autoCorrectEnabled=true, " +
                "entity=Entity(name=TestEntity, signature=TestEntitySignature, " +
                "location=Location(source=1:1, endSource=1:1, text=0:0, " +
                "path=${codeSmell.location.path}), ktElement=FakeKtElement), " +
                "message=TestMessage, references=[])"
        )
    }
}
