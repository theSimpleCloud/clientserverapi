package eu.thesimplecloud.clientserverapi.lib.packetresponse

import com.google.common.collect.Maps
import eu.thesimplecloud.clientserverapi.lib.packet.WrappedPacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import java.util.*

class PacketResponseManager : IPacketResponseManager {

    val packetResponseHandlers = Maps.newConcurrentMap<UUID, WrappedResponseHandler<out Any>>()


    @Synchronized
    override fun registerResponseHandler(packetUniqueId: UUID, wrappedResponseHandler: WrappedResponseHandler<out Any>) {
        packetResponseHandlers[packetUniqueId] = wrappedResponseHandler
    }

    @Synchronized
    override fun getResponseHandler(packetUniqueId: UUID): WrappedResponseHandler<out Any>? {
        return packetResponseHandlers[packetUniqueId]
    }


    @Synchronized
    fun incomingPacket(wrappedPacket: WrappedPacket) {
        println("incoming packet " + wrappedPacket.packetData.sentPacketName)
        val wrappedResponseHandler: WrappedResponseHandler<out Any>? = packetResponseHandlers.remove(wrappedPacket.packetData.uniqueId)
        println("response handler for ${wrappedPacket.packetData.sentPacketName}: $wrappedResponseHandler")
        wrappedResponseHandler
                ?: throw IllegalStateException("Incoming: No response handler was available for packet by id ${wrappedPacket.packetData.uniqueId}")
        val response = wrappedResponseHandler.packetResponseHandler.handleResponse(wrappedPacket.packet)
        val packetPromise = wrappedResponseHandler.communicationPromise
        println("found promise $packetPromise | response: $response ; ${response?.let { it::class }}")
        packetPromise as ICommunicationPromise<Any>
        if (response is Throwable) {
            packetPromise.tryFailure(response)
        } else {
            packetPromise.trySuccess(response)
        }
    }


}