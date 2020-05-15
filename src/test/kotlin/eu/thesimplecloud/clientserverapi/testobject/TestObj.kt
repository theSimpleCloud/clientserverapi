package eu.thesimplecloud.clientserverapi.testobject

import eu.thesimplecloud.clientserverapi.lib.json.PacketExclude

class TestObj(private val number: Int) : ITestObj {

    @PacketExclude
    val referenceObj = RecursiveReferenceObj(this)

    override fun getNumber(): Int {
        return number
    }
}