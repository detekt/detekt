package io.gitlab.arturbosch.detekt.invoke

import io.gitlab.arturbosch.detekt.gradle.TestFileCollection
import io.gitlab.arturbosch.detekt.internal.ClassLoaderCache
import org.assertj.core.api.Assertions.assertThatCode
import org.gradle.api.GradleException
import org.gradle.api.file.FileCollection
import org.spekframework.spek2.Spek
import java.net.URLClassLoader

internal class DefaultCliInvokerTest : Spek({

    test("catches ClassCastException and fails build") {
        val stubbedCache = object : ClassLoaderCache {
            override fun getOrCreate(classpath: FileCollection): URLClassLoader {
                return URLClassLoader(emptyArray())
            }
        }

        assertThatCode {
            DefaultCliInvoker(stubbedCache)
                .invokeCli(listOf(), TestFileCollection(), "detekt", ignoreFailures = false)
        }.isInstanceOf(GradleException::class.java)
            .hasMessageContaining("testing reflection wrapper...")
    }
})
