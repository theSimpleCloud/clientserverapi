package eu.thesimplecloud.clientserverapi.testobject.server

import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.ObjectPacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import eu.thesimplecloud.clientserverapi.testobject.TestObj

class PacketInMessage : ObjectPacket<String>() {
    override suspend fun handle(connection: IConnection): ICommunicationPromise<out Any> {
        return success(TestObj(this.value!!.length))
    }
}