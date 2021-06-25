package io.gitlab.arturbosch.detekt.invoke

import io.gitlab.arturbosch.detekt.gradle.TestFileCollection
import io.gitlab.arturbosch.detekt.internal.ClassLoaderCache
import org.assertj.core.api.Assertions.assertThatCode
import org.gradle.api.GradleException
import org.spekframework.spek2.Spek
import java.net.URLClassLoader

internal class DefaultCliInvokerSpec : Spek({

    test("catches ClassCastException and fails build") {
        val stubbedCache = ClassLoaderCache { URLClassLoader(emptyArray()) }

        assertThatCode {
            DefaultCliInvoker(stubbedCache)
                .invokeCli(listOf(), TestFileCollection(), "detekt", ignoreFailures = false)
        }.isInstanceOf(GradleException::class.java)
            .hasMessageContaining("testing reflection wrapper...")
    }
})
