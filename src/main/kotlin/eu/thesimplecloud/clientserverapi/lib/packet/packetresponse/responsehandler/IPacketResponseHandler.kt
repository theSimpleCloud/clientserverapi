package eu.thesimplecloud.clientserverapi.lib.packet.packetresponse.responsehandler

import eu.thesimplecloud.clientserverapi.lib.packet.IPacket

@FunctionalInterface
interface IPacketResponseHandler<T : Any> {

    companion object {
        private val NULL_HANDLER = object : IPacketResponseHandler<Any> {
            override fun handleResponse(packet: IPacket): Any? {
                return null
            }
        }

        fun <T : Any> getNullHandler() = NULL_HANDLER as IPacketResponseHandler<T>
    }

    /**
     * Handles a packets sent as response and returns its content as an object.
     */
    fun handleResponse(packet: IPacket): T?

}