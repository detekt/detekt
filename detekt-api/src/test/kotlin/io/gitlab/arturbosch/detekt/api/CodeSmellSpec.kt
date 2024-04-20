package io.gitlab.arturbosch.detekt.api

import io.gitlab.arturbosch.detekt.test.fromRelative
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.io.File
import kotlin.io.path.Path

class CodeSmellSpec {

    @Test
    fun `toString contains all information`() {
        val codeSmell = CodeSmell(
            entity = Entity(
                name = "TestEntity",
                signature = "TestEntitySignature",
                location = Location(
                    source = SourceLocation(1, 1),
                    endSource = SourceLocation(1, 1),
                    text = TextLocation(0, 0),
                    filePath = fromRelative(Path("/Users/tester/detekt/"), Path("TestFile.kt"))
                ),
                ktElement = null
            ),
            message = "TestMessage"
        )
        val basePath = "${File.separator}Users${File.separator}tester${File.separator}detekt"

        assertThat(codeSmell.toString()).isEqualTo(
            "CodeSmell(entity=Entity(name=TestEntity, signature=TestEntitySignature, " +
                "location=Location(source=1:1, endSource=1:1, text=0:0, " +
                "filePath=FilePath(absolutePath=$basePath${File.separator}TestFile.kt, " +
                "basePath=$basePath, relativePath=TestFile.kt)), ktElement=null), message=TestMessage, " +
                "references=[])"
        )
    }
}
