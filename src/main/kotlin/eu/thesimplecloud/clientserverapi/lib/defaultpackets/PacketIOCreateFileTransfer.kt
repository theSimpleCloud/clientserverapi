package eu.thesimplecloud.clientserverapi.lib.defaultpackets

import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.IPacket
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.JsonPacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import java.util.*

class PacketIOCreateFileTransfer() : JsonPacket() {

    constructor(uuid: UUID, path: String): this() {
        this.jsonData.append("uuid", uuid).append("path", path)
    }

    override suspend fun handle(connection: IConnection): ICommunicationPromise<Unit> {
        val uuid = this.jsonData.getObject("uuid", UUID::class.java) ?: return contentException("uuid")
        val path = this.jsonData.getString("path") ?: return contentException("path")
        connection.getCommunicationBootstrap().getTransferFileManager().creteFileTransfer(uuid, path)
        return unit()
    }
}