package dev.detekt.rules.potentialbugs

import dev.detekt.api.Config
import dev.detekt.test.assertj.assertThat
import dev.detekt.test.junit.KotlinCoreEnvironmentTest
import dev.detekt.test.lintWithContext
import dev.detekt.test.utils.KotlinEnvironmentContainer
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest(additionalJavaSourcePaths = ["java"])
class IgnoredJavaDefaultMethodSpec(private val env: KotlinEnvironmentContainer) {
    private val subject = IgnoredJavaDefaultMethod(Config.empty)

    @Test
    fun `reports delegation to a Java interface with a default method`() {
        val code = """
            import com.example.delegation.JavaInterfaceWithDefaults

            class Wrapper(private val delegate: JavaInterfaceWithDefaults) :
                JavaInterfaceWithDefaults by delegate
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `reports a default method inherited from a Java super-interface`() {
        val code = """
            import com.example.delegation.JavaSubInterface

            class Wrapper(private val delegate: JavaSubInterface) : JavaSubInterface by delegate
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `does not report when the class overrides the default method`() {
        val code = """
            import com.example.delegation.JavaInterfaceWithDefaults

            class Wrapper(private val delegate: JavaInterfaceWithDefaults) :
                JavaInterfaceWithDefaults by delegate {
                override fun optional() {
                    delegate.optional()
                }
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does not report a Java interface without default methods`() {
        val code = """
            import com.example.delegation.JavaInterfaceWithoutDefaults

            class Wrapper(private val delegate: JavaInterfaceWithoutDefaults) :
                JavaInterfaceWithoutDefaults by delegate
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does not report a static method on a Java interface`() {
        val code = """
            import com.example.delegation.JavaInterfaceWithStatics

            class Wrapper(private val delegate: JavaInterfaceWithStatics) :
                JavaInterfaceWithStatics by delegate
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does not report a Kotlin interface with a default method`() {
        val code = """
            interface KotlinInterface {
                fun required()
                fun optional() {}
            }

            class Wrapper(private val delegate: KotlinInterface) : KotlinInterface by delegate
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does not report delegation to a Kotlin collection mapped onto a Java interface`() {
        val code = """
            class MyList(private val list: List<String>) : List<String> by list
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does not report delegation to a mutable Kotlin collection`() {
        val code = """
            class MyList(private val list: MutableList<String>) : MutableList<String> by list
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does not report a class implementing the Java interface without delegation`() {
        val code = """
            import com.example.delegation.JavaInterfaceWithDefaults

            class Wrapper : JavaInterfaceWithDefaults {
                override fun required() {}
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does not report when a superclass overrides the default method`() {
        val code = """
            import com.example.delegation.JavaInterfaceWithDefaults

            abstract class Base : JavaInterfaceWithDefaults {
                override fun optional() {}
            }

            class Wrapper(private val delegate: JavaInterfaceWithDefaults) :
                Base(), JavaInterfaceWithDefaults by delegate {
                override fun required() {}
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `reports a default method reached through a Kotlin interface extending a Java one`() {
        val code = """
            import com.example.delegation.JavaInterfaceWithDefaults

            interface KotlinSub : JavaInterfaceWithDefaults

            class Wrapper(private val delegate: KotlinSub) : KotlinSub by delegate
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code))
            .singleElement()
            .hasMessage(
                "Delegating to `KotlinSub` ignores what the delegate does for the default " +
                    "method `optional()`. Override it to delegate explicitly."
            )
    }

    @Test
    fun `does not report when a Kotlin interface in the chain overrides the default method`() {
        val code = """
            import com.example.delegation.JavaInterfaceWithDefaults

            interface KotlinSub : JavaInterfaceWithDefaults {
                override fun optional() {}
            }

            class Wrapper(private val delegate: KotlinSub) : KotlinSub by delegate
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `reports the default method but not the private method of a Java interface`() {
        val code = """
            import com.example.delegation.JavaInterfaceWithPrivate

            class Wrapper(private val delegate: JavaInterfaceWithPrivate) :
                JavaInterfaceWithPrivate by delegate
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code))
            .singleElement()
            .hasMessage(
                "Delegating to `JavaInterfaceWithPrivate` ignores what the delegate does for the " +
                    "default method `run()`. Override it to delegate explicitly."
            )
    }

    @Test
    fun `reports the default methods of a generic Java interface`() {
        val code = """
            import com.example.delegation.JavaGenericInterface

            class Wrapper(private val delegate: JavaGenericInterface<String>) :
                JavaGenericInterface<String> by delegate
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code))
            .singleElement()
            .hasMessage(
                "Delegating to `JavaGenericInterface` ignores what the delegate does for the " +
                    "default methods `substituted(T)`, `unsubstituted()`. Override them to delegate explicitly."
            )
    }

    @Test
    fun `does not report a generic Java interface whose defaults a superclass overrides`() {
        val code = """
            import com.example.delegation.JavaGenericInterface

            abstract class Base : JavaGenericInterface<String> {
                override fun substituted(input: String): String = input
                override fun unsubstituted(): String = ""
            }

            class Wrapper(private val delegate: JavaGenericInterface<String>) :
                Base(), JavaGenericInterface<String> by delegate {
                override fun required(): String = ""
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `reports a default method of a Java interface from the standard library`() {
        val code = """
            class Wrapper(private val delegate: Comparator<String>) : Comparator<String> by delegate
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `reports both overloads with their parameter types`() {
        val code = """
            import com.example.delegation.JavaOverloadedDefaults

            class Wrapper(
                private val delegate: JavaOverloadedDefaults,
            ) : JavaOverloadedDefaults by delegate
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code))
            .singleElement()
            .hasMessage(
                "Delegating to `JavaOverloadedDefaults` ignores what the delegate does for the " +
                    "default methods `write(Int)`, `write(String)`. Override them to delegate explicitly."
            )
    }

    @Test
    fun `reports only the overload the class leaves unforwarded`() {
        val code = """
            import com.example.delegation.JavaOverloadedDefaults

            class Wrapper(
                private val delegate: JavaOverloadedDefaults,
            ) : JavaOverloadedDefaults by delegate {
                override fun write(text: String) {
                    delegate.write(text)
                }
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code))
            .singleElement()
            .hasMessage(
                "Delegating to `JavaOverloadedDefaults` ignores what the delegate does for the " +
                    "default method `write(Int)`. Override it to delegate explicitly."
            )
    }

    @Test
    fun `reports only the same-named overload the class leaves unforwarded`() {
        val code = """
            import com.example.delegation.JavaOverloadA
            import com.example.delegation.JavaOverloadB

            class Wrapper(
                private val first: JavaOverloadA,
                private val second: JavaOverloadB,
            ) : JavaOverloadA by first, JavaOverloadB by second {
                override fun foo(s: String) {
                    second.foo(s)
                }
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code))
            .singleElement()
            .hasMessage(
                "Delegating to `JavaOverloadA` ignores what the delegate does for the default " +
                    "method `foo()`. Override it to delegate explicitly."
            )
    }

    @Test
    fun `does not report a same-named default of an interface implemented without delegation`() {
        val code = """
            import com.example.delegation.JavaOverloadA
            import com.example.delegation.JavaOverloadB

            class Wrapper(private val second: JavaOverloadB) : JavaOverloadA, JavaOverloadB by second {
                override fun requiredA() {}
                override fun foo(s: String) {
                    second.foo(s)
                }
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `reports each delegated Java interface separately`() {
        val code = """
            import com.example.delegation.JavaInterfaceWithDefaults
            import com.example.delegation.JavaOtherInterface

            class Wrapper(
                private val first: JavaInterfaceWithDefaults,
                private val second: JavaOtherInterface,
            ) : JavaInterfaceWithDefaults by first, JavaOtherInterface by second
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).hasSize(2)
    }

    @Test
    fun `reports a vararg parameter as a vararg`() {
        val code = """
            import com.example.delegation.JavaVarargDefault

            class Wrapper(private val delegate: JavaVarargDefault) : JavaVarargDefault by delegate
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code))
            .singleElement()
            .hasMessage(
                "Delegating to `JavaVarargDefault` ignores what the delegate does for the default " +
                    "method `log(vararg String)`. Override it to delegate explicitly."
            )
    }

    @Test
    fun `reports both overloads that differ only by array element type`() {
        val code = """
            import com.example.delegation.JavaArrayOverloads

            class Wrapper(private val delegate: JavaArrayOverloads) : JavaArrayOverloads by delegate
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code))
            .singleElement()
            .hasMessage(
                "Delegating to `JavaArrayOverloads` ignores what the delegate does for the default " +
                    "methods `put(Array<Int>)`, `put(Array<String>)`. Override them to delegate explicitly."
            )
    }

    @Test
    fun `reports the whole declaration when two overloads render the same`() {
        val code = """
            import com.example.delegation.JavaBoxedOverloads

            class Wrapper(private val delegate: JavaBoxedOverloads) : JavaBoxedOverloads by delegate
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code))
            .singleElement()
            .hasMessage(
                "Delegating to `JavaBoxedOverloads` ignores what the delegate does for the default " +
                    "methods `fun write(number: kotlin.Int)`, `fun write(number: kotlin.Int?)`. " +
                    "Override them to delegate explicitly."
            )
    }

    @Test
    fun `reports the whole declaration when parameter types share a simple name`() {
        val code = """
            import com.example.delegation.JavaCrossPackageOverloads

            class Wrapper(private val delegate: JavaCrossPackageOverloads) :
                JavaCrossPackageOverloads by delegate
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code))
            .singleElement()
            .hasMessage(
                "Delegating to `JavaCrossPackageOverloads` ignores what the delegate does for the " +
                    "default methods `fun handle(stamp: com.example.delegation.Stamp?)`, " +
                    "`fun handle(stamp: com.example.other.Stamp?)`. Override them to delegate explicitly."
            )
    }

    @Test
    fun `reports the whole declaration when two overloads differ only by type parameter bound`() {
        val code = """
            import com.example.delegation.JavaGenericBoundOverloads

            class Wrapper(private val delegate: JavaGenericBoundOverloads) :
                JavaGenericBoundOverloads by delegate
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code))
            .singleElement()
            .hasMessage(
                "Delegating to `JavaGenericBoundOverloads` ignores what the delegate does for the " +
                    "default methods `fun <T : kotlin.CharSequence?> accept(value: T?)`, " +
                    "`fun <T : kotlin.Number?> accept(value: T?)`. Override them to delegate explicitly."
            )
    }

    @Test
    fun `reports a deprecated overload on one line`() {
        val code = """
            import com.example.delegation.JavaDeprecatedOverloads

            class Wrapper(private val delegate: JavaDeprecatedOverloads) :
                JavaDeprecatedOverloads by delegate
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code))
            .singleElement()
            .hasMessage(
                "Delegating to `JavaDeprecatedOverloads` ignores what the delegate does for the " +
                    "default methods `fun write(number: kotlin.Int)`, " +
                    "`fun write(number: kotlin.Int?)`. Override them to delegate explicitly."
            )
    }
}
