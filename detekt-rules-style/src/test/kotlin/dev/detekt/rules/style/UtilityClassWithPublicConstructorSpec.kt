package dev.detekt.rules.style

import dev.detekt.api.Config
import dev.detekt.api.Finding
import dev.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class UtilityClassWithPublicConstructorSpec {

    val subject = UtilityClassWithPublicConstructor(Config.Empty)

    @Nested
    inner class `several UtilityClassWithPublicConstructor rule violations` {

        lateinit var findings: List<Finding>

        @BeforeEach
        fun beforeEachTest() {
            findings = subject.lint(
                """
                    class UtilityClassWithDefaultConstructor { // violation
                        companion object {
                            val C = 0
                        }
                    }
                    
                    class UtilityClassWithPrimaryConstructor1 constructor() { // violation
                        companion object {
                            val C = 0
                        }
                    }
                    
                    class UtilityClassWithPrimaryConstructor2() { // violation
                        companion object {
                            val C = 0
                        }
                    }
                    
                    @Suppress("ConvertSecondaryConstructorToPrimary", "RedundantSuppression")
                    class UtilityClassWithSecondaryConstructor { // violation
                        constructor()
                    
                        companion object {
                            val C = 0
                        }
                    }
                    
                    class UtilityClassWithEmptyCompanionObj { // violation
                        companion object
                    }
                    
                    @Suppress("ConvertSecondaryConstructorToPrimary", "RedundantSuppression")
                    open class OpenUtilityClass { // violation - utility class should be final
                        internal constructor()
                    
                        companion object {
                            val C = 0
                        }
                    }
                    
                    sealed class SealedParent {
                        companion object {
                            fun create(foo: Int?, bar: String?): SealedParent? =
                                when {
                                    foo != null -> FooChild(foo)
                                    bar != null -> BarChild(bar)
                                    else -> null
                                }
                        }
                    }
                    
                    data class FooChild(val foo: Int) : SealedParent()
                    data class BarChild(val bar: String) : SealedParent()
                """.trimIndent()
            )
        }

        @Test
        fun `reports utility classes with a public constructor`() {
            assertThat(findings).hasSize(6)
        }

        @Test
        fun `reports utility classes which are marked as open`() {
            val count =
                findings.count { it.message.contains("The utility class OpenUtilityClass should be final.") }
            assertThat(count).isEqualTo(1)
        }
    }

    @Nested
    inner class `several classes which adhere to the UtilityClassWithPublicConstructor rule` {

        @Suppress("LongMethod") // TODO split this up into multiple test case functions.
        @Test
        fun `does not report given classes`() {
            val findings = subject.lint(
                """
                    class UtilityClassWithPrimaryPrivateConstructorOk private constructor() {
                        companion object {
                            val C = 0
                        }
                    }
                    
                    class UtilityClassWithPrimaryInternalConstructorOk internal constructor() {
                        companion object {
                            val C = 0
                        }
                    }
                    
                    @Suppress("ConvertSecondaryConstructorToPrimary", "RedundantSuppression")
                    class UtilityClassWithSecondaryConstructorOk {
                        private constructor()
                    
                        companion object {
                            val C = 0
                        }
                    }
                    
                    @Suppress("ConvertSecondaryConstructorToPrimary", "RedundantSuppression")
                    class NoUtilityClassBecauseOfInterface : InterfaceWithCompanionObject {
                        constructor()
                    
                        companion object {
                            val C = 0
                        }
                    }
                    
                    open class UtilityClassesNegativeParent(val i: Int)
                    @Suppress("ConvertSecondaryConstructorToPrimary", "RedundantSuppression")
                    class NoUtilityClassBecauseOfInheritance : UtilityClassesNegativeParent {
                        constructor(i: Int) : super(i)
                    
                        companion object {
                            val C = 0
                        }
                    }
                    
                    class NoUtilityClasses {
                        private val i = 0
                    
                        class EmptyClass1 {}
                        class EmptyClass2
                    
                        @Suppress("ConvertSecondaryConstructorToPrimary", "RedundantSuppression")
                        class ClassWithSecondaryConstructor {
                            constructor()
                        }
                    
                        class ClassWithInstanceFunc {
                    
                            fun f() {}
                    
                            companion object {
                                val C = 0
                            }
                        }
                    
                        class ClassWithPrimaryConstructorParameter1(val i: Int) {
                    
                            companion object {
                                val C = 0
                            }
                        }
                    
                        class ClassWithPrimaryConstructorParameter2 constructor(val i: Int) {
                    
                            companion object {
                                val C = 0
                            }
                        }
                    
                        @Suppress("ConvertSecondaryConstructorToPrimary", "RedundantSuppression")
                        class ClassWithSecondaryConstructorParameter {
                    
                            constructor(i: Int)
                    
                            companion object {
                                val C = 0
                            }
                        }
                    
                        companion object {
                            val C = 0
                        }
                    }
                    
                    interface InterfaceWithCompanionObject {
                        companion object {
                            val C = 0
                        }
                    }
                    
                    interface SomeInterface
                    class SomeImplementation : SomeInterface
                    class NotUtilityClass : SomeInterface by SomeImplementation() {
                        // Issue#682 - Class with delegate is no utility class
                        companion object {
                            val C = 0
                        }
                    }
                """.trimIndent()
            )
            assertThat(findings).isEmpty()
        }
    }

    @Nested
    inner class `annotations class` {

        @Test
        fun `should not get triggered for utility class`() {
            val code = """
                @Retention(AnnotationRetention.SOURCE)
                @StringDef(
                    Gender.MALE,
                    Gender.FEMALE
                )
                annotation class Gender {
                    companion object {
                        const val MALE = "male"
                        const val FEMALE = "female"
                    }
                }
            """.trimIndent()
            assertThat(subject.lint(code, compile = false)).isEmpty()
        }
    }
}
