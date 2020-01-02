package eu.thesimplecloud.clientserverapi.lib.defaultpackets

import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.IPacket
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.ObjectPacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import org.apache.commons.io.FileUtils
import java.io.File

class PacketIODeleteFile() : ObjectPacket<String>() {

    constructor(path: String) : this() {
        this.value = path
    }

    override suspend fun handle(connection: IConnection): ICommunicationPromise<Unit> {
        val value = this.value ?: return contentException("value")
        val file = File(value)
        if (file.exists()) {
            if (file.isDirectory) {
                FileUtils.deleteDirectory(file)
            } else {
                file.delete()
            }
        }
        return unit()
    }
}