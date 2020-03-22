package eu.thesimplecloud.clientserverapi.json

import eu.thesimplecloud.clientserverapi.lib.json.JsonData
import org.junit.Test

class JsonTest {


    @Test
    fun test() {
        val list = listOf("test1", "test2", "test3")
        println(JsonData.fromObject("test2224").getAsJsonString())
    }

}