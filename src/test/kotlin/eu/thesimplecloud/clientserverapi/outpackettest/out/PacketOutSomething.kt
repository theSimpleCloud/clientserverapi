package eu.thesimplecloud.clientserverapi.outpackettest.out

import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.JsonPacket
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.ObjectPacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise

class PacketOutSomething() : ObjectPacket<String>("Hallo - - - -") {



    override suspend fun handle(connection: IConnection): ICommunicationPromise<out Any> {
        return unit()
    }
}