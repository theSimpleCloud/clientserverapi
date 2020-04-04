package eu.thesimplecloud.clientserverapi.directorysync

import eu.thesimplecloud.clientserverapi.client.NettyClient
import eu.thesimplecloud.clientserverapi.communication.testclasses.TestConnectedClientValue
import eu.thesimplecloud.clientserverapi.server.NettyServer
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.Test
import java.io.File

class DirectorySyncLoopTest {

    var nettyServer = NettyServer<TestConnectedClientValue>("127.0.0.1", 1919)
    var nettyClient = NettyClient("127.0.0.1", 1919)

    @Test
    fun test() {
        GlobalScope.launch {
            nettyServer.start()
        }
        while (!nettyServer.isListening()) {
            Thread.sleep(10)
        }
        GlobalScope.launch {
            nettyClient.start()
        }

        while (!nettyClient.isOpen()) {
            Thread.sleep(10)
        }
        Thread.sleep(1000)
        val dir = File("testSync/testSyncAServer/")
        dir.mkdirs()
        nettyServer.getDirectorySyncManager().setTmpZipDirectory(File("storage/zippedTemplates/"))
        val directorySync = nettyServer.getDirectorySyncManager().createDirectorySync(dir, "testSync/testSyncOtherSide/")
        val clientOnServerSide = this.nettyServer.clientManager.getClients().firstOrNull()!!
        val promise = directorySync.syncDirectory(clientOnServerSide)
        promise.awaitUninterruptibly()

        while (true) {
            Thread.sleep(100)
        }
    }

}