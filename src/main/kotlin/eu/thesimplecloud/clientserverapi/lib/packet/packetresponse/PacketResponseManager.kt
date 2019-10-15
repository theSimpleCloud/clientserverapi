package eu.thesimplecloud.clientserverapi.lib.packet.packetresponse

import eu.thesimplecloud.clientserverapi.lib.packet.WrappedPacket
import eu.thesimplecloud.clientserverapi.lib.packet.communicationpromise.ICommunicationPromise
import eu.thesimplecloud.clientserverapi.utils.printEmptyLine
import java.util.*
import kotlin.collections.HashMap

class PacketResponseManager : IPacketResponseManager {

    private val packetResponseHandlers = HashMap<UUID, WrappedResponseHandler<out Any>>()


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
        wrappedResponseHandler ?: return
        val response = wrappedResponseHandler.packetResponseHandler.handleResponse(wrappedPacket.packet)
        val packetPromise = wrappedResponseHandler.communicationPromise
        packetPromise as ICommunicationPromise<Any?>
        packetPromise.setSuccess(response)
    }


}