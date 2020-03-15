package eu.thesimplecloud.clientserverapi.json

import eu.thesimplecloud.clientserverapi.lib.json.JsonData
import org.junit.Test

class JsonTest {


    @Test
    fun test() {
        println(JsonData.fromObject(TestObj(listOf("test", "test222"))).getAsJsonString())
    }

}