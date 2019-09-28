package eu.thesimplecloud.clientserverapi.lib.json

import com.google.gson.ExclusionStrategy
import com.google.gson.FieldAttributes


class GsonExcludeExclusionStrategy : ExclusionStrategy {
    override fun shouldSkipField(fieldAttributes: FieldAttributes): Boolean {
        return fieldAttributes.getAnnotation(GsonExclude::class.java) != null
    }

    override fun shouldSkipClass(aClass: Class<*>): Boolean {
        return false
    }
}
