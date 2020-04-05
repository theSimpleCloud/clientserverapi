package eu.thesimplecloud.clientserverapi

import eu.thesimplecloud.clientserverapi.lib.packet.IPacket
import eu.thesimplecloud.clientserverapi.lib.packetmanager.PacketManager
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test


class PacketManagerTest {

    @Test
    fun before() {
        val packetManager = PacketManager()
        val job = GlobalScope.launch {
            val packetId = packetManager.getPacketIdRegisteredPromise(IPacket::class.java)
                    .getBlockingOrNull()
            Assert.assertEquals(0, packetId)
        }
        packetManager.registerPacket(0, IPacket::class.java)
        Assert.assertEquals(1, packetManager.getUnusedId())
        Assert.assertEquals(0, packetManager.getIdFromPacket(IPacket::class.java))
        runBlocking {
            job.join()
        }
    }

}