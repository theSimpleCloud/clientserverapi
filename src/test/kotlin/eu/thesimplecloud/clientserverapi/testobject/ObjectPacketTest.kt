package eu.thesimplecloud.clientserverapi.testobject

class ObjectPacketTest {

    /*
    @Test
    fun test() {
        val nettyServer = NettyServer<TestConnectedClientValue>("127.0.0.1", 1921)
        nettyServer.addPacketsByPackage("eu.thesimplecloud.clientserverapi.testobject")
        GlobalScope.launch {
            nettyServer.start()
        }
        while (!nettyServer.isListening()) {
            Thread.sleep(10)
        }
        val nettyClient = NettyClient("127.0.0.1", 1921)
        nettyClient.addPacketsByPackage("eu.thesimplecloud.clientserverapi.testobject")
        GlobalScope.launch {
            nettyClient.start()
        }

        //nettyClient.sendQuery<JsonData>(PacketIOMessage("hi"))
        //        .thenNonNull { println(it.getString("test")) }
        //        .addFailureListener { println(it.message) }
        nettyClient.sendQuery<TestObj>(PacketIOMessage("hi"), 10000).addResultListener { println("result: " + it) }
                .addFailureListener { println("failure ${it::class.java.simpleName} ${it.message}") }
        GlobalScope.launch {

            /*
            val completedPromise = nettyClient.sendUnitQuery(PacketIOMessage("hi")).awaitUninterruptibly()
            println("success: " + completedPromise.isSuccess)
            val completedPromise2 = nettyClient.sendQuery<ITestObj>(PacketIOMessage("hi")).awaitUninterruptibly()
            println("success2: " + completedPromise2.isSuccess)
            */
        }
        //nettyClient.sendUnitQuery(PacketIOMessage("hi")).awaitUninterruptibly().addResultListener { println("test: " + it.toString()) }
        //7nettyClient.sendUnitQuery(PacketIOMessage("hi")).awaitUninterruptibly().addResultListener { println("test: " + it.toString()) }
        //nettyClient.sendUnitQuery(PacketIOMessage("hi")).awaitUninterruptibly().addResultListener { println("test: " + it.toString()) }
        Thread.sleep(2000)
    }
    */

}