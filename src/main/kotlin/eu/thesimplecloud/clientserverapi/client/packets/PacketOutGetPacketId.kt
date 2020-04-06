package eu.thesimplecloud.clientserverapi.client.packets

import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.ObjectPacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise

class PacketOutGetPacketId() : ObjectPacket<Array<String>>() {


    constructor(packetNames: ArrayList<String>) : this() {
       this.value = packetNames.toTypedArray()
    }

    override suspend fun handle(connection: IConnection): ICommunicationPromise<Unit> = unit()
}