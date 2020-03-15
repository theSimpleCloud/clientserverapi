package eu.thesimplecloud.clientserverapi.json

class TestObj(val list: List<String>) : ITestObj {

    override fun getAll(): List<String> = this.list
}