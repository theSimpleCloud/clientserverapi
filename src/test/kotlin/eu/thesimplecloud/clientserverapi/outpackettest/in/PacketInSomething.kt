package eu.thesimplecloud.clientserverapi.outpackettest.`in`

import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.ObjectPacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise

class PacketInSomething : ObjectPacket<String>() {
    override suspend fun handle(connection: IConnection): ICommunicationPromise<out Any> {
        println("received: " + this.value)
        return unit()
    }
}