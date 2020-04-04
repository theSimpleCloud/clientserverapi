package eu.thesimplecloud.clientserverapi.lib.defaultpackets

import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.IPacket
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.JsonPacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import java.util.*

class PacketIOCreateFileTransfer() : JsonPacket() {

    constructor(uuid: UUID, path: String, lastModified: Long): this() {
        this.jsonData.append("uuid", uuid).append("path", path).append("lastModified", lastModified)
    }

    override suspend fun handle(connection: IConnection): ICommunicationPromise<Unit> {
        val uuid = this.jsonData.getObject("uuid", UUID::class.java) ?: return contentException("uuid")
        val path = this.jsonData.getString("path") ?: return contentException("path")
        val lastModified = this.jsonData.getLong("lastModified") ?: return contentException("lastModified")
        connection.getCommunicationBootstrap().getTransferFileManager().creteFileTransfer(uuid, path, lastModified)
        return unit()
    }
}