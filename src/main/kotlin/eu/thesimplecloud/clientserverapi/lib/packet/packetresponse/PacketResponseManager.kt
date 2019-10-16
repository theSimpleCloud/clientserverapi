package eu.thesimplecloud.clientserverapi.lib.packet.packetresponse

import com.google.common.collect.Maps
import eu.thesimplecloud.clientserverapi.lib.packet.WrappedPacket
import eu.thesimplecloud.clientserverapi.lib.packet.communicationpromise.ICommunicationPromise
import eu.thesimplecloud.clientserverapi.utils.printEmptyLine
import java.lang.IllegalStateException
import java.util.*
import kotlin.collections.HashMap

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
        val wrappedResponseHandler: WrappedResponseHandler<out Any>? = packetResponseHandlers.remove(wrappedPacket.packetData.uniqueId)
        //println("calling consumer for packet ${wrappedPacket.packet::class.java.simpleName} uuid was ${wrappedPacket.packetData.uniqueId} $consumer")
        wrappedResponseHandler ?: throw IllegalStateException("Incoming: No response handler was available for packet by id ${wrappedPacket.packetData.uniqueId}")
        val response = wrappedResponseHandler.packetResponseHandler.handleResponse(wrappedPacket.packet)
        val packetPromise = wrappedResponseHandler.communicationPromise
        packetPromise as ICommunicationPromise<Any?>
        packetPromise.setSuccess(response)
    }


}