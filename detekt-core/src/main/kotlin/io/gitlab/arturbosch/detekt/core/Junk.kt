package io.gitlab.arturbosch.detekt.core

import java.nio.file.Files
import java.nio.file.Path
import java.util.HashMap
import java.util.stream.Collectors
import java.util.stream.Stream

/**
 * @author Artur Bosch
 */

fun <T> Stream<T>.toList(): List<T> = collect(Collectors.toList<T>())

fun <K, V> List<Pair<K, List<V>>>.toMergedMap(): Map<K, List<V>> {
	val map = HashMap<K, MutableList<V>>()
	this.forEach {
		map.merge(it.first, it.second.toMutableList(), { l1, l2 -> l1.apply { addAll(l2) } })
	}
	return map
}

fun Path.exists(): Boolean = Files.exists(this)
fun Path.isFile(): Boolean = Files.isRegularFile(this)
fun Path.isDirectory(): Boolean = Files.isDirectory(this)