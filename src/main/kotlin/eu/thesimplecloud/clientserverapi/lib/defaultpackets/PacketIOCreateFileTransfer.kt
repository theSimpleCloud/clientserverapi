package eu.thesimplecloud.clientserverapi.lib.defaultpackets

import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.IPacket
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.JsonPacket
import java.util.*

class PacketIOCreateFileTransfer() : JsonPacket() {

    constructor(uuid: UUID, path: String): this() {
        this.jsonData.append("uuid", uuid).append("path", path)
    }

    override suspend fun handle(connection: IConnection) {
        val uuid = this.jsonData.getObject("uuid", UUID::class.java) ?: return
        val path = this.jsonData.getString("path") ?: return
        connection.getCommunicationBootstrap().getTransferFileManager().creteFileTransfer(uuid, path)
    }
}