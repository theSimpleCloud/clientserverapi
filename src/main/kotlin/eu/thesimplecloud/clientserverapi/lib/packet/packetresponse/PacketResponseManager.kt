package eu.thesimplecloud.clientserverapi.lib.packet.packetresponse

import eu.thesimplecloud.clientserverapi.lib.packet.WrappedPacket
import eu.thesimplecloud.clientserverapi.lib.packet.packetpromise.IPacketPromise
import eu.thesimplecloud.clientserverapi.utils.printEmptyLine
import java.util.*
import java.util.function.Consumer
import kotlin.collections.HashMap

class PacketResponseManager : IPacketResponseManager {

    private val packetResponseHandlers = HashMap<UUID, WrappedResponseHandler<out Any>>()


    override fun registerResponseHandler(packetUniqueId: UUID, wrappedResponseHandler: WrappedResponseHandler<out Any>) {
        printEmptyLine()
        packetResponseHandlers[packetUniqueId] = wrappedResponseHandler
    }

    override fun getResponseHandler(packetUniqueId: UUID): WrappedResponseHandler<out Any>? {
        printEmptyLine()
        return packetResponseHandlers[packetUniqueId]
    }



    fun incomingPacket(wrappedPacket: WrappedPacket) {
        val wrappedResponseHandler: WrappedResponseHandler<out Any>? = packetResponseHandlers.remove(wrappedPacket.packetData.uniqueId)
        //println("calling consumer for packet ${wrappedPacket.packet::class.java.simpleName} uuid was ${wrappedPacket.packetData.uniqueId} $consumer")
        printEmptyLine()
        wrappedResponseHandler ?: return
        val response = wrappedResponseHandler.packetResponseHandler.handleResponse(wrappedPacket.packet)
        val packetPromise = wrappedResponseHandler.packetPromise
        packetPromise as IPacketPromise<Any?>
        packetPromise.setSuccess(response)
    }


}