package dev.detekt.tooling.dsl

interface Builder<T> {

    fun build(): T
}
