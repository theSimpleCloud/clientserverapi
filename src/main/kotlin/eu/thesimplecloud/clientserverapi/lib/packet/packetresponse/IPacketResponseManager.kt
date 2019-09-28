package eu.thesimplecloud.clientserverapi.lib.packet.packetresponse

import eu.thesimplecloud.clientserverapi.lib.packet.WrappedPacket
import java.util.*
import java.util.function.Consumer

interface IPacketResponseManager {

    /**
     * Registers a [WrappedResponseHandler]
     */
    fun registerResponseHandler(packetUniqueId: UUID, wrappedResponseHandler: WrappedResponseHandler<out Any>)

    /**
     * Returns a [WrappedResponseHandler] found by the specified id
     */
    fun getResponseHandler(packetUniqueId: UUID): WrappedResponseHandler<out Any>?

}