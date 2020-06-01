package eu.thesimplecloud.clientserverapi.testobject

class ObjectPacketTest {

    /*
    @Test
    fun test() {
        val nettyServer = NettyServer<TestConnectedClientValue>("127.0.0.1", 1921)
        nettyServer.packetManager.registerPacket(PacketIOWork::class.java)
        nettyServer.addPacketsByPackage("eu.thesimplecloud.clientserverapi.testobject.server")
        thread {
            nettyServer.start()
        }
        while (!nettyServer.isListening()) {
            Thread.sleep(10)
        }
        val nettyClient = NettyClient("127.0.0.1", 1921)
        nettyClient.addPacketsByPackage("eu.thesimplecloud.clientserverapi.testobject.client")
        thread {
            nettyClient.start()
        }

        //nettyClient.sendQuery<JsonData>(PacketIOMessage("hi"))
        //        .thenNonNull { println(it.getString("test")) }
        //        .addFailureListener { println(it.message) }
        Thread.sleep(1000)
        val time = System.currentTimeMillis()
        nettyClient.sendQuery<ITestObj>(PacketOutMessage("hi"), 1500).addResultListener { println("result: $it time: ${System.currentTimeMillis() - time}") }
                .addFailureListener { println("failure ${it::class.java.simpleName} ${it.message}") }
        nettyClient.sendQuery<ITestObj>(PacketIOWork(), 1500).addResultListener { println("result: $it time: ${System.currentTimeMillis() - time}") }
                .addFailureListener { println("failure ${it::class.java.simpleName} ${it.message}") }
        nettyServer.getDebugMessageManager().enable(DebugMessage.PACKET_RECEIVED)
        nettyClient.shutdown()
        Thread.sleep(1000)
    }
    */

}