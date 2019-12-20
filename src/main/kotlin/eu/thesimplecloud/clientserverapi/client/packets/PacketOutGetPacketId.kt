package eu.thesimplecloud.clientserverapi.client.packets

import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.IPacket
import eu.thesimplecloud.clientserverapi.lib.packet.packetsender.IPacketSender
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.ObjectPacket

class PacketOutGetPacketId() : ObjectPacket<String>() {


    constructor(packetName: String) : this() {
       this.value = packetName
    }

    override suspend fun handle(connection: IConnection): Any? {
        return null
    }
}