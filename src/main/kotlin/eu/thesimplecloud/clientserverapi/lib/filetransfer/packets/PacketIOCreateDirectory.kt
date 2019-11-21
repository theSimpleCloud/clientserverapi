package eu.thesimplecloud.clientserverapi.lib.filetransfer.packets

import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.IPacket
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.ObjectPacket
import java.io.File

class PacketIOCreateDirectory() : ObjectPacket<String>(String::class.java) {

    constructor(path: String) : this() {
        this.value = path
    }

    override suspend fun handle(connection: IConnection): IPacket? {
        val value = this.value ?: return null
        val file = File(value)
        file.mkdirs()
        return null
    }
}