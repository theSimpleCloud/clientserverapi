package eu.thesimplecloud.clientserverapi.lib.defaultpackets

import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.IPacket
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.JsonPacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import java.util.*

class PacketIOFileTransferComplete() : JsonPacket(){

    constructor(uuid: UUID) : this() {
        this.jsonData.append("uuid", uuid)
    }

    override suspend fun handle(connection: IConnection): ICommunicationPromise<Unit> {
        val uuid = this.jsonData.getObject("uuid", UUID::class.java) ?: return contentException("uuid")
        connection.getCommunicationBootstrap().getTransferFileManager().fileTransferComplete(uuid)
        return unit()
    }
}