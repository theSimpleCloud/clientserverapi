package eu.thesimplecloud.clientserverapi.testobject.client

import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.ObjectPacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise

class PacketOutMessage(val string: String) : ObjectPacket<String>() {

    init {
        this.value = string
    }

    override suspend fun handle(connection: IConnection): ICommunicationPromise<out Any> {
        return unit()
    }
}