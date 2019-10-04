package eu.thesimplecloud.clientserverapi.lib.filetransfer.packets

import eu.thesimplecloud.clientserverapi.lib.ByteBufStringHelper
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.IPacket
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.BytePacket
import java.util.*

class PacketIOFileTransfer() : BytePacket() {

    constructor(uuid: UUID, byteArray: ByteArray) : this() {
        ByteBufStringHelper.writeString(buffer, uuid.toString())
        buffer.writeInt(byteArray.size)
        buffer.writeBytes(byteArray)
    }

    override suspend fun handle(connection: IConnection): IPacket? {
        val uuidString = ByteBufStringHelper.nextString(buffer)
        val uuid = UUID.fromString(uuidString)
        val byteArray = ByteArray(buffer.readInt())
        buffer.readBytes(byteArray)
        connection.getCommunicationBootstrap().getTransferFileManager().addBytesToTransferFile(uuid, byteArray.toTypedArray())
        return null
    }
}