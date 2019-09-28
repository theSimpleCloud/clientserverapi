package eu.thesimplecloud.clientserverapi.client.packets

import eu.thesimplecloud.clientserverapi.lib.packet.IPacket
import eu.thesimplecloud.clientserverapi.lib.packet.packetsender.IPacketSender
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.ObjectPacket

class PacketOutGetPacketId() : eu.thesimplecloud.clientserverapi.lib.packet.packettype.ObjectPacket<String>(String::class.java) {


    constructor(packetName: String) : this() {
       this.value = packetName
    }

    override suspend fun handle(packetSender: IPacketSender): IPacket? {
        return null
    }
}