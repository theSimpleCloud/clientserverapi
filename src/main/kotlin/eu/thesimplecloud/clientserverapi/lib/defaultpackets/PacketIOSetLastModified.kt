package eu.thesimplecloud.clientserverapi.lib.defaultpackets

import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.filetransfer.FileInfo
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.ObjectPacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import java.io.File

class PacketIOSetLastModified() : ObjectPacket<FileInfo>() {

    constructor(fileInfo: FileInfo): this() {
        this.value = fileInfo
    }

    override suspend fun handle(connection: IConnection): ICommunicationPromise<out Any> {
        val fileInfo = this.value ?: return contentException("value")
        val file = File(fileInfo.relativePath)
        if (file.exists()) file.setLastModified(fileInfo.lastModified)
        return unit()
    }
}