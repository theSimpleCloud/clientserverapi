package eu.thesimplecloud.clientserverapi.testobject

import eu.thesimplecloud.clientserverapi.client.NettyClient
import eu.thesimplecloud.clientserverapi.communication.testclasses.TestConnectedClientValue
import eu.thesimplecloud.clientserverapi.server.NettyServer
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.Test

class ObjectPacketTest {

    /*

    @Test(timeout = 2000)
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
        while (!nettyClient.isOpen()) {
            Thread.sleep(10)
        }
        nettyClient.sendQuery(PacketIOMessage()).syncUninterruptibly().addResultListener { println("test: " + it.toString()) }
        nettyClient.sendQuery(PacketIOMessage()).syncUninterruptibly().addResultListener { println("test: " + it.toString()) }
        nettyClient.sendQuery(PacketIOMessage()).syncUninterruptibly().addResultListener { println("test: " + it.toString()) }
        nettyClient.sendQuery(PacketIOMessage()).syncUninterruptibly().addResultListener { println("test: " + it.toString()) }
        nettyClient.sendQuery(PacketIOMessage()).syncUninterruptibly().addResultListener { println("test: " + it.toString()) }
        Thread.sleep(200)
    }

    */


}