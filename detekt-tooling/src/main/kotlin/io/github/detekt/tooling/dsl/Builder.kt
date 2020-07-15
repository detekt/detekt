package io.github.detekt.tooling.dsl

interface Builder<T> {

    fun build(): T
}
