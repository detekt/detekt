package io.gitlab.arturbosch.detekt.test

import ch.tutteli.atrium.api.fluent.en_GB.feature
import ch.tutteli.atrium.api.fluent.en_GB.notToBe
import ch.tutteli.atrium.api.fluent.en_GB.toBe
import ch.tutteli.atrium.creating.Expect
import ch.tutteli.atrium.domain.builders.ExpectImpl
import ch.tutteli.atrium.domain.creating.changers.ExtractedFeaturePostStep
import ch.tutteli.atrium.reporting.RawString
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.internal.PathFilters
import java.nio.file.Paths

fun <T : Config> Expect<T>.hasNotKey(key: String): Expect<T> =
    _valueOrNull<T, Any>(this, key) { toBe(null) }

fun <T : Config> Expect<T>.hasKey(key: String): Expect<T> =
    _valueOrNull<T, Any>(this, key) { notToBe(null) }


fun <T : Config, V : Any> Expect<T>.hasKeyValue(key: String, value: V): Expect<T> =
    _valueOrNull<T, V>(this, key) { toBe(value) }

private fun <T : Config, V : Any> _valueOrNull(
    expect: Expect<T>,
    key: String,
    assertionCreator: Expect<V?>.() -> Unit
): Expect<T> =
    expect.feature("valueOrNull($key)", { valueOrNull(key) }, assertionCreator)

fun <T : Config, V : Any> Expect<T>.valueOrDefault(
    key: String,
    default: V
): Expect<V> = _valueOrDefault(this, key, default).getExpectOfFeature()

fun <T : Config, V : Any> Expect<T>.valueOrDefault(
    key: String,
    default: V,
    assertionCreator: Expect<V>.() -> Unit
): Expect<T> = _valueOrDefault(this, key, default).addToInitial(assertionCreator)

private fun <T : Config, V : Any> _valueOrDefault(
    expect: Expect<T>,
    key: String,
    default: V
): ExtractedFeaturePostStep<T, V> = ExpectImpl.feature.manualFeature(expect, "valueOrDefault($key, $default)") {
    valueOrDefault(key, default)
}

fun Expect<PathFilters?>.isIgnored(pathAsString: String) =
    createAndAddAssertion("is", RawString.create("ignored")) { it?.isIgnored(Paths.get(pathAsString)) == true }

fun Expect<PathFilters?>.isNotIgnored(pathAsString: String) =
    createAndAddAssertion(
        "is not",
        RawString.create("ignored")
    ) { it?.isIgnored(Paths.get(pathAsString)) == false }
