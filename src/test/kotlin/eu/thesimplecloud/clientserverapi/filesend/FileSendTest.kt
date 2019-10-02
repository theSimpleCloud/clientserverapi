package eu.thesimplecloud.clientserverapi.filesend

import eu.thesimplecloud.clientserverapi.client.NettyClient
import eu.thesimplecloud.clientserverapi.communication.testclasses.TestConnectedClientValue
import eu.thesimplecloud.clientserverapi.lib.json.JsonData
import eu.thesimplecloud.clientserverapi.server.NettyServer
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.io.File

class FileSendTest {

    var nettyServer = NettyServer<TestConnectedClientValue>("127.0.0.1", 1920)
    var nettyClient = NettyClient("127.0.0.1", 1920)


    @Test(timeout = 6000)
    fun test() {
        nettyServer.registerPacketsByPackage("eu.thesimplecloud.clientserverapi.filetransfer.packets")
        GlobalScope.launch {
            nettyServer.start()
        }
        while (!nettyServer.isListening()) {
            Thread.sleep(10)
        }
        nettyClient.addPacketsPackage("eu.thesimplecloud.clientserverapi.filetransfer.packets")
        GlobalScope.launch {
            nettyClient.start()
        }

        while (!nettyClient.isOpen()) {
            Thread.sleep(10)
        }
        JsonData().append("test", "Hallo123").saveAsFile(File("test.json"))
        val promise = nettyClient.sendFile(File("test.json"), "test123.json")
        promise.syncUninterruptibly()
        val file = File("test123.json")
        Assert.assertTrue(file.exists())
        Assert.assertEquals("Hallo123", JsonData.fromJsonFile(file).getString("test"))
    }


}