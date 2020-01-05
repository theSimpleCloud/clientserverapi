package eu.thesimplecloud.clientserverapi.client.packets

import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.IPacket
import eu.thesimplecloud.clientserverapi.lib.packet.packetsender.IPacketSender
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.ObjectPacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise

class PacketOutGetPacketId() : ObjectPacket<ArrayList<String>>() {


    constructor(packetNames: ArrayList<String>) : this() {
       this.value = packetNames
    }

    override suspend fun handle(connection: IConnection): ICommunicationPromise<Unit> = unit()
}