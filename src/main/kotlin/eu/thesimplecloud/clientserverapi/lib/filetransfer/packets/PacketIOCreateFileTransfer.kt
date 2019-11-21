package eu.thesimplecloud.clientserverapi.lib.filetransfer.packets

import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.IPacket
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.JsonPacket
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.ObjectPacket
import java.util.*

class PacketIOCreateFileTransfer() : JsonPacket() {

    constructor(uuid: UUID, path: String): this() {
        this.jsonData.append("uuid", uuid).append("path", path)
    }

    override suspend fun handle(connection: IConnection): IPacket? {
        val uuid = this.jsonData.getObject("uuid", UUID::class.java) ?: return null
        val path = this.jsonData.getString("path") ?: return null
        connection.getCommunicationBootstrap().getTransferFileManager().creteFileTransfer(uuid, path)
        return null
    }
}