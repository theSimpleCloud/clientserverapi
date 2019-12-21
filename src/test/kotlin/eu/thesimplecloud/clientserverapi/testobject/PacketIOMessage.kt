package eu.thesimplecloud.clientserverapi.testobject

import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.promise.CommunicationPromise
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.ObjectPacket
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class PacketIOMessage() : ObjectPacket<String>() {

    constructor(message: String): this() {
        this.value = message
    }

    override suspend fun handle(connection: IConnection): ICommunicationPromise<Int> {
        val promise = CommunicationPromise<Int>(300)
        handle0(promise)
        return promise
    }

    private fun handle0(promise: CommunicationPromise<Int>) {
        GlobalScope.launch {
            Thread.sleep(150)
            promise.setSuccess(2997)
        }
    }
}