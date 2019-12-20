package eu.thesimplecloud.clientserverapi.testobject

import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.json.JsonData
import eu.thesimplecloud.clientserverapi.lib.packet.IPacket
import eu.thesimplecloud.clientserverapi.lib.packet.exception.PacketException
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.ObjectPacket
import java.lang.NullPointerException

class PacketIOMessage() : ObjectPacket<String>() {

    constructor(message: String): this() {
        this.value = message
    }

    override suspend fun handle(connection: IConnection): ArrayIndexOutOfBoundsException {
        return ArrayIndexOutOfBoundsException("test test 123")
    }
}