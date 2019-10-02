package eu.thesimplecloud.clientserverapi.directorysync

import eu.thesimplecloud.clientserverapi.client.NettyClient
import eu.thesimplecloud.clientserverapi.communication.testclasses.TestConnectedClientValue
import eu.thesimplecloud.clientserverapi.lib.json.JsonData
import eu.thesimplecloud.clientserverapi.server.NettyServer
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.apache.commons.io.FileUtils
import org.junit.After
import org.junit.Assert
import org.junit.Test
import java.io.File

class DirectorySyncTest {


    var nettyServer = NettyServer<TestConnectedClientValue>("127.0.0.1", 1919)
    var nettyClient = NettyClient("127.0.0.1", 1919)

    @Test(timeout = 6500)
    fun test(){
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

        val file = File("syncFolder/")
        val otherSideDir = File("syncFolderOtherSide/")
        val file1 = File(file, "json1.json")
        val file2 = File(file, "json2.json")
        file.mkdirs()
        JsonData().append("first", "test1").saveAsFile(file1)
        JsonData().append("second", "test2").saveAsFile(file2)

        val directorySync = nettyClient.getCommunicationBootstrap().getDirectorySyncManager().createDirectorySync(file, "syncFolderOtherSide/")
        directorySync.syncDirectory(nettyClient)
        Thread.sleep(1000)
        //test1
        Assert.assertEquals("test1", JsonData.fromJsonFile(File(otherSideDir, "json1.json")).getString("first"))
        Assert.assertEquals("test2", JsonData.fromJsonFile(File(otherSideDir, "json2.json")).getString("second"))

        JsonData().append("first", "test5").saveAsFile(file1)
        file2.delete()
        Thread.sleep(2500)
        //test2
        Assert.assertEquals("test5", JsonData.fromJsonFile(File(otherSideDir, "json1.json")).getString("first"))
        Assert.assertFalse(File(otherSideDir, "json2.json").exists())
        Thread.sleep(300)
    }

    @After
    fun after(){
        FileUtils.deleteDirectory(File("syncFolder/"))
        FileUtils.deleteDirectory(File("syncFolderOtherSide/"))
    }

}