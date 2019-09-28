package eu.thesimplecloud.clientserverapi

import eu.thesimplecloud.clientserverapi.lib.packet.IPacket
import eu.thesimplecloud.clientserverapi.lib.packetmanager.PacketManager
import org.junit.Assert
import org.junit.Test


class PacketManagerTest {

    @Test
    fun before() {
        val packetManager = PacketManager()
        val packetIdCompletableFuture = packetManager.getPacketIdCompletableFuture(IPacket::class.java)
        packetManager.registerPacket(0, IPacket::class.java)
        Assert.assertEquals(0, packetIdCompletableFuture.get())
        Assert.assertEquals(1, packetManager.getUnusedId())
        Assert.assertEquals(0, packetManager.getIdFromPacket(IPacket::class.java))
    }

}