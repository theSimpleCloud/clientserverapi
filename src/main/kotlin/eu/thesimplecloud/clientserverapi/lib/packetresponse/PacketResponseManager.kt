package eu.thesimplecloud.clientserverapi.lib.packetresponse

import com.google.common.collect.Maps
import eu.thesimplecloud.clientserverapi.lib.LogFile
import eu.thesimplecloud.clientserverapi.lib.packet.WrappedPacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import java.util.*

class PacketResponseManager : IPacketResponseManager {

    private val packetResponseHandlers = Maps.newConcurrentMap<UUID, WrappedResponseHandler<out Any>>()


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
        LogFile.INSTANCE.addMessage("incoming packet " + wrappedPacket.packetData.sentPacketName + " (${wrappedPacket.packetData.uniqueId})")
        val wrappedResponseHandler: WrappedResponseHandler<out Any>? = packetResponseHandlers.remove(wrappedPacket.packetData.uniqueId)
        LogFile.INSTANCE.addMessage("response handler for ${wrappedPacket.packetData.sentPacketName}: $wrappedResponseHandler (${wrappedPacket.packetData.uniqueId})")
        wrappedResponseHandler
                ?: throw IllegalStateException("Incoming: No response handler was available for packet by id ${wrappedPacket.packetData.uniqueId}")
        val response = wrappedResponseHandler.packetResponseHandler.handleResponse(wrappedPacket.packet)
        val packetPromise = wrappedResponseHandler.communicationPromise
        LogFile.INSTANCE.addMessage("found promise $packetPromise | response: $response | ${response?.let { it::class }} (${wrappedPacket.packetData.uniqueId})")
        LogFile.INSTANCE.addMessage("throwable (${wrappedPacket.packetData.uniqueId}) ${response is Throwable}")
        packetPromise as ICommunicationPromise<Any>
        if (response is Throwable) {
            packetPromise.tryFailure(response)
        } else {
            packetPromise.trySuccess(response)
        }
    }


}