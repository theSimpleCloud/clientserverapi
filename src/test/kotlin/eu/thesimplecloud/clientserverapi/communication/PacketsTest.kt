package eu.thesimplecloud.clientserverapi.communication


class PacketsTest {
    /*

    var nettyServer = NettyServer<TestConnectedClientValue>("127.0.0.1", 1917)
    var nettyClient = NettyClient("127.0.0.1", 1917)



    @Test(timeout = 6000)
    fun test() {
        println(0)
        nettyServer.registerPacketsByPackage("eu.thesimplecloud.clientserverapi.communication.testclasses")
        thread {
            nettyServer.start()
        }
        while (!nettyServer.isListening()) {
            Thread.sleep(10)
        }
        nettyClient.addPacketsPackage("eu.thesimplecloud.clientserverapi.communication.testclasses")
        thread {
            nettyClient.start()
        }
        while (!nettyClient.isOpen()) {
            Thread.sleep(10)
        }

        val packetPromise: IPacketPromise<String> = nettyClient.sendQuery(TestJsonPacket("test")) { packet ->
            packet as JsonPacket
            packet.jsonData.getString("message")
        }
        packetPromise.addPacketPromiseListener { println(it.get()) }
        packetPromise.sync()
        Assert.assertEquals(packetPromise.get(), "test")
    }

    @After
    fun after() {
        nettyServer.shutdown()
        nettyClient.shutdown()
    }
    */

}