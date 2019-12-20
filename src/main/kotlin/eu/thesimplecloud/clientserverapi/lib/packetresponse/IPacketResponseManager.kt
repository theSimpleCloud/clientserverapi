package eu.thesimplecloud.clientserverapi.lib.packetresponse

import java.util.*

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