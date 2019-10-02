package eu.thesimplecloud.clientserverapi.filetransfer.packets

import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.IPacket
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.JsonPacket
import java.util.*

class PacketIOFileTransferComplete() : JsonPacket(){

    constructor(uuid: UUID, savePath: String) : this() {
        this.jsonData.append("uuid", uuid).append("savePath", savePath)
    }

    override suspend fun handle(connection: IConnection): IPacket? {
        val uuid = this.jsonData.getObject("uuid", UUID::class.java) ?: return null
        val savePath = this.jsonData.getString("savePath") ?: return null
        connection.getCommunicationBootstrap().getTransferFileManager().fileTransferComplete(uuid, savePath)
        return null
    }
}