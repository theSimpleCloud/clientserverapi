package eu.thesimplecloud.clientserverapi.testobject

class TestObj(private val number: Int) : ITestObj {
    override fun getNumber(): Int {
        return number
    }
}