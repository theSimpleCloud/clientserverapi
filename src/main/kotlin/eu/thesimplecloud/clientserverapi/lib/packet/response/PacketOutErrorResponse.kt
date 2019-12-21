package eu.thesimplecloud.clientserverapi.lib.packet.response

import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.ObjectPacket

/**
 * This packet will be sent as response when the handle method of a packet threw an exception.
 */
class PacketOutErrorResponse(exception: Throwable) : ObjectPacket<Throwable>() {

    init {
        this.value = exception
    }

    override suspend fun handle(connection: IConnection) {

    }
}