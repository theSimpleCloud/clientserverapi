package eu.thesimplecloud.clientserverapi.lib.defaultpackets

import eu.thesimplecloud.clientserverapi.lib.connection.AbstractConnection
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.ObjectPacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise

class PacketIOConnectionWillClose() : ObjectPacket<Unit>() {

    override suspend fun handle(connection: IConnection): ICommunicationPromise<out Any> {
        connection as AbstractConnection
        connection.setConnectionCloseIntended()
        return unit()
    }
}