package eu.thesimplecloud.clientserverapi.lib.extension

fun <K, V> Map<K, V>.getKey(value: V): K? {
    return getKey { it == value }
}

fun <K, V> Map<K, V>.getKey(predicate: (V) -> Boolean): K? {
    return this.entries.firstOrNull { predicate(it.value) }?.key
}