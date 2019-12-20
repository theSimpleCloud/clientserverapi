package eu.thesimplecloud.clientserverapi.directorysync

import eu.thesimplecloud.clientserverapi.client.NettyClient
import eu.thesimplecloud.clientserverapi.communication.testclasses.TestConnectedClientValue
import eu.thesimplecloud.clientserverapi.server.NettyServer
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.Test
import java.io.File

class DirectorySyncTest2 {

    /*

    var nettyServer = NettyServer<TestConnectedClientValue>("127.0.0.1", 1919)
    var nettyClient = NettyClient("127.0.0.1", 1919)

    @Test()
    fun test() {
        nettyServer.addPacketsByPackage("eu.thesimplecloud.clientserverapi.lib.filetransfer.packets")
        GlobalScope.launch {
            nettyServer.start()
        }
        while (!nettyServer.isListening()) {
            Thread.sleep(10)
        }
        nettyClient.addPacketsByPackage("eu.thesimplecloud.clientserverapi.lib.filetransfer.packets")
        GlobalScope.launch {
            nettyClient.start()
        }

        while (!nettyClient.isOpen()) {
            Thread.sleep(10)
        }
        nettyClient.getPacketIdsSyncPromise().syncUninterruptibly()
        val dir = File("templates/")
        dir.mkdirs()
        val otherSideDir = File("templatesOtherSide/")
        val directorySync = nettyServer.getDirectorySyncManager().createDirectorySync(dir, "templatesOtherSide/")
        val clientOnServerSide = this.nettyServer.clientManager.getClients()[0]
        val promise = directorySync.syncDirectory(clientOnServerSide)
        while(true) {

        }

    }


    */

}