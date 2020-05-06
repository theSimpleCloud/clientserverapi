package eu.thesimplecloud.clientserverapi.testobject

import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.ObjectPacket
import eu.thesimplecloud.clientserverapi.lib.promise.CommunicationPromise
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class PacketIOMessage() : ObjectPacket<String>() {

    constructor(message: String): this() {
        this.value = message
    }

    override suspend fun handle(connection: IConnection): ICommunicationPromise<out Any> {
        return CommunicationPromise.of(TestObj(6))
    }

    private fun handle0(promise: CommunicationPromise<Int>) {
        GlobalScope.launch {
            Thread.sleep(150)
            promise.setSuccess(2997)
        }
    }
}