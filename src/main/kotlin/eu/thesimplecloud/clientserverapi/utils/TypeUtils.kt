package eu.thesimplecloud.clientserverapi.utils

import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

fun <T : Any> Any.getClassOfGenericTypeAtIndex(index: Int): Class<T> {
    val type = this::class.java.genericSuperclass
    val ttype = (type as ParameterizedType).actualTypeArguments[index]
    val className = ttype.toString().split(" ")[1]
    return Class.forName(className) as Class<T>
}