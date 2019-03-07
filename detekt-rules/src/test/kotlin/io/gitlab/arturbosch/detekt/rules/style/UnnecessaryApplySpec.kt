package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class UnnecessaryApplySpec : Spek({

    val subject by memoized { UnnecessaryApply(Config.empty) }

    describe("UnnecessaryApply rule") {

        context("unnecessary apply expressions that can be changed to ordinary method call") {

            it("reports an apply on non-nullable type") {
                assertThat(subject.lint("""
                    fun f() {
                        val a : Int = 0
                        a.apply {
                            plus(1)
                        }
                    }
                """)).hasSize(1)
            }

            it("reports an apply on nullable type") {
                assertThat(subject.lint("""
                    fun f() {
                        val a : Int? = null
                        // Resolution: we can't say here if plus is on 'this' or just a side effect when a is not null
                        // However such cases should be better handled with an if-null check instead misusing apply
                        a?.apply {
                            plus(1)
                        }
                    }
                """)).hasSize(1)
            }

            it("reports a false negative apply on nullable type - #1485") {
                assertThat(subject.lint("""
                    val firebaseHttpMetric: Any? = Any()
                    fun Any.setHttpResponseCode() = Unit
                    fun f() {
                        firebaseHttpMetric?.apply {
                            setHttpResponseCode()
                        }
                    }
                """)).hasSize(1)
            }

            it("does not report an apply with lambda block") {
                assertThat(subject.lint("""
                    fun f() {
                        val a : Int? = null
                        a.apply({
                            plus(1)
                        })
                    }
                """)).isEmpty()
            }

            it("does report single statement in apply used as function argument") {
                assertThat(subject.lint("""
                    fun b(i : Int?) {
                    }
                    fun f() {
                        val a : Int? = null
                        b(a.apply {
                            toString()
                        })
                    }
               """)).isEmpty()
            }

            it("does report single assignment statement in apply used as function argument - #1517") {
                assertThat(subject.lint("""
                    list.add(
                        if (something) {
                            SomeObject().apply {
                                x = y
                            }
                        } else {
                            SomeOtherObject()
                        }
                    )
                """)).isEmpty()
            }

            it("does not report applies with lambda body containing more than one statement") {
                assertThat(subject.lint("""
                    fun b(i : Int?) {
                    }
                    fun f() {
                        val a : Int? = null
                        a.apply {
                            plus(1)
                            plus(2)
                        }
                        a?.apply {
                            plus(1)
                            plus(2)
                        }
                        b(1.apply {
                            plus(1)
                            plus(2)
                        })
                    }
                """)).isEmpty()
            }
        }

        context("reported false positives - #1305") {

            it("is used within an assignment expr itself") {
                assertThat(subject.lint("""
                    val content = Intent().apply { putExtra("", 1) }
                """.trimIndent())).isEmpty()
            }

            it("is used as return type of extension function") {
                assertThat(subject.lint("""
                    fun setColor(color: Int) = apply { initialColor = color }
                """.trimIndent())).isEmpty()
            }

            it("should not flag apply when assigning property on this") {
                assertThat(subject.lint("""
                    private val requestedInterval by lazy {
                        MutableLiveData<Int>().apply { value = UsageFragment.INTERVAL_DAY }
                    }
                """.trimIndent())).isEmpty()
            }

            it("should not report apply when using it after returning something") {
                assertThat(subject.lint("""
                    inline class Money(var amount: Int)
                    fun returnMe = (Money(5)).apply { amount = 10 }
                """.trimIndent())).isEmpty()
            }

            it("should not report apply usage inside safe chained expressions") {
                assertThat(subject.lint("""
                    fun test() {
                        val arguments = listOf(1,2,3)
                        ?.map { it * 2 }
                        ?.apply { if (true) add(4) }
                        ?: listOf(0)
                    }
                """.trimIndent())).isEmpty()
            }
        }

        context("false positive in single nesting expressions - #1473") {

            it("should not report the if expression") {
                assertThat(subject.lint("""
                    class Preference {
                        fun shouldDisable() = true
                    }
                    fun falsePositive() {
                        Preference().apply {
                            if (shouldDisable()) {
                                // actions
                            }
                        }
                    }
                """.trimIndent())).isEmpty()
            }

            it("should report reference expressions") {
                assertThat(subject.lint("""
                    class Preference { val propertyAccess = 5 }
                    fun correctReporting() {
                        Preference().apply {
                            propertyAccess
                        }

                        Preference().apply {
                            this.propertyAccess
                        }
                    }
                """.trimIndent())).hasSize(2)
            }
        }
    }
})
