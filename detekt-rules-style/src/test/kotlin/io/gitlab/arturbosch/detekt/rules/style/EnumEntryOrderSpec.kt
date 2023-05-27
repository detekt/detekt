package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class EnumEntryOrderSpec(private val env: KotlinCoreEnvironment) {

    private val subject = EnumEntryOrder(
        TestConfig(
            EnumEntryOrder.INCLUDE_ANNOTATIONS to listOf("com.example.Alphabetical")
        )
    )

    @Nested
    inner class `annotation on enum` {
        @Test
        fun `reports out of order`() {
            val code = """
                package com.example
    
                annotation class Alphabetical
    
                @Alphabetical
                enum class Fruit {
                    BANANA,
                    APPLE,
                }         
            """.trimIndent()

            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
            assertThat(findings[0]).hasSourceLocation(7, 5)
            assertThat(findings[0]).hasMessage(
                "Entries for enum `Fruit` are not declared in alphabetical order. " +
                    "Reorder so that `APPLE` is before `BANANA`."
            )
        }

        @Test
        fun `does not report in order`() {
            val code = """
                package com.example
    
                annotation class Alphabetical
    
                @Alphabetical
                enum class Fruit {
                    APPLE,
                    BANANA
                }         
            """.trimIndent()

            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `does not report empty enum`() {
            val code = """
                package com.example
    
                annotation class Alphabetical
    
                @Alphabetical
                enum class Fruit

                @Alphabetical
                enum class Vegetable {
                    ;
                    abstract fun eat()
                }
            """.trimIndent()

            assertThat(subject.compileAndLintWithContext(env, code))
                .isEmpty()
        }

        @Test
        fun `does not report nested enum`() {
            val code = """
                package com.example
                    
                annotation class Alphabetical
                
                @Alphabetical
                enum class Meal {
                    BREAKFAST,
                    LUNCH,
                    SUPPER,
                    ;
                    enum class Fruit {
                        BANANA, APPLE
                    }
                }                
            """.trimIndent()

            assertThat(subject.compileAndLintWithContext(env, code))
                .isEmpty()
        }

        @Test
        fun `reports emoji entries out of order`() {
            // https://en.wikipedia.org/wiki/Emoji#Unicode_blocks

            @Suppress("EnumEntryName")
            val code = """
                package com.example
    
                annotation class Alphabetical
    
                @Alphabetical
                enum class Fruit {
                    `🍒`,
                    `🍎`,
                }         
            """.trimIndent()

            assertThat(subject.compileAndLintWithContext(env, code))
                .hasSize(1)
                .hasTextLocations("`🍒`,")
        }

        @Test
        fun `reports names in backticks out of order`() {
            @Suppress("EnumEntryName", "RemoveRedundantBackticks")
            val code = """
                package com.example
    
                annotation class Alphabetical
    
                @Alphabetical
                enum class Fruit {
                    apple,
                    ` apple`,
                }         
            """.trimIndent()

            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
            assertThat(findings[0]).hasMessage(
                "Entries for enum `Fruit` are not declared in alphabetical order. " +
                    "Reorder so that ``apple`` is before ` apple`."
            )
        }

        @Test
        fun `reports keyword names out of order`() {
            @Suppress("EnumEntryName")
            val code = """
                package com.example
    
                annotation class Alphabetical
    
                @Alphabetical
                enum class Fruit {
                    `null`,
                    NULL,
                }         
            """.trimIndent()

            assertThat(subject.compileAndLintWithContext(env, code))
                .hasSize(1)
        }

        @Test
        fun `reports out of order for more complex enum`() {
            val code = """
                package com.example
    
                annotation class Alphabetical
    
                @Alphabetical
                enum class Fruit(val id: String) {                
                    BANANA("banana") {
                        override val calories: Int = 100
                        override fun eat() {
                            println("Eating a banana!")
                        }
                    },
                    APPLE("apple") {
                        override val calories: Int = 50
                        override fun eat() {
                            println("Eating an apple!")
                        }
                    };
            
                    abstract val calories: Int
            
                    abstract fun eat()
            
                    fun eatAll() {
                        println("Eating all the fruit!")
                    }
                }         
            """.trimIndent()

            assertThat(subject.compileAndLintWithContext(env, code))
                .hasSize(1)
        }
    }

    @Nested
    inner class `annotation on supertype` {
        @Test
        fun `reports out of order for super interface`() {
            val code = """
                package com.example
                
                annotation class Alphabetical
                
                @Alphabetical
                interface Identifiable {
                    val id: String
                }
                
                enum class Fruit(override val id: String) : Identifiable {
                    BANANA("banana"),
                    APPLE("apple"),
                }
            """.trimIndent()

            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
            assertThat(findings[0]).hasMessage(
                "Entries for enum `Fruit` (which implements `Identifiable`) are not declared " +
                    "in alphabetical order. Reorder so that `APPLE` is before `BANANA`."
            )
        }

        @Test
        fun `reports out of order for super super interface`() {
            val code = """
                package com.example                

                annotation class Alphabetical
                
                @Alphabetical
                interface Identifiable {
                    val id: String
                }
                
                interface Delicious

                interface PrettyPrintable : Identifiable {
                    val prettyString: String
                }
                
                enum class Fruit(override val prettyString: String, override val id: String) : PrettyPrintable, Delicious {
                    BANANA("Yellow banana", "banana"),
                    APPLE("Red apple", "apple"),
                }
            """.trimIndent()

            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
        }

        @Test
        fun `reports interface with recursive type parameters out of order`() {
            val code = """
                package com.example                

                annotation class Alphabetical
                                
                @Alphabetical
                interface Tree<T : Tree<T>> {
                    fun getChildren(): List<T>
                }
                
                enum class BinaryTree : Tree<BinaryTree> {
                    NODE {
                        override fun getChildren(): List<BinaryTree> = listOf(LEAF, LEAF)
                    },
                    LEAF {
                        override fun getChildren(): List<BinaryTree> = emptyList()
                    },
                }
            """.trimIndent()

            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
        }
    }
}
